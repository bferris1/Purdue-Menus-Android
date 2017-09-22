package com.moufee.purduemenus.ui.settings;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import com.moufee.purduemenus.util.SingleFragmentActivity;

/**
 * Created by Ben on 09/08/2017.
 */

public class SettingsActivity extends SingleFragmentActivity {
    public static final String KEY_PREF_SHOW_SERVING_TIMES = "show_serving_times";


    @Override
    public void recreate() {
        finish();
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        startActivity(getIntent());
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static Intent getIntent(Context packageContext){
        return new Intent(packageContext, SettingsActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return new SettingsFragment();
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
