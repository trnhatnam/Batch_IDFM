package fr.natsystem.batch.IDFTransports.processor;

import fr.natsystem.batch.IDFTransports.models.Agency;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class ImportAgencyProcessorConfig {

    @Bean(name="agencyProcessor")
    public ItemProcessor importProcessor() {
        return (ItemProcessor<Agency, Agency>) agency -> isValidAgency(agency) ?  agency : null;
    }

    public boolean isValidAgency(Agency agency){
        return isNotBlank(agency.agencyId()) &&
                isNotBlank(agency.agencyName()) &&
                isNotBlank(agency.agencyUrl()) &&
                isNotBlank(agency.agencyTimezone());
    }

    public boolean isNotBlank(String field){
          return Optional.ofNullable(field)
                  .filter(s -> !s.isBlank())
                  .isPresent();
    }
}
