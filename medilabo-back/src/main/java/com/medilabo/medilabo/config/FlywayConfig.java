package com.medilabo.medilabo.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Configuration class to verify and log Flyway initialization.
 * This confirms that Flyway is properly configured and will execute migrations.
 */
@Configuration
@ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = false)
public class FlywayConfig {

    private static final Logger logger = LoggerFactory.getLogger(FlywayConfig.class);

    @Autowired(required = false)
    private Flyway flyway;

    @PostConstruct
    public void logFlywayConfiguration() {
        if (flyway != null) {
            logger.info("✅ Flyway is ENABLED and configured successfully!");
            logger.info("Flyway configuration:");
            logger.info("  - Locations: {}", (Object) flyway.getConfiguration().getLocations());
            logger.info("  - Baseline version: {}", flyway.getConfiguration().getBaselineVersion());
            logger.info("  - Baseline on migrate: {}", flyway.getConfiguration().isBaselineOnMigrate());
        } else {
            logger.warn("⚠️ Flyway bean not found - this should not happen if spring.flyway.enabled=true");
        }
    }
}
