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