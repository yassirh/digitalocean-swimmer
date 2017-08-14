package com.yassirh.digitalocean.model;

import java.util.Date;
import java.util.List;

public class Droplet {
	
	private long id;
	private String name;
	private int memory;
	private int cpu;
	private int disk;
	private Image image;
	private Size size;
	private Region region;
	private boolean locked;
	private boolean virtIoEnabled;
	private boolean privateNetworkingEnabled;
	private boolean backupsEnabled;
	private boolean ipv6Enabled;
	private String status;
	private Date createdAt;
	private List<Network> networks;
	
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
	public int getMemory() {
		return memory;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	public int getCpu() {
		return cpu;
	}
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}
	public int getDisk() {
		return disk;
	}
	public void setDisk(int disk) {
		this.disk = disk;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public Size getSize() {
		return size;
	}
	public void setSize(Size size) {
		this.size = size;
	}
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public boolean isVirtIoEnabled() {
		return virtIoEnabled;
	}
	public void setVirtIoEnabled(boolean virtIoEnabled) {
		this.virtIoEnabled = virtIoEnabled;
	}
	public boolean isPrivateNetworkingEnabled() {
		return privateNetworkingEnabled;
	}
	public void setPrivateNetworkingEnabled(boolean privateNetworkingEnabled) {
		this.privateNetworkingEnabled = privateNetworkingEnabled;
	}
	public boolean isBackupsEnabled() {
		return backupsEnabled;
	}
	public void setBackupsEnabled(boolean backupsEnabled) {
		this.backupsEnabled = backupsEnabled;
	}
	public boolean isIpv6Enabled() {
		return ipv6Enabled;
	}
	public void setIpv6Enabled(boolean ipv6Enabled) {
		this.ipv6Enabled = ipv6Enabled;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public List<Network> getNetworks() {
		return networks;
	}
	public void setNetworks(List<Network> networks) {
		this.networks = networks;
	}
}
