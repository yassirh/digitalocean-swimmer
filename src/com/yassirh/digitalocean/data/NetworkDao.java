package com.yassirh.digitalocean.data;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.Droplet;
import com.yassirh.digitalocean.model.Network;

public class NetworkDao extends SqlDao<Network> {

	private DatabaseHelper databaseHelper;

	public NetworkDao(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	public Network newInstance(Cursor c) {
		Droplet droplet = new DropletDao(databaseHelper).findById(c.getLong(c.getColumnIndex(NetworkTable.DROPLET_ID)));
		Network network = new Network();
		network.setId(c.getLong(c.getColumnIndex(NetworkTable.ID)));
		network.setCidr(c.getString(c.getColumnIndex(NetworkTable.CIDR)));
		network.setDroplet(droplet);
		network.setGateway(c.getString(c.getColumnIndex(NetworkTable.GATEWAY)));
		network.setIpAddress(c.getString(c.getColumnIndex(NetworkTable.IP_ADDRESS)));
		network.setNetmask(c.getString(c.getColumnIndex(NetworkTable.NETMASK)));
		network.setType(c.getString(c.getColumnIndex(NetworkTable.TYPE)));
		return network;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return databaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new NetworkTable();
	}

	public void createOrUpdate(Network network) {
		boolean update = network.getId() != 0 && findById(network.getId()) != null;
		
		ContentValues values = new ContentValues();
		if(update)
			values.put(NetworkTable.ID, network.getId());
		values.put(NetworkTable.CIDR, network.getCidr());
		values.put(NetworkTable.GATEWAY, network.getGateway());
		values.put(NetworkTable.IP_ADDRESS, network.getIpAddress());
		values.put(NetworkTable.TYPE, network.getType());
		values.put(NetworkTable.NETMASK, network.getNetmask());
		values.put(NetworkTable.DROPLET_ID, network.getDroplet().getId());

		if(update){
			db.updateWithOnConflict(getTableHelper().TABLE_NAME,values,NetworkTable.ID +"= ?",new String[]{network.getId()+""},SQLiteDatabase.CONFLICT_REPLACE);
		}else{
			db.insert(getTableHelper().TABLE_NAME, null, values);
		}
	}

	public List<Network> findByDropletId(long id) {
		return getAllByProperty(NetworkTable.DROPLET_ID, id + "");
	}
	
}
