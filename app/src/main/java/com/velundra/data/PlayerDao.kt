package com.velundra.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlayerDao {
    @Insert
    suspend fun insert(player: PlayerEntity)

    @Query("SELECT * FROM players")
    fun getAllPlayers(): LiveData<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getPlayerById(id: Int): PlayerEntity?

    @Delete
    suspend fun delete(player: PlayerEntity)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

}
