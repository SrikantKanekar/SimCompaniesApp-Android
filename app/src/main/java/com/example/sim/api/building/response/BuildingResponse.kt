package com.example.sim.api.building.response

import android.os.Parcelable
import androidx.room.Entity
import com.example.sim.api.resource.responses.ResourceResponse
import com.example.sim.util.Constants.Companion.BASE_IMAGE_URL
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BuildingResponse(
    val name: String,
    val image: String,
    val cost: Int,
    val costUnits: Int,
    val steel: Int,
    val wages: Float,
    val secondsToBuild: Int,
    val category: String,
    val kind: String,
    val production: List<Production>?,
    val retail: List<Retail>?
) : Parcelable {

    val buildingImageUrl get() = "$BASE_IMAGE_URL$image"

    @Parcelize
    data class Production(
        val resourceResponse: ResourceResponse,
        val anHour: Float
    ) : Parcelable

    @Parcelize
    data class Retail(
        val resourceResponse: ResourceResponse,
        val averagePrice: Float,
        val saturation: Float,
        val retailModeling: String
    ) : Parcelable
}