package com.moufee.purduemenus.binding

import android.view.View
import androidx.databinding.BindingAdapter
import com.moufee.purduemenus.util.Resource

/**
 * Data Binding Adapters
 */
object BindingAdapters {
    @JvmStatic
    @BindingAdapter("visibleIfTrue")
    fun showHide(view: View, show: Boolean?) {
        view.visibility = if (show == true) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("selected")
    fun select(view: View, isSelected: Boolean?) {
        view.isSelected = isSelected == true
    }
}

@BindingAdapter("visibleIfLoading")
fun View.visibleIfLoading(resource: Resource<*>?) {
    visibility = if (resource is Resource.Loading) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleIfLoaded")
fun View.visibleIfSuccess(resource: Resource<*>?) {
    visibility = if (resource is Resource.Success) View.VISIBLE else View.GONE
}