package com.moufee.purduemenus.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moufee.purduemenus.R
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class CustomOrderActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    internal lateinit var mDispatchingAndroidInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>


    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> {
        return mDispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CustomOrderFragment.newInstance())
                .commit()
    }
}
