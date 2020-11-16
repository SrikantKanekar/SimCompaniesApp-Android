package com.example.sim.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sim.api.player.response.PlayerResponse
import com.example.sim.models.Player
import com.example.sim.models.Resource

@Dao
interface PlayerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(player: Player)

    //update values

    @Query("SELECT * FROM player_table WHERE id = :id")
    suspend fun getById(id: Int): Player
}