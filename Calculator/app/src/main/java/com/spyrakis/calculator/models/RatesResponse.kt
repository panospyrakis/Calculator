package com.spyrakis.calculator.models

import com.google.gson.annotations.SerializedName

data class RatesResponse(

        @field:SerializedName("date")
        val date: String? = null,

        @field:SerializedName("success")
        val success: Boolean? = null,

        @field:SerializedName("rates")
        val rates: Rates? = null,

        @field:SerializedName("timestamp")
        val timestamp: Int? = null,

        @field:SerializedName("base")
        val base: String? = null
)