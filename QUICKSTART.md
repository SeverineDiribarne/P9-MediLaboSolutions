# Quick Start Guide - Flyway Activation Verification

## What Was Changed

```
P9-MediLaboSolutions/
├── .gitignore                                    [NEW] - Exclude build artifacts
├── SOLUTION.md                                   [NEW] - Full solution explanation
└── medilabo-back/
    ├── FLYWAY_README.md                          [NEW] - Complete Flyway documentation
    ├── pom.xml                                   [MODIFIED] - Added Flyway dependencies
    ├── src/main/java/com/medilabo/medilabo/config/
    │   └── FlywayConfig.java                     [NEW] - Verification class
    └── src/main/resources/
        ├── application.properties                [MODIFIED] - Flyway configuration
        └── db/migration/
            └── V1__Initial_schema.sql            [NEW] - Initial migration
```

## How to Test

### Option 1: Quick Test with Docker

```bash
# 1. Start MySQL
cd medilabo-back
docker-compose up -d

# 2. Update compose.yaml to use correct database name
# Change MYSQL_DATABASE to 'db_medilabo'
# Change MYSQL_ROOT_PASSWORD to 'rootroot'

# 3. Run the application
mvn spring-boot:run
```

### Option 2: With Existing MySQL

```bash
# 1. Ensure MySQL is running on localhost:3306

# 2. Create database if not exists
mysql -u root -p
CREATE DATABASE IF NOT EXISTS db_medilabo;
exit

# 3. Run the application
cd medilabo-back
mvn spring-boot:run
```

## What to Look For

### ✅ Success Indicators

When you run the application, look for these log messages:

```
INFO o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 9.22.3 by Redgate
INFO o.f.core.internal.command.DbValidate     : Successfully validated 1 migration
INFO o.f.core.internal.command.DbMigrate      : Current version of schema `db_medilabo`: << Empty Schema >>
INFO o.f.core.internal.command.DbMigrate      : Migrating schema `db_medilabo` to version "1 - Initial schema"
INFO o.f.core.internal.command.DbMigrate      : Successfully applied 1 migration to schema `db_medilabo`
INFO c.m.m.config.FlywayConfig                : ✅ Flyway is ENABLED and configured successfully!
```

### Database Verification

After startup, check your database:

```sql
-- View migration history
SELECT * FROM flyway_schema_history;

-- Expected output:
-- installed_rank | version | description      | type | script                    | checksum    | installed_by | installed_on         | execution_time | success
-- 1              | 1       | Initial schema   | SQL  | V1__Initial_schema.sql   | -123456789  | root         | 2025-11-16 17:00:00 | 45             | 1

-- View created tables
SHOW TABLES;

-- Expected output:
-- flyway_schema_history
-- patient
-- user
```

## Key Configuration Changes

### application.properties - Before
```properties
spring.jpa.hibernate.ddl-auto=update
```

### application.properties - After
```properties
spring.jpa.hibernate.ddl-auto=validate

# Flyway configuration
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-version=0
```

### pom.xml - Added
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

## Troubleshooting

### Problem: "Cannot connect to database"
**Solution**: Ensure MySQL is running and credentials in `application.properties` are correct

### Problem: "No Flyway logs appear"
**Solution**: Check that `spring.flyway.enabled=true` is set and migration files exist in `db/migration/`

### Problem: "Schema validation failed"
**Solution**: Drop the database and recreate it, or set `spring.flyway.baseline-on-migrate=true`

## Adding New Migrations

When you need to modify the database schema:

1. Create a new file: `V2__Your_description.sql`
2. Place it in: `src/main/resources/db/migration/`
3. Write your SQL DDL statements
4. Restart the application

Example:
```sql
-- V2__Add_user_roles.sql
ALTER TABLE user ADD COLUMN role VARCHAR(50) DEFAULT 'USER';
```

## Documentation

- **Complete Guide**: See `medilabo-back/FLYWAY_README.md`
- **Solution Explanation**: See `SOLUTION.md` at project root
- **Flyway Official Docs**: https://flywaydb.org/documentation/

## Summary

✅ Flyway is now **fully configured and activated**
✅ Initial migration script created for User and Patient tables
✅ Hibernate set to validate-only mode
✅ Automatic migration on application startup
✅ Full version control for database schema
