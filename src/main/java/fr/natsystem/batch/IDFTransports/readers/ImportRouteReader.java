package fr.natsystem.batch.IDFTransports.readers;

import fr.natsystem.batch.IDFTransports.models.Route;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import static fr.natsystem.batch.IDFTransports.utils.ReaderUtils.getFieldValue;

@Component("importRouteReader")
public class ImportRouteReader implements ReaderStrategy {

    private static final String ROUTES_FILE = "routes.txt";

    @Override
    public FlatFileItemReader<Route> getCSVReader() {
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
                        getFieldValue(fs,"route_id", s -> s),
                        getFieldValue(fs,"agency_id", s -> s),
                        getFieldValue(fs,"route_short_name", s -> s),
                        getFieldValue(fs,"route_long_name", s -> s),
                        getFieldValue(fs,"route_desc", s -> s),
                        getFieldValue(fs,"route_type", Integer::valueOf),
                        getFieldValue(fs,"route_url", s -> s),
                        getFieldValue(fs,"route_color", s -> s),
                        getFieldValue(fs, "route_text_color", s -> s),
                        getFieldValue(fs, "route_sort_order", s -> s)
                ))
                .linesToSkip(1)
                .build();
    }



}
