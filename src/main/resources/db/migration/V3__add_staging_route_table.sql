CREATE TABLE routes_staging (
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