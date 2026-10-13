package fr.natsystem.batch.IDFTransports.readers;

import fr.natsystem.batch.IDFTransports.models.Route;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import static fr.natsystem.batch.IDFTransports.utils.ReaderUtils.getFieldValue;

@Configuration
public class ImportRouteReader  {

    private static final String ROUTES_FILE = "routes.txt";

    @Bean
    public FlatFileItemReader<Route> routesCSVReader() {
        return new FlatFileItemReaderBuilder<Route>()
                .name("importStopReader")
                .resource(new ClassPathResource(ROUTES_FILE))
                .delimited()
                .delimiter(",")
                .names(
                        "route_id",
                        "agency_id",
                        "route_short_name",
                        "route_long_name",
                        "route_desc",
                        "route_type",
                        "route_url",
                        "route_color",
                        "route_text_color",
                        "route_sort_order"
                )
                .fieldSetMapper(fs -> new Route(
                        getFieldValue(fs,"route_id", String::valueOf),
                        getFieldValue(fs,"agency_id", String::valueOf),
                        getFieldValue(fs,"route_short_name", String::valueOf),
                        getFieldValue(fs,"route_long_name", String::valueOf),
                        getFieldValue(fs,"route_desc", String::valueOf),
                        getFieldValue(fs,"route_type", Integer::valueOf),
                        getFieldValue(fs,"route_url", String::valueOf),
                        getFieldValue(fs,"route_color", String::valueOf),
                        getFieldValue(fs, "route_text_color", String::valueOf),
                        getFieldValue(fs, "route_sort_order", String::valueOf)
                ))
                .linesToSkip(1)
                .build();
    }



}
