package fr.natsystem.batch.IDFTransports.jobs;

import fr.natsystem.batch.IDFTransports.listener.StepProgressListener;
import fr.natsystem.batch.IDFTransports.models.Agency;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportAgencyJobConfig {

    private static int CHUNK_SIZE = 200;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    public ImportAgencyJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcTemplate jdbcTemplate) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public Job importAgencyJob(@Qualifier("stagingAgencyStep")  Step stagingStep,
                               @Qualifier("importAgencyStep") Step importStep,
                               @Qualifier("deleteObsoleteAgencyStep") Step deleteObsoleteStep,
                               @Qualifier("cleanAgencyStagingTableStep") Step cleaningStep){
        return new JobBuilder(jobRepository)
                .start(stagingStep)
                .next(importStep)
                .next(deleteObsoleteStep)
                .next(cleaningStep)
                .build();
    }

    @Bean
    public Step stagingAgencyStep(@Qualifier("agencyCSVReader") FlatFileItemReader<Agency> reader,
                                 @Qualifier("agencyDBWriter") JdbcBatchItemWriter<Agency> writer,
                                 @Qualifier("agencyProcessor") ItemProcessor<Agency,Agency> processor,
                                 StepProgressListener listener) {
        return new StepBuilder(jobRepository)
                .<Agency,Agency>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(listener)
                .build();

    }
    @Bean
    public Step importAgencyStep(StepProgressListener listener){
        String query = """
            INSERT INTO agency (
                agency_id,agency_name,agency_url,agency_timezone,agency_lang,
                agency_phone,agency_email,agency_fare_url,ticketing_deep_link_id
            )
            SELECT
                agency_id,agency_name,agency_url,agency_timezone,agency_lang,
                agency_phone,agency_email,agency_fare_url,ticketing_deep_link_id
            FROM agency_staging
            ON CONFLICT (agency_id)
            DO UPDATE
            SET (
                agency_name,
                agency_url,
                agency_timezone,
                agency_lang,
                agency_phone,
                agency_email,
                agency_fare_url,
                ticketing_deep_link_id
            ) = (
                excluded.agency_name,
                excluded.agency_url,
                excluded.agency_timezone,
                excluded.agency_lang,
                excluded.agency_phone,
                excluded.agency_email,
                excluded.agency_fare_url,
                excluded.ticketing_deep_link_id
            )
            WHERE (
                agency.agency_name,
                agency.agency_url,
                agency.agency_timezone,
                agency.agency_lang,
                agency.agency_phone,
                agency.agency_email,
                agency.agency_fare_url,
                agency.ticketing_deep_link_id
            ) IS DISTINCT FROM (
                excluded.agency_name,
                excluded.agency_url,
                excluded.agency_timezone,
                excluded.agency_lang,
                excluded.agency_phone,
                excluded.agency_email,
                excluded.agency_fare_url,
                excluded.ticketing_deep_link_id
            );
        """;

        return new StepBuilder(jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    chunkContext.getStepContext().getStepExecution().setWriteCount(jdbcTemplate.update(query));
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .listener(listener)
                .build();
    }

    @Bean
    public Step deleteObsoleteAgencyStep(){
        String query = """
            DELETE FROM agency a
            WHERE NOT EXISTS (
                SELECT 1
                FROM agency_staging ags
                WHERE ags.agency_id = a.agency_id
            );
        """;
        return new StepBuilder(jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.update(query);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

    @Bean
    public Step cleanAgencyStagingTableStep(){
        String query = "TRUNCATE agency_staging";
        return new StepBuilder("cleanTablesStep",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.execute(query);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
}
