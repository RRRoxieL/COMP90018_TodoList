package com.example.todolist.ui.calendar;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.DAO.DateTask;
import com.example.todolist.DAO.Task;
import com.example.todolist.MainActivity;
import com.example.todolist.R;
import com.example.todolist.databinding.DialogPopouttaskBinding;
import com.example.todolist.databinding.FragmentDateBinding;
import com.example.todolist.tools.TomToolkit;

import java.util.Date;


public class PopOutTaskDialog extends DialogFragment {

    private DialogPopouttaskBinding binding;
    private String date;
    private Handler handler;
    private Task task;
    public PopOutTaskDialog(String date,Handler handler) {
        this.date = date;
        this.handler=handler;
    }

    public PopOutTaskDialog(String date,Handler handler,Task task){
        this.date = date;
        this.handler = handler;
        this.task = task;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window win = getDialog().getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        win.setGravity(Gravity.BOTTOM);
//        registerForContextMenu(binding.importance);


        PopOutTaskViewModel popOutTaskViewModel = new ViewModelProvider(this).get(PopOutTaskViewModel.class);
        binding = DialogPopouttaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setContents();



        binding.btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                setTask();
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putChar("actionTag",'a');
                bundle.putSerializable("task",task);
                message.setData(bundle);
                handler.sendMessage(message);
                dismiss();
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putChar("actionTag",'d');
                bundle.putLong("ID",task.getID());
                message.setData(bundle);
                handler.sendMessage(message);
                dismiss();
            }
        });

        binding.importance.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        MenuInflater menuInflater = new MenuInflater(getContext());


        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setContents(){
        if(task!=null){
            binding.nameText.setText(task.getName());
            binding.timepicker.setHour(task.getHour());
            binding.timepicker.setMinute(task.getMinute());
            binding.descrptText.setText(task.getDescription());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTask(){
        if(task==null){
            task = new Task(getActivity(),binding.timepicker.getHour(),binding.timepicker.getMinute(),binding.nameText.getText().toString(),binding.descrptText.getText().toString(),binding.importance.getText().toString(),null);
        }else{
            task.setName(binding.nameText.getText().toString());
            task.setDescription(binding.descrptText.getText().toString());
            task.setHour(binding.timepicker.getHour());
            task.setMinute(binding.timepicker.getMinute());
            task.setImportance(Task.Importance.toImportance(binding.importance.getText().toString()));
        }
    }

//    @Override
//    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
//        MenuInflater inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.importance_menu,menu);
//    }
//
//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        return super.onContextItemSelected(item);
//    }
}