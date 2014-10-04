package com.yassirh.digitalocean.model;

public class Record {
	private long id;
	private Domain domain;
	private String recordType;
	private String name;
	private String data;
	private Integer priority;
	private Integer port;
	private Integer weight;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Domain getDomain() {
		return domain;
	}
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
	        return true;
	    if (o == null)
	        return false;
	    if (o instanceof Record){
	        Record other = (Record)o;
	        return other.getId() == id;	        		
	    }
	    else
	        return false;
	}
}
