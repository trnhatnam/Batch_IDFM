DROP TABLE IF EXISTS agency;
DROP TABLE IF EXISTS stop_times;
DROP TABLE IF EXISTS stops;
DROP TABLE IF EXISTS trips;
DROP TABLE IF EXISTS routes;




CREATE TABLE stops (
    stop_id              VARCHAR(255) PRIMARY KEY,
    stop_code            VARCHAR(255),
    stop_name            VARCHAR(255),
    stop_desc            TEXT,
    stop_lon             DOUBLE PRECISION,
    stop_lat             DOUBLE PRECISION,
    zone_id              VARCHAR(255),
    stop_url             TEXT,
    location_type        SMALLINT,
    parent_station       VARCHAR(255),
    stop_timezone        VARCHAR(64),
    level_id             VARCHAR(255),
    wheelchair_boarding  SMALLINT,
    platform_code        VARCHAR(255),
    stop_access          VARCHAR(255)
);

CREATE TABLE stop_times (
    trip_id VARCHAR(255),
    arrival_time BIGINT,
    departure_time BIGINT ,
    start_pickup_drop_off_window VARCHAR(255),
    end_pickup_drop_off_window VARCHAR(255),
    stop_id VARCHAR(255),
    stop_sequence SMALLINT,
    pickup_type SMALLINT,
    drop_off_type SMALLINT,
    local_zone_id VARCHAR(255),
    stop_headsign VARCHAR(255),
    timepoint SMALLINT,
    pickup_booking_rule_id VARCHAR(255),
    drop_off_booking_rule_id VARCHAR(255)
);

CREATE TABLE  trips (
    route_id VARCHAR(255),
    service_id VARCHAR(255),
    trip_id VARCHAR(255) PRIMARY KEY,
    trip_headsign VARCHAR(255),
    trip_short_name VARCHAR(255),
    direction_id SMALLINT,
    block_id VARCHAR(255),
    shape_id VARCHAR(255),
    wheelchair_accessible SMALLINT,
    bikes_allowed SMALLINT
);

CREATE TABLE routes (
   route_id VARCHAR(255) PRIMARY KEY,
   agency_id VARCHAR(255),
   route_short_name VARCHAR(255),
   route_long_name VARCHAR(255),
   route_desc TEXT,
   route_type SMALLINT,
   route_url TEXT,
   route_color VARCHAR(255),
   route_text_color VARCHAR(255),
   route_sort_order VARCHAR(255)
);

CREATE TABLE agency (
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