package com.example.sim.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sim.api.building.response.BuildingResponse
import com.example.sim.api.building.response.BuildingResponse.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "building_table")
class Building(
    @PrimaryKey(autoGenerate = false)
    val kind: String,
    val name: String,
    val image: String,
    val cost: Int,
    val costUnits: Int,
    val steel: Int,
    val wages: Float,
    val secondsToBuild: Int,
    val category: String,
    val production: List<Production>? = null,
    val retail: List<Retail>? = null
) : Parcelable{

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}