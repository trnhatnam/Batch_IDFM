CREATE TABLE IF NOT EXISTS stops (
    stop_id              VARCHAR(255),
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

CREATE TABLE IF NOT EXISTS stop_times (
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
)