package com.yassirh.digitalocean.model;

public class Network {
	
	private long id;
	private String ipAddress;
	private String gateway;
	private String type;
	private String cidr;
	private String netmask;
	private Droplet droplet;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCidr() {
		return cidr;
	}
	public void setCidr(String cidr) {
		this.cidr = cidr;
	}
	public String getNetmask() {
		return netmask;
	}
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}
	public Droplet getDroplet() {
		return droplet;
	}
	public void setDroplet(Droplet droplet) {
		this.droplet = droplet;
	}
}
