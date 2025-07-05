package com.angel.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;

    private OnTaskCheckedChangeListener checkedChangeListener;
    private OnTaskDeleteListener deleteListener;
    private OnTaskEditListener editListener;

    public interface OnTaskCheckedChangeListener {
        void onTaskCheckedChanged(int position, boolean isChecked);
    }

    public interface OnTaskDeleteListener {
        void onTaskDelete(int position);
    }

    public interface OnTaskEditListener {
        void onTaskEdit(int position);
    }

    public TaskAdapter(List<Task> taskList, OnTaskCheckedChangeListener listener) {
        this.taskList = taskList;
        this.checkedChangeListener = listener;
    }

    public void setOnTaskDeleteListener(OnTaskDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void setOnTaskEditListener(OnTaskEditListener listener) {
        this.editListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.titleTextView.setText(task.getTitle());

        // Evita callback no deseado al cambiar el estado del CheckBox
        holder.completedCheckBox.setOnCheckedChangeListener(null);
        holder.completedCheckBox.setChecked(task.isCompleted());
        holder.completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int pos = holder.getAdapterPosition();
            if (checkedChangeListener != null) {
                checkedChangeListener.onTaskCheckedChanged(pos, isChecked);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (deleteListener != null) {
                deleteListener.onTaskDelete(pos);
            }
        });

        holder.titleTextView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (editListener != null) {
                editListener.onTaskEdit(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        CheckBox completedCheckBox;
        ImageButton buttonDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskTitle);
            completedCheckBox = itemView.findViewById(R.id.taskCompleted);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
