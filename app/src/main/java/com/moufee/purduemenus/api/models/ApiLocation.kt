package com.moufee.purduemenus.api.models

data class ApiLocation(
        val LocationId: String,
        val Name: String,
        val FormalName: String,
        val Type: String?, // todo: enum?
        val LogoUrl: String?
)