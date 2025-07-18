package com.example.odootest.addons.todotask.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.odootest.R;
import com.example.odootest.addons.todotask.model.TodoTask;
import java.util.ArrayList;
import java.util.List;

public class TodoTaskAdapter extends RecyclerView.Adapter<TodoTaskAdapter.TaskViewHolder> {
    public interface OnTaskInteractionListener {
        void onTaskLongClicked(TodoTask task);
        void onTaskClicked(TodoTask task);
    }

    private List<TodoTask> tasks = new ArrayList<>();
    private final OnTaskInteractionListener listener;

    public TodoTaskAdapter(OnTaskInteractionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TodoTask currentTask = tasks.get(position);
        holder.bind(currentTask, listener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<TodoTask> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTaskName;
        private final TextView tvTaskDescription;
        private final TextView tvTaskDueDate;
        private final CheckBox cbTaskDone;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvTaskDueDate = itemView.findViewById(R.id.tvTaskDueDate);
            cbTaskDone = itemView.findViewById(R.id.cbTaskDone);
        }

        public void bind(final TodoTask task, final OnTaskInteractionListener listener) {
            tvTaskName.setText(task.getName());
            tvTaskDescription.setText(task.getDescription());
            tvTaskDueDate.setText("Due: " + task.getDueDate());
            cbTaskDone.setChecked(task.isDone());
            cbTaskDone.setClickable(false);
            cbTaskDone.setOnCheckedChangeListener(null);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClicked(task);
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onTaskLongClicked(task);
                    return true;
                }
                return false;
            });
        }
    }
}