package com.moufee.purduemenus.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moufee.purduemenus.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CustomOrderFragment.newInstance())
                .commit()
    }
}
