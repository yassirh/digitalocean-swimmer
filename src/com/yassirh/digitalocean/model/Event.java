package com.yassirh.digitalocean.model;

public class Event {
	
	private long id;
	private String action_status;
	private Droplet droplet;
	private int eventTypeId;
	private float percentage;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAction_status() {
		return action_status;
	}
	public void setAction_status(String action_status) {
		this.action_status = action_status;
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
	public float getPercentage() {
		return percentage;
	}
	public void setPercentage(float percentage) {
		this.percentage = percentage;
	}
}
