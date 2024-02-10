package com.lukyanov.app.component.films.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lukyanov.app.component.films.data.db.model.FavouriteFilmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteFilmsDao {

    @Query("SELECT * FROM favourite_films")
    fun getAll(): Flow<List<FavouriteFilmEntity>>

    @Query("SELECT id FROM favourite_films")
    suspend fun getAllIds(): List<String>

    @Query("SELECT id FROM favourite_films")
    fun getAllIdsFlow(): Flow<List<String>>

    @Query("SELECT * FROM favourite_films WHERE id = :filmId")
    suspend fun getFilm(filmId: String): FavouriteFilmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFilm(favouriteFilmEntity: FavouriteFilmEntity)

    @Delete
    suspend fun deleteFilm(favouriteFilmEntity: FavouriteFilmEntity)
}