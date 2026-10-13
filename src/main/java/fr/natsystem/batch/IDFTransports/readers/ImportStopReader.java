package fr.natsystem.batch.IDFTransports.readers;

import fr.natsystem.batch.IDFTransports.models.Stop;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;

import static fr.natsystem.batch.IDFTransports.utils.ReaderUtils.getFieldValue;

@Configuration
public class ImportStopReader {

    private static final String STOPS_FILE = "stops.txt";

    @Bean
    public FlatFileItemReader<Stop> stopsCSVReader() {
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
                        getFieldValue(fs,"stop_id", String::valueOf),
                        getFieldValue(fs,"stop_code", String::valueOf),
                        getFieldValue(fs,"stop_name", String::valueOf),
                        getFieldValue(fs,"stop_desc", String::valueOf),
                        getFieldValue(fs,"stop_lon", BigDecimal::new),
                        getFieldValue(fs,"stop_lat", BigDecimal::new),
                        getFieldValue(fs,"zone_id", String::valueOf),
                        getFieldValue(fs,"stop_url", String::valueOf),
                        getFieldValue(fs,"location_type", Integer::valueOf),
                        getFieldValue(fs, "parent_station", String::valueOf),
                        getFieldValue(fs, "stop_timezone", String::valueOf),
                        getFieldValue(fs, "level_id", String::valueOf),
                        getFieldValue(fs,"wheelchair_boarding", Integer::valueOf),
                        getFieldValue(fs, "platform_code", String::valueOf),
                        getFieldValue(fs, "stop_access", String::valueOf)
                ))
                .linesToSkip(1)
                .build();
    }



}
