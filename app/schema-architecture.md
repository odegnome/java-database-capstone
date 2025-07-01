## Architecture

This project uses Spring boot, Thymeleaf, MySQL and MongoDB for a
clinic management system. The spring boot application contains two
architectures: [MVC](#mvc) and [REST](#rest).

Both MVC and REST architectures have **Controllers** that are the first
checkpoint in the request processing. These _Controllers_ use **Services**
to handle the business logic. _Services_, in turn, use **Repositories**
that handle the underlying database operations. _Repositories_ map the
data between the database object and Java object and vice versa.

MySQL database is used to store _patient_, _doctor_, _appointment_
and _Admin_ details. **Spring Boot JPA** is the adapter for interacting
with the database from Java.

MongoDB database is used to store _prescriptions_ as they don't
follow strict relationships. This allows rapid schema evolution.

### MVC

MVC stands for Model-View-Controller which is used to separate the core
functionality of a system into 3 sub categories.

1. **Model**: Handles the database logic, i.e. CRUD.
2. **View**: Handles the GUI logic, i.e. Thymeleaf.
3. **Controller**: Handles the incoming requests by delegating the
necessary tasks to eiter _Model_ or _View_ and then generating a response.

In this application, _AdminDashboard_ and _DoctorDashboard_ have been
developed using this architecture.

### REST

REST stands for Representational State Transfer. It is a methodology to
create client-server applications that follow a specific set of [rules](https://restfulapi.net).
It uses HTTP methods like GET, PUT, POST and DELETE for interacting
with resources.

In this application, _Appointments_, _PatientDashboard_ and _PatientRecord_
have been developed using this architecture.

## Control Flow

1. User accesses Doctor or Patient Dashboard.
2. The request is directed to the relevant _Controller_ by Spring Boot.
3. It is then routed to the _Service_ that will apply the relevant
logic based on the request type.
4. _Service_ then depends on **Spring DATA JPA**(for MYSQL) and
**Spring Data MongoDB**(for MongoDB) to complete necessary operations
and persist data in Db.
5. Each _Repository_ handles the database operations through drivers.
6. These Db operations are mapped from Java Objects to Database objects.
7. Then any response, if needed, is returned to the user. For ex:
html page for the MVC Controller or JSON response for the REST Controller.