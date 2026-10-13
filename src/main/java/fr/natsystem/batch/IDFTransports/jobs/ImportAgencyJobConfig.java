package fr.natsystem.batch.IDFTransports.jobs;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportAgencyJobConfig {

    private static int CHUNK_SIZE = 200;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ImportAgencyJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job importAgencyJob(@Qualifier("importAgencyStep") Step importStep){
        return new JobBuilder(jobRepository)
                .start(importStep)
                .build();
    }

    @Bean
    public Step importAgencyStep(@Qualifier("agencyCSVReader") FlatFileItemReader reader,
                                 @Qualifier("agencyDBWriter") JdbcBatchItemWriter writer) {
        return new StepBuilder(jobRepository)
                .chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(reader)
                .writer(writer)
                .build();

    }
}
