package com.yassirh.digitalocean.data;

public class AccountTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String CLIENT_ID = "client_id";
	public static final String API_KEY = "api_key";
	public static final String SELECTED = "selected";
	
	public AccountTable(){		
		columns.put(ID, "integer primary key autoincrement");
		columns.put(NAME, "text");
		columns.put(CLIENT_ID, "text");
		columns.put(API_KEY, "text");
		columns.put(SELECTED, "integer");
		TABLE_NAME = "accounts";
	}	
	
}
