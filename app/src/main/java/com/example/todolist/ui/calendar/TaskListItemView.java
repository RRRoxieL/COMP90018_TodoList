package com.example.todolist.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.todolist.DAO.Task;
import com.example.todolist.R;
import com.example.todolist.databinding.DialogPopouttaskBinding;
import com.example.todolist.databinding.TaskListItemBinding;

public class TaskListItemView extends ConstraintLayout {
    private TaskListItemBinding binding;

    public TaskListItemView(Context context, Task task, String dateString, Handler handler, Fragment fragment) {
        super(context);

        LayoutInflater inflator = LayoutInflater.from(context);
        binding = TaskListItemBinding.inflate(inflator, this, true);

        if(task!=null){
            binding.isfinished.setChecked(task.isTaskDown());
            binding.nameText.setText(task.getName());
            binding.descText.setText(task.getDescription());
            binding.btnEdit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopOutTaskDialog popOutTaskDialog = new PopOutTaskDialog(dateString,handler,task);
                    popOutTaskDialog.show(fragment.getParentFragmentManager(), "Task Editor Dialog");
                }
            });
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
