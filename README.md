# Bursary Match System

A desktop Java application that matches South African students to bursaries
and scholarships they qualify for, based on their academic results. Bursary
providers can list, edit, and manage their own offers.

Originally designed and specified as a school PAT (Practical Assessment
Task) in 2021 (see `docs/`), currently being rebuilt as a portfolio project
with a modern, portable stack.

## Status

Rebuilding from the original design documents. Currently in place:

- [x] Maven project structure
- [x] Data model classes (`Person`, `Student`, `StudentMarks`, `BursaryProvider`, `Offer`, `OfferArr`)
- [x] Database schema (PostgreSQL)
- [x] JDBC connection wrapper (`DataBase`)
- [ ] Swing GUI (LogInGUI, SignUpOptionGUI, MainScreenGUI, Search/Profile/Help tabs)
- [ ] CRUD operations wired up to the GUI
- [ ] Help text files and FAQ system
- [ ] Eligibility ranking wired into the Search tab

## Tech stack

- Java 21, Maven
- PostgreSQL (recommend a free-tier hosted instance via [Neon](https://neon.tech) or [Supabase](https://supabase.com) so anyone can run this without a local DB install)
- Swing (GUI, to be rebuilt)

## Setup

1. Create a free PostgreSQL database (Neon or Supabase both work well) and
   copy its connection details.
2. Run `src/main/resources/schema.sql` against that database to create the
   tables.
3. Set these environment variables (never commit real credentials):
   ```
   DB_URL=jdbc:postgresql://<host>/<dbname>?sslmode=require
   DB_USER=<your-db-username>
   DB_PASSWORD=<your-db-password>
   ```
4. Build and run:
   ```
   mvn clean compile exec:java
   ```
   You should see `Database connection successful.` printed to the console.

## Original design documents

The `docs/` folder (add these yourself, or keep them wherever you like)
contains the Phase 1 specification and Phase 2 GUI/class design that this
rebuild is based on.

## Notes on changes from the original design

- **Database**: switched from Microsoft Access to PostgreSQL. Access requires
  a paid subscription, is Windows-only, and Java dropped native JDBC-ODBC
  support years ago — none of that is workable for a public repo.
- **Class names**: `Offers` → `Offer`, `BursaryProviders` → `BursaryProvider`
  (singular class names for individual objects is standard Java convention).
- **OfferArr**: backed by an `ArrayList` internally instead of a raw fixed-size
  array, while keeping the same `sort()`/`search()` behaviour from the design.
