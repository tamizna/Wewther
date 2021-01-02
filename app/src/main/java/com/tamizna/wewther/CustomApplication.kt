package com.tamizna.wewther

import android.app.Application
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CustomApplication : Application() {

    lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()

            retrofit = Retrofit.Builder().baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create()).build()
    }
}