package ca.bcit.a8531_a3_client.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Replication.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ReplicationContract.ReplicationEntry.TABLE_NAME + " (" +
                    ReplicationContract.ReplicationEntry._ID + " INTEGER PRIMARY KEY," +
                    ReplicationContract.ReplicationEntry.COLUMN_NAME_CONTENTS + " TEXT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ReplicationContract.ReplicationEntry.TABLE_NAME;

    public boolean transactionInProgress = false;
    private SQLiteDatabase db;

    //region Core methods

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }

    //endregion

    // Operations

    public <T> void insert(List<T> itemsToInsert) {
        if (db == null) {
            db = getWritableDatabase();
        }
        db.beginTransaction();
        transactionInProgress = true;

        for (T item : itemsToInsert) {
            ContentValues values = new ContentValues();
            values.put(ReplicationContract.ReplicationEntry.COLUMN_NAME_CONTENTS, item.toString());
            db.insert(ReplicationContract.ReplicationEntry.TABLE_NAME, null, values);
        }
    }

    public boolean commitTransaction() {
        if (db == null) {
            db = getWritableDatabase();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        transactionInProgress = false;
        return true;
    }

    public boolean rollbackTransaction() {
        if (db == null) {
            db = getWritableDatabase();
        }
        db.endTransaction();
        transactionInProgress = false;
        return true;
    }
}
