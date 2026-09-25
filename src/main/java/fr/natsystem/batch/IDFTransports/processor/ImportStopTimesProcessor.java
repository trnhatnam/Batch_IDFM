package fr.natsystem.batch.IDFTransports.processor;

import fr.natsystem.batch.IDFTransports.dto.StopTimeDTO;
import fr.natsystem.batch.IDFTransports.mapper.StopTimesMapper;
import fr.natsystem.batch.IDFTransports.models.StopTime;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;


@Component("importStopTimesProcessor")
public class ImportStopTimesProcessor implements ProcessorStrategy {

    private final StopTimesMapper stopTimesMapper;

    public ImportStopTimesProcessor(StopTimesMapper stopTimesMapper) {
        this.stopTimesMapper = stopTimesMapper;
    }

    @Override
    public ItemProcessor getProcessor() {
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
