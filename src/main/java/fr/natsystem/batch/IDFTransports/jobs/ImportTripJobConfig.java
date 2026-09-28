package fr.natsystem.batch.IDFTransports.jobs;

import fr.natsystem.batch.IDFTransports.processor.ProcessorStrategy;
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
public class ImportTripJobConfig extends ImportJobConfig {

    public ImportTripJobConfig(
            @Qualifier("importTripReader") ReaderStrategy readerStrategy,
            @Qualifier("importTripWriter") WriterStrategy writerStrategy,
            @Qualifier("importTripProcessor") ProcessorStrategy processorStrategy,
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        super(readerStrategy, writerStrategy, processorStrategy, jobRepository, transactionManager);
    }

    @Bean
    public Job importTripJob(@Qualifier("importTripStep") Step importStep){
        return createImportJob("importTripJob", importStep);
    }

    @Bean
    public Step importTripStep(){
        return createImportStep("importTripStep");
    }
}
