package com.example.sim.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sim.api.resource.responses.ResourceResponse
import com.example.sim.models.Resource

@Dao
interface ResourceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resource: Resource)

    //update values

    @Query("SELECT * FROM resource_table")
    suspend fun getAll(): List<Resource>

    @Query("SELECT * FROM resource_table WHERE db_letter = :id")
    suspend fun getById(id: Int): Resource

}