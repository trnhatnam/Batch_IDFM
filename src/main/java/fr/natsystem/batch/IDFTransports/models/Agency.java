package fr.natsystem.batch.IDFTransports.models;

public record Agency (
   String agencyId,
   String agencyName,
   String agencyUrl,
   String agencyTimezone,
   String agencyLang,
   String agencyPhone,
   String agencyEmail,
   String agencyFareUrl,
   String ticketingDeepLinkId
) {
}
