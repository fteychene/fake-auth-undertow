# Fake auth app using undertow
 
:warning: This application id a real poc do not try to use it in production, it is not designed for performance and stability :warning:

## Security 

```kotlin
fun securityHandler(authentication: AuthProvider): (HttpHandler) -> HttpHandler = { next ->
    HttpHandler { exchange ->
        val test = exchange.requestHeaders.get(Headers.AUTHORIZATION)
        when (authentication.checkAccess(test[0])) {
            is InvalidAuthentication -> ResponseCodeHandler(401).handleRequest(exchange)
            is Forbidden -> ResponseCodeHandler(403).handleRequest(exchange)
            is SuccessAuthentication -> next.handleRequest(exchange)
        }
    }
}
```