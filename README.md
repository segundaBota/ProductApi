# Product Management API 🚀

A RESTful API for product management, built with **Java 21** and **Spring Boot 3**. This project follows **Jakarta EE** standards and adopts an "API-First" approach using OpenAPI specifications.

## 🛠️ Tech Stack & Tools

*   **Language:** Java 21 (LTS)
*   **Framework:** Spring Boot 3.x
*   **Persistence:** Spring Data JPA with Jakarta Persistence
*   **Security:** Spring Security (Basic Auth with USER/ADMIN roles)
*   **Mapping:** MapStruct for Entity-to-DTO conversion
*   **Documentation:** OpenAPI 3.0 (Swagger)
*   **Quality:** JaCoCo for test coverage reporting
*   **Utilities:** Lombok

## 🛠️ Project Structure

The project follows a clean, loosely coupled architecture. This is achieved by preventing the business logic from importing infrastructure or UI packages and by using interfaces. The main components are:
*   `controller`: Application entry ports via REST APIs and API converters/adapters.
*   `service`: Business logic.
*   `repository/entity`: Application entry ports via REST APIs and API converters/adapters.

## 📖 API Reference

All endpoints (except `GET`) require **ADMIN** privileges.

| Method | Endpoint | Description | Role |
| :--- | :--- | :--- | :--- |
| `GET` | `/products` | List all available products | USER |
| `GET` | `/products/{id}` | Get detailed info of a single product | USER |
| `GET` | `/products/search` | Search products with dynamic filters | USER |
| `POST` | `/products` | Register a new product | ADMIN |
| `PUT` | `/products/{id}` | Update an existing product | ADMIN |
| `DELETE` | `/products/{id}` | Permanently delete a product | ADMIN |

**Note:** For `search`, you can use query parameters like `?name=...`, `?price=...`, or `?description=...`.

## 🏃 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/segundaBota/ProductApi.git
   ```
2. Build the project:
   ```bash
   ./mvnw clean install
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## 🚀 Future Improvements & Next Steps

To evolve this API into a production-ready microservice, the following enhancements should be planned:

-   🔐 **JWT Authentication:** Replace Basic Auth with Stateless JWT (JSON Web Tokens) for better scalability and security.
-   📡 **Traceability & Observability:** Add **Spring Boot Actuator** and Micrometer to monitor application health, performance metrics, and logging.
-   📄 **Pagination & Sorting:** Implement `Pageable` in `GET` endpoints to handle large datasets efficiently.
-   🗄️ **Database Migrations:** Integrate a robust database to persist data.
-   🐳 **Containerization:** Add a `Dockerfile` and `docker-compose.yml` for easy deployment and local development environments.
-   📊 **API Versioning:** Introduce `/v1/` prefixes to the URI to support backward compatibility in future changes.

## 🧪 Testing & Coverage

### Running Tests
```bash
mvn test
```