package com.lukyanov.app.component.auth

interface TokenProvider {
    fun provideToken(): String
}