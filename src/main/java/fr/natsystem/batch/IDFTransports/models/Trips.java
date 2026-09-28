package fr.natsystem.batch.IDFTransports.models;

public record Trips(
    String route_id,
    String service_id,
    String trip_id,
    String trip_headsign,
    String trip_short_name,
    Integer direction_id,
    String block_id,
    String shape_id,
    Integer wheelchair_accessible,
    String bikes_allowed
) {
}
