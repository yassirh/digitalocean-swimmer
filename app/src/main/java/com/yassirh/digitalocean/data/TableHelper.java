package com.yassirh.digitalocean.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;


public class TableHelper{
	
	protected HashMap<String, String> columns = new HashMap<>();
	public String TABLE_NAME = "";
	public static final String ID = "id";
	
	public String getCreateSql() {
		String sql = "CREATE TABLE " + TABLE_NAME + " (";
		for (Entry<String, String> column : columns.entrySet()){
			sql += column.getKey() + " " + column.getValue() + ","; 
		}
		sql = sql.substring(0, sql.length() -1);
		sql += ");";
		return sql;
	}
	
	public String getDropSql(){
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}

	public String[] getAllColumns() {
		String[] columnsStrings = new String[this.columns.size()];
		Set<String> keys = this.columns.keySet();
		Iterator<String> iterator = keys.iterator();
		for(int i = 0;i < this.columns.size(); i++){
			columnsStrings[i] = iterator.next();
		}
		return columnsStrings;
	}
}
