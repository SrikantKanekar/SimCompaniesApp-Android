package com.example.sim.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sim.api.building.response.BuildingResponse
import com.example.sim.models.Building

@Dao
interface BuildingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(building: Building)

    @Query("SELECT * FROM building_table")
    suspend fun getAll(): List<Building>

    @Query("SELECT * FROM building_table WHERE kind = :kind")
    suspend fun getByKind(kind: String): Building
}