package fr.natsystem.batch.IDFTransports.jobs;

import fr.natsystem.batch.IDFTransports.readers.ReaderStrategy;
import fr.natsystem.batch.IDFTransports.writers.WriterStrategy;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportStopJobConfig extends ImportJobConfig {

    public ImportStopJobConfig(
            @Qualifier("importStopReader") ReaderStrategy readerStrategy,
            @Qualifier("importStopWriter") WriterStrategy writerStrategy,
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        super(readerStrategy, writerStrategy, jobRepository, transactionManager);
    }

    @Bean
    public Job importStopJob(@Qualifier("importStopStep") Step importStep){
        return createImportJob("importStopJob",importStep);
    }

    @Bean
    public Step importStopStep(){
        return createImportStep("importStopStep");
    }
}
