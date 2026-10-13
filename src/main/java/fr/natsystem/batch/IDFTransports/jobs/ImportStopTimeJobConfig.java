package fr.natsystem.batch.IDFTransports.jobs;

import fr.natsystem.batch.IDFTransports.dto.StopTimeDTO;
import fr.natsystem.batch.IDFTransports.models.StopTime;
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
public class ImportStopTimeJobConfig  {

    private static int CHUNK_SIZE = 200;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ImportStopTimeJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job importStopTimesJob(@Qualifier("importStopTimesStep") Step importStep){
        return new JobBuilder(jobRepository)
                .start(importStep)
                .build();
    }

    @Bean
    public Step importStopTimesStep(@Qualifier("stopTimesCSVReader") FlatFileItemReader<StopTime> reader,
                                    @Qualifier("stopTimesDBWriter") JdbcBatchItemWriter<StopTimeDTO> writer,
                                    @Qualifier("stopTimesProcessor") ItemProcessor<StopTime, StopTimeDTO> processor) {
        return new StepBuilder(jobRepository)
                .<StopTime,StopTimeDTO>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();

    }
}
