package fr.natsystem.batch.IDFTransports.processor;

import fr.natsystem.batch.IDFTransports.models.Route;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Configuration
public class ImportRouteProcessorConfig {

    private static final List<Integer> MODE_ROUTE = IntStream.rangeClosed(0, 7).boxed().toList();
    private static final Logger log = LogManager.getLogger(ImportRouteProcessorConfig.class);

    @Bean(name="routesProcessor")
    public ItemProcessor importProcessor() {
        return (ItemProcessor<Route, Route>) route -> {
            if (isValidRoute(route)) {
                return route;
            }
            log.info("Filtré : {} ", route);
            return null;
        };
    }

    public boolean isValidRoute(Route route){
        return isNotBlank(route.routeId()) &&
               isNotBlank(route.agencyId()) &&
                isNotBlank(route.routeShortName()) &&
                isNotBlank(route.routeLongName()) &&
                MODE_ROUTE.contains(route.routeType());
    }

    public boolean isNotBlank(String field){
        return Optional.ofNullable(field)
                .filter(s -> !s.isBlank())
                .isPresent();
    }
}
