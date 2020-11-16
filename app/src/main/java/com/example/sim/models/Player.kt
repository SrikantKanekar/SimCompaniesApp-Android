package com.example.sim.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sim.api.player.response.PlayerResponse
import com.example.sim.api.player.response.PlayerResponse.*

@Entity(tableName = "player_table")
class Player (
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val company: String,
    val logo: String,
    val level: Int,
    val maxBuildings: Int,
    val rank: Int,
    val timezoneOffset: Int,
    val workers: Int,
    val administrationOverhead: Float,
    val productionModifier: Int,
    val salesModifier: Int,
    val recreationBonus: Int,
    val buildings: List<PlayerBuilding>,
    val message: String
)