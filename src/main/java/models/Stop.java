package models;

import java.math.BigDecimal;

public record Stop(
    String stopId,
    String stopCode,
    String stopName,
    String stopDesc,
    BigDecimal stopLon,
    BigDecimal stopLat,
    String zoneId,
    String stopUrl,
    Integer locationType,
    String parentStation,
    String stopTimezone,
    String levelId,
    Integer wheelchairBoarding,
    String platformCode,
    String stopAccess
) {
}
