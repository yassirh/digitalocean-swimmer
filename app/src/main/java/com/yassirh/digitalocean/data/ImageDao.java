package com.yassirh.digitalocean.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.Image;

public class ImageDao extends SqlDao<Image> {

	private DatabaseHelper mDatabaseHelper;

	public ImageDao(DatabaseHelper databaseHelper) {
		super();
		this.mDatabaseHelper = databaseHelper;
	}

	public long create(Image image) {
		ContentValues values = new ContentValues();
		values.put(ImageTable.ID, image.getId());
		values.put(ImageTable.NAME, image.getName());
		values.put(ImageTable.DISTRIBUTION, image.getDistribution());
		values.put(ImageTable.SLUG, image.getSlug());
        values.put(ImageTable.IS_IN_USE, image.isInUse());
		values.put(ImageTable.PUBLIC, image.isPublic() ? 1 : 0);
        values.put(ImageTable.REGIONS, image.getRegions());
        values.put(ImageTable.MINDISKSIZE, image.getMinDiskSize());
        return db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
	}	

	public Image newInstance(Cursor c) {
		Image image = new Image();
		image.setId(c.getLong(c.getColumnIndex(ImageTable.ID)));
		image.setName(c.getString(c.getColumnIndex(ImageTable.NAME)));
		image.setDistribution(c.getString(c.getColumnIndex(ImageTable.DISTRIBUTION)));
		image.setSlug(c.getString(c.getColumnIndex(ImageTable.SLUG)));
		image.setPublic(c.getInt(c.getColumnIndex(ImageTable.PUBLIC)) > 0);
        image.setRegions(c.getString(c.getColumnIndex(ImageTable.REGIONS)));
        image.setMinDiskSize(c.getInt(c.getColumnIndex(ImageTable.MINDISKSIZE)));
		return image;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this.mDatabaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new ImageTable();
	}

	public List<Image> getSnapshotsOnly() {
		List<Image> snapshots = new ArrayList<>();
		Cursor cursor = db.query(getTableHelper().TABLE_NAME,
				getTableHelper().getAllColumns(), ImageTable.PUBLIC + " = ? AND " + ImageTable.IS_IN_USE + " = ?", new String[]{"0","1"}, null, null, ImageTable.NAME);
		
		if(cursor.moveToFirst()){
			while (!cursor.isAfterLast()) {
				Image snapshot = newInstance(cursor);
				snapshots.add(snapshot);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return snapshots;
	}
	
	public List<Image> getImagesOnly() {
		List<Image> images = new ArrayList<>();
		Cursor cursor = db.query(getTableHelper().TABLE_NAME,
				getTableHelper().getAllColumns(), ImageTable.PUBLIC + " = ? AND " + ImageTable.IS_IN_USE + " = ?", new String[]{"1","1"}, null, null, ImageTable.NAME);

		if(cursor.moveToFirst()){
			while (!cursor.isAfterLast()) {
				Image image = newInstance(cursor);
				images.add(image);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return images;
	}

    public void deleteAll() {
        db.delete(getTableHelper().TABLE_NAME, ImageTable.IS_IN_USE + " = ?", new String[]{"1"});
    }
}
