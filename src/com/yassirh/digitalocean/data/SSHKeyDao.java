package com.yassirh.digitalocean.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.SSHKey;

public class SSHKeyDao extends SqlDao<SSHKey> {

	private DatabaseHelper mDatabaseHelper;
	
	public SSHKeyDao(DatabaseHelper databaseHelper) {
		super();
		this.mDatabaseHelper = databaseHelper;
	}

	public long create(SSHKey sshKey) {
		ContentValues values = new ContentValues();
		values.put(SSHKeyTable.ID, sshKey.getId());
		values.put(SSHKeyTable.NAME, sshKey.getName());
		values.put(SSHKeyTable.SSH_PUB_KEY, sshKey.getSshPubKey());
		long id = db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		return id;
	}	

	public SSHKey newInstance(Cursor c) {
		SSHKey sshKey = new SSHKey();
		sshKey.setId(c.getLong(c.getColumnIndex(SSHKeyTable.ID)));
		sshKey.setName(c.getString(c.getColumnIndex(SSHKeyTable.NAME)));
		sshKey.setSshPubKey(c.getString(c.getColumnIndex(SSHKeyTable.SSH_PUB_KEY)));
		return sshKey;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this.mDatabaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new SSHKeyTable();
	}

}
