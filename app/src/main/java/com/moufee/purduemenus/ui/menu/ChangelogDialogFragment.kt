package com.moufee.purduemenus.ui.menu

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.moufee.purduemenus.BuildConfig
import com.moufee.purduemenus.R

class ChangelogDialogFragment : androidx.fragment.app.DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
                .setTitle(getString(R.string.changelog_title, BuildConfig.VERSION_NAME))
                .setPositiveButton("OK", null)
                .setMessage(R.string.changelog)

        return builder.create()
    }
}