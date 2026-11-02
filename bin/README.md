# Himalayan Ride and Care

An MCA mini-project based on the supplied case study, **A Case Study on Himalayan Ride & Care**. It manages customers, motorcycles, rental bookings, pickup and return readings, payment records, destinations, and roadside assistance. The web application uses Java Spring Boot, Hibernate/JPA, JDBC, and MySQL.

## Main functions

- Maintain customer and motorcycle records; mark fleet vehicles available, under maintenance, or inactive.
- Create rental requests and reject unavailable motorcycles or overlapping pending, confirmed, or active rentals.
- Confirm rentals, check motorcycles out, and complete returns with odometer checks.
- Save payment records against rentals. These are records only; the application does not charge a payment provider.
- Open roadside assistance cases, set priorities, assign responders, and move cases through their statuses.
- Show destinations and operational counts in a browser dashboard.

## Requirements

- Java 17 or newer
- Maven 3.8 or newer
- MySQL 8.x and MySQL Workbench

## Set up and run

1. Start MySQL.
2. In MySQL Workbench, run `database/schema.sql` to create the database.
3. Run `database/case_study_schema_mysql.sql` to create the case-study tables and sample data. (The Java app can also create/update entity tables on startup.)
4. Optionally run `database/adbms_schema_mysql.sql`, then `database/adbms_routines_mysql.sql` to add the ADBMS lab exercises described below.
5. Check the username/password in `src/main/resources/application.properties`, or set `DB_USER`, `DB_PASSWORD`, and optionally `DB_URL` environment variables.
6. Open a terminal in this folder and run `mvn spring-boot:run`.
7. Open `http://localhost:8080`.

Sample customers, motorcycles, and destinations are added automatically by the app when those tables are empty. Sample data is for learning only.

## Typical workflow

1. Create a rental request for one of the sample customers and an available motorcycle.
2. Confirm the request. The application checks that the motorcycle is still available.
3. Check out the rental and enter the pickup odometer reading.
4. Record any deposit or balance as a separate payment record.
5. If the customer needs help, open an assistance case with its location and priority; assign a responder and update its progress.
6. Record the return odometer reading to complete the rental and update the motorcycle's current odometer.

## REST endpoints

| Method and path | Purpose |
|---|---|
| `GET /api/customers`, `POST /api/customers` | List or add customers |
| `GET /api/motorcycles`, `POST /api/motorcycles` | List or add motorcycles |
| `PATCH /api/motorcycles/{id}/status` | Set `AVAILABLE`, `MAINTENANCE`, or `INACTIVE` |
| `GET /api/destinations`, `POST /api/destinations` | List or add destinations |
| `GET /api/rentals`, `POST /api/rentals` | List or create rental requests |
| `PATCH /api/rentals/{id}/status` | Confirm or cancel a request |
| `POST /api/rentals/{id}/checkout` | Record pickup odometer and activate rental |
| `POST /api/rentals/{id}/return` | Record return odometer and complete rental |
| `GET /api/payments`, `POST /api/payments` | List or record a payment |
| `GET /api/assistance`, `POST /api/assistance` | List or open a support case |
| `PATCH /api/assistance/{id}` | Assign a responder or update case status |

JSON example for `POST /api/rentals` (IDs refer to seeded rows; dates must be future dates):

```json
{
  "customerId": 1,
  "motorcycleId": 1,
  "destinationId": 1,
  "startDate": "2027-06-10",
  "endDate": "2027-06-14"
}
```

## Database design

The core case-study tables are `customers`, `motorcycles`, `destinations`, `rental_bookings`, `payment_records`, and `assistance_requests`. A rental references one customer and one motorcycle, and may reference a destination. A rental can have several payment records. An assistance request always has a customer and may optionally reference a rental. Foreign keys protect those links. `database/case_study_schema_mysql.sql` contains the DDL and seed data.

## ADBMS practicals and the MySQL/PL/SQL difference

The provided ADBMS exercise sheet names PL/SQL practicals, and the case study document refers to Oracle SQL/PL/SQL. This project is configured for MySQL, so it uses MySQL stored-program syntax for equivalent concepts. MySQL scripts are **not Oracle PL/SQL scripts**. If your instructor specifically requires Oracle syntax, these routines need an Oracle version.

After running the two ADBMS SQL files in Workbench, the examples at the end of `database/adbms_routines_mysql.sql` demonstrate:

- minimum balance validation before creating a wallet account;
- an explicit cursor that reports low-stock service parts;
- a discount function;
- a transaction that records a parts order and adjusts stock;
- a guarded wallet transfer with rollback on invalid accounts or insufficient funds;
- a trigger that audits wallet balance changes.

These are standalone database practicals adapted from the lab sheet. They complement the rental case study; the wallet exercises do not process web payments.

## Project folders

```text
src/main/java/in/himalayanridecare/  Java app, data models, APIs, repositories
src/main/resources/static/           Browser application (HTML, CSS, JavaScript)
database/schema.sql                  Database creation
database/case_study_schema_mysql.sql Rental case-study tables and sample rows
database/adbms_schema_mysql.sql      ADBMS exercise tables
database/adbms_routines_mysql.sql    ADBMS procedures, function, cursor, trigger
PROJECT_REPORT.md                    Editable project report
STEP_BY_STEP.md                      Beginner walkthrough and viva practice
```

## Limits

This is a classroom prototype. It has no login or role control, live GPS, map service, weather alerts, payment gateway, insurance processing, or automatic emergency dispatch. An urgent case entered in the database does not contact emergency services. Staff must follow their real-world escalation process.
