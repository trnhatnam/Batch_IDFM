package fr.natsystem.batch.IDFTransports.mapper;

import fr.natsystem.batch.IDFTransports.dto.StopTimeDTO;
import fr.natsystem.batch.IDFTransports.models.StopTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface StopTimesMapper {
    @Mapping(target = "arrivalTimeSeconds", source="arrivalTimeSeconds")
    @Mapping(target = "departureTimeSeconds", source="departureTimeSeconds")
    StopTimeDTO toStopTimeDTO(StopTime stopTime, Long arrivalTimeSeconds, Long departureTimeSeconds);

}
