package com.moufee.purduemenus.api.models

data class RemoteLocation(
        val LocationId: String,
        val Name: String,
        val FormalName: String,
        val Type: String?, // todo: enum?
        val LogoUrl: String?
)