package com.lukyanov.app.component.films.di

import com.lukyanov.app.component.films.FilmsRepo
import com.lukyanov.app.component.films.data.FilmsRepoImpl
import com.lukyanov.app.component.films.data.db.FilmsDatabase
import com.lukyanov.app.component.films.data.network.FilmsApi
import com.lukyanov.app.component.films.usecase.GetPopularFilmsUseCase
import org.koin.dsl.module
import retrofit2.Retrofit

val filmsComponentModule = module {

    single<FilmsRepo> {
        FilmsRepoImpl(
            filmsApi = get(),
            favouriteFilmsDao = get(),
            filmsDao = get(),
        )
    }

    single<FilmsApi> {
        val retrofit: Retrofit = get()
        retrofit.create(FilmsApi::class.java)
    }

    single {
        FilmsDatabase.getInstance(application = get()).favouriteFilmsDao()
    }

    single {
        FilmsDatabase.getInstance(application = get()).filmsDao()
    }

    factory {
        GetPopularFilmsUseCase(
            filmsRepo = get(),
        )
    }
}