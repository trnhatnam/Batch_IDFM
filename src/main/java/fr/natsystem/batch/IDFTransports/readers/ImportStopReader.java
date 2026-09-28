package fr.natsystem.batch.IDFTransports.readers;

import fr.natsystem.batch.IDFTransports.models.Stop;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static fr.natsystem.batch.IDFTransports.utils.ReaderUtils.getFieldValue;

@Component("importStopReader")
public class ImportStopReader implements ReaderStrategy {

    private static final String STOPS_FILE = "stops.txt";

    @Override
    public FlatFileItemReader<Stop> getCSVReader() {
        return new FlatFileItemReaderBuilder<Stop>()
                .name("importStopReader")
                .resource(new ClassPathResource(STOPS_FILE))
                .delimited()
                .delimiter(",")
                .names(
                        "stop_id",
                        "stop_code",
                        "stop_name",
                        "stop_desc",
                        "stop_lon",
                        "stop_lat",
                        "zone_id",
                        "stop_url",
                        "location_type",
                        "parent_station",
                        "stop_timezone",
                        "level_id",
                        "wheelchair_boarding",
                        "platform_code",
                        "stop_access"
                )
                .fieldSetMapper(fs -> new Stop(
                        getFieldValue(fs,"stop_id", s -> s),
                        getFieldValue(fs,"stop_code", s -> s),
                        getFieldValue(fs,"stop_name", s -> s),
                        getFieldValue(fs,"stop_desc", s -> s),
                        getFieldValue(fs,"stop_lon", BigDecimal::new),
                        getFieldValue(fs,"stop_lat", BigDecimal::new),
                        getFieldValue(fs,"zone_id", s -> s),
                        getFieldValue(fs,"stop_url", s -> s),
                        getFieldValue(fs,"location_type", Integer::valueOf),
                        getFieldValue(fs, "parent_station", s -> s),
                        getFieldValue(fs, "stop_timezone", s -> s),
                        getFieldValue(fs, "level_id", s -> s),
                        getFieldValue(fs,"wheelchair_boarding", Integer::valueOf),
                        getFieldValue(fs, "platform_code", s -> s),
                        getFieldValue(fs, "stop_access", s -> s)
                ))
                .linesToSkip(1)
                .build();
    }



}
