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
    private static final String DATE_AND_TIME = "date_and_time";
    private static final String REMARKS = "remarks";

    private static final String USER_TABLE_NAME = "UserTable";
    private static final String USERNAME = "user_name";
    private static final String GENDER = "gender";
    private static final String HEIGHT = "height";
    private static final String BIRTHDATE = "birthdate";
    private static final String PERSONAL_GOALS = "personal_goals";
    private static final String USER_ICON_ID = "personal_goals";


    private static final String HEART_TABLE_NAME = "heartTable";
    private static final String PULSE_RATE = "pulse_rate";
    private static final String BLOOD_OXYGEN = "blood_oxygen";

    private static final String WEIGHT_TABLE_NAME = "weightTable";
    private static final String WEIGHT = "weight";
    private static final String MUSCLE_MASS = "muscle_mass";
    private static final String FAT = "fat";

    private static final String SLEEP_TABLE_NAME = "weightTable";
    private static final String SLEEP_TIME = "sleep_time";
    private static final String DEEP_SLEEP_TIME = "deep_sleep_time";

    private static final String BLOOD_PRESSURE_TABLE_NAME = "bloodPressureTable";
    private static final String SYSTOLIC_PRESSURE = "systolic_pressure";
    private static final String DIASTOLIC_PRESSURE = "diastolic_pressure";
    private static final String HEART_PULSE_RATE_FOR_BLOOD_PRESSURE = "heart_pulse_rate";

    private static final String ECG_TABLE_NAME = "ecgTable";
    private static final String RRI_MAXIMUM = "RRI_maximum";
    private static final String RRI_MINIMUM = "RRI_minimum";
    private static final String HEART_RATE_FOR_ECG = "heart_rate";
    private static final String HRV_ECG = "hrv";
    private static final String MOOD = "mood";
    private static final String RESPIRATORY_RATE = "respiratory_rate";

    private static final String tableStatementCreateUserTable = "CREATE TABLE " + USER_TABLE_NAME + "(" + USERNAME + " String," + GENDER + " String," + HEIGHT + " String,"  + PERSONAL_GOALS + " String," + USER_ICON_ID + " String," + BIRTHDATE + " String)";
    private static final String tableStatementCreateHeartTable = "CREATE TABLE " + HEART_TABLE_NAME + "(" + USERNAME + " String," + PULSE_RATE + " String," + BLOOD_OXYGEN + " String," + REMARKS + " String," + DATE_AND_TIME + " String)";
    private static final String tableStatementCreateWeightTable = "CREATE TABLE " + WEIGHT_TABLE_NAME + "(" + USERNAME + " String," + WEIGHT + " String," + MUSCLE_MASS + " String,"  + FAT + " String," + REMARKS + " String," + DATE_AND_TIME + " String)";
    private static final String tableStatementCreateSleepTable = "CREATE TABLE " + SLEEP_TABLE_NAME + "(" + USERNAME + " String," + SLEEP_TIME + " String," + DEEP_SLEEP_TIME + " String," + REMARKS + " String," + DATE_AND_TIME + " String)";
    private static final String tableStatementCreateBloodPressureTable = "CREATE TABLE " + BLOOD_PRESSURE_TABLE_NAME + "(" + USERNAME + " String," + SYSTOLIC_PRESSURE + " String," + DIASTOLIC_PRESSURE + " String,"  + HEART_PULSE_RATE_FOR_BLOOD_PRESSURE + " String," + REMARKS + " String," + DATE_AND_TIME + " String)";
    private static final String tableStatementCreateEcgTable = "CREATE TABLE " + ECG_TABLE_NAME + "(" + USERNAME + " String," + RRI_MAXIMUM + " String," + RRI_MINIMUM + " String,"  + HEART_RATE_FOR_ECG + " String," + HRV_ECG + " String," + MOOD + " String," + RESPIRATORY_RATE + " String," + REMARKS + " String," + DATE_AND_TIME + " String)";

    SQLiteDatabase db;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tableStatementCreateUserTable);
        db.execSQL(tableStatementCreateWeightTable);
        db.execSQL(tableStatementCreateHeartTable);
        db.execSQL(tableStatementCreateSleepTable);
        db.execSQL(tableStatementCreateBloodPressureTable);
        db.execSQL(tableStatementCreateEcgTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HEART_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WEIGHT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SLEEP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BLOOD_PRESSURE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ECG_TABLE_NAME);

    }

    public boolean addUser(String username, String gender, String age, String height, String personalGoals, String userIconId) throws SQLiteException {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(this.USERNAME, username);
        cv.put(this.GENDER, gender);
        cv.put(this.HEIGHT, height);
        cv.put(this.PERSONAL_GOALS, personalGoals);
        cv.put(this.USER_ICON_ID, personalGoals);
        cv.put(this.BIRTHDATE, age);

        long result = db.insert(USER_TABLE_NAME, null, cv);
        return result != 0;
    }

    public boolean addMeasurementHeart(String username,String pulserate, String bloodOxygen, String remarks, String dataAndTime) {
        db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(this.USERNAME,username);
        cv.put(this.PULSE_RATE,pulserate);
        cv.put(this.BLOOD_OXYGEN,bloodOxygen);
        cv.put(this.REMARKS, remarks);
        cv.put(this.DATE_AND_TIME,dataAndTime);
        long result = db.insert(HEART_TABLE_NAME, null, cv);
        return result != 0;
    }

    public boolean addMeasurementWeight(String username, String weight, String muscle, String fat,String remarks, String dateAndTime) throws SQLiteException {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(this.USERNAME, username);
        cv.put(this.WEIGHT, weight);
        cv.put(this.MUSCLE_MASS, muscle);
        cv.put(this.FAT, fat);
        cv.put(this.REMARKS, remarks);
        cv.put(this.DATE_AND_TIME, dateAndTime);

        long result = db.insert(WEIGHT_TABLE_NAME, null, cv);
        return result != 0;
    }

    public boolean addMeasurementSleep(String username,String sleepTime,String deepSleepTime ,String remarks, String dataAndTime) {
        db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(this.USERNAME,username);
        cv.put(this.SLEEP_TIME,sleepTime);
        cv.put(this.DEEP_SLEEP_TIME,deepSleepTime);
        cv.put(this.REMARKS, remarks);
        cv.put(this.DATE_AND_TIME,dataAndTime);
        long result = db.insert(SLEEP_TABLE_NAME, null, cv);
        return result != 0;
    }

    public boolean addMeasurementBloodPressure(String username, String systolicPressure, String diastolicPressure, String heartPulseRate ,String remarks, String dataAndTime) {
        db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(this.USERNAME,username);
        cv.put(this.SYSTOLIC_PRESSURE,systolicPressure);
        cv.put(this.DIASTOLIC_PRESSURE,diastolicPressure);
        cv.put(this.HEART_PULSE_RATE_FOR_BLOOD_PRESSURE,heartPulseRate);
        cv.put(this.REMARKS, remarks);
        cv.put(this.DATE_AND_TIME,dataAndTime);
        long result = db.insert(BLOOD_PRESSURE_TABLE_NAME, null, cv);
        return result != 0;
    }

    public boolean addMeasurementEcg(String username,String rriMaximum,String rriMinimum,String heartRate, String hrv,String mood, String respiratoryRate, String remarks, String dataAndTime) {
        db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(this.USERNAME,username);
        cv.put(this.RRI_MAXIMUM,rriMaximum);
        cv.put(this.RRI_MINIMUM,rriMinimum);
        cv.put(this.HEART_RATE_FOR_ECG,heartRate);
        cv.put(this.HRV_ECG,hrv);
        cv.put(this.MOOD,mood);
        cv.put(this.RESPIRATORY_RATE,respiratoryRate);
        cv.put(this.REMARKS, remarks);
        cv.put(this.DATE_AND_TIME,dataAndTime);
        long result = db.insert(ECG_TABLE_NAME, null, cv);
        return result != 0;
    }

    public Cursor getusers() {
        db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from " + USER_TABLE_NAME, null);
        return cur;
    }

    public Cursor getuserbyname(String username) {
        db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from " + USER_TABLE_NAME + " where " + this.USERNAME + "='" + username + "'", null);
        return cur;
    }

    public Cursor getWeightData(String username) {
        db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from " + WEIGHT_TABLE_NAME + " where " + this.USERNAME + "='" + username + "'", null);
        return cur;
    }

    public Cursor getBloodPressureData(String username) {
        db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from " + BLOOD_PRESSURE_TABLE_NAME + " where " + this.USERNAME + "='" + username + "'", null);
        return cur;
    }

    public Cursor getSleepData(String username) {
        db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from " + SLEEP_TABLE_NAME + " where " + this.USERNAME + "='" + username + "'", null);
        return cur;
    }
    public Cursor getEcgData(String username) {
        db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from " + ECG_TABLE_NAME + " where " + this.USERNAME + "='" + username + "'", null);
        return cur;
    }



//    //TimeOut
//    public boolean Updatemearement(String username, String weight, String muscle, String fat) {
//        db = this.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(this.USERNAME, username);
//        cv.put(this.weight, weight);
//        cv.put(this.muscle, muscle);
//        cv.put(this.fat, fat);
//        long result = db.update(measurementtable, cv, this.USERNAME + "='" + username + "'", null);
//        return result != 0;
//    }
}
