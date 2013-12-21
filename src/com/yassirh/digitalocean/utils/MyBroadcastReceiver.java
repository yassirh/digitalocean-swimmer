package com.yassirh.digitalocean.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yassirh.digitalocean.service.DomainService;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.ImageService;
import com.yassirh.digitalocean.service.RegionService;

public class MyBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ImageService imageService = new ImageService(context);
		imageService.getAllImagesFromAPI(false);
		
		DomainService domainService = new DomainService(context);
		domainService.getAllDomainsFromAPI(false);
		
		DropletService dropletService = new DropletService(context);
		dropletService.getAllDropletsFromAPI(false);
		
		RegionService regionService = new RegionService(context);
		regionService.getAllRegionsFromAPI(false);		
	}
	
}
