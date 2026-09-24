package fr.natsystem.batch.IDFTransports.jobs;

import fr.natsystem.batch.IDFTransports.models.Stop;
import fr.natsystem.batch.IDFTransports.readers.ReaderStrategy;
import fr.natsystem.batch.IDFTransports.writers.WriterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;

import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
public abstract class ImportJobConfig {

    private static int CHUNK_SIZE = 200;

    private final ReaderStrategy readerStrategy;
    private final WriterStrategy writerStrategy;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    protected Job createImportJob(String jobName, Step importStep) {
        return new JobBuilder(jobName,jobRepository)
                .start(importStep)
                .build();
    }

    protected Step createImportStep(String stepName) {
        return new StepBuilder(stepName, jobRepository)
                .<Stop, Stop>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(readerStrategy.getCSVReader())
                .writer(writerStrategy.getWriter())
                .build();

    }
}
