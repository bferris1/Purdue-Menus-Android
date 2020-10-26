package com.moufee.purduemenus.api.models

import org.joda.time.LocalDate

data class ApiDiningCourtMenu(
        val Location: String,
        val Date: LocalDate,
        val Notes: String?,
        val Meals: List<ApiMeal>
)