package com.yassirh.digitalocean.data;

public class AccountTable extends TableHelper {
	
	public static final String NAME = "name";
    public static final String TOKEN = "token";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String EXPIRES_IN = "expires_in";
	public static final String SELECTED = "selected";
	
	public AccountTable(){		
		columns.put(ID, "integer primary key autoincrement");
		columns.put(NAME, "text");
		columns.put(TOKEN, "text");
		columns.put(REFRESH_TOKEN, "text");
		columns.put(EXPIRES_IN, "long");
		columns.put(SELECTED, "integer");
		TABLE_NAME = "accounts";
	}	
	
}
