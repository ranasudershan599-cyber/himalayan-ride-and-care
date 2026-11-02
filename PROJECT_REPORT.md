# Himalayan Ride and Care
## MCA Mini Project Report

**Based on:** *A Case Study on Himalayan Ride & Care*  
**Subjects:** Advanced Internet Programming and Advanced Database Management System  
**Implementation database:** MySQL 8  
**Student:** Ayush Rana  
**Roll number:** 26MCI10020  
**College:** University Institute of Computing, Punjab, India

## Abstract

Himalayan Ride and Care is a web application for motorcycle rentals and roadside assistance in Himalayan travel regions. It stores customer and motorcycle details, checks rental periods for conflicts, records pickup and return odometer readings, links payment records to rentals, and tracks assistance requests. A Java Spring Boot server exposes REST endpoints that exchange JSON with a browser interface. Hibernate/JPA maps Java objects to MySQL records, while JDBC runs a dashboard summary query. Separate MySQL stored-program scripts demonstrate the ADBMS practical topics in the supplied lab sheet.

## 1. Introduction and problem statement

Rental operators need to know which motorcycles are ready to rent, which are already reserved, and which need maintenance. They also need to connect the customer's booking to pickup and return readings, payment entries, and any roadside incident. When these records live in separate registers or spreadsheets, conflicting reservations and slow incident lookups can result.

This project provides a small central system for these records. It is an academic prototype; its expected benefits have not been measured in a live business.

## 2. Objectives

1. Store customers, motorcycles, destinations, rentals, payments, and assistance requests.
2. Prevent a motorcycle from being assigned to overlapping pending, confirmed, or active rentals.
3. Prevent a maintenance or inactive motorcycle from being newly rented.
4. Track a rental from request through confirmation, pickup, return, or cancellation.
5. Check pickup and return odometer readings.
6. Record payment information without claiming that an external payment was collected.
7. Track assistance priority, responder, location, and progress.
8. Demonstrate Java web programming, REST, JSON, Spring Boot, JDBC, Hibernate, MySQL, and database routines.

## 3. Scope and assumptions

The initial version covers customer and fleet records, rental request and status changes, checkout and return readings, payment records, destination information, assistance cases, and operational summaries. Staff enter accurate information. Dates are calendar dates. A pending request reserves its motorcycle for overlap checking. Payment rows are records entered by staff; they do not independently establish settlement with a bank or payment provider.

The case study document describes Oracle SQL and PL/SQL. The web app was configured for MySQL as requested in the project conversation. The companion ADBMS routines use MySQL stored-program syntax and demonstrate equivalent database concepts; they cannot be run unchanged in Oracle.

## 4. Stakeholders and modules

| Stakeholder | Project module and responsibility |
|---|---|
| Tourist / customer | Provides contact details, requests a motorcycle, reports a roadside issue |
| Rental desk staff | Creates and confirms a booking; records pickup and return readings |
| Fleet manager | Adds motorcycles and marks them available, in maintenance, or inactive |
| Support operator | Opens, assigns, and updates roadside assistance cases |
| Administrator | Maintains the system's operational records; login/roles are future work |
| Business manager | Reviews fleet, rental, payment, and unresolved-support summaries |

### Modules

- **Customer management:** customer name, phone, and optional email.
- **Motorcycle fleet:** registration number, model, daily rate, odometer, and status.
- **Rental booking:** customer, motorcycle, optional destination, date range, and status.
- **Checkout and return:** records odometer readings and changes the rental to active or completed.
- **Payments:** multiple payment entries can be attached to one rental.
- **Destinations:** stores Himalayan destination names and descriptions.
- **Roadside assistance:** stores customer, optional rental, category, priority, description, location, responder, and status.
- **ADBMS lab routines:** wallet account and service-parts examples for the separate ADBMS exercise sheet.

## 5. Architecture

```text
Browser (HTML, CSS, JavaScript)
        |  HTTP methods and JSON
        v
Spring Boot REST controllers
        |                     |
 Hibernate / JPA             JDBC JdbcTemplate
        |                     |
        +------ MySQL --------+
          rental tables and reports
```

Spring Boot starts the web application with embedded Tomcat. A controller receives a URL and HTTP method, validates the input, and calls a repository. Spring Data JPA uses Hibernate to persist entities. The dashboard repository uses JDBC for direct SQL aggregation. Spring serializes the returned Java values as JSON for the browser.

## 6. Database design

| Table | Purpose and important relationships |
|---|---|
| `customers` | Customer contact details; referenced by rentals and assistance cases |
| `motorcycles` | Registration, model, rate, current odometer, and operating status |
| `destinations` | Destination name and description |
| `rental_bookings` | Customer and motorcycle foreign keys, optional destination, period, state, staff and odometer readings |
| `payment_records` | One row per payment entry, linked to a rental; a rental may have many entries |
| `assistance_requests` | Customer and optional rental links, issue, priority, location, responder, state |

