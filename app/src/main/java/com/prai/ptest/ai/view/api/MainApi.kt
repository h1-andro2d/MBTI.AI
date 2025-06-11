package com.prai.ptest.ai.view.api

import android.util.Log
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


interface MainApi {
    @POST("/analysis")
    fun analyzePersonality(@Body request: PersonalityRequest): Call<AnalysisResponse>
}

data class PersonalityRequest(
    val mbti_type: String,
    val traits: Map<String, Int>,
    val language: String = "ko"
)

data class AnalysisResponse(
    val mbti_type: String,
    val analysis: String,
    val language: String,
    val cached: Boolean
)

object ApiClient {
    private const val BASE_URL = "https://4uvtr6xaqk.execute-api.ap-northeast-2.amazonaws.com"
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃
        .readTimeout(30, TimeUnit.SECONDS) // 읽기 타임아웃
        .writeTimeout(30, TimeUnit.SECONDS) // 쓰기 타임아웃
        .retryOnConnectionFailure(true)
        .build()

    val retrofit: Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val analysisResponse = MutableStateFlow<AnalysisResponse?>(null)

    fun init() {
        Log.d("MainAPI", "ApiClient is initialized")
    }

    fun updateRequest(request: PersonalityRequest) {
        val apiService = retrofit.create(MainApi::class.java)
        val call = apiService.analyzePersonality(request)
        Log.d("MainAPI", "request:  ${request.mbti_type}, ${request.traits}, ${request.language}")
        call.enqueue(object : Callback<AnalysisResponse> {
            override fun onResponse(
                call: Call<AnalysisResponse>,
                response: Response<AnalysisResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    analysisResponse.value = response.body()
                    Log.d("MainAPI", "MainAPI : Analysis: ${response.body()!!.analysis}")
                } else {
                    analysisResponse.value = null
                    Log.d("MainAPI", "MainAPI: Request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AnalysisResponse>, t: Throwable) {
                analysisResponse.value = null
                Log.d("MainAPI", "MainAPI: Request failed: ${t.message} ${t.printStackTrace()}")
            }
        })
    }
}