package com.example.sim.api.resource.responses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResourceDetailResponse(
    val name: String,
    val image: String,
    val db_letter: Int,
    val transportation: Float,
    val retailable: Boolean,
    val research: Boolean,
    val producedFrom: List<ProducedFrom>,
    val soldAt: List<SoldAt>,
    val producedAt: ProducedAt,
    val neededFor: List<ResourceResponse>,
    val retailData: List<RetailData>,
    val improvesQualityOf: List<ResourceResponse>,
    val message: String
) : Parcelable {
    val resourceImageUrl get() = "https://d1fxy698ilbz6u.cloudfront.net/static/$image"

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