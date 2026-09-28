package fr.natsystem.batch.IDFTransports.readers;

import fr.natsystem.batch.IDFTransports.models.StopTime;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static fr.natsystem.batch.IDFTransports.utils.ReaderUtils.getFieldValue;

@Component("importStopTimeReader")
public class ImportStopTimeReader implements ReaderStrategy {

    private static final String STOPS_FILE = "stop_times.txt";
    private static final int THREE = 3;

    @Override
    public FlatFileItemReader<StopTime> getCSVReader() {
        return new FlatFileItemReaderBuilder<StopTime>()
                .name("importStopTimeReader")
                .resource(new ClassPathResource(STOPS_FILE))
                .delimited()
                .delimiter(",")
                .names(
                        "trip_id",
                        "arrival_time",
                        "departure_time",
                        "start_pickup_drop_off_window",
                        "end_pickup_drop_off_window",
                        "stop_id",
                        "stop_sequence",
                        "pickup_type",
                        "drop_off_type",
                        "local_zone_id",
                        "stop_headsign",
                        "timepoint",
                        "pickup_booking_rule_id",
                        "drop_off_booking_rule_id"
                )
                .fieldSetMapper(fs -> new StopTime(
                        getFieldValue(fs,"trip_id", s -> s),
                        getFieldValue(fs,"arrival_time", this::getArrivalOrDepartureTime),
                        getFieldValue(fs,"departure_time", this::getArrivalOrDepartureTime),
                        getFieldValue(fs,"start_pickup_drop_off_window", s -> s),
                        getFieldValue(fs,"end_pickup_drop_off_window", s -> s),
                        getFieldValue(fs,"stop_id", s -> s),
                        getFieldValue(fs,"stop_sequence", Integer::valueOf),
                        getFieldValue(fs,"pickup_type", Integer::valueOf),
                        getFieldValue(fs,"drop_off_type", Integer::valueOf),
                        getFieldValue(fs,"local_zone_id", s -> s),
                        getFieldValue(fs,"stop_headsign", s -> s),
                        getFieldValue(fs,"timepoint", Integer::valueOf),
                        getFieldValue(fs,"pickup_booking_rule_id", s -> s),
                        getFieldValue(fs,"drop_off_booking_rule_id", s -> s)
                        ))
                .linesToSkip(1)
                .build();
    }
    
    public Duration getArrivalOrDepartureTime(String time){
        String[] p = time.split(":");
        Duration d = null;
        if (p.length == THREE) {
            d = Duration.ofHours(Long.parseLong(p[0]))
                    .plusMinutes(Long.parseLong(p[1]))
                    .plusSeconds(Long.parseLong(p[2]));
        }
        return d;
    }
}
