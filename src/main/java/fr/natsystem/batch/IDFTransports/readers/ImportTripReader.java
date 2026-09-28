package fr.natsystem.batch.IDFTransports.readers;


import fr.natsystem.batch.IDFTransports.models.Trips;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ClassPathResource;

import static fr.natsystem.batch.IDFTransports.utils.ReaderUtils.getFieldValue;

public class ImportTripReader implements ReaderStrategy {

    private static final String TRIPS_FILE = "trips.txt";

    @Override
    public FlatFileItemReader<Trips> getCSVReader() {
        return new FlatFileItemReaderBuilder<Trips>()
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
                .fieldSetMapper(fs -> new Trips(
                        getFieldValue(fs,"route_id", s -> s),
                        getFieldValue(fs,"service_id", s -> s),
                        getFieldValue(fs,"trip_id", s -> s),
                        getFieldValue(fs,"trip_headsign", s -> s),
                        getFieldValue(fs,"trip_short_name", s -> s),
                        getFieldValue(fs,"direction_id", Integer::valueOf),
                        getFieldValue(fs,"block_id", s -> s),
                        getFieldValue(fs,"shape_id", s -> s),
                        getFieldValue(fs,"wheelchair_accessible", Integer::valueOf),
                        getFieldValue(fs,"bikes_allowed", s -> s)
                ))
                .linesToSkip(1)
                .build();
    }
}
