package com.example.sim.api.resource.responses

import android.os.Parcelable
import androidx.room.Entity
import com.example.sim.util.Constants
import com.example.sim.util.Constants.Companion.BASE_IMAGE_URL
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResourceDetailResponse(
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
    val improvesQualityOf: List<ResourceResponse>,
) : Parcelable {

    val resourceImageUrl get() = "$BASE_IMAGE_URL$image"

    @Parcelize
    data class ProducedFrom(
        val resourceResponse: ResourceResponse,
        val amount: Int
    ) : Parcelable

    @Parcelize
    data class SoldAt(
        val db_letter: String
    ) : Parcelable

    @Parcelize
    data class ProducedAt(
        val db_letter: String,
    ) : Parcelable

    @Parcelize
    data class RetailData(
        val averagePrice: Float,
        val demand: Float,
        val date: String,
        val label: String
    ) : Parcelable
}