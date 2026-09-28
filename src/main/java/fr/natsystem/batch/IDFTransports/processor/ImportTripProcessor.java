package fr.natsystem.batch.IDFTransports.processor;

import fr.natsystem.batch.IDFTransports.models.Trip;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;


@Component("importStopTimesProcessor")
public class ImportTripProcessor implements ProcessorStrategy {

    @Override
    public ItemProcessor getProcessor() {
        return (ItemProcessor<Trip, Trip>) trip -> trip;
    }
}
