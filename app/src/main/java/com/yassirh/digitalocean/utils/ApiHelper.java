package com.yassirh.digitalocean.utils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.yassirh.digitalocean.R;
import com.yassirh.digitalocean.data.AccountDao;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.model.Account;

public class ApiHelper {

    public static final String API_URL = "https://api.digitalocean.com/v2";
    public static SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

    public static Account getCurrentAccount(Context context) {
        Account currentAccount = null;
        AccountDao accountDao = new AccountDao(new DatabaseHelper(context));
        List<Account> accounts = accountDao.getAll(null);
        // used to get the config from the shared preferences
        if (accounts.size() == 0) {
            currentAccount = new Account();
            String token = getToken(context);
            String accountName = getAccountName(context);
            if ("".equals(accountName)) {
                accountName = "default";
            }
            if (isValidToken(token)) {
                currentAccount.setId(1L);
                currentAccount.setName(accountName);
                currentAccount.setToken(token);
                currentAccount.setSelected(true);
                accountDao.createOrUpdate(currentAccount);
            }
        } else {
            for (Account account : accounts) {
                if (account.isSelected()) {
                    currentAccount = account;
                    break;
                }
            }
            if (currentAccount != null && !getAccountName(context).equals(currentAccount.getName())) {
                currentAccount.setName(getAccountName(context));
                accountDao.createOrUpdate(currentAccount);
            }
        }

        return currentAccount;
    }

    public static boolean isValidToken(String token) {
        return token != null && token.matches("^[a-f0-9]{64}$");
    }

    public static List<Account> getAllAccounts(Context context) {
        AccountDao accountDao = new AccountDao(new DatabaseHelper(context));
        return accountDao.getAll(null);
    }

    public static String getAccountName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("account_name_preference", "");
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("token_preference", "");
    }

    public static void selectAccount(Context context, Account account) {
        AccountDao accountDao = new AccountDao(new DatabaseHelper(context));
        accountDao.unSelectAll();
        account.setSelected(true);
        accountDao.createOrUpdate(account);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account_name_preference", account.getName());
        editor.putString("token_preference", account.getToken());
        editor.commit();
    }

    private static Toast toast;

    public static void showAccessDenied() {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.getAppContext(), R.string.access_denied_message, Toast.LENGTH_SHORT);
        }
        if (toast.getView().getWindowToken() == null) {
            toast.show();
        }
    }
}
