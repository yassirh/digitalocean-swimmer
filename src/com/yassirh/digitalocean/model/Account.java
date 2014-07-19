package com.yassirh.digitalocean.model;

public class Account {

	private Long id;
	private String name;
	private String token;
	private boolean selected;
	
	public Account() {
	}
	
	public Account(Long id, String name, String token,
			boolean selected) {
		this.id = id;
		this.name = name;
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
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
