package com.yassirh.digitalocean.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.Size;

public class SizeDao extends SqlDao<Size> {

	private DatabaseHelper mDatabaseHelper;
	
	public SizeDao(DatabaseHelper databaseHelper) {
		super();
		this.mDatabaseHelper = databaseHelper;
	}

	public long create(Size size) {
		ContentValues values = new ContentValues();
		values.put(SizeTable.ID, size.getId());
		values.put(SizeTable.NAME, size.getName());
		values.put(SizeTable.SLUG, size.getSlug());
		values.put(SizeTable.MEMORY, size.getMemory());
		values.put(SizeTable.CPU, size.getCpu());
		values.put(SizeTable.DISK, size.getDisk());
		values.put(SizeTable.COST_PER_HOUR, size.getCostPerHour());
		values.put(SizeTable.COST_PER_MONTH, size.getCostPerMonth());
		long id = db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		return id;
	}	

	public Size newInstance(Cursor c) {
		Size size = new Size();
		size.setId(c.getLong(c.getColumnIndex(SizeTable.ID)));
		size.setName(c.getString(c.getColumnIndex(SizeTable.NAME)));
		size.setMemory(c.getInt(c.getColumnIndex(SizeTable.MEMORY)));
		size.setMemory(c.getInt(c.getColumnIndex(SizeTable.MEMORY)));
		size.setCpu(c.getInt(c.getColumnIndex(SizeTable.CPU)));
		size.setDisk(c.getInt(c.getColumnIndex(SizeTable.DISK)));
		size.setCostPerHour(c.getFloat(c.getColumnIndex(SizeTable.COST_PER_HOUR)));
		size.setCostPerMonth(c.getFloat(c.getColumnIndex(SizeTable.COST_PER_MONTH)));
		return size;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this.mDatabaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new SizeTable();
	}

}
