package com.example.sim.persistence

import android.util.Log
import androidx.room.*
import com.example.sim.models.Resource

private const val TAG = "DEBUG"

@Dao
interface ResourceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(resource: Resource)

    @Update
    suspend fun update(resource: Resource)

    @Query("SELECT * FROM resource_table")
    suspend fun getAll(): List<Resource>

    @Query("SELECT * FROM resource_table WHERE db_letter = :id")
    suspend fun getById(id: Int): Resource?

    @Query("UPDATE resource_table SET tracking = :tracking  WHERE db_letter = :id ")
    suspend fun updateTracking(id: Int, tracking: Boolean)

    suspend fun insertOrUpdate(resource: Resource) {
        val currentResource = getById(resource.db_letter)
        if (currentResource == null) {
            insert(resource)
            Log.d(TAG, "Inserted $resource")
        } else {
            update(resource)
            Log.d(TAG, "Updated $resource")
        }
    }
}