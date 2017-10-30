package joshcarroll.projects.android.taskpal.database;

/**
 * Created by Josh on 22/10/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import joshcarroll.projects.android.taskpal.data.NewTask;


/**
 * Created by Josh on 29/06/2017.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ToDoList";
    private static final String TABLE_NAME = "Tasks";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_FLAG = "flag";

    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_TITLE + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_LATITUDE + " DOUBLE PRECISION, "
                + KEY_LONGITUDE + " DOUBLE PRECISION, "
                + KEY_FLAG + " INTEGER "
                +")";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public void addTask(NewTask task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_LATITUDE, task.getLatitude());
        values.put(KEY_LONGITUDE, task.getLongitude());
        values.put(KEY_FLAG, task.getStatus());
        db.insert(TABLE_NAME, null, values);

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
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });
        db.close();
    }

    public void editTask(NewTask task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());

        db.update(TABLE_NAME, values, "id="+task.getId(), null);
        db.close();
    }

    public void updateStatus(NewTask task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_FLAG, task.getStatus());

        db.update(TABLE_NAME, contentValues, "id="+task.getId(), null);

        Log.d("Task Status", ""+task.getStatus());
        db.close();
    }
    public List<NewTask> getAllTasks() {
        List<NewTask> taskList = new ArrayList<NewTask>();

        String selectQuery = "SELECT * FROM "+ TABLE_NAME;
        int id = 1;
        String title = "title";
        String desc = "description";
        double latitude = 0.0;
        double longitude = 0.0;
        int status = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NewTask task = new NewTask(id, title, desc, latitude, longitude, status);
                //NewTask task = new NewTask(Parcel.obtain());
                task.setId(Integer.parseInt(cursor.getString(0)));
                task.setTitle(cursor.getString(1));
                task.setDescription(cursor.getString(2));
                task.setLatitude(Double.parseDouble(cursor.getString(3)));
                task.setLongitude(Double.parseDouble(cursor.getString(4)));
                task.setStatus(Integer.parseInt(cursor.getString(5)));
                // Adding task to list
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        // return task list
        return taskList;
    }
}
