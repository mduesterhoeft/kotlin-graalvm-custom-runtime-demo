package com.github.md

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.client.JavaHttpClient
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes


val routes: HttpHandler = routes(
    "/ping" bind GET to { _: Request -> Response(OK).body("""
            {
              "statusCode": 200,
              "headers": {"content-type":"application/json"},
              "body": "{\"ping\": \"pong\"}"
            }
        """.trimIndent())
    },
    "/greet/{name}" bind GET to { req: Request ->
        val path: String? = req.path("name")
        Response(OK).body("hello ${path ?: "anon!"}")
    }
)

val requestIdHeaderName = "lambda-runtime-aws-request-id"

val client: HttpHandler = JavaHttpClient()

val json = jacksonObjectMapper()


fun main(args: Array<String>) {
    while (true) {
        val runtimeApiEndpoint = System.getenv("AWS_LAMBDA_RUNTIME_API")
        val handler = System.getenv("_HANDLER")
        val invocationResponse: Response = client(Request(GET, "http://$runtimeApiEndpoint/2018-06-01/runtime/invocation/next"))

        val requestId = invocationResponse.header(requestIdHeaderName)
        println("Handling request for handler '$handler' with requestId '$requestId' - input is $invocationResponse")
        val functionRequest = json.readValue<ApiGatewayProxyRequest>(invocationResponse.body.stream).asHttp4k()

        println("Function request $functionRequest")
        val functionResponse = routes.invoke(functionRequest).asApiGateway()
        println("Function response $functionResponse")

        client(Request(POST, "http://$runtimeApiEndpoint/2018-06-01/runtime/invocation/$requestId/response")
            .body(json.writeValueAsString(functionResponse)))
    }
}

internal fun Response.asApiGateway() = ApiGatewayProxyResponse(status.code, headers.toMap().mapValues { it.value!! }, bodyString())

internal fun ApiGatewayProxyRequest.asHttp4k() = headers.toList().fold(
    Request(Method.valueOf(httpMethod!!), uri())
        .body(body.let(::MemoryBody))) { memo, (first, second) ->
    memo.header(first, second)
}

internal fun ApiGatewayProxyRequest.uri() = Uri.of(path ?: "").query((queryStringParameters).toList().toUrlFormEncoded())