# panlookup

## Overview
`panlookup` is a Spring Boot application designed to manage and process card range data. It includes functionality for generating, loading, and handling large datasets of card ranges.

## Features
- **Card Range JSON Generator**: Generates large JSON files with card range data.
- **Startup Loader**: Automatically loads card range data on application startup (configurable).
- **Database Integration**: Uses PostgreSQL for data persistence.
- **Hibernate Support**: Configured for automatic schema updates.

## Prerequisites
- Java 17 or higher
- Gradle
- PostgreSQL
- A `data` directory in the project root for storing generated JSON files.

## Configuration
The application is configured using `application.yml`. Key configurations include:
- Card range loader settings (`cardrange.load-on-startup`)

