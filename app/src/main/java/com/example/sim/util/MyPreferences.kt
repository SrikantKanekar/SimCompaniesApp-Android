package com.example.sim.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getMinimumProfit(): Int {
        return prefs.getString("min_profit_preferences", "5000")?.toInt()!!
    }

    fun getMaximumCost(): Int {
        return prefs.getString("max_cost_preferences", "1000000")?.toInt()!!
    }

    fun getMaximumQuality(): Int {
        return prefs.getInt("max_quality_preferences", 3)
    }

    fun getMaximumOrders(): Int {
        return prefs.getString("max_order_preferences", "3")?.toInt()!!
    }

    fun getProfitFilter(): String {
        return prefs.getString("profit_filter", Constants.SORT_PROFIT)!!
    }

    fun setProfitFilter(filter: String) {
        val editor = prefs.edit()
        editor.putString("profit_filter", filter)
        editor.apply()
    }
}