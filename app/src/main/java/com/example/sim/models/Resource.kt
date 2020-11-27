package com.example.sim.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sim.api.resource.responses.ResourceDetailResponse.*
import com.example.sim.api.resource.responses.ResourceResponse
import com.example.sim.util.Constants
import com.example.sim.util.Constants.Companion.BASE_IMAGE_URL
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
    val producedFrom: List<ProducedFrom>? = null,
    val soldAt: SoldAt? = null,
    val producedAt: ProducedAt? = null,
    val neededFor: List<ResourceResponse>? = null,
    val retailData: List<RetailData>? = null,
    val improvesQualityOf: List<ResourceResponse>? = null,
    val tracking: Boolean = false
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    companion object{
        fun dummyResource(): Resource {
            return Resource(
                db_letter = 0,
                name = "",
                image = "",
                transportation = 0F,
                retailable = false,
                research = false
            )
        }
    }
}