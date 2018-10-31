package com.yassirh.digitalocean.model;

import java.util.List;

public class Domain {
	
	private String name;
	private int ttl;
	private String zoneFile;
	private List<Record> records;

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
		return zoneFile;
	}
	public void setLiveZoneFile(String liveZoneFile) {
		this.zoneFile = liveZoneFile;
	}
	public List<Record> getRecords() {
		return records;
	}
	public void setRecords(List<Record> records) {
		this.records = records;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
	        return true;
	    if (o == null)
	        return false;
	    if (o instanceof Domain){
	        Domain other = (Domain)o;
	        return other.getName().equals(this.name);	        		
	    }
	    else
	        return false;
	}
}
