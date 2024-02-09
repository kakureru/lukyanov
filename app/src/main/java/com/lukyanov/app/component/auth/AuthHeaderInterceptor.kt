package com.lukyanov.app.component.auth

import okhttp3.Interceptor
import okhttp3.Response

internal class AuthHeaderInterceptor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain
            .request()
            .newBuilder()

        builder.addHeader("x-api-key", tokenProvider.provideToken())
        return chain.proceed(builder.build())
    }
}