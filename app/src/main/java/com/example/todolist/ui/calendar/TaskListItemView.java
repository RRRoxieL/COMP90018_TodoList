package com.example.todolist.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.todolist.tools.GlobalValues;
import com.example.todolist.tools.TomToolkit;

/**
 * this is the dynamic view that used to show one task on the dateFragment
 */
public class TaskListItemView extends ConstraintLayout {
    private TaskListItemBinding binding;
    private LinearLayout taskItem;
    private CheckBox isFinished;
    private ImageView timer;
    private TextView title;
    private TextView desc;

    /**
     * constructor of the view
     * @param context: the context for this view
     * @param task: task that this view represents
     * @param dateString: date of the task this view represents
     * @param handler: a handler that used to send request back to datefragment
     * @param fragment: the datefragment that used to create this view
     */
    public TaskListItemView(Context context, Task task, String dateString, Handler handler, Fragment fragment) {
        super(context);

        LayoutInflater inflator = LayoutInflater.from(context);
        binding = TaskListItemBinding.inflate(inflator, this, true);
        taskItem = binding.taskitem;
        isFinished = binding.isfinished;
        timer = binding.btnTimer;
        title = binding.nameText;
        desc = binding.descText;

        // if the task input is not empty, set the contents in the view.
        if(task!=null){

            //reset the view and fill in the contents from the task
            isFinished.setChecked(task.isTaskDown());
            viewCheckChange();
            title.setText(task.getName());
            desc.setText(task.getDescription());

            //if the user changed the check status of the task, update view and send the update request to
            isFinished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    viewCheckChange();
                    task.setTaskdown(isFinished.isChecked());
                    TomToolkit.sendMessage(handler,task,GlobalValues.BUNDLE_INFO_ACTIONTAG,GlobalValues.ACTIONTAG_UPD);
                }
            });

            //if the user clicked the view, popout a PopOutTaskDialog so that the user could manage the task
            taskItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopOutTaskDialog popOutTaskDialog = new PopOutTaskDialog(dateString,handler,task);
                    popOutTaskDialog.show(fragment.getParentFragmentManager(), "Task Editor Dialog");
                }
            });

            //if the user clicked the timer button,switch to timer fragment
            binding.btnTimer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavController controller = Navigation.findNavController(fragment.getActivity(), R.id.nav_host_fragment_activity_main);
                    final Bundle bundle = new Bundle();
                    bundle.putString("taskName", task.getName());
                    bundle.putString("taskID", task.getID());
                    controller.navigate(R.id.navigation_timer, bundle);
                }
            });

        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * change the appearance of the view when the check button is checked or not.
     */
    private void viewCheckChange(){
        if (isFinished.isChecked()){
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
            // 重新设置字体加粗
            //title.getPaint().setFakeBoldText(true);

            //setDutyText();
        }
    }


}
