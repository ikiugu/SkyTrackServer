CREATE TABLE flights (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flight_number VARCHAR(20) NOT NULL,
    dep_iata VARCHAR(3) NOT NULL,
    arr_iata VARCHAR(3) NOT NULL,
    dep_time TIMESTAMP NOT NULL,
    arr_time TIMESTAMP NOT NULL,
    gate TEXT,
    status VARCHAR(50) NOT NULL,
    flight_date DATE NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE(flight_number, flight_date),
    CONSTRAINT check_dep_before_arr CHECK (dep_time < arr_time)
);

CREATE INDEX idx_flights_route ON flights(dep_iata, arr_iata, flight_date);
CREATE INDEX idx_flights_number_date ON flights(flight_number, flight_date);
CREATE INDEX idx_flights_status ON flights(status);

