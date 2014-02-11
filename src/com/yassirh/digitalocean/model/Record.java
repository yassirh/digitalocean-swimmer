package com.yassirh.digitalocean.model;

public class Record {
	private long mId;
	private Domain mDomain;
	private String mRecordType;
	private String mName;
	private String mData;
	private Integer mPriority;
	private Integer mPort;
	private Integer mWeight;
	
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
	public Integer getPriority() {
		return mPriority;
	}
	public void setPriority(Integer priority) {
		this.mPriority = priority;
	}
	public Integer getPort() {
		return mPort;
	}
	public void setPort(Integer port) {
		this.mPort = port;
	}
	public Integer getWeight() {
		return mWeight;
	}
	public void setWeight(Integer weight) {
		this.mWeight = weight;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
	        return true;
	    if (o == null)
	        return false;
	    if (o instanceof Record){
	        Record other = (Record)o;
	        return other.getId() == mId;	        		
	    }
	    else
	        return false;
	}
}
