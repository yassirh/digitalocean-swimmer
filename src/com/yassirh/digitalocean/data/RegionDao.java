package com.yassirh.digitalocean.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.Region;

public class RegionDao extends SqlDao<Region> {

	private DatabaseHelper mDatabaseHelper;

	public RegionDao(DatabaseHelper databaseHelper) {
		super();
		this.mDatabaseHelper = databaseHelper;
	}

	public long create(Region region) {
		ContentValues values = new ContentValues();
		values.put(RegionTable.ID, region.getId());
		values.put(RegionTable.NAME, region.getName());
		values.put(RegionTable.SLUG, region.getSlug());
		long id = db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
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
		return this.mDatabaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new RegionTable();
	}
}
