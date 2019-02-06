package com.serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.util.*
import java.util.logging.Logger


class Handler: RequestHandler<Map<String, Any?>, ApiGatewayResponse> {

  val json = jacksonObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

  val client: HttpHandler = JavaHttpClient()

  var dynamoClient = DynamoDbClient.create()

  override fun handleRequest(input: Map<String, Any?>, context: Context):ApiGatewayResponse {
    val request = json.readValue<ApiGatewayRequest>(json.writeValueAsString(input))
    val name = request.path?.split("/")?.last()

    val response = dynamoClient.putItem(PutItemRequest.builder()
            .tableName("names")
            .item(mutableMapOf(
                    "id" to AttributeValue.builder().s(UUID.randomUUID().toString()).build(),
                    "name" to AttributeValue.builder().s(name).build()
    )).build())

    return ApiGatewayResponse(
      statusCode = 200,
      body = """{ "greeting": "hello $name"}""",
      headers = mapOf("content-type" to "application/json")
    )
  }

  companion object {
    private val LOG = Logger.getLogger(Handler::class.simpleName)
  }
}
