package com.example.sim.api.resource.responses

import android.os.Parcelable
import com.example.sim.models.Resource
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
    val soldAt: SoldAt,
    val producedAt: ProducedAt,
    val neededFor: List<ResourceResponse>,
    val retailData: List<RetailData>,
    val improvesQualityOf: List<ResourceResponse>,
) : Parcelable {

    private val resourceImageUrl get() = "$BASE_IMAGE_URL$image"

    @Parcelize
    data class ProducedFrom(
        val resourceResponse: ResourceResponse,
        val amount: Float
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

    fun toResource(): Resource {
        return Resource(
            db_letter = db_letter,
            name = name,
            image = resourceImageUrl,
            transportation = transportation,
            retailable = retailable,
            research = research,
            producedFrom = producedFrom,
            soldAt = soldAt,
            producedAt = producedAt,
            neededFor = neededFor,
            retailData = retailData,
            improvesQualityOf = improvesQualityOf
        )
    }
}