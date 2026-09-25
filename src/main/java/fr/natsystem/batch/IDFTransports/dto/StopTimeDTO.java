package fr.natsystem.batch.IDFTransports.dto;


public record StopTimeDTO(
    String tripId,
    Long arrivalTimeSeconds,
    Long departureTimeSeconds,
    String startPickupDropOffWindow,
    String endPickupDropOffWindow,
    String stopId,
    Integer stopSequence,
    Integer pickupType,
    Integer dropOffType,
    String localZoneId,
    String stopHeadsign,
    Integer timepoint,
    String pickupBookingRuleId,
    String dropOffBookingRuleId
) {
}

