package com.example.mynotes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TASK = "task";
    private static final String COLUMN_COMPLETED = "completed";

    private Context context;

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK + " TEXT NOT NULL, " +
                COLUMN_COMPLETED + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // ✅ Modify this to trigger a widget update
    public boolean addTask(String taskText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, taskText);
        values.put(COLUMN_COMPLETED, 0);

        long result = db.insert(TABLE_TASKS, null, values);
        db.close();

        if (result != -1) {
            updateWidget(context);  // Notify the widget
        }
        return result != -1;
    }

    // ✅ Modify this to trigger a widget update
    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
        db.close();

        updateWidget(context);  // Notify the widget

    }

    // ✅ Modify this to toggle completion state and update widget
    public void toggleTaskCompletion(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{COLUMN_COMPLETED}, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)}, null, null, null);

        if (cursor.moveToFirst()) {
            int newCompletedState = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1 ? 0 : 1;
            ContentValues values = new ContentValues();
            values.put(COLUMN_COMPLETED, newCompletedState);
            db.update(TABLE_TASKS, values, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
        }
        cursor.close();
        db.close();

        // Notify widget after updating completion status
//        notifyWidgetUpdate();
    }
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String taskText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK));
                    boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1;
                    taskList.add(new Task(id, taskText, isCompleted));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();
        return taskList;
    }
    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, TaskWidgetProvider.class);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.widgetTaskList);
    }
    // Add this method inside TaskDatabaseHelper
    public void updateTaskCompletion(int taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETED, isCompleted ? 1 : 0);

        db.update(TABLE_TASKS, values, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
        db.close();

        updateWidget(context);  // Refresh the widget

    }

}
