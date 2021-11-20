package tech.hazm.hazmandroid.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import tech.hazm.hazmandroid.Model.HeartBeatModel;
import tech.hazm.hazmandroid.Model.MqttMsgModel;
import tech.hazm.hazmandroid.Utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseQueryClass {

    private Context context;

    public DatabaseQueryClass(Context context){
        this.context = context;
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public long insertMsg(HeartBeatModel heartBeatModel){

        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_TIME, heartBeatModel.time);
        contentValues.put(Config.COLUMN_DDT, heartBeatModel.ddt);
        contentValues.put(Config.COLUMN_USER_NAME, heartBeatModel.id);
        contentValues.put(Config.COLUMN_LAT, heartBeatModel.lat);
        contentValues.put(Config.COLUMN_LON, heartBeatModel.lon);
        contentValues.put(Config.COLUMN_SEC, heartBeatModel.sec);
        contentValues.put(Config.COLUMN_TGS, heartBeatModel.tgs);
        contentValues.put(Config.COLUMN_D_BAT, heartBeatModel.d_bat);
        contentValues.put(Config.COLUMN_GPS, heartBeatModel.gps);
        contentValues.put(Config.COLUMN_BLE, heartBeatModel.ble);
        contentValues.put(Config.COLUMN_P_BAT, heartBeatModel.p_bat);
        contentValues.put(Config.COLUMN_LOC_ACCESS, heartBeatModel.loc_access);
        contentValues.put(Config.COLUMN_TYPE, heartBeatModel.type);
        contentValues.put(Config.COLUMN_SPEED, heartBeatModel.speed);
        contentValues.put(Config.COLUMN_BEARING, heartBeatModel.bearing);
        contentValues.put(Config.COLUMN_LOC_ACC, heartBeatModel.locAcc);
        contentValues.put(Config.COLUMN_LOC_MODE, heartBeatModel.locMode);

        try {
            id = sqLiteDatabase.insertOrThrow(Config.TB_MSG_LIST, null, contentValues);
        } catch (SQLiteException e){
            LogUtil.e("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }

    public List<HeartBeatModel> getAllMsg(){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TB_MSG_LIST, null, null, null, null, null, null, null);

            /**
                 // If you want to execute raw query then uncomment below 2 lines. And comment out above line.

                 String SELECT_QUERY = String.format("SELECT %s, %s, %s, %s, %s FROM %s", Config.COLUMN_STUDENT_ID, Config.COLUMN_STUDENT_NAME, Config.COLUMN_STUDENT_REGISTRATION, Config.COLUMN_STUDENT_EMAIL, Config.COLUMN_STUDENT_PHONE, Config.TABLE_STUDENT);
                 cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
             */

            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<HeartBeatModel> modelList = new ArrayList<>();
                    do {
                        long id = cursor.getLong(cursor.getColumnIndex(Config.COLUMN_ID));
                        String time = cursor.getString(cursor.getColumnIndex(Config.COLUMN_TIME));
                        String ddt = cursor.getString(cursor.getColumnIndex(Config.COLUMN_DDT));
                        String user_name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_NAME));
                        String lat = cursor.getString(cursor.getColumnIndex(Config.COLUMN_LAT));
                        String lon = cursor.getString(cursor.getColumnIndex(Config.COLUMN_LON));
                        String sec = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SEC));
                        String tgs = cursor.getString(cursor.getColumnIndex(Config.COLUMN_TGS));
                        String d_bat = cursor.getString(cursor.getColumnIndex(Config.COLUMN_D_BAT));
                        String gps = cursor.getString(cursor.getColumnIndex(Config.COLUMN_GPS));
                        String ble = cursor.getString(cursor.getColumnIndex(Config.COLUMN_BLE));
                        String p_bat = cursor.getString(cursor.getColumnIndex(Config.COLUMN_P_BAT));
                        String loc_access = cursor.getString(cursor.getColumnIndex(Config.COLUMN_LOC_ACCESS));
                        String type = cursor.getString(cursor.getColumnIndex(Config.COLUMN_TYPE));
                        String speed = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SPEED));
                        String bearing = cursor.getString(cursor.getColumnIndex(Config.COLUMN_BEARING));
                        String loc_acc = cursor.getString(cursor.getColumnIndex(Config.COLUMN_LOC_ACC));
                        String loc_mode = cursor.getString(cursor.getColumnIndex(Config.COLUMN_LOC_MODE));

                        modelList.add(new HeartBeatModel(time, ddt, user_name, lat, lon, sec, tgs, d_bat, gps, ble, p_bat, loc_access, type, speed, bearing, loc_acc, loc_mode));


                    }   while (cursor.moveToNext());

                    return modelList;
                }
        } catch (Exception e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public void deleteAllMsg(String time){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        sqLiteDatabase.delete(Config.TB_MSG_LIST, Config.COLUMN_TIME + "=" + time,  null);
        sqLiteDatabase.close();
    }
}