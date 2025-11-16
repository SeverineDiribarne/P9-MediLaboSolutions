# Flyway Configuration for medilabo-back

## Overview
Flyway is now configured and activated for the medilabo-back service. It will automatically manage database schema migrations on application startup.

## Configuration Details

### 1. Dependencies Added (pom.xml)
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

### 2. Application Properties
The following Flyway configurations have been added to `application.properties`:

```properties
# Flyway configuration
spring.flyway.enabled=true                    # Enables Flyway
spring.flyway.baseline-on-migrate=true        # Creates baseline for existing databases
spring.flyway.locations=classpath:db/migration # Location of migration scripts
spring.flyway.baseline-version=0              # Baseline version number

# JPA configuration changed
spring.jpa.hibernate.ddl-auto=validate        # Changed from 'update' to 'validate'
```

### 3. Migration Scripts Location
Migration scripts are located in: `src/main/resources/db/migration/`

Current migrations:
- `V1__Initial_schema.sql` - Creates initial User and Patient tables

## How Flyway Works

1. **On Application Startup**: Flyway will automatically:
   - Check the database for a `flyway_schema_history` table
   - If it doesn't exist, create it and establish a baseline
   - Execute any pending migrations in order (V1, V2, V3, etc.)
   - Record executed migrations in the history table

2. **Migration Naming Convention**:
   - Format: `V{version}__{description}.sql`
   - Example: `V1__Initial_schema.sql`, `V2__Add_user_roles.sql`
   - Version numbers must be unique and sequential

3. **Schema Management**:
   - Hibernate's `ddl-auto` is now set to `validate`
   - Flyway is responsible for all schema changes
   - Hibernate will only validate that entities match the database schema

## Testing Flyway Activation

### Prerequisites
Ensure MySQL is running and accessible with these credentials (from application.properties):
- URL: `jdbc:mysql://localhost:3306/db_medilabo`
- Username: `root`
- Password: `rootroot`

### Method 1: Using Docker Compose
1. Update `compose.yaml` to match application.properties credentials:
```yaml
services:
  mysql:
    image: 'mysql:latest'
    environment:
      - 'MYSQL_DATABASE=db_medilabo'
      - 'MYSQL_ROOT_PASSWORD=rootroot'
    ports:
      - '3306:3306'
```

2. Start the database:
```bash
docker-compose up -d
```

3. Run the application:
```bash
mvn spring-boot:run
```

### Method 2: Using Local MySQL
1. Create the database:
```sql
CREATE DATABASE IF NOT EXISTS db_medilabo;
```

2. Run the application:
```bash
mvn spring-boot:run
```

### Expected Output
When Flyway activates successfully, you should see logs similar to:

```
INFO o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 9.22.3 by Redgate
INFO o.f.core.internal.command.DbValidate     : Successfully validated 1 migration (execution time 00:00.012s)
INFO o.f.core.internal.command.DbMigrate      : Current version of schema `db_medilabo`: << Empty Schema >>
INFO o.f.core.internal.command.DbMigrate      : Migrating schema `db_medilabo` to version "1 - Initial schema"
INFO o.f.core.internal.command.DbMigrate      : Successfully applied 1 migration to schema `db_medilabo` (execution time 00:00.045s)
```

### Verification
After the application starts, you can verify Flyway executed successfully by:

1. Checking the database for the `flyway_schema_history` table:
```sql
SELECT * FROM flyway_schema_history;
```

2. Verifying the User and Patient tables were created:
```sql
SHOW TABLES;
```

## Adding New Migrations

To add a new migration:

1. Create a new SQL file in `src/main/resources/db/migration/`
2. Name it following the convention: `V{next_version}__{description}.sql`
   - Example: `V2__Add_email_verification.sql`
3. Write your SQL DDL statements
4. Restart the application - Flyway will automatically detect and apply the new migration

## Important Notes

⚠️ **Never modify existing migration files after they've been applied to any environment!**

✅ **Best Practices**:
- Always test migrations on a development database first
- Use version control for all migration scripts
- Keep migrations small and focused
- Add rollback scripts for complex changes (as separate files)
- Review migration order carefully

## Troubleshooting

### Issue: "Flyway failed to initialize"
**Solution**: Check database connectivity and credentials in `application.properties`

### Issue: "Validate failed: Migration checksum mismatch"
**Solution**: Do not modify existing migration files. Create a new migration to fix issues.

### Issue: "Schema-validation: missing table"
**Solution**: Ensure all migrations have been applied. Check `flyway_schema_history` table.

## Why Flyway?

Flyway was activated to provide:
1. **Version Control**: Track database schema changes alongside code changes
2. **Reproducibility**: Ensure consistent database state across all environments
3. **Automation**: Automatic schema updates on application deployment
4. **Safety**: Validate schema against entities before application startup
5. **History**: Complete audit trail of all schema changes

Previously, the application used `spring.jpa.hibernate.ddl-auto=update`, which automatically generated schema changes but provided no version control or migration history.
