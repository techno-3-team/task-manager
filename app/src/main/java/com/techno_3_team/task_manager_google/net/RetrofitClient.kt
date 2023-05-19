package com.techno_3_team.task_manager_google.net

import com.google.gson.Gson
import com.techno_3_team.task_manager_google.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(gson: Gson): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.ENDPOINT)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }

    fun provideOkHttpClient() =
        OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .build()
}