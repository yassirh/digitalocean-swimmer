package com.yassirh.digitalocean.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yassirh.digitalocean.utils.MyApplication;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 17;
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
    private TableHelper floatingIPTable = new FloatingIPTable();

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
        db.execSQL(floatingIPTable.getCreateSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 15) {
            db.execSQL(accountTable.getDropSql());
            db.execSQL(accountTable.getCreateSql());
        }
        if (oldVersion < 31) {
            db.execSQL("ALTER TABLE " + accountTable.TABLE_NAME + " ADD COLUMN " + AccountTable.EMAIL + " text;");
        }
        db.execSQL(imageTable.getDropSql());
        db.execSQL(regionTable.getDropSql());
        db.execSQL(sizeTable.getDropSql());
        db.execSQL(domainTable.getDropSql());
        db.execSQL(dropletTable.getDropSql());
        db.execSQL(recordTable.getDropSql());
        db.execSQL(sshKeyTable.getDropSql());
        db.execSQL(networkTable.getDropSql());
        db.execSQL(floatingIPTable.getDropSql());

        db.execSQL(imageTable.getCreateSql());
        db.execSQL(regionTable.getCreateSql());
        db.execSQL(sizeTable.getCreateSql());
        db.execSQL(domainTable.getCreateSql());
        db.execSQL(dropletTable.getCreateSql());
        db.execSQL(recordTable.getCreateSql());
        db.execSQL(sshKeyTable.getCreateSql());
        db.execSQL(networkTable.getCreateSql());
        db.execSQL(floatingIPTable.getCreateSql());
    }


    public Context getContext() {
        return context;
    }

}
