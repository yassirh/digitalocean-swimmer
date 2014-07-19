package com.yassirh.digitalocean.model;

public class Size {
	private String slug;
	private int memory;
	private int cpu;
	private int disk;
	private int transfer;
	private double costPerHour;
	private double costPerMonth;
	
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
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
	public int getTransfer() {
		return transfer;
	}
	public void setTransfer(int transfer) {
		this.transfer = transfer;
	}
	public double getCostPerHour() {
		return costPerHour;
	}
	public void setCostPerHour(double costPerHour) {
		this.costPerHour = costPerHour;
	}
	public double getCostPerMonth() {
		return costPerMonth;
	}
	public void setCostPerMonth(double costPerMonth) {
		this.costPerMonth = costPerMonth;
	}
}
