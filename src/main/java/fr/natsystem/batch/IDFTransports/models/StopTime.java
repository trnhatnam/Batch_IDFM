package fr.natsystem.batch.IDFTransports.models;

import java.time.Duration;

public record StopTime(
    String tripId,
    Duration arrivalTime,
    Duration departureTime,
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
