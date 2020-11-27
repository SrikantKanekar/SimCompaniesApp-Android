package com.example.sim.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.example.sim.api.building.BuildingApiService
import com.example.sim.api.market.MarketApiService
import com.example.sim.api.resource.ResourceApiService
import com.example.sim.persistence.AppDatabase
import com.example.sim.persistence.AppDatabase.Companion.DATABASE_NAME
import com.example.sim.persistence.BuildingDao
import com.example.sim.persistence.PlayerDao
import com.example.sim.persistence.ResourceDao
import com.example.sim.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideResourceApi(retrofit: Retrofit): ResourceApiService =
        retrofit.create(ResourceApiService::class.java)

    @Provides
    @Singleton
    fun provideBuildingApi(retrofit: Retrofit): BuildingApiService =
        retrofit.create(BuildingApiService::class.java)

    @Provides
    @Singleton
    fun provideMarketApi(retrofit: Retrofit): MarketApiService =
        retrofit.create(MarketApiService::class.java)

    @Singleton
    @Provides
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideBuildingDao(appDatabase: AppDatabase): BuildingDao {
        return appDatabase.getBuildingDao()
    }

    @Singleton
    @Provides
    fun providePlayerDao(appDatabase: AppDatabase): PlayerDao {
        return appDatabase.getPlayerDao()
    }

    @Singleton
    @Provides
    fun provideResourceDao(appDatabase: AppDatabase): ResourceDao {
        return appDatabase.getResourceDao()
    }
}