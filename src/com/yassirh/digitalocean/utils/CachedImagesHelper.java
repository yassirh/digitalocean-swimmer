package com.yassirh.digitalocean.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.content.Context;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.model.Image;

public class CachedImagesHelper {
	
	private static HashMap<Long,Image> sCachedImages = new HashMap<Long, Image>();
	
	public static Image getCachedImage(Context context, Long id){
		if(sCachedImages.size() == 0){
			// load the cached images
			InputStream is = context.getResources().openRawResource(R.raw.imagescache);
	        BufferedReader br = new BufferedReader(new InputStreamReader(is));
	        String readLine = "";
	        try {
	            while ((readLine = br.readLine()) != null) {
	            	if(!readLine.startsWith("#")){
	            		String[] imageLine = readLine.split("\\:");
	            		Long imageId = Long.parseLong(imageLine[0]);
	            		Image image = new Image(imageId, imageLine[1], imageLine[3], imageLine[2], true);
	            		sCachedImages.put(imageId, image);
	            	}
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		return sCachedImages.get(id);
	}
	
	
}