**Relationships:** a customer may have many rentals and support cases. A motorcycle may have many rentals over time, but not overlapping pending, confirmed, or active rental periods. A rental may have multiple payment entries and may have related support cases. An assistance request can also be created without a linked rental.

**Keys and constraints:** every table has a primary key. Foreign keys connect records. Registration numbers and customer emails are unique when supplied. Rates and odometers cannot be negative. Rental end dates must follow start dates. The Java application performs the overlap check before saving a new rental.

## 7. Important business rules implemented

1. A rental must refer to an existing customer and motorcycle.
2. Pickup and return dates must be ordered; a rental's end date is after its start date.
3. A motorcycle with maintenance or inactive status cannot receive a new rental.
4. An overlapping pending, confirmed, or active rental blocks another request for that motorcycle.
5. Only confirmed rentals can be checked out; only active rentals can be returned.
6. A pickup odometer cannot be lower than the current motorcycle odometer.
7. A return odometer cannot be lower than the pickup reading; a successful return updates the motorcycle odometer.
8. Payment amount must be positive and a payment method must be supported.
9. An assistance case requires a customer, issue category, description, and location. It can optionally reference a rental.

## 8. Booking and assistance statuses

**Rental booking:** `PENDING` → `CONFIRMED` → `ACTIVE` → `COMPLETED`; a request may be `CANCELLED`. The browser has actions for confirmation, checkout, return, and cancellation.

**Roadside assistance:** `OPEN` → `ASSIGNED` → `IN_PROGRESS` → `RESOLVED` → `CLOSED`; a case may also be `CANCELLED`. The operator enters a responder and advances the status.

An urgent status is only stored in the project. It does not call emergency responders or send alerts.

## 9. Advanced Database Management System syllabus mapping

The supplied ADBMS sheet contains these PL/SQL practical exercises. In this MySQL project, `database/adbms_schema_mysql.sql` and `database/adbms_routines_mysql.sql` implement the same database-programming ideas with MySQL syntax:

| Syllabus practical | Implementation |
|---|---|
| Validate minimum balance before creating an account | `create_wallet_account` validates a minimum opening balance before insert |
| Explicit cursor for low-stock products | `report_low_stock` fetches service-part inventory through an explicit cursor |
| User-defined discount function | `calculate_discount` returns a tiered discount percentage |
| Process orders and update inventory | `process_part_order` records a parts order and decrements stock in a transaction |
| Transfer funds with exceptions | `transfer_wallet_funds` validates accounts and balance, then commits or rolls back |
| Trigger to audit balance changes | `trg_wallet_balance_audit` stores the old and new wallet balance |

These are database lab examples, not rental payment features. The ADBMS sheet's sample project title refers to an online bookstore, but the selected project remains the user's Himalayan Ride & Care case study.

## 10. Advanced Internet Programming syllabus mapping

| Visible AIP topic | Project connection |
|---|---|
| Client/server architecture and web applications | Browser UI calls the Java server over HTTP |
| Server-side Java, servlet container, request/response | Spring MVC receives web requests through embedded Tomcat |
| JDBC and CRUD | Dashboard uses JDBC; REST operations create/read/update case records via repositories |
| Hibernate ORM | JPA entities map customer, motorcycle, rental, payment, destination, and assistance objects to tables |
| Spring Framework and Spring Boot | Boot configures the app; controllers handle API requests |
| REST methods and data exchange | APIs use GET, POST, PATCH, and JSON |
| JSP and session practicals | The app uses a static HTML/JavaScript view and does not need an HTTP session because it has no login. These syllabus topics are explained but not implemented as separate JSP/session demonstrations. |

The supplied screenshots are cropped at the edges, so this table covers the topics readable in them.

## 11. Setup and demonstration

Follow `README.md` to create the database, load sample data, and run the application. For a classroom demonstration:

1. Show the seeded customer, motorcycle, and destination lists.
2. Create a rental request and confirm it.
3. Try to create an overlapping request for the same bike; explain the rejection.
4. Check out the motorcycle with an odometer value at least as high as its current reading.
5. Record a payment and explain that it is only a database record.
6. Open an assistance case, assign a responder, and advance the status.
7. Return the motorcycle with a reading at least as high as pickup, then show the updated bike mileage.
8. In MySQL Workbench, query related tables and run one or more ADBMS stored routines.

## 12. Limitations and future work

This prototype has no login, authorization roles, live GPS, map or weather services, payment gateway, insurance claims, or automated emergency-service dispatch. Future improvements could add staff accounts, audit history for operational records, refund and late-return policies, inspection forms, location consent and retention controls, XML API output, and reports by date, status, or fleet utilization.

## Conclusion

Himalayan Ride and Care connects motorcycle rental operations with roadside support in a small Java web application backed by MySQL. It demonstrates the case study's central records and workflow rules, while the companion SQL scripts cover database programming exercises from the supplied ADBMS syllabus. Oracle-specific PL/SQL would require an Oracle implementation; the included routines are for MySQL.
