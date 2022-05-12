package ca.bcit.a8531_a3_client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SQLController {
    private DbHelper dbhelper;
    private Context ourcontext;
    private SQLiteDatabase database;

    public SQLController(Context c) {
        ourcontext = c;
    }

    public SQLController open() throws SQLException {
        dbhelper = new DbHelper(ourcontext);
        database = dbhelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbhelper.close();
    }

    public void insert(String contents) {
        ContentValues cv = new ContentValues();
        cv.put(ReplicationContract.ReplicationEntry.COLUMN_NAME_CONTENTS, contents);
        database.insert(ReplicationContract.ReplicationEntry.TABLE_NAME, null, cv);
    }

    public Cursor readEntry() {
        String[] allColumns = new String[]{
                ReplicationContract.ReplicationEntry.MEMBER_ID,
                ReplicationContract.ReplicationEntry.COLUMN_NAME_CONTENTS
        };
        Cursor c = database.query(ReplicationContract.ReplicationEntry.TABLE_NAME, allColumns, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

}
