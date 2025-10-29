# Aviationstack API Documentation

## Overview
This document describes the Aviationstack API specifications used for fetching flight data.

## Base URL
```
https://api.aviationstack.com/v1
```

## Endpoint
```
GET /flights
```

## Authentication
All requests require an `access_key` query parameter with your API key.

## Query Parameters

### Required Parameters
- `access_key` (string) - Your Aviationstack API access key
- `dep_iata` (string) - 3-letter IATA code for departure airport (e.g., "JFK")
- `arr_iata` (string) - 3-letter IATA code for arrival airport (e.g., "LAX")
- `flight_date` (string) - Flight date in format `YYYY-MM-DD` (e.g., "2024-01-15")

### Optional Parameters
- `flight_status` (string) - Filter by flight status (e.g., "active", "landed", "scheduled", "cancelled", "delayed")
- `flight_number` (string) - Specific flight number (e.g., "AA100")
- `airline_iata` (string) - Airline IATA code
- `airline_icao` (string) - Airline ICAO code

## Response Structure

The API returns a JSON object with the following structure:

```json
{
  "pagination": {
    "limit": 100,
    "offset": 0,
    "count": 10,
    "total": 10
  },
  "data": [
    {
      "flight": {
        "number": "AA100",
        "iata": "AA100",
        "icao": "AAL100",
        "codeshared": null
      },
      "airline": {
        "name": "American Airlines",
        "iata": "AA",
        "icao": "AAL"
      },
      "departure": {
        "airport": "John F Kennedy International",
        "iata": "JFK",
        "icao": "KJFK",
        "terminal": "8",
        "gate": "A12",
        "delay": 15,
        "scheduled": "2024-01-15T08:00:00+00:00",
        "estimated": "2024-01-15T08:15:00+00:00",
        "actual": "2024-01-15T08:18:00+00:00",
        "timezone": "America/New_York"
      },
      "arrival": {
        "airport": "Los Angeles International",
        "iata": "LAX",
        "icao": "KLAX",
        "terminal": "4",
        "gate": "23",
        "baggage": "3",
        "delay": null,
        "scheduled": "2024-01-15T11:30:00+00:00",
        "estimated": "2024-01-15T11:45:00+00:00",
        "actual": "2024-01-15T11:50:00+00:00",
        "timezone": "America/Los_Angeles"
      },
      "flight_status": "active",
      "flight_date": "2024-01-15"
    }
  ],
  "error": null
}
```

## Field Descriptions

### Flight Object
- `flight.number` (string) - Flight number (e.g., "AA100")
- `flight.iata` (string) - IATA flight number
- `flight.icao` (string) - ICAO flight number
- `flight.codeshared` (object|null) - Codeshare information if applicable

### Airline Object
- `airline.name` (string) - Airline name
- `airline.iata` (string) - 2-letter IATA code
- `airline.icao` (string) - 3-letter ICAO code

### Departure Object
- `departure.airport` (string) - Airport name
- `departure.iata` (string, 3 characters) - IATA airport code
- `departure.icao` (string, 4 characters) - ICAO airport code
- `departure.terminal` (string|null) - Terminal number/identifier
- `departure.gate` (string|null) - Gate number/identifier (nullable)
- `departure.delay` (integer|null) - Delay in minutes (nullable)
- `departure.scheduled` (string) - Scheduled departure time (ISO 8601)
- `departure.estimated` (string|null) - Estimated departure time (ISO 8601, nullable)
- `departure.actual` (string|null) - Actual departure time (ISO 8601, nullable)
- `departure.timezone` (string) - Timezone identifier (e.g., "America/New_York")

### Arrival Object
- `arrival.airport` (string) - Airport name
- `arrival.iata` (string, 3 characters) - IATA airport code
- `arrival.icao` (string, 4 characters) - ICAO airport code
- `arrival.terminal` (string|null) - Terminal number/identifier
- `arrival.gate` (string|null) - Gate number/identifier (nullable)
- `arrival.baggage` (string|null) - Baggage claim identifier (nullable)
- `arrival.delay` (integer|null) - Delay in minutes (nullable)
- `arrival.scheduled` (string) - Scheduled arrival time (ISO 8601)
- `arrival.estimated` (string|null) - Estimated arrival time (ISO 8601, nullable)
- `arrival.actual` (string|null) - Actual arrival time (ISO 8601, nullable)
- `arrival.timezone` (string) - Timezone identifier

### Top-level Fields
- `flight_status` (string) - Current flight status (e.g., "scheduled", "active", "landed", "cancelled", "delayed")
- `flight_date` (string) - Flight date (YYYY-MM-DD format)

## Error Response

When an error occurs, the response contains an `error` object:

```json
{
  "error": {
    "code": "invalid_access_key",
    "message": "You have not supplied a valid API Access Key.",
    "details": {}
  }
}
```

### Error Codes
Common error codes:
- `invalid_access_key` - Invalid or missing API key
- `usage_limit_reached` - Monthly request limit exceeded
- `invalid_parameters` - Invalid query parameters
- `invalid_request` - Malformed request

## Rate Limits

- Free tier: 1,000 requests per month
- Basic tier: 10,000 requests per month
- Business tier: 100,000 requests per month
- Enterprise: Custom limits

When rate limit is exceeded, the API returns a 429 status code with a `Retry-After` header indicating when to retry.

## Notes

1. All timestamp fields are in ISO 8601 format with timezone information
2. Nullable fields may be `null` for future flights or when data is not available
3. Gate information (`departure.gate`, `arrival.gate`) may change and should be tracked for change detection
4. Delay values are in minutes relative to scheduled time
5. Flight status values: "scheduled", "active", "landed", "cancelled", "delayed", "incident", "diverted"

