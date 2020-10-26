package com.moufee.purduemenus.binding

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Data Binding Adapters
 */
object BindingAdapters {
    @JvmStatic
    @BindingAdapter("visibleIfTrue")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("selected")
    fun select(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
    }
}