package com.example.mynotes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private ArrayList<Task> taskList;
    private TaskDatabaseHelper databaseHelper;
    private Context context;

    public TaskAdapter(Context context, ArrayList<Task> taskList) {
        this.taskList = taskList;
        this.databaseHelper = new TaskDatabaseHelper(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.checkBoxTask.setText(task.getTitle());

        // Remove old listener to avoid unwanted triggers
        holder.checkBoxTask.setOnCheckedChangeListener(null);

        holder.checkBoxTask.setChecked(task.isCompleted());

        holder.checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            databaseHelper.toggleTaskCompletion(task.getId()); // Call the renamed method
            task.setCompleted(isChecked);

//            // Update the widget when task completion changes
//            Intent intent = new Intent("com.example.mynotes.UPDATE_WIDGET");
//            context.sendBroadcast(intent);
            Intent intent = new Intent(context, TaskWidgetProvider.class);
            intent.setAction("com.example.mynotes.UPDATE_WIDGET");
            context.sendBroadcast(intent);
        });

        // Handle long-press to delete a task
        holder.checkBoxTask.setOnLongClickListener(v -> {
            removeTask(holder.getAdapterPosition());
            Toast.makeText(v.getContext(), "Task Deleted", Toast.LENGTH_SHORT).show();
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void removeTask(int position) {
        if (position >= 0 && position < taskList.size()) {
            Task task = taskList.get(position);
            databaseHelper.deleteTask(task.getId());  // Delete from database
            taskList.remove(position);  // Remove from list
            notifyItemRemoved(position);  // Notify RecyclerView
            notifyItemRangeChanged(position, taskList.size()); // Refresh list
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxTask;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBoxTask = itemView.findViewById(R.id.checkBoxTask);
        }
    }
}
