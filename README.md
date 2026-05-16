# SafeRoute: Civic-Tech Spatial Platform Backend

SafeRoute is a powerful, GIS-enabled backend infrastructure engineered to monitor road quality, ingest geographical infrastructure data, and handle citizen hazard complaints (like potholes or waterlogging). It leverages advanced spatial databases and cloud-native storage patterns to provide a scalable foundation for civic transparency and infrastructure management.

## 🌟 Core Domains & Features

The application is split into two primary domains, deeply wired with geographical contexts:

### 1. Spatial Road Network
The road network acts as the foundational layer of the platform.
- **Geographic Representation**: Roads are ingested and stored as `LineString` entities. This represents a continuous path of geographical coordinates.
- **Data Attributes**: Each road segment tracks critical administrative data such as the `contractorName`, `sanctionedAmount`, `lastRelayedDate`, and the responsible `executiveEngineer`.
- **Bulk Ingestion Support**: The system is designed to consume cleaned ETL datasets from external Python pipelines via a batch upload endpoint.

### 2. Citizen Complaint Engine & State Machine
The complaint engine allows for the tracking and resolution of specific road hazards.
- **Geographic Representation**: Hazards are stored as precise geographic `Point` data.
- **Relational Mapping**: Each complaint maps to a specific `Road` via a Many-to-One relationship, allowing administrators to correlate hazards with contractors and recent relay dates.
- **State Machine Workflow**: Complaints follow a strict lifecycle tracked via the `ComplaintStatus` enum: `REPORTED` -> `ASSIGNED` -> `IN_PROGRESS` -> `RESOLVED`.
- **Security Check**: To prevent malicious manipulation, the service layer forcefully intercepts all incoming complaints and initializes their state to `REPORTED`, overriding any external inputs.

## 🛠️ Architecture & Tech Stack

The platform is built on a robust, modern Java ecosystem tailored for geographical workloads:
- **Core Framework**: Java 21 & Spring Boot 3.3
- **Database**: PostgreSQL 15 paired with the **PostGIS 3.3** extension
- **ORM & Spatial Mapping**: Hibernate Spatial & Java Topology Suite (JTS)
- **Media Storage**: Cloudinary HTTP44 SDK

## 🗺️ GIS Wiring & Spatial Queries In-Depth

One of the most complex aspects of SafeRoute is its handling of geographic data.

### Coordinate Transformation via JTS
Frontend applications (like Leaflet.js) typically transmit and consume geographic data as simple JSON arrays: `[longitude, latitude]`. 
The backend utilizes the **Java Topology Suite (JTS)** `GeometryFactory` equipped with a `PrecisionModel` to programmatically parse these raw arrays into strict `Point` and `LineString` objects before they are committed to the database.

### The EPSG:4326 Spatial Reference System
All spatial data within SafeRoute is explicitly tagged with **SRID 4326** (WGS 84). This is the standard coordinate frame for the Earth used by GPS, ensuring perfectly accurate alignments with consumer maps like Google Maps or OpenStreetMap.

### Native PostGIS Spatial Queries
Instead of relying on standard, limited JPA repository methods, the backend leverages raw, native PostGIS queries for spatial lookups.
When querying for roads or complaints "nearby" a user's location, the backend uses `ST_DWithin`. 

**The Metric Calculation Paradigm:**
By default, `ST_DWithin` on generic geometries calculates distances using abstract map degrees, which is completely inaccurate for real-world distances. SafeRoute explicitly casts geometries to `geography` types within the SQL string:
`ST_DWithin(geom::geography, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, :radiusMeters)`
This forces PostGIS to calculate the distance over the curvature of the Earth, allowing frontend clients to request precise metric radii (e.g., "Find hazards within exactly 2000 meters").

## ☁️ Cloud-Native Storage & Ephemeral Integrity

SafeRoute is designed to be deployed to containerized or cloud-native environments (like Supabase, Heroku, or AWS ECS) where local disk storage is ephemeral (it resets when the container restarts).

### Cloudinary Byte-Stream Integration
When a citizen uploads an image with their complaint, the `MultipartFile` payload is intercepted by the `CloudinaryService`. 
Rather than calling standard file-writing methods to save the image temporarily to the server disk before uploading (which is a common anti-pattern that breaks in ephemeral clouds), the backend converts the file into a pure memory byte-stream. It then pushes the bytes directly to Cloudinary using `ObjectUtils.emptyMap()`, retrieving the persistent `secure_url` to store in the database.

## 🛡️ API Security & Resilience

- **Global Exception Handling**: The application employs a `@ControllerAdvice` global exception handler. Missing resources (`ResourceNotFoundException`) throw clean 404 JSONs, while all generic exceptions are caught and transformed into a sanitized 500 JSON ("Internal Server Error"). This guarantees that underlying Java stack traces and database exceptions never leak to the public frontend.
- **Hackathon CORS Hack**: To completely unblock cross-functional collaboration, a `WebMvcConfigurer` is globally implemented to permit all origins (`*`), HTTP methods, and headers.

---

> **Note to Developers:** Testing instructions and local mock data guides are documented in a separate `testing_guide.md` file.
