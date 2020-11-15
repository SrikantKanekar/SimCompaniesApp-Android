package com.example.sim.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sim.api.resource.responses.ResourceResponse

@Database(entities = [ResourceResponse::class], version = 1)
abstract class AppDatabase : RoomDatabase(){

    companion object{
        const val DATABASE_NAME = "app_db"
    }
}