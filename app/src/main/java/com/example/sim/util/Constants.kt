package com.example.sim.util

class Constants {
    companion object{
        const val TAG = "DEBUG"

        const val BASE_URL = "https://www.simcompanies.com/api/"
        const val BASE_IMAGE_URL = "https://d1fxy698ilbz6u.cloudfront.net/static/"

        const val NETWORK_TIMEOUT = 60000L
        const val CACHE_TIMEOUT = 2000L

        const val TRANSPORT_COST = 0.38F

        const val SORT_PROFIT = "SORT_PROFIT"
        const val SORT_COST = "SORT_COST"
        const val SORT_QUALITY = "SORT_QUALITY"
        const val SORT_ORDERS = "SORT_ORDERS"

        const val PROFIT_TYPE = 2000
        const val BUTTON_SCAN_TYPE = 2001
        const val NOT_FOUND_TYPE = 2002
    }
}