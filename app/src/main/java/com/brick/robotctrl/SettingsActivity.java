package com.brick.robotctrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.List;


public class SettingsActivity extends AppCompatPreferenceActivity{
    private static final String TAG = "SettingsActivity";

    //  UserTimer userTimer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"--------1");
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        setupActionBar();
        Log.d(TAG,"--------2");
        BaseActivity.clearTimerCount();
    }

    // display the return button in action bar
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        Log.d(TAG,"--------3");
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
//        loadHeadersFromResource(R.xml.pref_headers, target);
        //NOTE1 用简单的GeneralPreferenceFragment配置界面 代替 默认的Headers 设置列表
        Log.d(TAG,"--------4");
        getFragmentManager().beginTransaction().
                add(android.R.id.content, new GeneralPreferenceFragment()).commit();
        Log.i(TAG, "onBuildHeaders");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // menu context
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {

        private static Preference  developerPreference;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.d(TAG,"--------5");
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            setHasOptionsMenu(true);
            Log.d(TAG,"--------6");
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.serverIp)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.serverPort)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.robotName)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.serialCOM)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.serialBaud)));
            bindPreferenceSummaryToValue(findPreference("developerKey"));
            developerPreference =  findPreference("developerKey");

        }


        //----------------------it's not easy!
        private static void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
            preference.setOnPreferenceClickListener(clickListener);
            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
       private static Preference.OnPreferenceClickListener clickListener = new Preference.OnPreferenceClickListener() {
           long[] mHits = new long[5];
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Log.d(TAG,"onPreferenceClick:点击有效");
                System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
                mHits[mHits.length-1] = SystemClock.uptimeMillis();
                //Log.d(TAG, "onPreferenceClick:mHits" + mHits[4]+ ","+mHits[3]+"," + mHits[2]+"," + mHits[1]+"," + mHits[0]);
                if (mHits[0] >= (SystemClock.uptimeMillis()-3000)) {
                    //Log.d(TAG,"onPreferenceClick:进入");
                    if (preference.getKey().equals("robotIDKey")) {
                        developerPreference.setEnabled(true);
                    }
                }
                return true;
            }
        };

        private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                String stringValue = value.toString();

                BaseActivity.clearTimerCount();

                if (preference instanceof ListPreference) {
                    // For list preferences, look up the correct display value in
                    // the preference's 'entries' list.
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);

                    // Set the summary to reflect the new value.
                    preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

                } else {
                    // For all other preferences, set the summary to the value's
                    // simple string representation.
                    preference.setSummary(stringValue);
                }
                return true;
            }
        };

        };

    // relative ssdb
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data", true);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    public static void activityStart(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onRestart() {
        View decorView = getWindow().getDecorView();
//        Hide both the navigation bar and the status bar.
//        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//        a general rule, you should design your app to hide the status bar whenever you
//        hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);

        super.onRestart();
    }
}
