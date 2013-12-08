package com.yassirh.digitalocean.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 9;
	private static final String DATABASE_NAME = "digital_ocean";
	
	private TableHelper imageTable = new ImageTable();
	private TableHelper regionTable = new RegionTable();
	private TableHelper sizeTable = new SizeTable();
	private TableHelper domainTable = new DomainTable();
	private TableHelper dropletTable = new DropletTable();
	private TableHelper recordTable = new RecordTable();
	static DatabaseHelper sDatabaseHelper;
	static SQLiteDatabase sSQLiteDatabase;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	
	public static DatabaseHelper getInstance(Context context) {
		if (sDatabaseHelper == null) {
			sDatabaseHelper = new DatabaseHelper(context.getApplicationContext());
		}
		return sDatabaseHelper;
	}
	
	public static SQLiteDatabase getWritableDatabaseInstance() {
		if (sDatabaseHelper == null) {
			// TODO
		}
		return sDatabaseHelper.getWritableDatabase();
	}
	
	public static SQLiteDatabase getReadableDatabaseInstance() {
		if (sDatabaseHelper == null) {
			// TODO
		}
		return sDatabaseHelper.getReadableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(imageTable.getCreateSql());
		db.execSQL(regionTable.getCreateSql());
		db.execSQL(sizeTable.getCreateSql());
		db.execSQL(domainTable.getCreateSql());
		db.execSQL(dropletTable.getCreateSql());
		db.execSQL(recordTable.getCreateSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO : restore the previous data
		db.execSQL(imageTable.getDeleteSql());
		db.execSQL(regionTable.getDeleteSql());
		db.execSQL(sizeTable.getDeleteSql());
		db.execSQL(domainTable.getDeleteSql());
		db.execSQL(dropletTable.getDeleteSql());
		db.execSQL(recordTable.getDeleteSql());
		onCreate(db);
	}

}
