package fr.natsystem.batch.IDFTransports.readers;


import fr.natsystem.batch.IDFTransports.models.Trip;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import static fr.natsystem.batch.IDFTransports.utils.ReaderUtils.getFieldValue;

@Configuration
public class ImportTripReader {

    private static final String TRIPS_FILE = "trips.txt";

    @Bean
    public FlatFileItemReader<Trip> tripsCSVReader() {
        return new FlatFileItemReaderBuilder<Trip>()
                .name("importTripsReader")
                .resource(new ClassPathResource(TRIPS_FILE))
                .delimited()
                .delimiter(",")
                .names(
                        "route_id",
                        "service_id",
                        "trip_id",
                        "trip_headsign",
                        "trip_short_name",
                        "direction_id",
                        "block_id",
                        "shape_id",
                        "wheelchair_accessible",
                        "bikes_allowed"
                )
                .fieldSetMapper(fs -> new Trip(
                        getFieldValue(fs,"route_id", String::valueOf),
                        getFieldValue(fs,"service_id", String::valueOf),
                        getFieldValue(fs,"trip_id", String::valueOf),
                        getFieldValue(fs,"trip_headsign", String::valueOf),
                        getFieldValue(fs,"trip_short_name", String::valueOf),
                        getFieldValue(fs,"direction_id", Integer::valueOf),
                        getFieldValue(fs,"block_id", String::valueOf),
                        getFieldValue(fs,"shape_id", String::valueOf),
                        getFieldValue(fs,"wheelchair_accessible", Integer::valueOf),
                        getFieldValue(fs,"bikes_allowed", Integer::valueOf)
                ))
                .linesToSkip(1)
                .build();
    }
}
