package com.lukyanov.app.component.films.di

import com.lukyanov.app.component.films.FilmsRepo
import com.lukyanov.app.component.films.data.FilmsRepoImpl
import com.lukyanov.app.component.films.data.network.FilmsApi
import org.koin.dsl.module
import retrofit2.Retrofit

val filmsComponentModule = module {

    single<FilmsRepo> {
        FilmsRepoImpl(
            filmsApi = get(),
        )
    }

    single<FilmsApi> {
        val retrofit: Retrofit = get()
        retrofit.create(FilmsApi::class.java)
    }
}