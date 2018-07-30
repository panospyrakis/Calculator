package com.spyrakis.calculator.interfaces

import com.spyrakis.calculator.BuildConfig
import com.spyrakis.calculator.models.RatesResponse
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
Created by pspyrakis on 30/7/18.
 */
interface CalculatorService {
    /**
     * get news list by page
     */
    @GET("latest")
    fun getRates(@Query("access_key") key: String = BuildConfig.FIXER_API_KEY, @Query("base") base: String = "EUR",@Query ("symbols") symbols:String="USD,GBP,JPY" ): Observable<RatesResponse>

    companion object {
        fun create(baseUrl: String): CalculatorService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            val retrofit = Retrofit.Builder()
                    .client(client)
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create())
                    .baseUrl("$baseUrl/")
                    .build()

            return retrofit.create(CalculatorService::class.java)
        }
    }
}