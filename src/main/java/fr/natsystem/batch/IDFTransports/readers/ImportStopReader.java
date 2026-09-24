package fr.natsystem.batch.IDFTransports.readers;

import fr.natsystem.batch.IDFTransports.models.Stop;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@Qualifier("importStopReader")
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
                        fs.readString("stop_id"),
                        fs.readString("stop_code"),
                        fs.readString("stop_name"),
                        fs.readString("stop_desc"),
                        Optional.ofNullable(fs.readString("stop_lon")).filter(s -> !s.isBlank()).map(BigDecimal::new).orElse(null),
                        Optional.ofNullable(fs.readString("stop_lat")).filter(s -> !s.isBlank()).map(BigDecimal::new).orElse(null),
                        fs.readString("zone_id"),
                        fs.readString("stop_url"),
                        Optional.ofNullable(fs.readString("location_type")).filter(s -> !s.isBlank()).map(Integer::valueOf).orElse(null),
                        fs.readString("parent_station"),
                        fs.readString("stop_timezone"),
                        fs.readString("level_id"),
                        Optional.ofNullable(fs.readString("wheelchair_boarding")).filter(s -> !s.isBlank()).map(Integer::valueOf).orElse(null),
                        fs.readString("platform_code"),
                        fs.readString("stop_access")
                ))
                .linesToSkip(1)
                .build();
    }
}
