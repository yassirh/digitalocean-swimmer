package com.yassirh.digitalocean.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.content.Context;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Region;

public class CachedRegionsHelper {
	
	private static HashMap<Long,Region> sCachedRegions = new HashMap<Long, Region>();
	
	public static Region getCachedImage(Context context, Long id){
		if(sCachedRegions.size() == 0){
			// load the cached regions
			InputStream is = context.getResources().openRawResource(R.raw.regionscache);
	        BufferedReader br = new BufferedReader(new InputStreamReader(is));
	        String readLine = "";
	        try {
	            while ((readLine = br.readLine()) != null) {
	            	if(!readLine.startsWith("#")){
	            		String[] regionLine = readLine.split("\\:");
	            		Long regionId = Long.parseLong(regionLine[0]);
	            		Region region = new Region(regionId, regionLine[1], regionLine[2]);
	            		sCachedRegions.put(regionId, region);
	            	}
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		return sCachedRegions.get(id);
	}
	
	
}
