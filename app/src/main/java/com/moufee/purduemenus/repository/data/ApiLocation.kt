package com.moufee.purduemenus.repository.data

data class ApiLocation(
        val LocationId: String,
        val Name: String,
        val FormalName: String,
        val Type: String?, // todo: enum?
        val LogoUrl: String?
)