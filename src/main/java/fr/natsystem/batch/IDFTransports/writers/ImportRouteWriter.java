package fr.natsystem.batch.IDFTransports.writers;

import fr.natsystem.batch.IDFTransports.models.Stop;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ImportRouteWriter {

    private final DataSource ds;

    public ImportRouteWriter(DataSource ds) {
        this.ds = ds;
    }

    @Bean
    public JdbcBatchItemWriter<Stop> routesDBWriter(){
        return new JdbcBatchItemWriterBuilder<Stop>()
                .dataSource(ds)
                .sql("""
                    INSERT INTO routes (
                       route_id,
                       agency_id,
                       route_short_name,
                       route_long_name,
                       route_desc,
                       route_type,
                       route_url,
                       route_color,
                       route_text_color,
                       route_sort_order
                    ) VALUES (
                        :routeId,
                        :agencyId,
                        :routeShortName,
                        :routeLongName,
                        :routeDesc,
                        :routeType,
                        :routeUrl,
                        :routeColor,
                        :routeTextColor,
                        :routeSortOrder
                    );
                """
                )
                .beanMapped()
                .build();
    }
}
