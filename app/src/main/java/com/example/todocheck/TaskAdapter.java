package com.example.todocheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    private final Context context;
    private final List<Task> tasks;
    private TaskCheckListener taskCheckListener;

    public interface TaskCheckListener {
        void onTaskCheckedChanged(int position, boolean isChecked);
    }

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, R.layout.task_item, tasks);
        this.context = context;
        this.tasks = tasks;
    }

    public void setTaskCheckListener(TaskCheckListener listener) {
        this.taskCheckListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.task_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.task_image);
            viewHolder.titleTextView = convertView.findViewById(R.id.task_title);
            viewHolder.descriptionTextView = convertView.findViewById(R.id.task_description);
            viewHolder.checkBox = convertView.findViewById(R.id.checkbox_task);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Task currentTask = tasks.get(position);

        if (currentTask.hasCustomImage()) {
            viewHolder.imageView.setImageURI(currentTask.getImageUri());
        } else {
            viewHolder.imageView.setImageResource(currentTask.getIconResource());
        }

        viewHolder.titleTextView.setText(currentTask.getTitle());
        viewHolder.descriptionTextView.setText(currentTask.getDescription());
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setChecked(currentTask.isCompleted());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentTask.setCompleted(isChecked);

                if (taskCheckListener != null) {
                    taskCheckListener.onTaskCheckedChanged(position, isChecked);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView descriptionTextView;
        CheckBox checkBox;
    }
}