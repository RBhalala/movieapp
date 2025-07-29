## 🏃‍♂️ How to Run the Movie App
This application allows users to check if a movie won a "Best Picture" Oscar, fetch top-rated movies based on box office amount from the OMDb API, and store/view user ratings.

###  ✅ Prerequisites
```
Java 17
Maven 3.6+
PostgreSQL
OMDb API Key (free from http://www.omdbapi.com/apikey.aspx)
(They have free setup which provides 1000 request a day)
```
### 🔧 Configuration
src/main/resources/application.properties:
```code
spring.application.name=movieapp

# PostgreSQL DB connection
spring.datasource.url=jdbc:postgresql://localhost:5432/movieapp
spring.datasource.username=root
spring.datasource.password=root

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Use PostgreSQL dialect
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Server port
server.port=8080

api.token=supersecrettoken123

omdb.api.token=139c6896
```

### ▶️ Run the App
Load Oscar winners CSV file (academy_awards.csv) - automatically runs at startup

### Run the app:
```code
mvn spring-boot:run
```
> Note: If your local repositories are already configured and you don't intend to modify them, you can use the following command.
```code
mvn spring-boot:run --settings ./settings-central-only.xml
```
#### The application will fetch and store metadata in real time from OMDb API when a movie is requested.

### API Access
All endpoints require an API token. Pass it via HTTP header:
```code
X-TOKEN-API: <api.token>
```

###  Endpoints

GET `/api/movies/{title}` – Get movie details with ratings and Oscar win status
GET `/api/movies/{title}/best-picture` – Get message string with response whether movie won Oscar or not

POST `/api/movies/rate?title=${title}` – Add or update user rating

GET `/api/movies/top-rated` – View top 10 movies by user rating and box office