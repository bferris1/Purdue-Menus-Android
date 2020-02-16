package com.moufee.purduemenus.repository.data

data class MenuItem(val ID: String, val Name: String, val IsVegetarian: Boolean, val Allergens: List<ApiAllergen>?)