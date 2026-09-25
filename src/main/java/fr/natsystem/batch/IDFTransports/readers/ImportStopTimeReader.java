package fr.natsystem.batch.IDFTransports.readers;

import fr.natsystem.batch.IDFTransports.models.StopTime;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component("importStopTimesReader")
public class ImportStopTimeReader implements ReaderStrategy {

    private static final String STOPS_FILE = "stop_times.txt";
    private static final int THREE = 3;

    @Override
    public FlatFileItemReader<StopTime> getCSVReader() {
        return new FlatFileItemReaderBuilder<StopTime>()
                .name("importStopTimesReader")
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
                        fs.readString("trip_id"),
                        Optional.ofNullable(fs.readString("arrival_time")).filter(s -> !s.isBlank()).map(this::getArrivalOrDepartureTime).orElse(null),
                        Optional.ofNullable(fs.readString("departure_time")).filter(s -> !s.isBlank()).map(this::getArrivalOrDepartureTime).orElse(null),
                        fs.readString("start_pickup_drop_off_window"),
                        fs.readString("end_pickup_drop_off_window"),
                        fs.readString("stop_id"),
                        Optional.ofNullable(fs.readString("stop_sequence")).filter(s -> !s.isBlank()).map(Integer::valueOf).orElse(null),
                        Optional.ofNullable(fs.readString("pickup_type")).filter(s -> !s.isBlank()).map(Integer::valueOf).orElse(null),
                        Optional.ofNullable(fs.readString("drop_off_type")).filter(s -> !s.isBlank()).map(Integer::valueOf).orElse(null),
                        fs.readString("local_zone_id"),
                        fs.readString("stop_headsign"),
                        Optional.ofNullable(fs.readString("timepoint")).filter(s -> !s.isBlank()).map(Integer::valueOf).orElse(null),
                        fs.readString("pickup_booking_rule_id"),
                        fs.readString("drop_off_booking_rule_id")
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
