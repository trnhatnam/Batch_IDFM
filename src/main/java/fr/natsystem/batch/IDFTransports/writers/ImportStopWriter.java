package fr.natsystem.batch.IDFTransports.writers;

import fr.natsystem.batch.IDFTransports.models.Stop;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ImportStopWriter {

    private final DataSource ds;

    public ImportStopWriter(DataSource ds) {
        this.ds = ds;
    }

    @Bean
    public JdbcBatchItemWriter<Stop> stopsDBWriter(){
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
