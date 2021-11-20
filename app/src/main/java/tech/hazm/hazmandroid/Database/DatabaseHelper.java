package tech.hazm.hazmandroid.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper databaseHelper;

    // All Static variables
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = Config.DATABASE_NAME;

    // Constructor
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public static synchronized DatabaseHelper getInstance(Context context){
        if(databaseHelper==null){
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tables SQL execution
        String CREATE_MSG_TABLE = "CREATE TABLE " + Config.TB_MSG_LIST + "("
                + Config.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_TIME + " TEXT NOT NULL, "
                + Config.COLUMN_DDT + " TEXT NOT NULL, "
                + Config.COLUMN_USER_NAME + " TEXT NOT NULL, "
                + Config.COLUMN_LAT + " TEXT NOT NULL, "
                + Config.COLUMN_LON + " TEXT NOT NULL, "
                + Config.COLUMN_SEC + " TEXT NOT NULL, "
                + Config.COLUMN_TGS + " TEXT NOT NULL, "
                + Config.COLUMN_D_BAT + " TEXT NOT NULL, "
                + Config.COLUMN_GPS + " TEXT NOT NULL, "
                + Config.COLUMN_BLE + " TEXT NOT NULL, "
                + Config.COLUMN_P_BAT + " TEXT NOT NULL, "
                + Config.COLUMN_LOC_ACCESS + " TEXT NOT NULL, "
                + Config.COLUMN_TYPE + " TEXT NOT NULL, "
                + Config.COLUMN_SPEED + " TEXT NOT NULL, "
                + Config.COLUMN_BEARING + " TEXT NOT NULL, "
                + Config.COLUMN_LOC_ACC + " TEXT NOT NULL, "
                + Config.COLUMN_LOC_MODE + " TEXT NOT NULL "
                + ")";

        Logger.d("Table create SQL: " + CREATE_MSG_TABLE);

        db.execSQL(CREATE_MSG_TABLE);

        Logger.d("DB created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Config.TB_MSG_LIST);

        // Create tables again
        onCreate(db);
    }

}
