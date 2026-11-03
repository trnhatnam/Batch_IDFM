package fr.natsystem.batch.IDFTransports.jobs;

import fr.natsystem.batch.IDFTransports.listener.StepProgressListener;
import fr.natsystem.batch.IDFTransports.models.Trip;
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
public class ImportTripJobConfig {

    private static int CHUNK_SIZE = 200;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    public ImportTripJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcTemplate jdbcTemplate) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public Job importTripJob(@Qualifier("stagingTripStep") Step stagingStep,
                             @Qualifier("importTripStep") Step importStep,
                             @Qualifier("deleteObsoleteTripsStep") Step deleteObsoleteStep,
                             @Qualifier("cleanTripsStagingTableStep") Step cleaningStep){
        return new JobBuilder(jobRepository)
                .start(stagingStep)
                .next(importStep)
                .next(deleteObsoleteStep)
                .next(cleaningStep)
                .build();
    }

    @Bean
    public Step stagingTripStep(@Qualifier("tripsCSVReader") FlatFileItemReader<Trip> reader,
                               @Qualifier("tripsWriter") JdbcBatchItemWriter<Trip> writer,
                               @Qualifier("tripProcessor") ItemProcessor<Trip,Trip> processor,
                               StepProgressListener listener) {
        return new StepBuilder(jobRepository)
                .<Trip, Trip>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(listener)
                .build();

    }


    @Bean
    public Step importTripStep(StepProgressListener listener){
        String query = """
            INSERT INTO trips (
                route_id,service_id,trip_id,trip_headsign,
                trip_short_name,direction_id,block_id,shape_id,
                wheelchair_accessible,bikes_allowed
            )
            SELECT
                route_id,service_id,trip_id,trip_headsign,
                trip_short_name,direction_id,block_id,shape_id,
                wheelchair_accessible,bikes_allowed
            FROM trips_staging
            ON CONFLICT (trip_id)
            DO UPDATE
            SET (
                route_id,
                service_id,
                trip_headsign,
                trip_short_name,
                direction_id,
                block_id,
                shape_id,
                wheelchair_accessible,
                bikes_allowed
            ) = (
                excluded.route_id,
                excluded.service_id,
                excluded.trip_headsign,
                excluded.trip_short_name,
                excluded.direction_id,
                excluded.block_id,
                excluded.shape_id,
                excluded.wheelchair_accessible,
                excluded.bikes_allowed
            )
            WHERE (
                trips.route_id,
                trips.service_id,
                trips.trip_headsign,
                trips.trip_short_name,
                trips.direction_id,
                trips.block_id,
                trips.shape_id,
                trips.wheelchair_accessible,
                trips.bikes_allowed
            ) IS DISTINCT FROM (
                excluded.route_id,
                excluded.service_id,
                excluded.trip_headsign,
                excluded.trip_short_name,
                excluded.direction_id,
                excluded.block_id,
                excluded.shape_id,
                excluded.wheelchair_accessible,
                excluded.bikes_allowed
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
    public Step deleteObsoleteTripsStep(){
        String query = """
            DELETE FROM trips t
            WHERE NOT EXISTS (
                SELECT 1
                FROM trips_staging ts
                WHERE ts.trip_id = t.trip_id
            );
        """;
        return new StepBuilder("deleteObsoleteStopTimesStep",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.update(query);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

    @Bean
    public Step cleanTripsStagingTableStep(){
        String query = "TRUNCATE trips_staging";
        return new StepBuilder("cleanTablesStep",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.execute(query);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
}
