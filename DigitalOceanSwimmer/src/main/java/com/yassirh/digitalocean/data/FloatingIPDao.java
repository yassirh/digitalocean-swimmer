package com.yassirh.digitalocean.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.FloatingIP;
import com.yassirh.digitalocean.model.Region;

import java.util.List;

public class FloatingIPDao extends SqlDao<FloatingIP> {

	private DatabaseHelper databaseHelper;

	public FloatingIPDao(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	public long create(FloatingIP floatingIP) {
		ContentValues values = new ContentValues();
		values.put(FloatingIPTable.IP_ADDRESS, floatingIP.getIp());
		if(floatingIP.getRegion() != null) {
			values.put(FloatingIPTable.REGION_SLUG, floatingIP.getRegion().getSlug());
		}
		if(floatingIP.getDroplet() != null) {
			values.put(FloatingIPTable.DROPLET_ID, floatingIP.getDroplet().getId());
		}
		return db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
	}	

	public FloatingIP newInstance(Cursor c) {
		FloatingIP floatingIP = new FloatingIP();
		floatingIP.setIp(c.getString(c.getColumnIndex(FloatingIPTable.IP_ADDRESS)));
		return floatingIP;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this.databaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new FloatingIPTable();
	}
}
