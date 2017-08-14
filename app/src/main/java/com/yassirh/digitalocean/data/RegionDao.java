package com.yassirh.digitalocean.data;

import java.util.List;

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
		values.put(RegionTable.NAME, region.getName());
		values.put(RegionTable.REGION_SLUG, region.getSlug());
		values.put(RegionTable.AVAILABLE, region.isAvailable());
		values.put(RegionTable.FEATURES, region.getFeatures());
        return db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
	}	

	public Region newInstance(Cursor c) {
		Region region = new Region();
		region.setAvailable(c.getInt(c.getColumnIndex(RegionTable.AVAILABLE)) > 0);
		region.setName(c.getString(c.getColumnIndex(RegionTable.NAME)));
		region.setSlug(c.getString(c.getColumnIndex(RegionTable.REGION_SLUG)));
		region.setFeatures(c.getString(c.getColumnIndex(RegionTable.FEATURES)));
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

	public List<Region> getAllOrderedByName() {
		return getAll(RegionTable.NAME);
	}
}
