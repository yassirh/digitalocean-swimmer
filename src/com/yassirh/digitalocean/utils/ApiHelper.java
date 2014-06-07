package com.yassirh.digitalocean.utils;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.AccountDao;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.model.Account;
import com.yassirh.digitalocean.service.DomainService;
import com.yassirh.digitalocean.service.DropletService;
import com.yassirh.digitalocean.service.ImageService;
import com.yassirh.digitalocean.service.RegionService;
import com.yassirh.digitalocean.service.SSHKeyService;
import com.yassirh.digitalocean.service.SizeService;

public class ApiHelper {
	
	public static final String API_STATUS_OK = "OK";
	public static final String API_STATUS_ERROR = "ERROR";
	
	
	public static Account getCurrentAccount(Context context){
		Account currentAccount = null;
		AccountDao accountDao = new AccountDao(DatabaseHelper.getInstance(context));
		List<Account> accounts = accountDao.getAll(null);
		// used to get the config from the shared preferences
		if(accounts.size() == 0){
			currentAccount = new Account();
			String apiKey = getAPIKey(context);
			String clientId = getClientId(context);
			String accountName = getAccountName(context);
			if("".equals(accountName)){
				accountName = "default";
			}
			if(isValidApiKeyOrClientId(apiKey) && isValidApiKeyOrClientId(clientId)){
				currentAccount.setId(1L);
				currentAccount.setName(accountName);
				currentAccount.setApiKey(apiKey);
				currentAccount.setClientId(clientId);
				currentAccount.setSelected(true);
				accountDao.createOrUpdate(currentAccount);
			}
		}else{
			for (Account account : accounts) {
				if(account.isSelected()){
					currentAccount = account;
					break;
				}
			}
			if(currentAccount != null && !getAccountName(context).equals(currentAccount.getName())){
				currentAccount.setName(getAccountName(context));
				accountDao.createOrUpdate(currentAccount);
			}
		}		
		
		return currentAccount;
	}

	public static List<Account> getAllAccounts(Context context){
		AccountDao accountDao = new AccountDao(DatabaseHelper.getInstance(context));
		return accountDao.getAll(null);
	}
	
	public static String getAccountName(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getString("account_name_preference", "");
	}
	
	public static String getAPIKey(Context context){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getString("api_key_preference", "");
	}
	
	public static String getClientId(Context context){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getString("client_id_preference", "");
	}
	
	public static boolean isValidApiKeyOrClientId(String value){
		if(value == null){
			return false;
		}
		return value.matches("^[a-f0-9]{32}$");
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
		else if(recordType.equals("AAAA")){
        	return R.drawable.aaaa;
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

	public static void selectAccount(Context context, Account account) {
		AccountDao accountDao = new AccountDao(DatabaseHelper.getInstance(context));
		accountDao.unSelectAll();
		account.setSelected(true);
		accountDao.createOrUpdate(account);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("account_name_preference", account.getName());
		editor.putString("api_key_preference", account.getApiKey());
		editor.putString("client_id_preference", account.getClientId());
		editor.commit();
		
		ImageService imageService = new ImageService(context);
		imageService.deleteAll();
		imageService.getAllImagesFromAPI(true);
		
		DomainService domainService = new DomainService(context);
		domainService.deleteAll();
		domainService.getAllDomainsFromAPI(true);
		
		DropletService dropletService = new DropletService(context);
		dropletService.deleteAll();
		dropletService.getAllDropletsFromAPI(true);
		
		RegionService regionService = new RegionService(context);
		regionService.deleteAll();
		regionService.getAllRegionsFromAPI(true);		
		
		SSHKeyService sshKeyService = new SSHKeyService(context);
		sshKeyService.deleteAll();
		sshKeyService.getAllSSHKeysFromAPI(true);
		
		SizeService sizeService = new SizeService(context);
		sizeService.deleteAll();
		sizeService.getAllSizesFromAPI(true);
	}
	static Toast sToast;
	public static void showAccessDenied() {
		if(sToast == null){
			sToast = Toast.makeText(MyApplication.getAppContext(), R.string.access_denied_message, Toast.LENGTH_SHORT);
		}
		if(sToast.getView().getWindowToken() == null){
			sToast.show();	
		}
	}
}
