package com.example.exammp3player;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MusicDataDBHelper extends SQLiteOpenHelper {
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public MusicDataDBHelper(Context context, String name) {
        super(context, name, null, 1);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create Table likeTBL(title char(30) not null ,artist char(30) not null,song char(60) not null primary key,likeCount Integer default 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists likeTBL");
        onCreate(sqLiteDatabase);
    }

    public boolean likeSong(MusicData musicData) {
        boolean result = false;
        try {
            sqLiteDatabase = this.getWritableDatabase();
            sqLiteDatabase.execSQL("update likeTBL set likeCount=" + 1 + " where song='" + musicData.getTitle() + musicData.getSinger() + "';");
            Log.d("update DB", "성공");
            result = true;
        } catch (SQLException e) {
            Log.d("update DB", e.getMessage());

            result = false;
        } finally {
            sqLiteDatabase.close();
        }
        return result;

    }

    public boolean unlikeSong(MusicData musicData) {
        boolean result = false;
        try {
            sqLiteDatabase = this.getWritableDatabase();
            sqLiteDatabase.execSQL("update likeTBL set likeCount=" + 0 + " where song='" + musicData.getTitle() + musicData.getSinger() + "';");
            Log.d("unlikeSong", "성공");
            result = true;
        } catch (SQLException e) {
            Log.d("unlikeSong", e.getMessage());

            result = false;
        } finally {
            sqLiteDatabase.close();
        }
        return result;

    }

    public ArrayList<MusicData> likeSelectMethod() {
        ArrayList<MusicData> arrayList = new ArrayList<MusicData>();
        sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select title,artist from likeTBL where likeCount=1;", null);
        while (cursor.moveToNext()) {
            arrayList.add(new MusicData(cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();
        sqLiteDatabase.close();
        return arrayList;
    }

    public ArrayList<MusicData> userSelectMethod(String listName) {
        ArrayList<MusicData> arrayList = new ArrayList<MusicData>();
        sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select title,artist from "+listName +"TBL;", null);

        while (cursor.moveToNext()) {
            arrayList.add(new MusicData(cursor.getString(0), cursor.getString(1)));
            Log.d("userSelectMethod",cursor.getString(0)+ cursor.getString(1));
        }
        cursor.close();
        sqLiteDatabase.close();
        return arrayList;
    }

    public boolean insertMethod(String tblName,MusicData musicData) {
        boolean result = false;
        try {
            sqLiteDatabase = this.getWritableDatabase();
            sqLiteDatabase.execSQL("insert into "+tblName+"TBL (title,artist,song) values('" + musicData.getTitle() + "','" + musicData.getSinger() + "','" + musicData.getTitle() + musicData.getSinger() + "');");
            Log.d("insert DB", "성공");
            result = true;
        } catch (SQLException e) {
            Log.d("insert DB", e.getMessage());
            result = false;
        } finally {
            sqLiteDatabase.close();
        }
        return result;

    }

    public boolean initMethod() {
        boolean result = false;
        try {
            sqLiteDatabase = this.getWritableDatabase();
            this.onUpgrade(sqLiteDatabase, 1, 2);
            sqLiteDatabase.close();
            result = true;
        } catch (SQLException e) {
            Log.d("delete DB", e.getMessage());
            result = false;
        } finally {
            sqLiteDatabase.close();
        }
        return result;
    }

    public void createTable(String sqlStatement) {
        sqLiteDatabase = this.getWritableDatabase();
        this.sqLiteDatabase.execSQL(sqlStatement);

    }

    public void dropTable(String tblName) {
        try {
            sqLiteDatabase = this.getWritableDatabase();
            this.sqLiteDatabase.execSQL("drop table if exists " + tblName + "TBL");
            Log.d("dropTable", "성공");
        } catch (SQLException e) {
            Log.d("dropTable", e.getMessage());
        }


    }

    public ArrayList<String> getTableNames() {
        sqLiteDatabase = this.getWritableDatabase();
        ArrayList<String> tableNameList = new ArrayList<String>();
        Cursor c = sqLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                tableNameList.add(c.getString(0));
                c.moveToNext();
            }
        }

        c.close();
        return tableNameList;
    }

}
