package com.yassirh.digitalocean.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.SSHKey;

public class SSHKeyDao extends SqlDao<SSHKey> {

	private DatabaseHelper databaseHelper;
	
	public SSHKeyDao(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	public long create(SSHKey sshKey) {
		ContentValues values = new ContentValues();
		values.put(SSHKeyTable.ID, sshKey.getId());
		values.put(SSHKeyTable.NAME, sshKey.getName());
		values.put(SSHKeyTable.PUBLIC_KEY, sshKey.getPublicKey());
		values.put(SSHKeyTable.FINGERPRINT, sshKey.getFingerprint());
        return db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
	}	

	public SSHKey newInstance(Cursor c) {
		SSHKey sshKey = new SSHKey();
		sshKey.setId(c.getLong(c.getColumnIndex(SSHKeyTable.ID)));
		sshKey.setName(c.getString(c.getColumnIndex(SSHKeyTable.NAME)));
		sshKey.setPublicKey(c.getString(c.getColumnIndex(SSHKeyTable.PUBLIC_KEY)));
		sshKey.setFingerprint(c.getString(c.getColumnIndex(SSHKeyTable.FINGERPRINT)));
		return sshKey;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this.databaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new SSHKeyTable();
	}

}