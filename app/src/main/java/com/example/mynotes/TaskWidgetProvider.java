package com.example.mynotes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.List;

public class TaskWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_ADD_TASK = "com.example.mynotes.ADD_TASK";
    public static final String ACTION_ITEM_CLICK = "com.example.mynotes.ITEM_CLICK";
    public static final String EXTRA_ITEM_POSITION = "com.example.mynotes.ITEM_POSITION";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Intent for opening the app when clicking "Add Task"
            Intent addTaskIntent = new Intent(context, MainActivity.class);
            PendingIntent addTaskPendingIntent = PendingIntent.getActivity(context, 0, addTaskIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widgetAddTaskButton, addTaskPendingIntent);

            // Setting up the StackView
            Intent serviceIntent = new Intent(context, TaskWidgetService.class);
            views.setRemoteAdapter(R.id.widgetTaskList, serviceIntent);

            // Click event for tasks
            Intent clickIntent = new Intent(context, TaskWidgetProvider.class);
            clickIntent.setAction(ACTION_ITEM_CLICK);
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setPendingIntentTemplate(R.id.widgetTaskList, clickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

//        if (intent.getAction() != null && intent.getAction().equals("com.example.mynotes.TOGGLE_TASK")) {
//            int taskId = intent.getIntExtra("task_id", -1);
//            boolean isCompleted = intent.getBooleanExtra("task_completed", false);
//
//            if (taskId != -1) {
//                TaskDatabaseHelper db = new TaskDatabaseHelper(context);
//                db.updateTaskCompletion(taskId, !isCompleted); // Toggle task completion
//
//                // Update widget after change
//                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//                ComponentName thisWidget = new ComponentName(context, TaskWidgetProvider.class);
//                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
//                onUpdate(context, appWidgetManager, appWidgetIds);
//            }
//        }
        if ("com.example.mynotes.COMPLETE_TASK".equals(intent.getAction())) {
            int taskId = intent.getIntExtra("task_id", -1);
            boolean isCompleted = intent.getBooleanExtra("task_completed", false);

            if (taskId != -1) {
                TaskDatabaseHelper db = new TaskDatabaseHelper(context);
                db.updateTaskCompletion(taskId, !isCompleted); // ✅ Toggle completion
                db.close();  // ✅ Close DB to prevent leaks

                // ✅ Refresh widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName thisWidget = new ComponentName(context, TaskWidgetProvider.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetTaskList);
            }
        }
    }

}


//public class TaskWidgetProvider extends AppWidgetProvider {
//
//    @Override
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        for (int appWidgetId : appWidgetIds) {
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//
//            // Intent to open MainActivity when "Add Task" is clicked
//            Intent intent = new Intent(context, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(
//                    context,
//                    0,
//                    intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//            );
//
//            views.setOnClickPendingIntent(R.id.widgetAddTaskButton, pendingIntent);
//
//            // Set up ListView for tasks
//            Intent listViewIntent = new Intent(context, TaskWidgetService.class);
//            views.setRemoteAdapter(R.id.widgetTaskList, listViewIntent);
//
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//        }
//    }
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        super.onReceive(context, intent);
//
//        if ("com.example.mynotes.UPDATE_WIDGET".equals(intent.getAction())) {
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//            ComponentName componentName = new ComponentName(context, TaskWidgetProvider.class);
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.widgetTaskList);
//        }
//    }
//
//}
