package com.lukyanov.app.libs

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.lukyanov.app.common.util.request_result.RequestResultAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

fun retrofitModule(baseUrl: String) = module {

    factory {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    } bind (Interceptor::class)

    single<Json> {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single<OkHttpClient> {
        OkHttpClient()
    }

    single<Retrofit> {
        val sharedOkHttpClient = get<OkHttpClient>()
        val clientBuilder = sharedOkHttpClient.newBuilder()
        val interceptors: List<Interceptor> = getAll()
        interceptors.forEach { clientBuilder.addInterceptor(it) }

        val json: Json = get()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RequestResultAdapterFactory())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(clientBuilder.build())
            .build()
    }
}