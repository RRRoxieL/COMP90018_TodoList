package com.example.todolist.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

        LinearLayout taskItem = binding.taskitem;
        CheckBox isFinished = binding.isfinished;
        ImageView timer = binding.btnTimer;
        TextView title = binding.nameText;
        TextView desc = binding.descText;

        if(task!=null){
            isFinished.setChecked(task.isTaskDown());
            title.setText(task.getName());
            desc.setText(task.getDescription());
            timer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(), "Should switch to timer", Toast.LENGTH_SHORT).show();
                    PopOutTaskDialog popOutTaskDialog = new PopOutTaskDialog(dateString,handler,task);
                    popOutTaskDialog.show(fragment.getParentFragmentManager(), "Task Editor Dialog");
                }
            });
            taskItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopOutTaskDialog popOutTaskDialog = new PopOutTaskDialog(dateString,handler,task);
                    popOutTaskDialog.show(fragment.getParentFragmentManager(), "Task Editor Dialog");
                }
            });
            binding.btnTimer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavController controller = Navigation.findNavController(fragment.getActivity(), R.id.nav_host_fragment_activity_main);
                    Bundle bundle = new Bundle();
                    bundle.putString("taskInfo",task.getID()+"$"+task.getName());
                    controller.navigate(R.id.navigation_timer, bundle);
                }
            });

        }
        isFinished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // 选中（已完成），背景为灰色
                    taskItem.setBackgroundColor(Color.parseColor("#cccccc"));
                    title.setTextColor(Color.parseColor("#999999"));
                    desc.setTextColor(Color.parseColor("#999999"));
                    // 添加删除线
                    title.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);  //中划线，会有锯齿
                    title.getPaint().setAntiAlias(true);
                    timer.setVisibility(INVISIBLE);
                    //dutyFinished++;
                } else {
                    // 没选中（未完成），背景为蓝色
                    taskItem.setBackgroundColor(Color.parseColor("#00ffff"));
                    title.setTextColor(Color.parseColor("#636363"));
                    desc.setTextColor(Color.parseColor("#636363"));
                    // 清除删除线
                    title.getPaint().setFlags(0);
                    title.invalidate();
                    timer.setVisibility(VISIBLE);
                    //dutyFinished--;
                }
                // 重新设置字体加粗
                //title.getPaint().setFakeBoldText(true);

                //setDutyText();
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
