package fr.natsystem.batch.IDFTransports.writers;

import fr.natsystem.batch.IDFTransports.models.Stop;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("importRouteWriter")
@RequiredArgsConstructor
public class ImportRouteWriter implements WriterStrategy {

    private final DataSource ds;

    @Override
    public JdbcBatchItemWriter<Stop> getWriter(){
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
