package com.yassirh.digitalocean.utils;

import java.util.Locale;

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
	
	public static Locale getLocal(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String localPreference = sharedPreferences.getString("pref_locale", "");
		if(!"".equals(localPreference)){
			return new Locale(localPreference);
		}
		return Locale.getDefault();
	}
}
