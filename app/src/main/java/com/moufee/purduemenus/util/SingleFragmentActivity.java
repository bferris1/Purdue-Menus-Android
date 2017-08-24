package com.moufee.purduemenus.util;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;

import android.support.v7.app.AppCompatActivity;

import com.moufee.purduemenus.R;

/**
 * Created by Ben on 31/05/2017.
 * An abstract activity that hosts a single fragment
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

    }

}
