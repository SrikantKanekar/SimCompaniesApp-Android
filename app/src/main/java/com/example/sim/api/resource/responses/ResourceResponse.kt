package com.example.sim.api.resource.responses

import android.os.Parcelable
import com.example.sim.models.Resource
import com.example.sim.models.Resource.*
import com.example.sim.util.Constants.Companion.BASE_IMAGE_URL
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResourceResponse(
    val db_letter: Int,
    val name: String,
    val image: String,
    val transportation: Float,
    val retailable: Boolean,
    val research: Boolean
) : Parcelable {

    private val resourceImageUrl get() = "$BASE_IMAGE_URL$image"

    fun toResource(): Resource {
        return Resource(
            db_letter = db_letter,
            name = name,
            image = resourceImageUrl,
            transportation = transportation,
            retailable = retailable,
            research = research,
        )
    }
}