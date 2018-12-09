package com.github.md

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test

class DeserTest {

    @Test
    fun `should deserialize request`() {

        val request = jacksonObjectMapper().readValue<ApiGatewayProxyRequest>("""{ "path": "/some" }""")
    }
}