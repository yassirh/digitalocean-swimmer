package com.yassirh.digitalocean.data;

public class SSHKeyTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String PUBLIC_KEY = "public_key";
	public static final String FINGERPRINT = "fingerprint";
	
	public SSHKeyTable(){		
		columns.put(ID, "integer primary key");
		columns.put(NAME, "text");
		columns.put(PUBLIC_KEY, "text");
		columns.put(FINGERPRINT, "text");
		TABLE_NAME = "ssh_keys";
	}	
	
}
