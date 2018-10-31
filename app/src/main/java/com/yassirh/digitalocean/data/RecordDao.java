package com.yassirh.digitalocean.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.Domain;
import com.yassirh.digitalocean.model.Record;

public class RecordDao extends SqlDao<Record> {

	private DatabaseHelper databaseHelper;

	public RecordDao(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	public Record newInstance(Cursor c) {
		Record record = new Record();
		Domain domain = new DomainDao(databaseHelper).findByProperty(DomainTable.NAME, c.getString(c.getColumnIndex(RecordTable.DOMAIN_NAME)));
		record.setId(c.getLong(c.getColumnIndex(RecordTable.ID)));
		record.setName(c.getString(c.getColumnIndex(RecordTable.NAME)));
		record.setDomain(domain);
		record.setRecordType(c.getString(c.getColumnIndex(RecordTable.RECORD_TYPE)));
		record.setData(c.getString(c.getColumnIndex(RecordTable.DATA)));
		record.setPort(c.getInt(c.getColumnIndex(RecordTable.PORT)));
		record.setPriority(c.getInt(c.getColumnIndex(RecordTable.PRIORITY)));
		record.setWeight(c.getInt(c.getColumnIndex(RecordTable.WEIGHT)));
		return record;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return databaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new RecordTable();
	}

	public void createOrUpdate(Record record) {
		boolean update = findById(record.getId()) != null;
		
		ContentValues values = new ContentValues();
		values.put(RecordTable.ID, record.getId());
		values.put(RecordTable.NAME, record.getName());
		values.put(RecordTable.DOMAIN_NAME, record.getDomain().getName());
		values.put(RecordTable.RECORD_TYPE, record.getRecordType());
		values.put(RecordTable.DATA, record.getData());
		values.put(RecordTable.PORT, record.getPort());
		values.put(RecordTable.PRIORITY, record.getPriority());
		values.put(RecordTable.WEIGHT, record.getWeight());

		if(update){
			db.updateWithOnConflict(getTableHelper().TABLE_NAME,values,DropletTable.ID +"= ?",new String[]{record.getId()+""},SQLiteDatabase.CONFLICT_REPLACE);
		}else{
			db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		}
	}

	public List<Record> getAllByDomain(String domainName) {
		List<Record> records = new ArrayList<>();
		Cursor cursor = db.query(getTableHelper().TABLE_NAME,
				getTableHelper().getAllColumns(), RecordTable.DOMAIN_NAME + " = '" + domainName + "'", null, null, null, null);
		
		if(cursor.moveToFirst()){
			while (!cursor.isAfterLast()) {
				Record record = newInstance(cursor);
				records.add(record);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return records;
	}

	public void deleteAllRecordsByDomain(String domainName) {
		db.delete(getTableHelper().TABLE_NAME, RecordTable.DOMAIN_NAME + " = '" + domainName + "'", null);
	}
	
}
