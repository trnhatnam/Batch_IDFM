package fr.natsystem.batch.IDFTransports.jobs;

import fr.natsystem.batch.IDFTransports.listener.StepProgressListener;
import fr.natsystem.batch.IDFTransports.models.Stop;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportStopJobConfig {

    private static int CHUNK_SIZE = 200;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    public ImportStopJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcTemplate jdbcTemplate) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public Job importStopJob(@Qualifier("stagingStopStep") Step stagingStep,
                             @Qualifier("importStopStep") Step importStep,
                             @Qualifier("deleteObsoleteStopStep") Step deleteObsoleteStep,
                             @Qualifier("cleanStopStagingTableStep") Step cleaningStep){
        return new JobBuilder(jobRepository)
                .start(stagingStep)
                .next(importStep)
                .next(deleteObsoleteStep)
                .next(cleaningStep)
                .build();
    }

    @Bean
    public Step stagingStopStep(@Qualifier("stopsCSVReader") FlatFileItemReader<Stop> reader,
                                @Qualifier("stopsDBWriter") JdbcBatchItemWriter<Stop> writer,
                                StepProgressListener listener) {
        return new StepBuilder(jobRepository)
                .<Stop, Stop>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(reader)
                .writer(writer)
                .listener(listener)
                .build();

    }

    @Bean
    public Step importStopStep(StepProgressListener listener){
        String query = """
            INSERT INTO stops (
                stop_id,stop_code,stop_name,stop_desc,stop_lon,stop_lat,zone_id,
                stop_url,location_type,parent_station,stop_timezone,level_id,
                wheelchair_boarding,platform_code,stop_access
            )
            SELECT
                stop_id,stop_code,stop_name,stop_desc,stop_lon,stop_lat,zone_id,
                stop_url,location_type,parent_station,stop_timezone,level_id,
                wheelchair_boarding,platform_code,stop_access
            FROM stops_staging
            ON CONFLICT (stop_id)
            DO UPDATE
            SET (
                stop_code,
                stop_name,
                stop_desc,
                stop_lon,
                stop_lat,
                zone_id,
                stop_url,
                location_type,
                parent_station,
                stop_timezone,
                level_id,
                wheelchair_boarding,
                platform_code,
                stop_access
            ) = (
                excluded.stop_code,
                excluded.stop_name,
                excluded.stop_desc,
                excluded.stop_lon,
                excluded.stop_lat,
                excluded.zone_id,
                excluded.stop_url,
                excluded.location_type,
                excluded.parent_station,
                excluded.stop_timezone,
                excluded.level_id,
                excluded.wheelchair_boarding,
                excluded.platform_code,
                excluded.stop_access
            )
            WHERE (
                stops.stop_code,
                stops.stop_name,
                stops.stop_desc,
                stops.stop_lon,
                stops.stop_lat,
                stops.zone_id,
                stops.stop_url,
                stops.location_type,
                stops.parent_station,
                stops.stop_timezone,
                stops.level_id,
                stops.wheelchair_boarding,
                stops.platform_code,
                stops.stop_access
            ) IS DISTINCT FROM (
                excluded.stop_code,
                excluded.stop_name,
                excluded.stop_desc,
                excluded.stop_lon,
                excluded.stop_lat,
                excluded.zone_id,
                excluded.stop_url,
                excluded.location_type,
                excluded.parent_station,
                excluded.stop_timezone,
                excluded.level_id,
                excluded.wheelchair_boarding,
                excluded.platform_code,
                excluded.stop_access
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
    public Step deleteObsoleteStopStep(){
        String query = """
            DELETE FROM stops s
            WHERE NOT EXISTS (
                SELECT 1
                FROM stops_staging st
                WHERE st.stop_id = s.stop_id
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
    public Step cleanStopStagingTableStep(){
        String query = "TRUNCATE stops_staging";
        return new StepBuilder("cleanTablesStep",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.execute(query);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
}
