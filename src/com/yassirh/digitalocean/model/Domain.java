package com.yassirh.digitalocean.model;

import java.util.List;

public class Domain {
	
	private long mId;
	private String mName;
	private int mTtl;
	private String mLiveZoneFile;
	private String mError;
	private String mZoneFileWithError;
	private List<Record> mRecords;
	
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		this.mId = id;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public int getTtl() {
		return mTtl;
	}
	public void setTtl(int ttl) {
		this.mTtl = ttl;
	}
	public String getLiveZoneFile() {
		return mLiveZoneFile;
	}
	public void setLiveZoneFile(String liveZoneFile) {
		this.mLiveZoneFile = liveZoneFile;
	}
	public String getError() {
		return mError;
	}
	public void setError(String error) {
		this.mError = error;
	}
	public String getZoneFileWithError() {
		return mZoneFileWithError;
	}
	public void setZoneFileWithError(String zoneFileWithError) {
		this.mZoneFileWithError = zoneFileWithError;
	}
	public List<Record> getRecords() {
		return mRecords;
	}
	public void setRecords(List<Record> records) {
		this.mRecords = records;
	}
}
