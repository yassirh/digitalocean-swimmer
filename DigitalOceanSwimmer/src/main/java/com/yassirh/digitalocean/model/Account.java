package com.yassirh.digitalocean.model;

import java.util.Date;

public class Account {

	private Long id;
	private String name;
	private String token;
	private String refreshToken;
	private Date expiresIn;
	private boolean selected;
	
	public Account() {
	}
	
	public Account(Long id, String name, String token, String refreshToken,
			Date expiresIn, boolean selected) {
		this.id = id;
		this.name = name;
		this.token = token;
		this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
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
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public Date getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(Date expiresIn) {
		this.expiresIn = expiresIn;
	}
}
