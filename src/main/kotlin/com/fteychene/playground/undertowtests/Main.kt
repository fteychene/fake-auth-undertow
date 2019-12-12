package com.fteychene.playground.undertowtests

import arrow.core.toOption
import io.undertow.Undertow
import io.undertow.attribute.ExchangeAttributes
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.ResponseCodeHandler
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient
import io.undertow.server.handlers.proxy.ProxyHandler
import io.undertow.util.Headers
import java.net.URI
import java.util.*

val authProvider = object : AuthProvider {
    override fun checkAccess(token: String?): AuthenticationStatus =
        when (token) {
            "Invalid" -> InvalidAuthentication
            "Forbid" -> Forbidden
            else -> SuccessAuthentication
        }
}

fun securityHandler(authentication: AuthProvider): (HttpHandler) -> HttpHandler = { next ->
    HttpHandler { exchange ->
        when (authentication.checkAccess(exchange.requestHeaders.getFirst(Headers.AUTHORIZATION))) {
            is InvalidAuthentication -> ResponseCodeHandler(401).handleRequest(exchange)
            is Forbidden -> ResponseCodeHandler(403).handleRequest(exchange)
            is SuccessAuthentication -> next.handleRequest(exchange)
        }
    }
}

val security =
    securityHandler(authProvider)

val handler = HttpHandler { exchange ->
    exchange.responseHeaders.add(Headers.CONTENT_TYPE, "text/plain")
    exchange.responseSender.send(
        (
                listOf(
                    "Host : ${exchange.hostAndPort}",
                    "Scheme : ${exchange.requestScheme}",
                    "${exchange.requestMethod} ${exchange.requestPath}",
                    "Relative path : ${exchange.relativePath}",
                    "Query string : ${exchange.queryString}"
                ) + exchange.requestHeaders.map { headerValues ->
                    "${headerValues.headerName}: ${headerValues.joinToString(
                        " "
                    )}"
                })
            .joinToString("\n"))
}

fun main() {

    val reverse = Undertow.builder()
        .addHttpListener(8080, "0.0.0.0")
        .setIoThreads(4)
        .setHandler(security(handler))
        .build()
    reverse.start()
}