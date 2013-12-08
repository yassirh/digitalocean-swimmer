package com.yassirh.digitalocean.model;

public class Record {
	private long mId;
	private Domain mDomain;
	private String mRecordType;
	private String mName;
	private String mData;
	// TODO : add priority, port, weight
	
	public long getId() {
		return mId;
	}
	public void setId(long mId) {
		this.mId = mId;
	}
	public Domain getDomain() {
		return mDomain;
	}
	public void setDomain(Domain mDomain) {
		this.mDomain = mDomain;
	}
	public String getRecordType() {
		return mRecordType;
	}
	public void setRecordType(String mRecordType) {
		this.mRecordType = mRecordType;
	}
	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	public String getData() {
		return mData;
	}
	public void setData(String mData) {
		this.mData = mData;
	}
}
