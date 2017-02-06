package me.zongren.databasesample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 宗仁 on 2017/2/6.
 * All Rights Reserved By 秦皇岛商之翼网络科技有限公司.
 */

public class PersonOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEPARATOR = ",";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + PersonContract.PersonEntry.TABLE_NAME + " (" +
            PersonContract.PersonEntry._ID + " INTEGER PRIMARY KEY," +
            PersonContract.PersonEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEPARATOR +
            PersonContract.PersonEntry.COLUMN_NAME_AGE + TEXT_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PersonContract.PersonEntry.TABLE_NAME;
    private static final String DATABASE_NAME = "Person.db";

    public PersonOpenHelper(Context context) {
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

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
