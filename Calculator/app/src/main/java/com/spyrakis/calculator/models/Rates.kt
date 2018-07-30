package com.spyrakis.calculator.models

import com.google.gson.annotations.SerializedName

data class Rates(

        @field:SerializedName("JPY")
        val jPY: Double? = null,

        @field:SerializedName("GBP")
        val gBP: Double? = null,

        @field:SerializedName("USD")
        val uSD: Double? = null
)