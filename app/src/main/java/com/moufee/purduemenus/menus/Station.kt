package com.moufee.purduemenus.menus

import androidx.annotation.Keep

@Keep
data class Station(val name: String, val items: List<MenuItem>)