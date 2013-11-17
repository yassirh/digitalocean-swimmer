package com.yassirh.digitalocean.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.Region;

public class RegionDao extends SqlDao<Region> {

	private DatabaseHelper databaseHelper;

	public RegionDao(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public long create(Region region) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(RegionTable.ID, region.getId());
		values.put(RegionTable.NAME, region.getName());
		values.put(RegionTable.SLUG, region.getSlug());
		long id = db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
		return id;
	}	

	public Region newInstance(Cursor c) {
		Region region = new Region();
		region.setId(c.getLong(c.getColumnIndex(RegionTable.ID)));
		region.setName(c.getString(c.getColumnIndex(RegionTable.NAME)));
		region.setSlug(c.getString(c.getColumnIndex(RegionTable.SLUG)));
		return region;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this.databaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new RegionTable();
	}
}
