package com.lanhu.explosion.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBWrapper {

    private static DBWrapper sDBWrapper;

    public static DBWrapper getInstance(Context context){
        if(sDBWrapper == null){
            sDBWrapper = new DBWrapper(context.getApplicationContext());
        }
        return sDBWrapper;
    }

    private SQLiteDatabase mDB;

    public DBWrapper(Context context){
        mDB = new DBHelper(context).getWritableDatabase();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        mDB.close();
    }

    public long insertPicture(String path, int Status){
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_PATH, path);
        cv.put(DBHelper.COLUMN_STATUS, Status);
        long result = mDB.insert(DBHelper.TABLE_PICTURE_NAME, "", cv);
        return result;
    }

    public Cursor queryPicture(){
        Cursor cursor = mDB.query(DBHelper.TABLE_PICTURE_NAME, DBHelper.TABLE_COLUMNS, null,null,null,null,null);
        return cursor;
    }

    public static class DBHelper extends SQLiteOpenHelper {
        private static final String TAG = "DBHelper";

        private static final String DATABASE_NAME = "Database.db";
        private static final int DATABASE_VERSION = 1;

        private static final String TABLE_PICTURE_NAME = "picture";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_STATUS = "status";
        public static final String[] TABLE_COLUMNS = {
                COLUMN_ID,
                COLUMN_PATH,
                COLUMN_STATUS
        };

        public DBHelper(Context context) {
            this(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        }

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            String sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_PICTURE_NAME + " ( "
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_PATH + " TEXT,"
                    + COLUMN_STATUS + " INTEGER)";

            database.execSQL(sql);
        }
        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }


}
