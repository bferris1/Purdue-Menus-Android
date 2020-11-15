package com.moufee.purduemenus.util

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.moufee.purduemenus.R

/**
 * Created by Ben on 31/05/2017.
 * An abstract activity that hosts a single fragment
 */
abstract class SingleFragmentActivity : AppCompatActivity() {
    protected abstract fun createFragment(): Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            fragment = createFragment()
            fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
        }
    }
}