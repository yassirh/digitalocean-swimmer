package com.yassirh.digitalocean.model;

public class SSHKey {
	
	private long id;
	private String name;
	private String sshPubKey;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSshPubKey() {
		return sshPubKey;
	}
	public void setSshPubKey(String sshPubKey) {
		this.sshPubKey = sshPubKey;
	}
	
}
