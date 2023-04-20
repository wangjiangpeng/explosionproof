package com.lanhu.explosion.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.bean.GasInfo;
import com.lanhu.explosion.bean.PictureInfo;
import com.lanhu.explosion.bean.RecordInfo;

import java.util.ArrayList;

public class DBManager {

    private static DBManager sDBManager;

    public static DBManager getInstance() {
        if (sDBManager == null) {
            sDBManager = new DBManager(AApplication.getInstance().getApplicationContext());
        }
        return sDBManager;
    }

    private SQLiteDatabase mDB;

    public DBManager(Context context) {
        mDB = new DBHelper(context).getWritableDatabase();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        mDB.close();
    }

    public synchronized long insertPicture(PictureInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_PATH, info.getPath());
        cv.put(DBHelper.COLUMN_STATUS, info.getUploadStatus());
        long result = mDB.insert(DBHelper.TABLE_PICTURE_NAME, "", cv);
        return result;
    }

    public synchronized ArrayList<PictureInfo> queryPicture() {
        ArrayList<PictureInfo> list = new ArrayList<>();
        Cursor cursor = mDB.query(DBHelper.TABLE_PICTURE_NAME, DBHelper.TABLE_PICTURE_COLUMNS, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                PictureInfo info = new PictureInfo();
                info.setDbId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
                info.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PATH)));
                info.setUploadStatus(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STATUS)));
                list.add(info);
            }
            cursor.close();
        }
        return list;
    }

    public synchronized long updatePicture(PictureInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_ID, info.getDbId());
        cv.put(DBHelper.COLUMN_PATH, info.getPath());
        cv.put(DBHelper.COLUMN_STATUS, info.getUploadStatus());
        long result = mDB.update(DBHelper.TABLE_PICTURE_NAME, cv, "id=?", new String[]{String.valueOf(info.getDbId())});
        return result;
    }

    public synchronized long deletePicture(PictureInfo info) {
        long result = mDB.delete(DBHelper.TABLE_PICTURE_NAME, "id=?",  new String[]{String.valueOf(info.getDbId())});
        return result;
    }

    public synchronized long insertRecord(RecordInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_PATH, info.getPath());
        cv.put(DBHelper.COLUMN_STATUS, info.getUploadStatus());
        long result = mDB.insert(DBHelper.TABLE_RECORD_NAME, "", cv);
        return result;
    }

    public synchronized ArrayList<RecordInfo> queryRecord() {
        ArrayList<RecordInfo> list = new ArrayList<>();
        Cursor cursor = mDB.query(DBHelper.TABLE_RECORD_NAME, DBHelper.TABLE_RECORD_COLUMNS, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                RecordInfo info = new RecordInfo();
                info.setDbId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
                info.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PATH)));
                info.setUploadStatus(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STATUS)));
                list.add(info);
            }
            cursor.close();
        }
        return list;
    }

    public synchronized long updateRecord(RecordInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_ID, info.getDbId());
        cv.put(DBHelper.COLUMN_PATH, info.getPath());
        cv.put(DBHelper.COLUMN_STATUS, info.getUploadStatus());
        long result = mDB.update(DBHelper.TABLE_RECORD_NAME, cv, "id=?", new String[]{String.valueOf(info.getDbId())});
        return result;
    }

    public synchronized long deleteRecord(RecordInfo info) {
        long result = mDB.delete(DBHelper.TABLE_RECORD_NAME, "id=?",  new String[]{String.valueOf(info.getDbId())});
        return result;
    }

    public synchronized long insertGas(GasInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_O2, info.O2);
        cv.put(DBHelper.COLUMN_CO, info.CO);
        cv.put(DBHelper.COLUMN_CH4, info.CH4);
        cv.put(DBHelper.COLUMN_H2S, info.H2S);
        cv.put(DBHelper.COLUMN_TIME, info.time);
        long result = mDB.insert(DBHelper.TABLE_GAS_NAME, "", cv);
        return result;
    }

    public synchronized ArrayList<GasInfo> queryGas() {
        ArrayList<GasInfo> list = new ArrayList<>();
        Cursor cursor = mDB.query(DBHelper.TABLE_GAS_NAME, DBHelper.TABLE_GAS_COLUMNS, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                GasInfo info = new GasInfo();
                info.O2 = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_O2));
                info.CO = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CO));
                info.CH4 = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CH4));
                info.H2S = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_H2S));
                info.time = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_TIME));
                list.add(info);
            }
            cursor.close();
        }
        return list;
    }

    public static class DBHelper extends SQLiteOpenHelper {
        private static final String TAG = "DBHelper";

        private static final String DATABASE_NAME = "Database.db";
        private static final int DATABASE_VERSION = 1;

        private static final String TABLE_PICTURE_NAME = "picture";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_STATUS = "status";

        private static final String TABLE_GAS_NAME = "gas";
        public static final String COLUMN_O2 = "O2";
        public static final String COLUMN_CO = "CO";
        public static final String COLUMN_CH4 = "CH4";
        public static final String COLUMN_H2S = "H2S";
        public static final String COLUMN_TIME = "time";

        private static final String TABLE_RECORD_NAME = "record";

        public static final String[] TABLE_PICTURE_COLUMNS = {
                COLUMN_ID,
                COLUMN_PATH,
                COLUMN_STATUS
        };

        public static final String[] TABLE_GAS_COLUMNS = {
                COLUMN_ID,
                COLUMN_O2,
                COLUMN_CO,
                COLUMN_CH4,
                COLUMN_H2S,
                COLUMN_TIME
        };

        public static final String[] TABLE_RECORD_COLUMNS = {
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

            sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_GAS_NAME + " ( "
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_O2 + " INTEGER,"
                    + COLUMN_CO + " INTEGER,"
                    + COLUMN_CH4 + " INTEGER,"
                    + COLUMN_H2S + " INTEGER,"
                    + COLUMN_TIME + " BIGINT)";
            database.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_RECORD_NAME + " ( "
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
