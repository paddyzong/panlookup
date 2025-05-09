# panlookup

## Overview

**panlookup** is a high-performance Spring Boot application for managing and querying large sets of card range (BIN range) data. It provides tools for generating test data, loading card ranges from files or a database, and performing fast lookups using either in-memory caching or direct database access.

## Features

- **Card Range JSON Generator** - Generate large datasets of card ranges in JSON format for testing and bulk processing.
- **Configurable Startup Loader** - Optionally load card range data from a JSON file at application startup.
- **Database Integration** - Persist card range data using PostgreSQL with Spring Data JPA.
- **In-Memory Caching** - Use a `TreeMap` for efficient range-based lookups. This can be enabled or disabled via configuration.
- **Automatic Schema Updates** - Hibernate is configured to automatically update the schema during development.

## Requirements

- Java 17 or higher
- Gradle
- PostgreSQL
- A `data/` directory in the project root to store generated JSON files

## Configuration

Application settings in `application.yml`:

```yaml
cardrange:
  load-on-startup: false  # Load card range data from file at startup
  cache-enabled: true     # Enable in-memory caching for lookups
```

## Performance

Performance metrics from `CardRangeLoaderPerformanceTest` on MacBook Air M1:

- 2.8 million card ranges
- ~4.04 ms average lookup time (direct database access)
- ~160 MB memory usage with TreeMap cache (700,000 ranges)
- Warning: Full dataset in-memory may cause OOM errors

### Alternative Approach

If you need better performance than direct database lookups:

- **Redis Sorted Set (ZSET)** (not yet implemented):  
  Suitable for larger datasets when in-memory Java caching is not feasible.  
  Store each card range with `startRange` as the Redis ZSET score. Perform a `ZREVRANGEBYSCORE` to efficiently find the matching floor range and check if the PAN falls within its end range.  
  This provides fast range queries with lower memory pressure on the JVM.


## Setup

1. Clone repository:
```bash
git clone https://github.com/your-org/panlookup.git
cd panlookup
```

2. Start PostgreSQL:
```bash
docker compose up -d
```

3. Create data directory:
```bash
mkdir -p data
```
4. Generate sample data:
```bash
./gradlew run -PmainClass=com.example.panlookup.data.CardRangeJsonGenerator
```
This will create a sample JSON file in the `data/` directory, or you can copy `data/pres.json.data` manually to `data/`.

## Build and Run

Build with Gradle:
```bash
./gradlew clean build
```

Run application:
```bash
./gradlew bootRun
```
