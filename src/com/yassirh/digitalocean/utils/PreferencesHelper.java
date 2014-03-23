package com.yassirh.digitalocean.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesHelper {
	public static int getSynchronizationInterval(Context context){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String val = sharedPreferences.getString("pref_sync_frequency", "");
		if(val.equals("")){
			val = "60";
			sharedPreferences.edit().putString("pref_sync_frequency", val);
		}
		return Integer.parseInt(val);
	}
}
