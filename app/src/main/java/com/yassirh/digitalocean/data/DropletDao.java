package com.yassirh.digitalocean.data;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.Image;
import com.yassirh.digitalocean.model.Region;
import com.yassirh.digitalocean.model.Size;

public class DropletDao extends SqlDao<Droplet> {

	private DatabaseHelper databaseHelper;
	
	public DropletDao(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	public long createOrUpdate(Droplet droplet) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		boolean update = findById(droplet.getId()) != null;
		ContentValues values = new ContentValues();
		values.put(DropletTable.ID, droplet.getId());
		values.put(DropletTable.NAME, droplet.getName());
		values.put(DropletTable.MEMORY, droplet.getMemory());
		values.put(DropletTable.CPU, droplet.getCpu());
		values.put(DropletTable.DISK, droplet.getDisk());
		values.put(DropletTable.IMAGE_ID, droplet.getImage().getId());
		values.put(DropletTable.REGION_SLUG, droplet.getRegion().getSlug());
		values.put(DropletTable.SIZE_SLUG, droplet.getSize().getSlug());
		values.put(DropletTable.CREATED_AT, droplet.getCreatedAt().getTime());
		values.put(DropletTable.LOCKED, droplet.isLocked());
		values.put(DropletTable.BACKUPS_ENABLED, droplet.isBackupsEnabled());
		values.put(DropletTable.IPV6_ENABLED, droplet.isIpv6Enabled());
		values.put(DropletTable.PRIVATE_NETWORKING_ENABLED, droplet.isPrivateNetworkingEnabled());
		values.put(DropletTable.VIRTIO_ENABLED, droplet.isVirtIoEnabled());
		values.put(DropletTable.STATUS, droplet.getStatus());
		long id = droplet.getId();
		if(update){
			db.updateWithOnConflict(getTableHelper().TABLE_NAME,values,DropletTable.ID +"= ?",new String[]{droplet.getId()+""},SQLiteDatabase.CONFLICT_REPLACE);
		}else{
			id = db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		}
		return id;
	}	


	public Droplet newInstance(Cursor c) {
		Image image = new ImageDao(databaseHelper).findById(c.getLong(c.getColumnIndex(DropletTable.IMAGE_ID)));
		Region region = new RegionDao(databaseHelper).findByProperty(DropletTable.REGION_SLUG, c.getString(c.getColumnIndex(DropletTable.REGION_SLUG)));
		Size size = new SizeDao(databaseHelper).findByProperty(DropletTable.SIZE_SLUG, c.getString(c.getColumnIndex(DropletTable.SIZE_SLUG)));
		Droplet droplet = new Droplet();
		droplet.setId(c.getLong(c.getColumnIndex(DropletTable.ID)));
		droplet.setName(c.getString(c.getColumnIndex(DropletTable.NAME)));
		droplet.setMemory(c.getInt(c.getColumnIndex(DropletTable.MEMORY)));
		droplet.setCpu(c.getInt(c.getColumnIndex(DropletTable.CPU)));
		droplet.setDisk(c.getInt(c.getColumnIndex(DropletTable.DISK)));
		droplet.setImage(image);
		droplet.setRegion(region);
		droplet.setSize(size);
		droplet.setCreatedAt(new Date(c.getLong(c.getColumnIndex(DropletTable.CREATED_AT))));
		droplet.setLocked(c.getInt(c.getColumnIndex(DropletTable.LOCKED)) > 0);
		droplet.setBackupsEnabled(c.getInt(c.getColumnIndex(DropletTable.BACKUPS_ENABLED)) > 0);
		droplet.setIpv6Enabled(c.getInt(c.getColumnIndex(DropletTable.IPV6_ENABLED)) > 0);
		droplet.setPrivateNetworkingEnabled(c.getInt(c.getColumnIndex(DropletTable.PRIVATE_NETWORKING_ENABLED)) > 0);
		droplet.setVirtIoEnabled(c.getInt(c.getColumnIndex(DropletTable.VIRTIO_ENABLED)) > 0);
		droplet.setStatus(c.getString(c.getColumnIndex(DropletTable.STATUS)));
		return droplet;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this.databaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new DropletTable();
	}

}
