# Learn Himalayan Ride and Care step by step

## 1. What the project does

Imagine a small motorcycle rental desk in the Himalayas. Staff need to know who is renting, which motorcycle is free, when it is picked up and returned, whether a payment was recorded, and how to follow a roadside problem. This application keeps those records together.

## 2. Main modules

1. **Customers:** name and contact information.
2. **Motorcycles:** registration number, model, daily rate, odometer and operating status.
3. **Destinations:** names and short descriptions for trip planning.
4. **Rentals:** customer, motorcycle, dates and booking status.
5. **Checkout/return:** pickup and return odometer readings.
6. **Payments:** one rental may have several payment records, such as a deposit and a remaining balance.
7. **Roadside assistance:** issue type, priority, location, assigned responder and progress.
8. **ADBMS routines:** separate MySQL stored-program practice for cursor, function, procedure, transaction and trigger concepts.

## 3. How the parts talk

```text
Browser page → HTTP + JSON → Spring Boot controller → Hibernate/JPA → MySQL
                                                └→ JDBC for dashboard counts
```

- **HTML** creates page fields and tables.
- **CSS** styles them for different screen sizes.
- **JavaScript** sends JSON requests and refreshes the page.
- **Spring Boot** starts the Java web server.
- **REST controllers** receive URLs and HTTP methods.
- **Hibernate/JPA** maps Java objects into database rows.
- **JDBC** sends direct SQL to MySQL for a summary query.
- **MySQL** stores the records.

## 4. Understand the tables

- `customers` stores customer contact information.
- `motorcycles` stores fleet vehicles and their current status.
- `destinations` stores locations.
- `rental_bookings` links one customer to one motorcycle and optionally a destination.
- `payment_records` links payment entries to rentals. Multiple payments can refer to one rental.
- `assistance_requests` links an incident to a customer and optionally to a rental.

Each table has a primary key. A foreign key connects related rows. For example, `rental_bookings.motorcycle_id` must refer to a motorcycle that exists.

## 5. Follow a rental from start to finish

1. The staff member selects a customer, motorcycle, destination, and dates.
2. The browser sends a POST request with JSON to `/api/rentals`.
3. Spring checks required fields and dates.
4. The server checks the motorcycle is marked available.
5. The server checks if another pending, confirmed, or active rental overlaps the requested dates.
6. If valid, Hibernate saves a pending rental in MySQL.
7. Staff confirm it, then use **Check out** to enter the pickup odometer; its status becomes active.
8. A payment record can be entered separately. It records information only and does not process money.
9. If something goes wrong on the road, staff open an assistance case, set priority/location and assign a responder.
10. On return, staff enter the final odometer. The app checks it is not below pickup, completes the rental and updates the motorcycle's current reading.

## 6. Run it on your computer

1. Install Java 17+, Maven, and MySQL 8.
2. Start MySQL and run `database/schema.sql` in Workbench.
3. Run `database/case_study_schema_mysql.sql` for the rental system tables and sample data.
4. Set your MySQL username and password in `application.properties` or environment variables.
5. From the project folder, run `mvn spring-boot:run`.
6. Open `http://localhost:8080`.
7. Add a customer if needed, create a rental, and follow the workflow above.

## 7. Business rules to understand

- A motorcycle under maintenance or marked inactive cannot be newly rented.
- A pending, confirmed, or active rental reserves its date range against conflicts.
- The return date must come after the pickup date.
- Pickup odometer must not be lower than the bike's current odometer.
- Return odometer must not be lower than pickup odometer.
- A payment amount must be greater than zero.
- An assistance case needs a customer, category, description and location.
- Recording an urgent case does not call emergency services.

## 8. ADBMS SQL exercises

The case-study document refers to Oracle SQL and PL/SQL, while this app's database is MySQL. So the included ADBMS exercises use MySQL stored-program syntax for equivalent ideas; the scripts cannot run unchanged in Oracle. In Workbench, run `database/adbms_schema_mysql.sql`, then `database/adbms_routines_mysql.sql`.

1. `create_wallet_account` checks a minimum opening balance before inserting.
2. `report_low_stock` uses an explicit cursor to fetch inventory rows.
3. `calculate_discount` returns a percentage based on order value.
4. `process_part_order` records a service-parts order and reduces stock in one transaction.
5. `transfer_wallet_funds` checks account numbers and available balance; an error rolls the transaction back.
6. `trg_wallet_balance_audit` records old and new wallet balances automatically after an update.

These are database exercises based on the ADBMS lab sheet, not payment operations in the rental website.

## 9. Try the REST API

You can use a browser for GET requests or Postman for other methods. Examples:

- `GET /api/motorcycles` lists the fleet.
- `POST /api/rentals` creates a rental request.
- `POST /api/rentals/1/checkout` with `{"odometerKm":12550}` checks out rental 1.
- `POST /api/rentals/1/return` with `{"odometerKm":12820}` completes it.
- `POST /api/assistance` opens a roadside case.
- `PATCH /api/assistance/1` with `{"assignedTo":"Ravi","status":"ASSIGNED"}` assigns a responder.

The API details and a full rental example are in `README.md`.

## 10. Viva practice

**What is client/server architecture?**  
The browser is the client. It sends an HTTP request. The Java server processes it and returns a response.

**What is REST?**  
An approach to web APIs that organizes URLs around resources and uses HTTP methods to read or change them.

**What does Hibernate do?**  
It maps Java objects to relational database rows and handles common SQL persistence operations.

**What is JDBC?**  
Java Database Connectivity is the standard Java API for sending SQL to a database. Spring's `JdbcTemplate` is a helper built on JDBC.

**Where is JDBC used?**  
In the dashboard summary query. Hibernate/JPA handles the case-study entities.

**What is a primary key?**  
A value that uniquely identifies a row, such as `motorcycles.id`.

**What is a foreign key?**  
A database constraint that links a value to a real row in another table.

**How does the system prevent two reservations for the same motorcycle?**  
Before saving, it looks for a pending, confirmed, or active booking whose date range intersects the requested range.

**Why store payments separately?**  
A customer may pay in multiple parts. Each transaction can be recorded against the same rental without overwriting earlier entries.

**Does the payment feature collect money?**  
No. It records payment information entered by staff. There is no payment gateway connection.

**What is a transaction?**  
A group of database changes treated as one unit. If a failure occurs, rollback prevents a partial update.

**What is a trigger?**  
Database code that runs automatically when a defined event happens. The wallet audit trigger logs balance changes after an update.

**Is this Oracle PL/SQL?**  
No. The case-study document mentions Oracle and PL/SQL, but this project uses MySQL. Its stored routines use MySQL syntax and demonstrate similar concepts.

**What is outside the project scope?**  
Live GPS, maps, payment processing, login/roles, insurance claims, and automated emergency dispatch.

## 11. Best way to learn it

Read `README.md` first, then trace one booking in `app.js`, `CaseStudyControllers.java`, `RentalBooking.java`, and `RentalBookingRepository.java`. Finally inspect the foreign keys in `database/case_study_schema_mysql.sql`. Try the rental once in the browser and once through Postman. Explain the request, validation, overlap check, save, and response in your own words.
