package com.fteychene.playground.undertowtests

sealed class AuthenticationStatus
object SuccessAuthentication : AuthenticationStatus()
object InvalidAuthentication : AuthenticationStatus()
object Forbidden : AuthenticationStatus()