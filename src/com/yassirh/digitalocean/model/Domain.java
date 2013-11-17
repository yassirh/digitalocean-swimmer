package com.yassirh.digitalocean.model;

public class Domain {
	
	private long id;
	private String name;
	private int ttl;
	private String liveZoneFile;
	private String error;
	private String zoneFileWithError;
	
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
	public int getTtl() {
		return ttl;
	}
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	public String getLiveZoneFile() {
		return liveZoneFile;
	}
	public void setLiveZoneFile(String liveZoneFile) {
		this.liveZoneFile = liveZoneFile;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getZoneFileWithError() {
		return zoneFileWithError;
	}
	public void setZoneFileWithError(String zoneFileWithError) {
		this.zoneFileWithError = zoneFileWithError;
	}
}
