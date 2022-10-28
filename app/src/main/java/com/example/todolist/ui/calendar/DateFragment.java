package com.example.todolist.ui.calendar;

import static android.content.ContentValues.TAG;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolist.DAO.DateTask;
import com.example.todolist.DAO.Task;
import com.example.todolist.R;
import com.example.todolist.databinding.FragmentCalendarBinding;
import com.example.todolist.databinding.FragmentDateBinding;
import com.example.todolist.tools.TomToolkit;
import com.example.todolist.ui.calendar.CalendarViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Date;
import java.util.Map;


public class DateFragment extends Fragment {
    private FragmentDateBinding binding;
    private DateTask dateTask;
    private String dateString;
    private Handler handler;
    private DatabaseReference databaseTable = TomToolkit.getDatabaseTable();
    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //initialize the attributes except dateTask
        initialize(inflater,container,savedInstanceState);
        updateView();

        binding.btnAddwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopOutTaskDialog popOutTaskDialog = new PopOutTaskDialog(dateString,handler);
                popOutTaskDialog.show(getParentFragmentManager(),"Task Editor Dialog");
            }
        });

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
//                TomToolkit.saveToFireBase(dateString,dateTask);
                Task task = new Task(getActivity(),dateString,0,0,"name","default",null,null);
            }
        });

        binding.btnPstdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseTable = TomToolkit.getDatabaseTable();
                databaseTable.child(dateString).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Object value = snapshot.getValue();
                        dateTask = new Gson().fromJson(value.toString(), DateTask.class);
                        Toast.makeText(getContext(),"prebtn:"+dateTask.toString(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "loadPost:onCancelled", error.toException());
                    }
                });

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateView(){
        if(dateTask!=null && dateTask.getTasks()!=null){
            binding.workLayout.removeAllViews();
            for (Map.Entry<String,Task> entry: dateTask.getTasks().entrySet()) {
                TaskListItemView taskListItemView = new TaskListItemView(getContext(),entry.getValue(),dateString,handler,this);
                binding.workLayout.addView(taskListItemView);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initialize(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        DateViewModel dateViewModel = new ViewModelProvider(this).get(DateViewModel.class);
        binding = FragmentDateBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        CharSequence date = bundle.getCharSequence("time");
        dateString = date.toString();

        try {
            dateTask = new DateTask(TomToolkit.getDate(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        bindDateTask();


        handler=new Handler(Looper.getMainLooper()){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                char actionTag = data.getChar("actionTag");
                if(actionTag=='a'){
                    Task task = (Task)data.getSerializable("task");
                    Toast.makeText(getContext(),"handler report task: "+task.toString(),Toast.LENGTH_LONG).show();
                    dateTask.addTask(task);
                }else if(actionTag=='d'){
                    Toast.makeText(getContext(),Boolean.toString(dateTask.deleteTask(data.getString("ID"))), Toast.LENGTH_SHORT).show();
                }else if(actionTag=='u'){
                    Task task = (Task)data.getSerializable("task");
                    Toast.makeText(getContext(),"handler report task: "+task.toString(),Toast.LENGTH_LONG).show();
                    dateTask.downTask(task);
                }

                saveData();
                updateView();
            }
        };

        getActivity().setTitle(date);
    }

    private void saveData(){
        TomToolkit.saveToFireBase(dateString,dateTask);
    }

    private void readData(){

    }

    private void bindDateTask(){
        databaseTable.child(dateString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object value = snapshot.getValue();
                if(value!=null){
                    dateTask = new Gson().fromJson(value.toString(), DateTask.class);
                    if(dateTask==null || dateTask.getTasks()==null){
                        Toast.makeText(getContext(), "read data == null", Toast.LENGTH_SHORT).show();
//                        saveData();
                    }
                    updateView();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }



}
