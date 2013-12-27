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

	private DatabaseHelper mDatabaseHelper;
	
	public DropletDao(DatabaseHelper databaseHelper) {
		super();
		this.mDatabaseHelper = databaseHelper;
	}

	public long createOrUpdate(Droplet droplet) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		boolean update = findById(droplet.getId()) != null;
		db = mDatabaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DropletTable.ID, droplet.getId());
		values.put(DropletTable.NAME, droplet.getName());
		values.put(DropletTable.IMAGE_ID, droplet.getImage().getId());
		values.put(DropletTable.REGION_ID, droplet.getRegion().getId());
		values.put(DropletTable.SIZE_ID, droplet.getSize().getId());
		values.put(DropletTable.BACKUPS_ACTIVE, droplet.isBackupsActive());
		values.put(DropletTable.CREATED_AT, droplet.getCreatedAt().getTime());
		values.put(DropletTable.IP_ADDRESS, droplet.getIpAddress());
		values.put(DropletTable.PRIVATE_IP_ADDRESS, droplet.getPrivateIpAddress());
		values.put(DropletTable.LOCKED, droplet.isLocked());
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
		Image image = new ImageDao(mDatabaseHelper).findById(c.getLong(c.getColumnIndex(DropletTable.IMAGE_ID)));
		Region region = new RegionDao(mDatabaseHelper).findById(c.getLong(c.getColumnIndex(DropletTable.REGION_ID)));
		Size size = new SizeDao(mDatabaseHelper).findById(c.getLong(c.getColumnIndex(DropletTable.SIZE_ID)));
		Droplet droplet = new Droplet();
		droplet.setId(c.getLong(c.getColumnIndex(DropletTable.ID)));
		droplet.setName(c.getString(c.getColumnIndex(DropletTable.NAME)));
		droplet.setImage(image);
		droplet.setRegion(region);
		droplet.setSize(size);
		droplet.setBackupsActive(c.getInt(c.getColumnIndex(DropletTable.BACKUPS_ACTIVE)) > 0);
		droplet.setCreatedAt(new Date(c.getLong(c.getColumnIndex(DropletTable.CREATED_AT))));
		droplet.setIpAddress(c.getString(c.getColumnIndex(DropletTable.IP_ADDRESS)));
		droplet.setPrivateIpAddress(c.getString(c.getColumnIndex(DropletTable.PRIVATE_IP_ADDRESS)));
		droplet.setLocked(c.getInt(c.getColumnIndex(DropletTable.LOCKED)) > 0);
		droplet.setStatus(c.getString(c.getColumnIndex(DropletTable.STATUS)));
		return droplet;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this.mDatabaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new DropletTable();
	}

}
