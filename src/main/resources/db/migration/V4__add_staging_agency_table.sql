
CREATE TABLE agency_staging (
   agency_id VARCHAR(255) PRIMARY KEY,
   agency_name VARCHAR(255),
   agency_url TEXT,
   agency_timezone VARCHAR(255),
   agency_lang VARCHAR(255),
   agency_phone VARCHAR(255),
   agency_email VARCHAR(255),
   agency_fare_url TEXT,
   ticketing_deep_link_id VARCHAR(255)
);