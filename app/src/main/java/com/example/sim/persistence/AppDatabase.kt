package com.example.sim.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sim.models.Building
import com.example.sim.models.Player
import com.example.sim.models.Resource
import com.example.sim.models.Resource.*

@Database(entities = [Building::class, Player::class, Resource::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getBuildingDao(): BuildingDao

    abstract fun getPlayerDao(): PlayerDao

    abstract fun getResourceDao(): ResourceDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}