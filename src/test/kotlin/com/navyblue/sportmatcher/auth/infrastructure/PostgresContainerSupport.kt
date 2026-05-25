package com.navyblue.sportmatcher.auth.infrastructure

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

abstract class PostgresContainerSupport {
    companion object {
        private val postgres =
            PostgreSQLContainer(
                DockerImageName.parse("postgres:18-alpine"),
            ).apply {
                start()
            }

        @JvmStatic
        @DynamicPropertySource
        fun registerDataSourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}
