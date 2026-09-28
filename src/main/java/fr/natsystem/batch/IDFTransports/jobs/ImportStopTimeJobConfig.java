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
public class ImportStopTimeJobConfig extends ImportJobConfig {

    public ImportStopTimeJobConfig(
            @Qualifier("importStopTimeReader") ReaderStrategy readerStrategy,
            @Qualifier("importStopTimeWriter") WriterStrategy writerStrategy,
            @Qualifier("importStopTimeProcessor") ProcessorStrategy processorStrategy,
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        super(readerStrategy, writerStrategy, processorStrategy, jobRepository, transactionManager);
    }

    @Bean
    public Job importStopTimeJob(@Qualifier("importStopTimeStep") Step importStep){
        return createImportJob("importStopTimeJob", importStep);
    }

    @Bean
    public Step importStopTimeStep(){
        return createImportStep("importStopTimeStep");
    }
}
