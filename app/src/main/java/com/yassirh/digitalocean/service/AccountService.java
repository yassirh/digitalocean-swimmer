package com.yassirh.digitalocean.service;


import android.content.Context;

import com.yassirh.digitalocean.data.AccountDao;
import com.yassirh.digitalocean.data.DatabaseHelper;
import com.yassirh.digitalocean.model.Account;

import java.util.List;

public class AccountService {

    private Context context;

    public AccountService(Context context) {
        this.context = context;
    }

    public boolean hasAccounts() {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        AccountDao accountDao = new AccountDao(databaseHelper);
        List<Account> accounts = accountDao.getAll(null);
        return accounts.size() > 0;
    }
}
