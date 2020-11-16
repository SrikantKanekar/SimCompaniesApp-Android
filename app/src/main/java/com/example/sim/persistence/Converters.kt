package com.example.sim.persistence

import androidx.room.TypeConverter
import com.example.sim.api.building.response.BuildingResponse.*
import com.example.sim.api.player.response.PlayerResponse
import com.example.sim.api.player.response.PlayerResponse.*
import com.example.sim.api.resource.responses.ResourceDetailResponse
import com.example.sim.api.resource.responses.ResourceDetailResponse.*
import com.example.sim.api.resource.responses.ResourceResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun productionToString(productionList: List<Production>): String {
        return Gson().toJson(productionList)
    }

    @TypeConverter
    fun stringToProduction(string: String): List<Production> {
        val productionList = object : TypeToken<List<Production>>(){}.type
        return Gson().fromJson(string, productionList)
    }

    @TypeConverter
    fun retailToString(retailList: List<Retail>): String {
        return Gson().toJson(retailList)
    }

    @TypeConverter
    fun stringToRetail(string: String): List<Retail> {
        val retailList = object : TypeToken<List<Retail>>(){}.type
        return Gson().fromJson(string, retailList)
    }

    @TypeConverter
    fun playerBuildingToString(playerBuildingList: List<PlayerBuilding>): String {
        return Gson().toJson(playerBuildingList)
    }

    @TypeConverter
    fun stringToPlayerBuilding(string: String): List<PlayerBuilding> {
        val playerBuildingList = object : TypeToken<List<PlayerBuilding>>(){}.type
        return Gson().fromJson(string, playerBuildingList)
    }

    @TypeConverter
    fun producedFromListToString(producedFromList: List<ProducedFrom>): String {
        return Gson().toJson(producedFromList)
    }

    @TypeConverter
    fun stringToProducedFrom(string: String): List<ProducedFrom> {
        val producedFromList = object : TypeToken<List<ProducedFrom>>(){}.type
        return Gson().fromJson(string, producedFromList)
    }

    @TypeConverter
    fun soldAtToString(soldAtList: List<SoldAt>): String {
        return Gson().toJson(soldAtList)
    }

    @TypeConverter
    fun stringToSoldAt(string: String): List<SoldAt> {
        val soldAtList = object : TypeToken<List<SoldAt>>(){}.type
        return Gson().fromJson(string, soldAtList)
    }

    @TypeConverter
    fun resourceResponseToString(resourceResponseList: List<ResourceResponse>): String {
        return Gson().toJson(resourceResponseList)
    }

    @TypeConverter
    fun stringToResourceResponse(string: String): List<ResourceResponse> {
        val resourceResponseList = object : TypeToken<List<ResourceResponse>>(){}.type
        return Gson().fromJson(string, resourceResponseList)
    }

    @TypeConverter
    fun retailDataToString(retailDataList: List<RetailData>): String {
        return Gson().toJson(retailDataList)
    }

    @TypeConverter
    fun stringToRetailData(string: String): List<RetailData> {
        val retailDataList = object : TypeToken<List<RetailData>>(){}.type
        return Gson().fromJson(string, retailDataList)
    }

    @TypeConverter
    fun producedAtToString(producedAt: ProducedAt): String {
        return Gson().toJson(producedAt)
    }

    @TypeConverter
    fun stringToProducedAt(string: String): ProducedAt {
        return Gson().fromJson(string, ProducedAt::class.java)
    }
}