package com.navyblue.sportmatcher.auth.utility

object CredentialsLogUtility {
    fun maskEmail(email: String): String {
        val localPart = email.substringBefore("@")
        val domain = email.substringAfter("@", missingDelimiterValue = "")
        val domainName = domain.substringBefore(".")
        val topLevelDomain = domain.substringAfter(".", missingDelimiterValue = "")

        val maskedLocalPart = maskPart(localPart)
        val maskedDomain = maskPart(domainName)

        return if (topLevelDomain.isBlank()) {
            "$maskedLocalPart@$maskedDomain"
        } else {
            "$maskedLocalPart@$maskedDomain.$topLevelDomain"
        }
    }

    private fun maskPart(value: String): String = "${value.first()}***"
}
