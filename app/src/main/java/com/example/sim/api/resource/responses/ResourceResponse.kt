package com.example.sim.api.resource.responses

import android.os.Parcelable
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
    val resourceImageUrl get() = "https://d1fxy698ilbz6u.cloudfront.net/static/$image"
}