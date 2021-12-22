package com.moufee.purduemenus.api.models

data class ApiMenuItem(val ID: String, val Name: String, val IsVegetarian: Boolean, val Allergens: List<ApiAllergen>?)