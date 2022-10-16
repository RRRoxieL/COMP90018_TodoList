package com.example.todolist.ui.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.example.todolist.DAO.DateTask;
import com.example.todolist.DAO.Task;
import com.example.todolist.R;
import com.example.todolist.databinding.FragmentCalendarBinding;
import com.example.todolist.databinding.FragmentDateBinding;
import com.example.todolist.tools.TomToolkit;
import com.example.todolist.ui.calendar.CalendarViewModel;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class DateFragment extends Fragment {
    private FragmentDateBinding binding;
    private DateTask dateTask;
    private String dateString;
    final static private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    private Handler handler;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //initialize the attributes except dateTask
        initialize(inflater,container,savedInstanceState);
        updateView(dateString);

        binding.btnAddwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopOutTaskDialog popOutTaskDialog = new PopOutTaskDialog(dateString,handler);
                popOutTaskDialog.show(getParentFragmentManager(),"Task Editor Dialog");
            }
        });

        binding.btnPredate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    dateTask = new DateTask(dateFormat.parse(dateString));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                Task testTask = new Task(10,15,"test task name","descfjdsklgkdhfkdjsabhfsdhifojbafiudsahhjkfshdaifhsiufehfoiewlhfiuegfhuisahskjld", null,null);
//
//                String s = new Gson().toJson(dateTask);
//                new Gson().fromJson(s,DateTask.class);

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateView(String date){
        dateTask = TomToolkit.readData(getActivity(), date);
        int count=0;
        if(dateTask!=null && dateTask.getTasks()!=null){
            binding.workLayout.removeAllViews();
            for (Map.Entry<Long,Task> entry: dateTask.getTasks().entrySet()) {
                Log.d("tag",String.valueOf(count++));
                TaskListItemView taskListItemView = new TaskListItemView(getContext(),entry.getValue(),dateString,handler,this);
                binding.workLayout.addView(taskListItemView);
            }
//            Task testTask = new Task(10,15,"test task name","descfjdsklgkdhfkdjsabhfsdhifojbafiudsahhjkfshdaifhsiufehfoiewlhfiuegfhuisahskjld", null,null);
//            dateTask.addTask("test",testTask);
//            TaskListItemView taskListItemView = new TaskListItemView(getContext(),testTask);
//            binding.workLayout.addView(taskListItemView);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initialize(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        DateViewModel dateViewModel = new ViewModelProvider(this).get(DateViewModel.class);
        binding = FragmentDateBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        CharSequence date = bundle.getCharSequence("time");
//        Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();

        dateString = date.toString();

        try {
            dateTask = new DateTask(dateFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
        TomToolkit.saveData(getActivity(),dateString,dateTask);
        if(TomToolkit.readData(getActivity(),dateString)==null){
            TomToolkit.saveData(getActivity(),dateString,dateTask);
        }
        handler=new Handler(Looper.getMainLooper()){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                char actionTag = data.getChar("actionTag");
                if(actionTag=='a'){
                    Task task = (Task)data.getSerializable("task");
                    dateTask.addTask(task);
                }else if(actionTag=='d'){
                    Toast.makeText(getContext(),Boolean.toString(dateTask.deleteTask(data.getLong("ID"))), Toast.LENGTH_SHORT).show();
                }


                Toast.makeText(getContext(), dateTask.toString(), Toast.LENGTH_SHORT).show();
                TomToolkit.saveData(getActivity(),dateString,dateTask);
                updateView(dateString);
            }
        };
        getActivity().setTitle(date);
    }

}
