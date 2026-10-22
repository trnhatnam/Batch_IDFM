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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportTripJobConfig {

    private static int CHUNK_SIZE = 200;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ImportTripJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job importTripJob(@Qualifier("importTripStep") Step importStep){
        return new JobBuilder(jobRepository)
                .start(importStep)
                .build();
    }

    @Bean
    public Step importTripStep(@Qualifier("tripsCSVReader") FlatFileItemReader<Trip> reader,
                               @Qualifier("tripsStagingWriter") JdbcBatchItemWriter<Trip> writer,
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
}
