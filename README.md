# Network Management System

A backend application simulating a network topology monitoring system. The server calculates available (reachable) devices in a graph in real-time and streams updates to clients using **Server-Sent Events (SSE)** with a delta-based approach.

## Technologies
* Java
* Spring Boot
* REST API
* Server-Sent Events (SSE)

## Features
* Loading the initial network topology (devices and connections) from the `topology.json` file.
* Calculating reachable devices while ignoring inactive nodes (BFS algorithm).
* `PATCH /devices/{id}` - Updating device status (turning on/off).
* `GET /devices/{id}/reachable-devices` - SSE subscription streaming live updates (`INITIAL_STATE`, `ADDED`, `REMOVED`).

## How to run the application?

1. Ensure you have Java (JDK) and Maven installed.
2. Clone the repository.
3. Run the following command in the main project directory:
   ```bash
   mvn spring-boot:run

## Demonstration
* Results for given scenarios are located in the `postman_images` folder and saved as screenshots from Postman.
