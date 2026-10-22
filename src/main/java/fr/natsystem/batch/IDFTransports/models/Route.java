package fr.natsystem.batch.IDFTransports.models;

public record Route(
    String routeId,
    String agencyId,
    String routeShortName,
    String routeLongName,
    String routeDesc,
    Integer routeType,
    String routeUrl,
    String routeColor,
    String routeTextColor,
    String routeSortOrder
) {
}
