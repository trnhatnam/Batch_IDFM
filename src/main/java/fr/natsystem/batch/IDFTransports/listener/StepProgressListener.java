package fr.natsystem.batch.IDFTransports.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;


@Component
public class StepProgressListener implements StepExecutionListener {
    private static final Logger log = LogManager.getLogger(StepProgressListener.class);

    @Override
    public ExitStatus afterStep(StepExecution stepExecution){
        log.info("Lectures : {}, Intégrés : {}, Filtrés : {}",
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getFilterCount());
        return stepExecution.getExitStatus();
    }
}
