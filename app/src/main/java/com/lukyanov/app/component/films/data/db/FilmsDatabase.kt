package com.lukyanov.app.component.films.data.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lukyanov.app.component.films.data.db.dao.FavouriteFilmsDao
import com.lukyanov.app.component.films.data.db.dao.FilmsDao
import com.lukyanov.app.component.films.data.db.model.FavouriteFilmEntity
import com.lukyanov.app.component.films.data.db.model.FilmEntity

@Database(
    entities = [
        FavouriteFilmEntity::class,
        FilmEntity::class
    ],
    version = 1
)
abstract class FilmsDatabase : RoomDatabase() {
    abstract fun favouriteFilmsDao(): FavouriteFilmsDao
    abstract fun filmsDao(): FilmsDao

    companion object {
        private var INSTANCE: FilmsDatabase? = null
        private val LOCK = Any()
        private const val DB_NAME = "films_database"

        fun getInstance(application: Application): FilmsDatabase {
            INSTANCE?.let { return it }
            synchronized(LOCK) {
                INSTANCE?.let { return it }
                val db = Room.databaseBuilder(application, FilmsDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = db
                return db
            }
        }
    }
}