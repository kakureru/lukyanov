package com.lukyanov.app.component.auth.di

import com.lukyanov.app.component.auth.AuthHeaderInterceptor
import com.lukyanov.app.component.auth.TokenProvider
import com.lukyanov.app.component.auth.TokenProviderImpl
import okhttp3.Interceptor
import org.koin.dsl.bind
import org.koin.dsl.module

val authComponentModule = module {

    factory {
        AuthHeaderInterceptor(tokenProvider = get())
    } bind (Interceptor::class)

    single<TokenProvider> {
        TokenProviderImpl()
    }
}