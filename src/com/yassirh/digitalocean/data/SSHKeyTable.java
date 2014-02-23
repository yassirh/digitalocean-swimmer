package com.yassirh.digitalocean.data;

public class SSHKeyTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String SSH_PUB_KEY = "ssh_pub_key";
	
	public SSHKeyTable(){		
		columns.put(ID, "integer primary key");
		columns.put(NAME, "text");
		columns.put(SSH_PUB_KEY, "text");
		TABLE_NAME = "ssh_keys";
	}	
	
}
