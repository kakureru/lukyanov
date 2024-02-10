package com.lukyanov.app.component.films.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lukyanov.app.component.films.data.db.model.FilmEntity

@Dao
interface FilmsDao {

    @Query("SELECT * FROM films")
    suspend fun getFilms(): List<FilmEntity>

    @Transaction
    suspend fun refreshFilms(films: List<FilmEntity>) {
        clearData()
        saveFilms(films = films)
    }

    @Query("DELETE FROM films")
    suspend fun clearData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFilms(films: List<FilmEntity>)
}