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
public class ImportStopTimesJobConfig extends ImportJobConfig {

    public ImportStopTimesJobConfig(
            @Qualifier("importStopTimesReader") ReaderStrategy readerStrategy,
            @Qualifier("importStopTimesWriter") WriterStrategy writerStrategy,
            @Qualifier("importStopTimesProcessor") ProcessorStrategy processorStrategy,
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        super(readerStrategy, writerStrategy, processorStrategy, jobRepository, transactionManager);
    }

    @Bean
    public Job importStopTimesJob(@Qualifier("importStopTimesStep") Step importStep){
        return createImportJob("importStopTimesJob", importStep);
    }

    @Bean
    public Step importStopTimesStep(){
        return createImportStep("importStopTimesStep");
    }
}
