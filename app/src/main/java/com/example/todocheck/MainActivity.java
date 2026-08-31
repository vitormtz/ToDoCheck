package com.example.todocheck;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskCheckListener {
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private AlertDialog mCurrentDialog;
    private ListView taskListView;
    private TextView emptyView;
    private List<Task> taskList;
    private TaskAdapter taskAdapter;
    private int taskIdCounter = 0;
    private int[] taskIcons = {
            R.drawable.ic_task_default
    };
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskList = new ArrayList<>();
        taskListView = findViewById(R.id.task_list_view);
        emptyView = findViewById(R.id.empty_view);
        Button addTaskButton = findViewById(R.id.add_task_button);
        taskAdapter = new TaskAdapter(this, taskList);
        taskAdapter.setTaskCheckListener(this);
        taskListView.setAdapter(taskAdapter);
        taskListView.setEmptyView(emptyView);
        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showTaskOptionsDialog(position);
                return true;
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();

                        try {
                            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                            getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }

                        AlertDialog dialog = getVisibleDialog();
                        if (dialog != null) {
                            ImageView imageView = dialog.findViewById(R.id.task_image_preview);
                            if (imageView != null) {
                                imageView.setImageURI(selectedImageUri);
                            }
                        }
                    }
                }
        );
        addSampleTasks();
    }

    @Override
    public void onTaskCheckedChanged(int position, boolean isChecked) {
        Task task = taskList.get(position);
        task.setCompleted(isChecked);

        taskAdapter.notifyDataSetChanged();

    }

    private void showMyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Título")
                .setMessage("Mensagem")
                .setPositiveButton("OK", (dialog, which) -> {
                });

        mCurrentDialog = builder.create();
        mCurrentDialog.show();
    }

    private void showAddTaskDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_task, null);

        final EditText titleEditText = dialogView.findViewById(R.id.edit_task_title);
        final EditText descriptionEditText = dialogView.findViewById(R.id.edit_task_description);
        final ImageView imagePreview = dialogView.findViewById(R.id.task_image_preview);
        Button chooseImageButton = dialogView.findViewById(R.id.btn_choose_image);

        selectedImageUri = null;

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_task)
                .setView(dialogView)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = titleEditText.getText().toString().trim();
                        String description = descriptionEditText.getText().toString().trim();

                        if (!title.isEmpty()) {
                            Task newTask;
                            if (selectedImageUri != null) {
                                newTask = new Task(
                                        taskIdCounter++,
                                        title,
                                        description,
                                        false,
                                        selectedImageUri
                                );
                            } else {
                                newTask = new Task(
                                        taskIdCounter++,
                                        title,
                                        description,
                                        false,
                                        taskIcons[0]
                                );
                            }
                            taskList.add(newTask);
                            taskAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, R.string.task_added, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null);

        builder.create().show();
    }

     private void showTaskOptionsDialog(final int position) {
        final Task task = taskList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(task.getTitle())
                .setItems(new CharSequence[]{getString(R.string.edit), getString(R.string.delete)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        showEditTaskDialog(position);
                                        break;
                                    case 1:
                                        deleteTask(position);
                                        break;
                                }
                            }
                        });

        builder.create().show();
    }

    private void showEditTaskDialog(final int position) {
        final Task task = taskList.get(position);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_task, null);

        final EditText titleEditText = dialogView.findViewById(R.id.edit_task_title);
        final EditText descriptionEditText = dialogView.findViewById(R.id.edit_task_description);
        final ImageView imagePreview = dialogView.findViewById(R.id.task_image_preview);
        Button chooseImageButton = dialogView.findViewById(R.id.btn_choose_image);

        titleEditText.setText(task.getTitle());
        descriptionEditText.setText(task.getDescription());

        if (task.hasCustomImage()) {
            imagePreview.setImageURI(task.getImageUri());
            selectedImageUri = task.getImageUri();
        } else {
            imagePreview.setImageResource(task.getIconResource());
            selectedImageUri = null;
        }

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit)
                .setView(dialogView)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = titleEditText.getText().toString().trim();
                        String description = descriptionEditText.getText().toString().trim();

                        if (!title.isEmpty()) {
                            task.setTitle(title);
                            task.setDescription(description);
                            if (selectedImageUri != null) {
                                task.setImageUri(selectedImageUri);
                            }

                            taskAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, R.string.task_updated, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null);

        builder.create().show();
    }

    private void deleteTask(int position) {
        taskList.remove(position);
        taskAdapter.notifyDataSetChanged();

        Toast.makeText(this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Selecione uma imagem"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            AlertDialog dialog = getVisibleDialog();
            if (dialog != null) {
                ImageView imageView = dialog.findViewById(R.id.task_image_preview);
                if (imageView != null) {
                    imageView.setImageURI(selectedImageUri);
                }
            }
        }
    }

    private AlertDialog getVisibleDialog() {
        if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
            return mCurrentDialog;
        }
        return null;
    }

    private void addSampleTasks() {
        taskList.add(new Task(taskIdCounter++, "Comprar leite", "Ir ao supermercado e comprar 2 litros de leite", false, taskIcons[0]));
        taskList.add(new Task(taskIdCounter++, "Responder email", "Responder o email do chefe sobre o relatório mensal", false, taskIcons[0]));
        taskList.add(new Task(taskIdCounter++, "Marcar consulta", "Ligar para o médico e agendar consulta de rotina", false, taskIcons[0]));

        taskAdapter.notifyDataSetChanged();
    }
}