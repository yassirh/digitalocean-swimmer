package com.yassirh.digitalocean.data;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class SqlDao<T> {
	
	public abstract DatabaseHelper getDatabaseHelper();
	public abstract TableHelper getTableHelper();
	public abstract T newInstance(Cursor cursor);
	protected SQLiteDatabase db;
	
	public SqlDao(){
		db = DatabaseHelper.getWritableDatabaseInstance();
	}
	
	public List<T> getAll(String orderBy) {
		List<T> collection = new ArrayList<>();
		Cursor cursor = db.query(getTableHelper().TABLE_NAME,
				getTableHelper().getAllColumns(), null, null, orderBy, null, null);
		
		if(cursor.moveToFirst()){
			while (!cursor.isAfterLast()) {
				T object = newInstance(cursor);
				collection.add(object);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return collection;
	}
	
	public List<T> getAllByProperty(String property, String value) {
		List<T> collection = new ArrayList<>();
		Cursor cursor = db.query(getTableHelper().TABLE_NAME,
				getTableHelper().getAllColumns(), String.format("%s = '%s'", property, value), null, null, null, null);
		
		if(cursor.moveToFirst()){
			while (!cursor.isAfterLast()) {
				T object = newInstance(cursor);
				collection.add(object);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return collection;
	}
	

	public void deleteAll() {
		db.delete(getTableHelper().TABLE_NAME,null,null);
	}
	
	public void delete(long id) {
		db.delete(getTableHelper().TABLE_NAME,TableHelper.ID + " = " + id,null);
	}
	
	public T findById(long id) {
		T t = null;
		Cursor cursor = db.query(getTableHelper().TABLE_NAME,
				getTableHelper().getAllColumns(), TableHelper.ID + " = " + id, null, null, null, null);
		if(cursor.moveToNext())		
			t = newInstance(cursor);
		cursor.close();
		return t;
	}
	
	public T findByProperty(String property, String value) {
		T t = null;
		Cursor cursor = db.query(getTableHelper().TABLE_NAME,
				getTableHelper().getAllColumns(), String.format("%s = '%s'", property, value), null, null, null, null);
		if(cursor.moveToNext())		
			t = newInstance(cursor);
		cursor.close();
		return t;
	}
}
