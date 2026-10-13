package fr.natsystem.batch.IDFTransports.processor;

import fr.natsystem.batch.IDFTransports.models.Route;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
public class ImportRouteProcessorConfig {

    private static final List<Integer> MODE_ROUTE = List.of(0,1,2,3,7);

    @Bean(name="routesProcessor")
    public ItemProcessor importProcessor() {
        return (ItemProcessor<Route, Route>) route -> isValidRoute(route) ?  route : null;
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
