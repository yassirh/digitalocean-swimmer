package com.yassirh.digitalocean.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yassirh.digitalocean.R;

public class ApiHelper {
	
	public static final String API_STATUS_OK = "OK";
	public static final String API_STATUS_ERROR = "ERROR";
	
	public static String getAPIKey(Context context){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getString("api_key_preference", "");
	}
	
	public static String getClientId(Context context){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getString("client_id_preference", "");
	}
	
	public static int getDistributionLogo(String distribution,String status){
		if(distribution.equals("Ubuntu")){
        	return status.equals("active") ? R.drawable.ubuntu_active : R.drawable.ubuntu;
        }
        else if(distribution.equals("Debian")){
        	return status.equals("active") ? R.drawable.debian_active : R.drawable.debian;
        }
        else if(distribution.equals("CentOS")){
        	return status.equals("active") ? R.drawable.centos_active : R.drawable.centos;
        }
        else if(distribution.equals("Fedora")){
        	return status.equals("active") ? R.drawable.fedora_active : R.drawable.fedora;
        }
        else if(distribution.equals("Arch Linux")){
        	return status.equals("active") ? R.drawable.arch_linux_active : R.drawable.arch_linux;
        }		
		return R.drawable.unknown;
	}
	
	public static int getLocationFlag(String region){
		if(region.contains("Amsterdam"))
			return R.drawable.nl_flag;
		else if(region.contains("New York") || region.contains("San Francisco"))
			return R.drawable.us_flag;
		else if(region.contains("Singapore"))
			return R.drawable.sg_flag;
		else
			return R.drawable.unknown;
	}
	
	public static int getRecordLabel(String recordType){
		if(recordType.equals("A")){
        	return R.drawable.a;
        }
		else if(recordType.equals("CNAME")){
        	return R.drawable.cname;
        }
		else if(recordType.equals("MX")){
        	return R.drawable.mx;
        }
		else if(recordType.equals("TXT")){
        	return R.drawable.txt;
        }
		else if(recordType.equals("SRV")){
        	return R.drawable.srv;
        }
		else if(recordType.equals("NS")){
        	return R.drawable.ns;
        }
		return 0;
	}
}
