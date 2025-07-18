package com.example.odootest.addons.todotask.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odootest.R;
import com.example.odootest.addons.todotask.model.TodoTask;
import com.example.odootest.addons.todotask.viewmodel.TodoTaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class TodoTaskActivity extends AppCompatActivity implements TodoTaskAdapter.OnTaskInteractionListener {
    private TodoTaskViewModel viewModel;
    private RecyclerView recyclerView;
    private TodoTaskAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvError;
    private SearchView searchView;
    private FloatingActionButton fabAddTask;
    // Lưu trữ thông tin xác thực
    private String odooUrl, odooDb, odooPassword;
    private int odooUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_task);
        if (!loadAuthInfo()) {
            return;
        }
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
        setupViews();
        setupViewModel();
        setupListeners();
        viewModel.fetchTasks(odooUrl, odooDb, odooUid, odooPassword, "");
    }

    private boolean loadAuthInfo() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "Authentication info missing!", Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        odooUrl = extras.getString("ODOO_URL");
        odooDb = extras.getString("ODOO_DB");
        odooUid = extras.getInt("ODOO_UID", -1);
        odooPassword = extras.getString("ODOO_PASSWORD");
        if (odooUid == -1) {
            Toast.makeText(this, "Invalid UID!", Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        return true;
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.rvTodoTasks);
        progressBar = findViewById(R.id.progressBarTasks);
        tvError = findViewById(R.id.tvError);
        searchView = findViewById(R.id.searchView);
        fabAddTask = findViewById(R.id.fabAddTask);
        adapter = new TodoTaskAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(TodoTaskViewModel.class);
        viewModel.getIsLoading().observe(this, isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));
        viewModel.getTasks().observe(this, tasks -> {
            recyclerView.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE);
            adapter.setTasks(tasks);
        });
        viewModel.getError().observe(this, errorMsg -> {
            Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
        });
        viewModel.getEventMessage().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        fabAddTask.setOnClickListener(v -> showCreateTaskDialog());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.fetchTasks(odooUrl, odooDb, odooUid, odooPassword, query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    viewModel.fetchTasks(odooUrl, odooDb, odooUid, odooPassword, "");
                }
                return true;
            }
        });
    }

    private void showCreateTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_task, null);
        final TextInputEditText etName = dialogView.findViewById(R.id.etDialogTaskName);
        final TextInputEditText etDescription = dialogView.findViewById(R.id.etDialogTaskDescription);
        final TextInputEditText etDueDate = dialogView.findViewById(R.id.etDialogTaskDueDate);
        final CheckBox cbDone = dialogView.findViewById(R.id.cbDialogTaskDone);
        builder.setView(dialogView)
                .setTitle("Create New Task")
                .setPositiveButton("Create", (dialog, id) -> {
                    String name = etName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String dueDate = etDueDate.getText().toString().trim();
                    boolean isDone = cbDone.isChecked();

                    if (!name.isEmpty()) {
                        viewModel.createTask(odooUrl, odooDb, odooUid, odooPassword, name, description, dueDate, isDone);
                    } else {
                        Toast.makeText(this, "Task name is required.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    private void showEditTaskDialog(final TodoTask task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_task, null);
        final TextInputEditText etName = dialogView.findViewById(R.id.etDialogTaskName);
        final TextInputEditText etDescription = dialogView.findViewById(R.id.etDialogTaskDescription);
        final TextInputEditText etDueDate = dialogView.findViewById(R.id.etDialogTaskDueDate);
        final CheckBox cbDone = dialogView.findViewById(R.id.cbDialogTaskDone);
        etName.setText(task.getName());
        etDescription.setText(task.getDescription());
        cbDone.setChecked(task.isDone());
        if (!task.getDueDate().equals("No date")) {
            etDueDate.setText(task.getDueDate());
        }
        builder.setView(dialogView)
                .setTitle("Edit Task")
                .setPositiveButton("Save", (dialog, id) -> {
                    String name = etName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String dueDate = etDueDate.getText().toString().trim();
                    boolean isDone = cbDone.isChecked();
                    if (!name.isEmpty()) {
                        viewModel.updateTask(odooUrl, odooDb, odooUid, odooPassword, task.getId(), name, description, dueDate, isDone);
                    } else {
                        Toast.makeText(this, "Task name is required.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    private void showDeleteConfirmationDialog(TodoTask task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task: '" + task.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) ->
                        viewModel.deleteTask(odooUrl, odooDb, odooUid, odooPassword, task.getId()))
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onTaskLongClicked(TodoTask task) {
        showDeleteConfirmationDialog(task);
    }

    @Override
    public void onTaskClicked(TodoTask task) {
        showEditTaskDialog(task);
    }
}