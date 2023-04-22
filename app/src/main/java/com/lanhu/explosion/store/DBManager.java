package com.lanhu.explosion.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.bean.GasItem;
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
        long result = mDB.delete(DBHelper.TABLE_PICTURE_NAME, "id=?", new String[]{String.valueOf(info.getDbId())});
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
        long result = mDB.delete(DBHelper.TABLE_RECORD_NAME, "id=?", new String[]{String.valueOf(info.getDbId())});
        return result;
    }

    //select max(xxx) from  where type=1;select max(case when type=1 then xxx end )
    public synchronized void insertGas(ArrayList<GasItem> list) {
        for (GasItem item : list) {
            insertGas(item);
        }
    }

    public synchronized long insertGas(GasItem item) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_TYPE, item.getType());
        cv.put(DBHelper.COLUMN_STANDARD, item.getStandard());
        cv.put(DBHelper.COLUMN_CHANNEL, item.getChannel());
        cv.put(DBHelper.COLUMN_VALUE, item.getValue());
        cv.put(DBHelper.COLUMN_TIME, item.getTime());
        long result = mDB.insert(DBHelper.TABLE_GAS_NAME, "", cv);
        return result;
    }

    public synchronized ArrayList<GasItem> queryGas() {
        ArrayList<GasItem> list = new ArrayList<>();
        Cursor cursor = mDB.query(DBHelper.TABLE_GAS_NAME, DBHelper.TABLE_GAS_COLUMNS, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                GasItem item = new GasItem();
                item.setType(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_TYPE)));
                item.setStandard(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STANDARD)));
                item.setChannel(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CHANNEL)));
                item.setValue(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_VALUE)));
                item.setTime(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_TIME)));
                list.add(item);
            }
            cursor.close();
        }
        return list;
    }

    public synchronized GasItem queryGasLastTime(int type) {
        GasItem item = null;
        Cursor cursor = mDB.query(DBHelper.TABLE_GAS_NAME, DBHelper.TABLE_GAS_COLUMNS, "type=?",
                new String[]{String.valueOf(type)}, null, null, DBHelper.COLUMN_TIME + " desc");
        if (cursor != null && cursor.moveToNext()) {
            item = new GasItem();
            item.setType(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_TYPE)));
            item.setStandard(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STANDARD)));
            item.setChannel(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CHANNEL)));
            item.setValue(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_VALUE)));
            item.setTime(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_TIME)));
        }
        cursor.close();
        return item;
    }

    public synchronized long deleteGas(GasItem item) {
        long result = mDB.delete(DBHelper.TABLE_RECORD_NAME, "id=?", new String[]{String.valueOf(item.getDbId())});
        return result;
    }

    public synchronized long updateGas(GasItem item) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_ID, item.getDbId());
        cv.put(DBHelper.COLUMN_TYPE, item.getType());
        cv.put(DBHelper.COLUMN_STANDARD, item.getStandard());
        cv.put(DBHelper.COLUMN_CHANNEL, item.getChannel());
        cv.put(DBHelper.COLUMN_VALUE, item.getValue());
        cv.put(DBHelper.COLUMN_TIME, item.getTime());
        long result = mDB.update(DBHelper.TABLE_RECORD_NAME, cv, "id=?", new String[]{String.valueOf(item.getDbId())});
        return result;
    }

    public static class DBHelper extends SQLiteOpenHelper {
        private static final String TAG = "DBHelper";

        private static final String DATABASE_NAME = "Database.db";
        private static final int DATABASE_VERSION = 1;

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_STANDARD = "standard";
        public static final String COLUMN_CHANNEL = "channel";
        public static final String COLUMN_VALUE = "value";

        private static final String TABLE_PICTURE_NAME = "picture";
        private static final String TABLE_GAS_NAME = "gas";
        private static final String TABLE_RECORD_NAME = "record";

        public static final String[] TABLE_PICTURE_COLUMNS = {
                COLUMN_ID,
                COLUMN_PATH,
                COLUMN_STATUS
        };

        public static final String[] TABLE_GAS_COLUMNS = {
                COLUMN_ID,
                COLUMN_TYPE,
                COLUMN_STANDARD,
                COLUMN_CHANNEL,
                COLUMN_VALUE,
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
                    + TABLE_RECORD_NAME + " ( "
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_PATH + " TEXT,"
                    + COLUMN_STATUS + " INTEGER)";
            database.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_GAS_NAME + " ( "
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TYPE + " INTEGER,"
                    + COLUMN_STANDARD + " INTEGER,"
                    + COLUMN_CHANNEL + " INTEGER,"
                    + COLUMN_VALUE + " INTEGER,"
                    + COLUMN_TIME + " BIGINT)";
            database.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }


}
