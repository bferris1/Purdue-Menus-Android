package com.moufee.purduemenus.ui.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.moufee.purduemenus.ui.theme.MenusTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomOrderActivity : AppCompatActivity() {
    private val viewModel: LocationSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MenusTheme {
                CustomOrderScreen(viewModel)
            }
        }
    }
}
