package com.moufee.purduemenus.api.models

data class RemoteMenuItem(val ID: String, val Name: String, val IsVegetarian: Boolean, val Allergens: List<RemoteAllergen>?)