package com.yassirh.digitalocean.utils;

import java.util.Date;
import java.util.Random;

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
	
	public static boolean shouldDisplayAnAd(Context context){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String val = sharedPreferences.getString("pref_ads_frequency", "3");
		int frequency = Integer.parseInt(val);
		if(frequency == 0)
			return false;
		else{
			return new Random(new Date().getTime()).nextDouble() <= (1./frequency);
		}
	}
}
