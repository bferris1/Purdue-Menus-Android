package com.moufee.purduemenus.repository.data

import org.joda.time.LocalDate

data class ApiDiningCourtMenu(
        val Location: String,
        val Date: LocalDate,
        val Notes: String?,
        val Meals: List<ApiMeal>
)