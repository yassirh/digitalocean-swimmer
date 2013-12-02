package com.yassirh.digitalocean.model;

public class Event {
	
	private long id;
	private String actionStatus;
	private Droplet droplet;
	private int eventTypeId;
	private int percentage;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getActionStatus() {
		return actionStatus;
	}
	public void setActionStatus(String action_status) {
		this.actionStatus = action_status;
	}
	public Droplet getDroplet() {
		return droplet;
	}
	public void setDroplet(Droplet droplet) {
		this.droplet = droplet;
	}
	public int getEventTypeId() {
		return eventTypeId;
	}
	public void setEventTypeId(int eventTypeId) {
		this.eventTypeId = eventTypeId;
	}
	public int getPercentage() {
		return percentage;
	}
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
}
