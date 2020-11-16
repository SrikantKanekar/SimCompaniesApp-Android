package com.example.sim.api.resource.responses

import android.os.Parcelable
import androidx.room.Entity
import com.example.sim.util.Constants.Companion.BASE_IMAGE_URL
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResourceResponse(
    val name: String,
    val image: String,
    val db_letter: Int,
    val transportation: Float,
    val retailable: Boolean,
    val research: Boolean
) : Parcelable {

    val resourceImageUrl get() = "$BASE_IMAGE_URL$image"

}