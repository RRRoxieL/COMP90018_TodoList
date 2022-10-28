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
import com.example.todolist.tools.GlobalValues;
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

/**
 * DateFragment is responsible for the management panel of the data for the same date.
 */
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

        //logic for add button, add a new PopOutTaskDialog to manage one task.
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

    /**
     * refresh the view on the page, according to data stored in dataTask.
     */
    private void updateView(){
        if(dateTask!=null && dateTask.getTasks()!=null){
            //remove old views
            binding.workLayout.removeAllViews();
            //add new views
            for (Map.Entry<String,Task> entry: dateTask.getTasks().entrySet()) {
                TaskListItemView taskListItemView = new TaskListItemView(getContext(),entry.getValue(),dateString,handler,this);
                binding.workLayout.addView(taskListItemView);
            }
        }
    }

    /**
     * initialization of the datefragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initialize(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        DateViewModel dateViewModel = new ViewModelProvider(this).get(DateViewModel.class);
        binding = FragmentDateBinding.inflate(inflater, container, false);
        //initialize current date managed and save it to field dateString
        Bundle bundle = getArguments();
        CharSequence date = bundle.getCharSequence(GlobalValues.BUNDLE_INFO_TIME);
        this.dateString = date.toString();

        //because read data from the database takes some time, initialize an empty datatask to avoid crash
        //this datetask would be replaced by data fetched from database successfully.
        try {
            this.dateTask = new DateTask(TomToolkit.getDate(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //bind datetask to the data in the database and start data fetch
        bindDateTask();

        //start a handler that would be passed to other fragments for communication
        //the message send back should have a actionTag and a task or only taskID
        handler=new Handler(Looper.getMainLooper()){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                char actionTag = data.getChar(GlobalValues.BUNDLE_INFO_ACTIONTAG);
                if(actionTag==GlobalValues.ACTIONTAG_ADD){
                    //the returned data called for add new task to the date, add the new task returned
                    Task task = (Task)data.getSerializable(GlobalValues.BUNDLE_INFO_TASK);
                    dateTask.addTask(task);
                }else if(actionTag==GlobalValues.ACTIONTAG_DEL){
                    //the returned data called for delete a task from the date, make a toast to hint the user.
                    Toast.makeText(getContext(),Boolean.toString(dateTask.deleteTask(data.getString(GlobalValues.BUNDLE_INFO_TASKID))), Toast.LENGTH_SHORT).show();
                }else if(actionTag==GlobalValues.ACTIONTAG_UPD){
                    //the returned data called for delete a task from the date, update the task in the datetask
                    Task task = (Task)data.getSerializable(GlobalValues.BUNDLE_INFO_TASK);
                    dateTask.downTask(task);
                }

                //after the datetask was updated, save the new datetask to database and update the view on this page.
                saveData();
                updateView();
            }
        };
//        getActivity().setTitle(date);
    }

    /**
     * API reserved for save data to database
     */
    private void saveData(){
        TomToolkit.saveToFireBase(dateString,dateTask);
    }

    /**
     * API reserved for read data from database
     * not used
     */
    private DateTask readData(){
        return null;
    }

    /**
     * bind datetask to the data on the database.
     * this function replaced readData()
     */
    private void bindDateTask(){
        databaseTable.child(dateString).addValueEventListener(new ValueEventListener() {
            /**
             * when the data in database changed, read the data into dateTask and update view
             * @param snapshot
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object value = snapshot.getValue();
                if(value!=null){
                    dateTask = new Gson().fromJson(value.toString(), DateTask.class);
                    if(dateTask==null || dateTask.getTasks()==null){
                        Toast.makeText(getContext(), "read data == null", Toast.LENGTH_SHORT).show();
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
