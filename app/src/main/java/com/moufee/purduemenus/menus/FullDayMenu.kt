package com.moufee.purduemenus.menus

import org.joda.time.DateTime

/**
 * Contains all the menus for all campus dining courts for one day
 */
data class FullDayMenu(val menus: Map<String, DiningCourtMenu>, val date: DateTime, val hasLateLunch: Boolean)