package com.example.sim.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sim.api.resource.responses.ResourceDetailResponse
import com.example.sim.api.resource.responses.ResourceDetailResponse.*
import com.example.sim.api.resource.responses.ResourceResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "resource_table")
class Resource(
    @PrimaryKey(autoGenerate = false)
    val db_letter: Int,
    val name: String,
    val image: String,
    val transportation: Float,
    val retailable: Boolean,
    val research: Boolean,
    val producedFrom: List<ProducedFrom>,
    val soldAt: List<SoldAt>,
    val producedAt: ProducedAt,
    val neededFor: List<ResourceResponse>,
    val retailData: List<RetailData>,
    val improvesQualityOf: List<ResourceResponse>
) : Parcelable {

}