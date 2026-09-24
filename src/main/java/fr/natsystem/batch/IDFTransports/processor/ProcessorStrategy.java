package fr.natsystem.batch.IDFTransports.processor;

import org.springframework.batch.infrastructure.item.ItemProcessor;

public interface ProcessorStrategy {
    ItemProcessor getProcessor();
}
