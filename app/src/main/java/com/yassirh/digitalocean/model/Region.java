package com.yassirh.digitalocean.model;

public class Region {

	private String name;
	private String slug;
	private Boolean available;
	private String features;
	
	public Region(){
	}
	
	public Region(String name, String slug, Boolean available, String features) {
		this.name = name;
		this.slug = slug;
		this.available = available;
		this.features = features;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public Boolean isAvailable(){
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;		
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}
}
