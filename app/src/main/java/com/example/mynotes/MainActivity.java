package com.example.mynotes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTask;
    private Button btnAddTask;
    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private TaskDatabaseHelper databaseHelper;
    private ArrayList<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_col));


        // Initialize UI components
        editTextTask = findViewById(R.id.editTextTask);
        btnAddTask = findViewById(R.id.btnAddTask);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

        // Initialize database and task list
        databaseHelper = new TaskDatabaseHelper(this);
        taskList = databaseHelper.getAllTasks(); // Load tasks from DB

        // Setup RecyclerView
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskList);
        recyclerViewTasks.setAdapter(taskAdapter);

        // ✅ Add Task button click listener
        btnAddTask.setOnClickListener(v -> {
            String taskText = editTextTask.getText().toString().trim();

            if (!taskText.isEmpty()) {
                boolean isInserted = databaseHelper.addTask(taskText);

                if (isInserted) {
                    Toast.makeText(MainActivity.this, "Task Added!", Toast.LENGTH_SHORT).show();
                    loadTasks();  // Refresh RecyclerView
                } else {
                    Toast.makeText(MainActivity.this, "Error adding task!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Enter a task", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Method to reload tasks from the database
    private void loadTasks() {
        taskList.clear();
        taskList.addAll(databaseHelper.getAllTasks());
        taskAdapter.notifyDataSetChanged();
    }
}
