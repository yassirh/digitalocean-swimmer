package com.yassirh.digitalocean.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.service.AccountService;
import com.yassirh.digitalocean.utils.ApiHelper;
import com.yassirh.digitalocean.utils.MyApplication;
import com.yassirh.digitalocean.utils.PreferencesHelper;

import java.util.Locale;

public class PrefsFragment extends PreferenceFragment {

    Activity activity;

    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key) {
                    Account currentAccount = ApiHelper.getCurrentAccount(MyApplication.getAppContext());
                    if(currentAccount == null){
                        currentAccount = new Account();
                    }
                    // Clear all the previously stored data and get the new account data.
                    if(key.equals("token_preference") || key.equals("account_name_preference")){
                        Context context = MyApplication.getAppContext();
                        currentAccount.setToken(ApiHelper.getToken(context));
                        currentAccount.setName(ApiHelper.getAccountName(context));
                        if(currentAccount.getName().equals("")){
                            currentAccount.setName("default");
                        }
                        ApiHelper.selectAccount(context, currentAccount);
                    }
                    if(key.equals("pref_locale")){
                        Locale locale = PreferencesHelper.getLocal(activity);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        activity.getResources().updateConfiguration(config, null);
                        Intent intent = new Intent(activity,SettingsActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }
            };

    public PrefsFragment() {
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference clearDataPref = findPreference("pref_clear_data");
        clearDataPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AccountService(activity).clearData();
                                Intent i = activity.getPackageManager()
                                        .getLaunchIntentForPackage(activity.getPackageName());
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                .show();

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        super.onPause();
    }
}