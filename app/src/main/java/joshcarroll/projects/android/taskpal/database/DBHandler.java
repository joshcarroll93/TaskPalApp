package joshcarroll.projects.android.taskpal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import joshcarroll.projects.android.taskpal.data.NewTask;
import joshcarroll.projects.android.taskpal.data.Setting;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "ToDoList";
    private static final String TASK_TABLE_NAME = "Tasks";
    private static final String SETTING_TABLE_NAME = "Settings";

    private static final String KEY_ID = "Id";
    private static final String KEY_TITLE = "Title";
    private static final String KEY_DESCRIPTION = "Description";
    private static final String KEY_LATITUDE = "Latitude";
    private static final String KEY_LONGITUDE = "Longitude";
    private static final String KEY_ADDRESS = "Address";
    private static final String KEY_FLAG = "Flag";

    private static final String KEY_NOTIFICATION_SETTING = "notification_setting";


    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTable = "CREATE TABLE ";

        String CREATE_TASK_TABLE = createTable + TASK_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_TITLE + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_LATITUDE + " DOUBLE PRECISION, "
                + KEY_LONGITUDE + " DOUBLE PRECISION, "
                + KEY_ADDRESS + " TEXT, "
                + KEY_FLAG + " INTEGER "
                + ")";

        String CREATE_SETTINGS_TABLE = createTable + SETTING_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_NOTIFICATION_SETTING + " INTEGER "
                + ")";


        sqLiteDatabase.execSQL(CREATE_SETTINGS_TABLE);

        sqLiteDatabase.execSQL(CREATE_TASK_TABLE);


    }

    public boolean isTableExists(String tableName, boolean openDb) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        if(openDb) {
            if(mDatabase == null || !mDatabase.isOpen()) {
                mDatabase = getReadableDatabase();
            }

            if(!mDatabase.isReadOnly()) {
                mDatabase.close();
                mDatabase = getReadableDatabase();
            }
        }

        Cursor cursor = mDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        String dropTable = "DROP TABLE IF EXISTS ";

        sqLiteDatabase.execSQL(dropTable + TASK_TABLE_NAME);
        sqLiteDatabase.execSQL(dropTable + SETTING_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addTask(NewTask task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_LATITUDE, task.getLatitude());
        values.put(KEY_LONGITUDE, task.getLongitude());
        values.put(KEY_ADDRESS, task.getAddress());
        values.put(KEY_FLAG, task.getStatus());
        db.insert(TASK_TABLE_NAME, null, values);

        Log.d("entry",
                task.getTitle() + "\n"
                        + task.getDescription() + "\n"
                        + task.getLatitude() + "\n"
                        + task.getLongitude() + "\n"
                        + task.getStatus()
        );
        db.close();
    }

    public void deleteTask(NewTask task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TASK_TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });
        db.close();
    }

    public void editTask(NewTask task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());

        db.update(TASK_TABLE_NAME, values, "id="+task.getId(), null);
        db.close();
    }

    public void updateStatus(NewTask task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_FLAG, task.getStatus());

        db.update(TASK_TABLE_NAME, contentValues, "id="+task.getId(), null);

        Log.d("Task Status", ""+task.getStatus());
        db.close();
    }
    public List<NewTask> getAllTasks() {

        List<NewTask> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM "+ TASK_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NewTask task = new NewTask(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        Double.parseDouble(cursor.getString(3)),
                        Double.parseDouble(cursor.getString(4)),
                        cursor.getString(5),
                        Integer.parseInt(cursor.getString(6))
                );

                // Adding task to list
                taskList.add(task);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return task list
        return taskList;

    }

    public void setNotification(int position){
        //0 is off 1 is on
        SQLiteDatabase db = this.getWritableDatabase();


//        db.delete(SETTING_TABLE_NAME, KEY_ID + " = ?",
//                new String[] { String.valueOf(0) });

        ContentValues values = new ContentValues();

        values.put(KEY_ID, 0);
        values.put(KEY_NOTIFICATION_SETTING, position);

        db.insert(SETTING_TABLE_NAME, null, values);
        db.close();
    }

    public int getNotificationSetting(){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + SETTING_TABLE_NAME;

        List<Setting> settingList = new ArrayList<>();
        int notification= 0;
        Cursor cursor = db.rawQuery(selectQuery, null);

        try{
            if (cursor.moveToFirst()) {
                do{
                    Setting settings = new Setting(
                            Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(1))
                    );
                    settingList.add(settings);
                    notification = settingList.get(0).getNotification();
                }while (cursor.moveToNext());
            }
        }
        catch (IllegalStateException ise){
                ise.printStackTrace();
        }
        cursor.close();
        db.close();
        return notification;
    }
}
