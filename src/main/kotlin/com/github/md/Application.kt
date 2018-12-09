package com.github.md

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.MemoryBody
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.toUrlFormEncoded
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes


val routes: HttpHandler = routes(
    "/ping" bind GET to { _: Request -> Response(OK).body("""{"ping": "pong"}""") },
    "/greet/{name}" bind GET to { req: Request ->
        val path: String? = req.path("name")
        Response(OK).body("""{ "greeting": "hello $path"}""")
    }
)

const val requestIdHeaderName = "lambda-runtime-aws-request-id"

val client: HttpHandler = JavaHttpClient()

val json = jacksonObjectMapper().disable(FAIL_ON_UNKNOWN_PROPERTIES)


fun main(args: Array<String>) {
    try {
        while (true) {
            val runtimeApiEndpoint = System.getenv("AWS_LAMBDA_RUNTIME_API")
            val handler = System.getenv("_HANDLER")

            val invocationResponse: Response =
                client(Request(GET, "http://$runtimeApiEndpoint/2018-06-01/runtime/invocation/next"))

            val requestId = invocationResponse.header(requestIdHeaderName)
            val functionRequest = json.readValue<ApiGatewayProxyRequest>(invocationResponse.body.stream).asHttp4k()

            val functionResponse = routes.invoke(functionRequest).asApiGateway()

            client(Request(POST, "http://$runtimeApiEndpoint/2018-06-01/runtime/invocation/$requestId/response")
                .body(json.writeValueAsString(functionResponse)))
        }
    } catch (t: Throwable) {
        t.printStackTrace()
        println("request loop failed with ${t.message}")
    }
}

internal fun Response.asApiGateway() = ApiGatewayProxyResponse(status.code, headers.toMap().mapValues { it.value!! }, bodyString())

internal fun ApiGatewayProxyRequest.asHttp4k() = (headers?: emptyMap()).toList().fold(
    Request(Method.valueOf(httpMethod!!), uri())
        .body(body.orEmpty().let(::MemoryBody))) { memo, (first, second) ->
    memo.header(first, second)
}

internal fun ApiGatewayProxyRequest.uri() = Uri.of(path ?: "").query((queryStringParameters?: emptyMap()).toList().toUrlFormEncoded())