package fr.natsystem.batch.IDFTransports.processor;

import fr.natsystem.batch.IDFTransports.models.Trip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;


@Configuration
public class ImportTripProcessorConfig {

    private static final Logger log = LogManager.getLogger(ImportTripProcessorConfig.class);
    private static final List<Integer> DIRECTIONS = List.of(0,1);
    private static final List<Integer> WHEELCHAR_ACCESSIBLE = List.of(0,1,2);
    private static final List<Integer> BIKES_ALLOWED = List.of(0,1,2);

    @Bean(name="tripProcessor")
    public ItemProcessor importProcessor() {
        return (ItemProcessor<Trip, Trip>) trip -> {
            if (isValidTrip(trip)) {
                return trip;
            }
            log.info("Filtré : {} ", trip);
            return null;
        };
    }

    public boolean isValidTrip(Trip trip){
        return isNotBlank(trip.routeId()) &&
                isNotBlank(trip.serviceId()) &&
                isNotBlank(trip.tripId()) &&
                isNotBlank(trip.shapeId()) &&
                DIRECTIONS.contains(trip.directionId()) &&
                WHEELCHAR_ACCESSIBLE.contains(trip.wheelchairAccessible()) &&
                BIKES_ALLOWED.contains(trip.bikesAllowed());
    }

    public boolean isNotBlank(String field){
        return Optional.ofNullable(field)
                .filter(s -> !s.isBlank())
                .isPresent();
    }
}
