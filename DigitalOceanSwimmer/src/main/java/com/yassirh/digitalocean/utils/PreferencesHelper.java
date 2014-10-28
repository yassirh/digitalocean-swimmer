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
			sharedPreferences.edit().putString("pref_sync_frequency", val).commit();
		}
		return Integer.parseInt(val);
	}
	
	public static Locale getLocal(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String localPreference = sharedPreferences.getString("pref_locale", "");
		if(!"".equals(localPreference)){
			if(localPreference.equals("zh_CN")){
			    return Locale.SIMPLIFIED_CHINESE;
			}
			else if(localPreference.equals("zh_TW")){
			    return Locale.TRADITIONAL_CHINESE;
			}
			return new Locale(localPreference);
		}
		return Locale.getDefault();
	}

    public static boolean isAutoRestartingDropetsEnabled(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("pref_auto_start_droplets",false);
    }

    public static void clearAll(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
