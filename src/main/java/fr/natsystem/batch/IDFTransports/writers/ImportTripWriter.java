package fr.natsystem.batch.IDFTransports.writers;

import fr.natsystem.batch.IDFTransports.models.Stop;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("importTripWriter")
@RequiredArgsConstructor
public class ImportTripWriter implements WriterStrategy {

    private final DataSource ds;

    @Override
    public JdbcBatchItemWriter<Stop> getWriter(){
        return new JdbcBatchItemWriterBuilder<Stop>()
                .dataSource(ds)
                .sql("""
                    INSERT INTO trips (
                       route_id,
                       service_id,
                       trip_id,
                       trip_headsign,
                       trip_short_name,
                       direction_id,
                       block_id,
                       shape_id,
                       wheelchair_accessible,
                       bikes_allowed
                    ) VALUES (
                        :routeId,
                        :serviceId,
                        :tripId,
                        :tripHeadsign,
                        :tripShortName,
                        :directionId,
                        :blockId,
                        :shapeId,
                        :wheelchairAccessible,
                        :bikesAllowed
                    );
                """
                )
                .beanMapped()
                .build();
    }
}
