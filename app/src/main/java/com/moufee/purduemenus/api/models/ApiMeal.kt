package com.moufee.purduemenus.api.models

data class ApiMeal(
        val ID: String,
        val Name: String,
        val Order: Int,
        val Status: String,
        val Type: String,
        val Hours: ApiHours?,
        val Stations: List<ApiStation>
)