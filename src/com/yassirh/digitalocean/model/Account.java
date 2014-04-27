package com.yassirh.digitalocean.model;

public class Account {

	private Long id;
	private String name; 
	private String clientId;
	private String apiKey;
	private boolean selected;
	
	public Account() {
	}
	
	public Account(Long id, String name, String clientId, String apiKey,
			boolean selected) {
		this.id = id;
		this.name = name;
		this.clientId = clientId;
		this.apiKey = apiKey;
		this.selected = selected;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
