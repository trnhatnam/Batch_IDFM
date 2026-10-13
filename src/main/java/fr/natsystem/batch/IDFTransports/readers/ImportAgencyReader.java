package fr.natsystem.batch.IDFTransports.readers;

import fr.natsystem.batch.IDFTransports.models.Agency;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import static fr.natsystem.batch.IDFTransports.utils.ReaderUtils.getFieldValue;

@Configuration
public class ImportAgencyReader {

    private static final String AGENCY_FILE = "agency.txt";

    @Bean
    public FlatFileItemReader<Agency> agencyCSVReader() {
        return new FlatFileItemReaderBuilder<Agency>()
                .name("importStopReader")
                .resource(new ClassPathResource(AGENCY_FILE))
                .delimited()
                .delimiter(",")
                .names(
                        "agency_id",
                        "agency_name",
                        "agency_url",
                        "agency_timezone",
                        "agency_lang",
                        "agency_phone",
                        "agency_email",
                        "agency_fare_url",
                        "ticketing_deep_link_id"
                )
                .fieldSetMapper(fs -> new Agency(
                        getFieldValue(fs,"agency_id", String::valueOf),
                        getFieldValue(fs, "agency_name", String::valueOf),
                        getFieldValue(fs,"agency_url", String::valueOf),
                        getFieldValue(fs,"agency_timezone", String::valueOf),
                        getFieldValue(fs,"agency_lang", String::valueOf),
                        getFieldValue(fs,"agency_phone", String::valueOf),
                        getFieldValue(fs, "agency_email", String::valueOf),
                        getFieldValue(fs,"agency_fare_url", String::valueOf),
                        getFieldValue(fs, "ticketing_deep_link_id", String::valueOf)
                ))
                .linesToSkip(1)
                .build();
    }
}
