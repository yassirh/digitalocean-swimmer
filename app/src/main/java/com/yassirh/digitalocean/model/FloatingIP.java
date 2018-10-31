package com.yassirh.digitalocean.model;

public class FloatingIP {
    private long id;
    private String ip;
    private Region region;
    private Droplet droplet;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Droplet getDroplet() {
        return droplet;
    }

    public void setDroplet(Droplet droplet) {
        this.droplet = droplet;
    }
}
