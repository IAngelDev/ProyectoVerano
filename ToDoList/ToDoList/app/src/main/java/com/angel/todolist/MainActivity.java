package com.angel.todolist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;


import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private EditText editTextTask;
    private Button buttonAddTask;

    private void saveTasks() {
        SharedPreferences prefs = getSharedPreferences("tasks_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(taskList);

        editor.putString("task_list", json);
        editor.apply();
    }
    private void showEditTaskDialog(int position) {
        Task task = taskList.get(position);

        EditText editText = new EditText(this);
        editText.setText(task.getTitle());
        editText.setSelection(task.getTitle().length()); // pone el cursor al final

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Editar tarea")
                .setView(editText)
                .setPositiveButton("Guardar", (dialogInterface, i) -> {
                    String newTitle = editText.getText().toString().trim();
                    if (!newTitle.isEmpty()) {
                        task.setTitle(newTitle);
                        taskAdapter.notifyItemChanged(position);
                        saveTasks();
                    } else {
                        Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();

        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null
                    && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER
                    && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                return true;
            }
            return false;
        });

        dialog.show();

        // Habilita el botón Guardar inmediatamente
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    }


    private List<Task> loadTasks() {
        SharedPreferences prefs = getSharedPreferences("tasks_prefs", MODE_PRIVATE);
        String json = prefs.getString("task_list", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Task>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = loadTasks();

        taskAdapter = new TaskAdapter(taskList, (position, isChecked) -> {
            taskList.get(position).setCompleted(isChecked);
            saveTasks();
        });
        recyclerView.setAdapter(taskAdapter);

        editTextTask = findViewById(R.id.editTextTask);
        editTextTask.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String taskTitle = editTextTask.getText().toString().trim();
                if (!taskTitle.isEmpty()) {
                    Task newTask = new Task(taskTitle, false);
                    taskList.add(newTask);
                    taskAdapter.notifyItemInserted(taskList.size() - 1);
                    editTextTask.setText("");
                    saveTasks();
                } else {
                    Toast.makeText(MainActivity.this, "Introduce una tarea", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
            return false;
        });

        buttonAddTask = findViewById(R.id.buttonAdd);

        taskAdapter.setOnTaskDeleteListener(position -> {
            taskList.remove(position);
            taskAdapter.notifyItemRemoved(position);
            saveTasks();
        });

        buttonAddTask.setOnClickListener(v -> {
            String taskTitle = editTextTask.getText().toString().trim();
            if (!taskTitle.isEmpty()) {
                Task newTask = new Task(taskTitle, false);
                taskList.add(newTask);
                taskAdapter.notifyItemInserted(taskList.size() - 1);
                editTextTask.setText("");
                saveTasks();
            } else {
                Toast.makeText(this, "Introduce una tarea", Toast.LENGTH_SHORT).show();
            }
        });
        taskAdapter.setOnTaskEditListener(this::showEditTaskDialog);


    }

    @Override
    protected void onPause() {
        super.onPause();
        saveTasks();
    }
}
