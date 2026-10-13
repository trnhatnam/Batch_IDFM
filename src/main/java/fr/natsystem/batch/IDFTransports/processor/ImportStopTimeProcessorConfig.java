package fr.natsystem.batch.IDFTransports.processor;

import fr.natsystem.batch.IDFTransports.dto.StopTimeDTO;
import fr.natsystem.batch.IDFTransports.mapper.StopTimesMapper;
import fr.natsystem.batch.IDFTransports.models.StopTime;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Optional;


@Configuration
public class ImportStopTimeProcessorConfig {

    private final StopTimesMapper stopTimesMapper;

    public ImportStopTimeProcessorConfig(StopTimesMapper stopTimesMapper) {
        this.stopTimesMapper = stopTimesMapper;
    }

    @Bean
    public ItemProcessor stopTimesProcessor() {
        return (ItemProcessor<StopTime, StopTimeDTO>) stopTime -> stopTimesMapper.toStopTimeDTO(
                stopTime,
                Optional.ofNullable(stopTime.arrivalTime())
                        .map(Duration::getSeconds)
                        .orElse(null),
                Optional.ofNullable(stopTime.departureTime())
                        .map(Duration::getSeconds)
                        .orElse(null)
                );
    }
}
