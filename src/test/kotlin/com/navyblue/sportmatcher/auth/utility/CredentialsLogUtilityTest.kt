package com.navyblue.sportmatcher.auth.utility

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CredentialsLogUtilityTest {
    @Test
    fun `maskEmail masks local part and domain name`() {
        val maskedEmail = CredentialsLogUtility.maskEmail("dsdsad@sos.pl")

        assertThat(maskedEmail).isEqualTo("d***@s***.pl")
    }
}
