package com.yassirh.digitalocean.model;

public class Region {

	private long id;
	private String name;
	private String slug;
	
	public Region(){
	}
	
	public Region(long id, String name, String slug) {
		super();
		this.id = id;
		this.name = name;
		this.slug = slug;
	}
	
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
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	
		
	
}
