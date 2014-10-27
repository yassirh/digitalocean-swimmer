package com.yassirh.digitalocean.utils;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.service.AccountService;
import com.yassirh.digitalocean.service.DomainService;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.ImageService;
import com.yassirh.digitalocean.service.RegionService;
import com.yassirh.digitalocean.service.SSHKeyService;
import com.yassirh.digitalocean.service.SizeService;

public class MyBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Account currentAccount = ApiHelper.getCurrentAccount(context);
		if(currentAccount == null){
			return;
		}
		if(!ApiHelper.isValidToken(currentAccount.getToken())){
			return;
		}
		
		ImageService imageService = new ImageService(context);
		imageService.getAllImagesFromAPI(false);
		
		DomainService domainService = new DomainService(context);
		domainService.getAllDomainsFromAPI(false);
		
		DropletService dropletService = new DropletService(context);
		dropletService.getAllDropletsFromAPI(false, true);
		
		RegionService regionService = new RegionService(context);
		regionService.getAllRegionsFromAPI(false);		
		
		SSHKeyService sshKeyService = new SSHKeyService(context);
		sshKeyService.getAllSSHKeysFromAPI(false);
		
		SizeService sizeService = new SizeService(context);
		sizeService.getAllSizesFromAPI(false);

		// 3 days until access token expires 
		if(currentAccount.getExpiresIn().getTime() - new Date().getTime() < 259200000){
			AccountService accountService = new AccountService(context);
			accountService.getNewToken();
		}
	}
	
}
