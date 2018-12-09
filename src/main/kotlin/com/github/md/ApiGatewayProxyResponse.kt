package com.github.md

data class ApiGatewayProxyResponse(val statusCode: Int, val headers: Map<String, String>, val body: String)