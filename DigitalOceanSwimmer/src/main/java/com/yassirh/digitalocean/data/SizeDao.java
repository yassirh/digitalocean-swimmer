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
		values.put(SizeTable.SIZE_SLUG, size.getSlug());
		values.put(SizeTable.MEMORY, size.getMemory());
		values.put(SizeTable.CPU, size.getCpu());
		values.put(SizeTable.DISK, size.getDisk());
		values.put(SizeTable.TRANSFER, size.getTransfer());
		values.put(SizeTable.COST_PER_HOUR, size.getCostPerHour());
		values.put(SizeTable.COST_PER_MONTH, size.getCostPerMonth());
        return db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
	}	

	public Size newInstance(Cursor c) {
		Size size = new Size();
		size.setSlug(c.getString(c.getColumnIndex(SizeTable.SIZE_SLUG)));
		size.setMemory(c.getInt(c.getColumnIndex(SizeTable.MEMORY)));
		size.setCpu(c.getInt(c.getColumnIndex(SizeTable.CPU)));
		size.setDisk(c.getInt(c.getColumnIndex(SizeTable.DISK)));
		size.setTransfer(c.getInt(c.getColumnIndex(SizeTable.TRANSFER)));
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
