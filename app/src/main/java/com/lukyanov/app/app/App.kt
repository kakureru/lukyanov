package com.lukyanov.app.app

import android.app.Application
import com.lukyanov.app.BuildConfig
import com.lukyanov.app.app.di.appModule
import com.lukyanov.app.component.auth.di.authComponentModule
import com.lukyanov.app.component.films.di.filmsComponentModule
import com.lukyanov.app.features.film_details.di.filmDetailsFeatureModule
import com.lukyanov.app.features.films.di.filmsFeatureModule
import com.lukyanov.app.libs.retrofitModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(Level.DEBUG)
            modules(
                appModule,
                retrofitModule(BuildConfig.BASE_URL),
                authComponentModule,
                filmsComponentModule,
                filmsFeatureModule,
                filmDetailsFeatureModule,
            )
        }
    }
}