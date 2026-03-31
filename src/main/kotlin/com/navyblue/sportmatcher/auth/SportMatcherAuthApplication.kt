package com.navyblue.sportmatcher.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SportMatcherAuthApplication

fun main(args: Array<String>) {
	runApplication<SportMatcherAuthApplication>(*args)
}
