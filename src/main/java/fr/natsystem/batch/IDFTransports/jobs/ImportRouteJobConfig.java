package fr.natsystem.batch.IDFTransports.jobs;

import fr.natsystem.batch.IDFTransports.listener.StepProgressListener;
import fr.natsystem.batch.IDFTransports.models.Route;
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
public class ImportRouteJobConfig {

    private static int CHUNK_SIZE = 200;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    public ImportRouteJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcTemplate jdbcTemplate) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public Job importRouteJob(@Qualifier("stagingRouteStep")  Step stagingStep,
                              @Qualifier("importRouteStep") Step importStep,
                              @Qualifier("deleteObsoleteRouteStep") Step deleteObsoleteStep,
                              @Qualifier("cleanRouteStagingTableStep") Step cleaningStep){
        return new JobBuilder(jobRepository)
                .start(stagingStep)
                .next(importStep)
                .next(deleteObsoleteStep)
                .next(cleaningStep)
                .build();
    }

    @Bean
    public Step stagingRouteStep(@Qualifier("routesCSVReader") FlatFileItemReader<Route> reader,
                                @Qualifier("routesDBWriter") JdbcBatchItemWriter<Route> writer,
                                @Qualifier("routesProcessor") ItemProcessor<Route, Route> processor,
                                StepProgressListener listener) {
        return new StepBuilder(jobRepository)
                .<Route,Route>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(listener)
                .build();

    }

    @Bean
    public Step importRouteStep(StepProgressListener listener){
        String query = """
            INSERT INTO routes (
                route_id,agency_id,route_short_name,route_long_name,route_desc,
                route_type,route_url,route_color,route_text_color,route_sort_order
            )
            SELECT
                route_id,agency_id,route_short_name,route_long_name,route_desc,
                route_type,route_url,route_color,route_text_color,route_sort_order
            FROM routes_staging
            ON CONFLICT (route_id)
            DO UPDATE
            SET (
                agency_id,
                route_short_name,
                route_long_name,
                route_desc,
                route_type,
                route_url,
                route_color,
                route_text_color,
                route_sort_order
            ) = (
                excluded.agency_id,
                excluded.route_short_name,
                excluded.route_long_name,
                excluded.route_desc,
                excluded.route_type,
                excluded.route_url,
                excluded.route_color,
                excluded.route_text_color,
                excluded.route_sort_order
            )
            WHERE (
                routes.agency_id,
                routes.route_short_name,
                routes.route_long_name,
                routes.route_desc,
                routes.route_type,
                routes.route_url,
                routes.route_color,
                routes.route_text_color,
                routes.route_sort_order
            ) IS DISTINCT FROM (
                excluded.agency_id,
                excluded.route_short_name,
                excluded.route_long_name,
                excluded.route_desc,
                excluded.route_type,
                excluded.route_url,
                excluded.route_color,
                excluded.route_text_color,
                excluded.route_sort_order
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
    public Step deleteObsoleteRouteStep(){
        String query = """
            DELETE FROM routes r
            WHERE NOT EXISTS (
                SELECT 1
                FROM routes_staging rs
                WHERE rs.route_id = r.route_id
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
    public Step cleanRouteStagingTableStep(){
        String query = "TRUNCATE routes_staging";
        return new StepBuilder("cleanTablesStep",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.execute(query);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
}
