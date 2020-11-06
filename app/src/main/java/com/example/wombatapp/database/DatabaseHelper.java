package com.example.wombatapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Diet.db";

    private static final String userTableName = "UserTable";
    private static final String username = "username";
    private static final String gender = "gender";
    private static final String height = "height";
    private static final String age = "age";

    private static final String measurementtable = "measurement";
    private static final String fat = "fat";
    private static final String muscle = "muscle";
    private static final String weight = "weight";

    private static final String pulsemeasurementtable = "pmeasurement";
    private static final String pulserate = "pulserate";
    private static final String maxpulserate = "maxpulserate";
    private static final String hrv = "hrv";

    private static final String tablestatement3 = "CREATE TABLE "
            + pulsemeasurementtable + "(" + pulserate + " String,"
            + maxpulserate + " String," + hrv + " String)";


    private static final String tablestatement = "CREATE TABLE "
            + userTableName + "(" + username + " String,"
            + gender + " String," + height + " String," + age + " String)";

    private static final String tablestatement2 = "CREATE TABLE "
            + measurementtable + "(" + username + " String," + weight + " String,"
            + muscle + " String," + fat + " String)";


    SQLiteDatabase db;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tablestatement);
        db.execSQL(tablestatement2);
        db.execSQL(tablestatement3);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + userTableName);
        db.execSQL("DROP TABLE IF EXISTS " + measurementtable);
        db.execSQL("DROP TABLE IF EXISTS " + pulsemeasurementtable);

    }

    public boolean adduser(String username, String gender, String age, String height) throws SQLiteException {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(this.username, username);
        cv.put(this.gender, gender);
        cv.put(this.age, age);
        cv.put(this.height, height);

        long result = db.insert(userTableName, null, cv);
        return result != 0;
    }

    public boolean addmeaurement(String username, String weight, String muscle, String fat) throws SQLiteException {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(this.username, username);
        cv.put(this.weight, weight);
        cv.put(this.muscle, muscle);
        cv.put(this.fat, fat);

        long result = db.insert(measurementtable, null, cv);
        return result != 0;
    }

    public boolean addpulsemeaurement(String pulserate,String maxpulse,String hrv) {
        db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(this.pulserate,pulserate);
        cv.put(this.maxpulserate,maxpulse);
        cv.put(this.hrv,hrv);
        long result = db.insert(pulsemeasurementtable, null, cv);
        return result != 0;
    }
    public void retrivepulserate(){

    }

    //TimeOut
    public boolean Updatemearement(String username, String weight, String muscle, String fat) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(this.username, username);
        cv.put(this.weight, weight);
        cv.put(this.muscle, muscle);
        cv.put(this.fat, fat);
        long result = db.update(measurementtable, cv, this.username + "='" + username + "'", null);
        return result != 0;
    }

    public Cursor getusers() {
        db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from " + userTableName, null);
        return cur;
    }

    public Cursor getuserbyname(String username) {
        db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from " + userTableName + " where " + this.username + "='" + username + "'", null);
        return cur;
    }

    public Cursor getMeasurement(String username) {
        db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from " + measurementtable + " where " + this.username + "='" + username + "'", null);
        return cur;
    }
}
