package fr.natsystem.batch.IDFTransports.writers;

import fr.natsystem.batch.IDFTransports.models.Agency;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ImportAgencyWriter {

    private final DataSource ds;

    public ImportAgencyWriter(DataSource ds) {
        this.ds = ds;
    }

    @Bean
    public JdbcBatchItemWriter<Agency> agencyDBWriter(){
        return new JdbcBatchItemWriterBuilder<Agency>()
                .dataSource(ds)
                .sql("""
                    INSERT INTO agency (
                        agency_id,
                        agency_name,
                        agency_url,
                        agency_timezone,
                        agency_lang,
                        agency_phone,
                        agency_email,
                        agency_fare_url,
                        ticketing_deep_link_id
                    ) VALUES (
                        :agencyId,
                        :agencyName,
                        :agencyUrl,
                        :agencyTimezone,
                        :agencyLang,
                        :agencyPhone,
                        :agencyEmail,
                        :agencyFareUrl,
                        :ticketingDeepLinkId
                    );
                """
                )
                .beanMapped()
                .build();
    }
}
