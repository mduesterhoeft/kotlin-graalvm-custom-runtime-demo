package com.github.md

import java.util.Collections

class ApiGatewayProxyRequest {
    var path: String? = null
    var httpMethod: String? = null
    var headers = emptyMap<String, String>()
    var queryStringParameters = emptyMap<String, String>()
    var body = ""
}