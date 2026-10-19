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
            MERGE INTO stop_times st
                    USING (
                        SELECT DISTINCT ON (trip_id, stop_sequence) *
                        FROM stop_times_staging
                        ORDER BY trip_id,
                                 stop_sequence,
                                 hash_record_extended(stop_times_staging.*,0) DESC
                    ) sts
            ON st.trip_id = sts.trip_id AND st.stop_sequence = sts.stop_sequence
            WHEN MATCHED AND hash_record_extended(sts.*,0) > hash_record_extended(st.*,0) THEN
                UPDATE SET (trip_id,arrival_time,departure_time,start_pickup_drop_off_window,end_pickup_drop_off_window,stop_id,stop_sequence,pickup_type,drop_off_type,local_zone_id,stop_headsign,timepoint,pickup_booking_rule_id,drop_off_booking_rule_id)
                = (sts.trip_id,sts.arrival_time,sts.departure_time,sts.start_pickup_drop_off_window,sts.end_pickup_drop_off_window,sts.stop_id,sts.stop_sequence,sts.pickup_type,sts.drop_off_type,sts.local_zone_id,sts.stop_headsign,sts.timepoint,sts.pickup_booking_rule_id,sts.drop_off_booking_rule_id)
            WHEN NOT MATCHED THEN
                INSERT (trip_id,arrival_time,departure_time,start_pickup_drop_off_window,end_pickup_drop_off_window,stop_id,stop_sequence,pickup_type,drop_off_type,local_zone_id,stop_headsign,timepoint,pickup_booking_rule_id,drop_off_booking_rule_id)
                VALUES (sts.trip_id,sts.arrival_time,sts.departure_time,sts.start_pickup_drop_off_window,sts.end_pickup_drop_off_window,sts.stop_id,sts.stop_sequence,sts.pickup_type,sts.drop_off_type,sts.local_zone_id,sts.stop_headsign,sts.timepoint,sts.pickup_booking_rule_id,sts.drop_off_booking_rule_id);
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
