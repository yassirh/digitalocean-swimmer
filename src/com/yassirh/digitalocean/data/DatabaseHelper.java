package com.yassirh.digitalocean.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 14;
	private static final String DATABASE_NAME = "digital_ocean";
	
	private TableHelper imageTable = new ImageTable();
	private TableHelper regionTable = new RegionTable();
	private TableHelper sizeTable = new SizeTable();
	private TableHelper domainTable = new DomainTable();
	private TableHelper dropletTable = new DropletTable();
	private TableHelper recordTable = new RecordTable();
	private TableHelper sshKeyTable = new SSHKeyTable();
	private TableHelper accountTable = new AccountTable();
	private TableHelper networkTable = new NetworkTable();
	
	static DatabaseHelper sDatabaseHelper;
	static SQLiteDatabase sSQLiteDatabase;
	private Context context;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
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
		db.execSQL(sshKeyTable.getCreateSql());
		db.execSQL(accountTable.getCreateSql());
		db.execSQL(networkTable.getCreateSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(imageTable.getDropSql());
		db.execSQL(regionTable.getDropSql());
		db.execSQL(sizeTable.getDropSql());
		db.execSQL(domainTable.getDropSql());
		db.execSQL(dropletTable.getDropSql());
		db.execSQL(recordTable.getDropSql());
		db.execSQL(sshKeyTable.getDropSql());
		db.execSQL(accountTable.getDropSql());
		db.execSQL(networkTable.getDropSql());
		
		db.execSQL(imageTable.getCreateSql());
		db.execSQL(regionTable.getCreateSql());
		db.execSQL(sizeTable.getCreateSql());
		db.execSQL(domainTable.getCreateSql());
		db.execSQL(dropletTable.getCreateSql());
		db.execSQL(recordTable.getCreateSql());
		db.execSQL(sshKeyTable.getCreateSql());
		db.execSQL(accountTable.getCreateSql());
		db.execSQL(networkTable.getCreateSql());
	}


	public Context getContext() {
		return context;
	}

}
