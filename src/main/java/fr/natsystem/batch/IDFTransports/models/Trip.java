package fr.natsystem.batch.IDFTransports.models;

public record Trip(
    String routeId,
    String serviceId,
    String tripId,
    String tripHeadsign,
    String tripShortName,
    Integer directionId,
    String blockId,
    String shapeId,
    Integer wheelchairAccessible,
    String bikesAllowed
) {
}
