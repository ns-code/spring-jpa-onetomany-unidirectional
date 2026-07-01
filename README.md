# spring-jpa-onetomany-unidirectional
This project is a Spring Data JPA one to many relationship example. The one-to-many relationship between Customer and Contact entities is implemented using @ManyToOne in the child entity - Contact. Uses an embedded H2 database.

## Build and Test
```
.\gradlew clean build

.\gradlew test
```

## Build and Run Swagger UI
All CRUD APIs for the two entities can be tested using the Swagger UI.
```
.\gradlew bootRun

http://localhost:8080/swagger-ui/index.html
```

## Browse DB Tables
```
.\gradlew bootRun

http://localhost:8080/h2-console
```

## Related Projects

OneToMany uni-directional relationship example:<br>
https://github.com/ns-code/spring-jpa-onetomany-bidirectional

ManyToMany relationships modeled using Bi-Directional OneToMany relationships:<br>
https://github.com/ns-code/spring-jpa-courseenrollments

Bi-Directional ManyToMany relationships modeled using composite keys:<br>
https://github.com/ns-code/spring-jpa-courseenrollments-2

