package fr.natsystem.batch.IDFTransports.writers;

import fr.natsystem.batch.IDFTransports.models.Stop;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("importStopWriter")
@RequiredArgsConstructor
public class ImportStopWriter implements WriterStrategy {

    private final DataSource ds;

    @Override
    public JdbcBatchItemWriter<Stop> getWriter(){
        return new JdbcBatchItemWriterBuilder<Stop>()
                .dataSource(ds)
                .sql("""
                    INSERT INTO stops (
                        stop_id,
                        stop_code,
                        stop_name,
                        stop_desc,
                        stop_lon,
                        stop_lat,
                        zone_id,
                        stop_url,
                        location_type,
                        parent_station,
                        stop_timezone,
                        level_id,
                        wheelchair_boarding,
                        platform_code,
                        stop_access
                    ) VALUES (
                        :stopId,
                        :stopCode,
                        :stopName,
                        :stopDesc,
                        :stopLon,
                        :stopLat,
                        :zoneId,
                        :stopUrl,
                        :locationType,
                        :parentStation,
                        :stopTimezone,
                        :levelId,
                        :wheelchairBoarding,
                        :platformCode,
                        :stopAccess
                    );
                """
                )
                .beanMapped()
                .build();
    }
}
