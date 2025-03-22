package com.example.mynotes;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import java.util.ArrayList;

public class TaskWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TaskRemoteViewsFactory(getApplicationContext());
    }

//    private class TaskRemoteViewsFactory implements RemoteViewsFactory {
//        private Context context;
//        private ArrayList<Task> taskList;
//
//        TaskRemoteViewsFactory(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        public void onCreate() {
//            loadTasks();
//        }
//
//        @Override
//        public void onDataSetChanged() {
//            loadTasks();
//        }
//
//        private void loadTasks() {
//            TaskDatabaseHelper dbHelper = new TaskDatabaseHelper(context);
//            taskList = dbHelper.getAllTasks();
//        }
//
//        @Override
//        public RemoteViews getViewAt(int position) {
//            RemoteViews row = new RemoteViews(context.getPackageName(), android.R.layout.simple_list_item_1);
//            row.setTextViewText(android.R.id.text1, taskList.get(position).getTitle());
//            return row;
//        }
//
//        @Override public int getCount() { return taskList.size(); }
//        @Override public long getItemId(int position) { return position; }
//        @Override public boolean hasStableIds() { return true; }
//        @Override public RemoteViews getLoadingView() { return null; }
//        @Override public int getViewTypeCount() { return 1; }
//        @Override public void onDestroy() { taskList.clear(); }
//    }
private class TaskRemoteViewsFactory implements RemoteViewsFactory {
    private Context context;
    private ArrayList<Task> taskList;
    private TaskDatabaseHelper dbHelper;

    TaskRemoteViewsFactory(Context context) {
        this.context = context;
        this.dbHelper = new TaskDatabaseHelper(context);
    }

    @Override
    public void onCreate() {
        loadTasks();
    }

    @Override
    public void onDataSetChanged() {
        loadTasks();
    }

    private void loadTasks() {
        taskList = dbHelper.getAllTasks();
        if (taskList == null) {
            taskList = new ArrayList<>();  // Prevent null pointer issues
        }
    }

//    @Override
//    public RemoteViews getViewAt(int position) {
//        if (taskList.isEmpty() || position >= taskList.size()) return null;
//
//        Task task = taskList.get(position);
//        RemoteViews row = new RemoteViews(context.getPackageName(), android.R.layout.simple_list_item_1);
//        row.setTextViewText(android.R.id.text1, task.getTitle());
//
//        // Mark task as completed when clicked
//        Intent fillInIntent = new Intent();
//        fillInIntent.putExtra("TASK_ID", task.getId());
//        row.setOnClickFillInIntent(android.R.id.text1, fillInIntent);
//
//        return row;
//    }
@Override
public RemoteViews getViewAt(int position) {
    Task task = taskList.get(position);
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_task_item);

    // Apply strikethrough if task is completed
    if (task.isCompleted()) {
        views.setTextViewText(R.id.widgetTaskText, task.getTitle() + " [Completed]");
    } else {
        views.setTextViewText(R.id.widgetTaskText, task.getTitle());
    }

    Intent clickIntent = new Intent();
    clickIntent.setAction("com.example.mynotes.COMPLETE_TASK");
    clickIntent.putExtra("task_id", task.getId());

//    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId(), clickIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//    views.setOnClickPendingIntent(R.id.widgetCheckButton, pendingIntent);

    return views;
}


    @Override public int getCount() { return taskList.size(); }
    @Override public long getItemId(int position) { return position; }
    @Override public boolean hasStableIds() { return true; }
    @Override public RemoteViews getLoadingView() { return null; }
    @Override public int getViewTypeCount() { return 1; }
    @Override public void onDestroy() { taskList.clear(); }
}

}
