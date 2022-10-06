package com.example.todolist.ui.timer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimerViewModel extends ViewModel{
    private MutableLiveData<Integer> time;

    public MutableLiveData<Integer> getTime() {
        if(time==null){
            time = new MutableLiveData<>();
            time.setValue(25);
        }
        return time;
    }

}
