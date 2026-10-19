package fr.natsystem.batch.IDFTransports.writers;

import fr.natsystem.batch.IDFTransports.dto.StopTimeDTO;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ImportStopTimeWriter {

    private final DataSource ds;

    public ImportStopTimeWriter(DataSource ds) {
        this.ds = ds;
    }

    @Bean(name="stopTimesDBWriter")
    public JdbcBatchItemWriter<StopTimeDTO> importWriter(){
        return new JdbcBatchItemWriterBuilder<StopTimeDTO>()
                .dataSource(ds)
                .sql("""
                    INSERT INTO stop_times (
                        trip_id,
                        arrival_time,
                        departure_time,
                        start_pickup_drop_off_window,
                        end_pickup_drop_off_window,
                        stop_id,
                        stop_sequence,
                        pickup_type,
                        drop_off_type,
                        local_zone_id,
                        stop_headsign,
                        timepoint,
                        pickup_booking_rule_id,
                        drop_off_booking_rule_id
                    ) VALUES (
                        :tripId,
                        :arrivalTimeSeconds,
                        :departureTimeSeconds,
                        :startPickupDropOffWindow,
                        :endPickupDropOffWindow,
                        :stopId,
                        :stopSequence,
                        :pickupType,
                        :dropOffType,
                        :localZoneId,
                        :stopHeadsign,
                        :timepoint,
                        :pickupBookingRuleId,
                        :dropOffBookingRuleId
                    );
                """
                )
                .beanMapped()
                .build();
    }

    @Bean(name="stopTimesStagingWriter")
    public JdbcBatchItemWriter<StopTimeDTO> stagingWriter(){
        return new JdbcBatchItemWriterBuilder<StopTimeDTO>()
                .dataSource(ds)
                .sql("""
                    INSERT INTO stop_times_staging (
                        trip_id,
                        arrival_time,
                        departure_time,
                        start_pickup_drop_off_window,
                        end_pickup_drop_off_window,
                        stop_id,
                        stop_sequence,
                        pickup_type,
                        drop_off_type,
                        local_zone_id,
                        stop_headsign,
                        timepoint,
                        pickup_booking_rule_id,
                        drop_off_booking_rule_id
                    ) VALUES (
                        :tripId,
                        :arrivalTimeSeconds,
                        :departureTimeSeconds,
                        :startPickupDropOffWindow,
                        :endPickupDropOffWindow,
                        :stopId,
                        :stopSequence,
                        :pickupType,
                        :dropOffType,
                        :localZoneId,
                        :stopHeadsign,
                        :timepoint,
                        :pickupBookingRuleId,
                        :dropOffBookingRuleId
                    );
                """
                )
                .beanMapped()
                .build();
    }
}
