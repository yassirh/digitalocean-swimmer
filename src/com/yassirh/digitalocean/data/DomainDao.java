package com.yassirh.digitalocean.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.Domain;

public class DomainDao extends SqlDao<Domain> {

	private DatabaseHelper mDatabaseHelper;

	public DomainDao(DatabaseHelper databaseHelper) {
		super();
		this.mDatabaseHelper = databaseHelper;
	}

	public long create(Domain domain) {		
		ContentValues values = new ContentValues();
		values.put(DomainTable.NAME, domain.getName());
		values.put(DomainTable.TTL, domain.getTtl());
		values.put(DomainTable.ZONE_FILE, domain.getLiveZoneFile());
		long id = db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		return id;
	}	

	public Domain newInstance(Cursor c) {
		Domain domain = new Domain();
		domain.setName(c.getString(c.getColumnIndex(DomainTable.NAME)));
		domain.setTtl(c.getInt(c.getColumnIndex(DomainTable.TTL)));
		domain.setLiveZoneFile(c.getString(c.getColumnIndex(DomainTable.ZONE_FILE)));
		return domain;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this.mDatabaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new DomainTable();
	}	
}
