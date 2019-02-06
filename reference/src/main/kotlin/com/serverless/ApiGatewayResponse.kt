package com.serverless

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import java.nio.charset.StandardCharsets
import java.util.*

class ApiGatewayResponse(
  val statusCode: Int = 200,
  var body: String? = null,
  val headers: Map<String, String>? = Collections.emptyMap(),
  val isBase64Encoded: Boolean = false
)
