package com.moufee.purduemenus.binding;

import android.view.View;

import androidx.databinding.BindingAdapter;

/**
 * Data Binding Adapters
 */

public class BindingAdapters {
    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("selected")
    public static void select(View view, boolean isSelected) {
        view.setSelected(isSelected);
    }
}
