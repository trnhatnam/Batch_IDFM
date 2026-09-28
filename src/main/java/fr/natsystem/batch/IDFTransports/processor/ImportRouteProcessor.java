package fr.natsystem.batch.IDFTransports.processor;

import fr.natsystem.batch.IDFTransports.models.Route;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;


@Component("importRouteProcessor")
public class ImportRouteProcessor implements ProcessorStrategy {

    @Override
    public ItemProcessor getProcessor() {
        return (ItemProcessor<Route, Route>) route -> route;
    }
}
