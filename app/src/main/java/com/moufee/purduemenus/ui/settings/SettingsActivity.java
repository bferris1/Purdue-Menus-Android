package com.moufee.purduemenus.ui.settings;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.moufee.purduemenus.util.SingleFragmentActivity;

/**
 * Created by Ben on 09/08/2017.
 */

public class SettingsActivity extends SingleFragmentActivity {
    public static final String KEY_PREF_SHOW_SERVING_TIMES = "show_serving_times";
    public static final String KEY_PREF_USE_NIGHT_MODE = "use_night_mode";

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
