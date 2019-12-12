package com.fteychene.playground.undertowtests

interface AuthProvider {

    fun checkAccess(token: String?): AuthenticationStatus
}