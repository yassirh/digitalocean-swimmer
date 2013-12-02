package com.yassirh.digitalocean.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.Domain;

public class DomainDao extends SqlDao<Domain> {

	private DatabaseHelper databaseHelper;

	public DomainDao(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public long create(Domain domain) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DomainTable.ID, domain.getId());
		values.put(DomainTable.NAME, domain.getName());
		values.put(DomainTable.TTL, domain.getTtl());
		values.put(DomainTable.LIVE_ZONE_FILE, domain.getLiveZoneFile());
		values.put(DomainTable.ERROR, domain.getError());
		values.put(DomainTable.ZONE_FILE_WITH_ERROR, domain.getZoneFileWithError());
		long id = db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
		return id;
	}	

	public Domain newInstance(Cursor c) {
		Domain domain = new Domain();
		domain.setId(c.getLong(c.getColumnIndex(DomainTable.ID)));
		domain.setName(c.getString(c.getColumnIndex(DomainTable.NAME)));
		domain.setTtl(c.getInt(c.getColumnIndex(DomainTable.TTL)));
		domain.setLiveZoneFile(c.getString(c.getColumnIndex(DomainTable.LIVE_ZONE_FILE)));
		domain.setError(c.getString(c.getColumnIndex(DomainTable.ERROR)));
		domain.setZoneFileWithError(c.getString(c.getColumnIndex(DomainTable.ZONE_FILE_WITH_ERROR)));
		return domain;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this.databaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new DomainTable();
	}
	
}
