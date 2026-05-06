package com.navyblue.sportmatcher.auth.utility

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CredentialsLogUtilityTest {
    @ParameterizedTest
    @CsvSource(
        "donny@sun, d***@s***",
        "dsdsad@sos.pl, d***@s***.pl",
    )
    fun `maskEmail masks local part and domain name`(
        email: String,
        expectedMaskedEmail: String,
    ) {
        val maskedEmail = CredentialsLogUtility.maskEmail(email)

        assertThat(maskedEmail).isEqualTo(expectedMaskedEmail)
    }
}
