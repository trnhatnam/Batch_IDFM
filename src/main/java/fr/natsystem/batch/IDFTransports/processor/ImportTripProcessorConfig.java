package fr.natsystem.batch.IDFTransports.processor;

import fr.natsystem.batch.IDFTransports.models.Trip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Configuration
public class ImportTripProcessorConfig {

    private static final Logger log = LogManager.getLogger(ImportTripProcessorConfig.class);
    private static final Set<Integer> DIRECTIONS = new HashSet<>(Set.of(0,1));
    private static final Set<Integer> WHEELCHAR_ACCESSIBLE = new HashSet<>(Set.of(0,1,2));
    private static final Set<Integer> BIKES_ALLOWED = new HashSet<>(Set.of(0,1,2));

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
                hasValidValue(trip.directionId(),DIRECTIONS) &&
                hasValidValue(trip.wheelchairAccessible(),WHEELCHAR_ACCESSIBLE) &&
                hasValidValue(trip.bikesAllowed(),BIKES_ALLOWED);
    }

    public boolean isNotBlank(String field){
        return Optional.ofNullable(field)
                .filter(s -> !s.isBlank())
                .isPresent();
    }

    public boolean hasValidValue(Object field, Set<? extends Object> validValues){
        return Optional.ofNullable(field)
                .map(validValues::contains)
                .orElse(true);
    }
}
