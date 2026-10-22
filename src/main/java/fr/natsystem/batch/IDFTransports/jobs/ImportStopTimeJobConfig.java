package fr.natsystem.batch.IDFTransports.jobs;

import fr.natsystem.batch.IDFTransports.dto.StopTimeDTO;
import fr.natsystem.batch.IDFTransports.listener.StepProgressListener;
import fr.natsystem.batch.IDFTransports.models.StopTime;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepExecution;
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
public class ImportStopTimeJobConfig  {

    private static int CHUNK_SIZE = 200;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    public ImportStopTimeJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcTemplate jdbcTemplate) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public Job importStopTimesJob(@Qualifier("stagingStopTimesStep") Step stagingStep,
                                  @Qualifier("importStopTimesStep") Step importStep,
                                  @Qualifier("cleanStopTimesStagingTableStep") Step cleaningStep){
        return new JobBuilder(jobRepository)
                .start(stagingStep)
                .next(importStep)
                .next(cleaningStep)
                .build();
    }

    @Bean
    public Step stagingStopTimesStep(@Qualifier("stopTimesCSVReader") FlatFileItemReader<StopTime> reader,
                                     @Qualifier("stopTimesStagingWriter") JdbcBatchItemWriter<StopTimeDTO> writer,
                                     @Qualifier("stopTimesProcessor") ItemProcessor<StopTime, StopTimeDTO> processor,
                                     StepProgressListener listener) {
        return new StepBuilder(jobRepository)
                .<StopTime,StopTimeDTO>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(listener)
                .build();

    }

    @Bean
    public Step importStopTimesStep(StepProgressListener listener){
        String query = """
            WITH ranked AS (
                SELECT
                    sts.*,
                    ROW_NUMBER() OVER (
                        PARTITION BY trip_id, stop_sequence
                        ORDER BY arrival_time, departure_time
                    ) AS rn
                FROM stop_times_staging sts
            )
            INSERT INTO stop_times (
                trip_id, arrival_time, departure_time,
                start_pickup_drop_off_window, end_pickup_drop_off_window,
                stop_id, stop_sequence, pickup_type, drop_off_type,
                local_zone_id, stop_headsign, timepoint,
                pickup_booking_rule_id, drop_off_booking_rule_id
            )
            SELECT
                trip_id, arrival_time, departure_time,
                start_pickup_drop_off_window, end_pickup_drop_off_window,
                stop_id, stop_sequence, pickup_type, drop_off_type,
                local_zone_id, stop_headsign, timepoint,
                pickup_booking_rule_id, drop_off_booking_rule_id
            FROM ranked
            WHERE rn = 1
            ON CONFLICT (trip_id, stop_sequence)
            DO UPDATE
            SET (
                arrival_time,
                departure_time,
                start_pickup_drop_off_window,
                end_pickup_drop_off_window,
                stop_id,
                pickup_type,
                drop_off_type,
                local_zone_id,
                stop_headsign,
                timepoint,
                pickup_booking_rule_id,
                drop_off_booking_rule_id
            ) = (
                excluded.arrival_time,
                excluded.departure_time,
                excluded.start_pickup_drop_off_window,
                excluded.end_pickup_drop_off_window,
                excluded.stop_id,
                excluded.pickup_type,
                excluded.drop_off_type,
                excluded.local_zone_id,
                excluded.stop_headsign,
                excluded.timepoint,
                excluded.pickup_booking_rule_id,
                excluded.drop_off_booking_rule_id
            )
            WHERE (
                stop_times.arrival_time,
                stop_times.departure_time,
                stop_times.start_pickup_drop_off_window,
                stop_times.end_pickup_drop_off_window,
                stop_times.stop_id,
                stop_times.pickup_type,
                stop_times.drop_off_type,
                stop_times.local_zone_id,
                stop_times.stop_headsign,
                stop_times.timepoint,
                stop_times.pickup_booking_rule_id,
                stop_times.drop_off_booking_rule_id
            ) IS DISTINCT FROM (
                excluded.arrival_time,
                excluded.departure_time,
                excluded.start_pickup_drop_off_window,
                excluded.end_pickup_drop_off_window,
                excluded.stop_id,
                excluded.pickup_type,
                excluded.drop_off_type,
                excluded.local_zone_id,
                excluded.stop_headsign,
                excluded.timepoint,
                excluded.pickup_booking_rule_id,
                excluded.drop_off_booking_rule_id
            );
        """;

        return new StepBuilder(jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    long write = jdbcTemplate.update(query);
                    StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
                    stepExecution.setWriteCount(write);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .listener(listener)
                .build();
    }

    @Bean
    public Step cleanStopTimesStagingTableStep(){
        String query = "TRUNCATE stop_times_staging";
        return new StepBuilder("cleanTablesStep",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.update(query);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

}
