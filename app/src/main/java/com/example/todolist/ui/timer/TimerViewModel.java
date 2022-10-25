package com.example.todolist.ui.timer;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Chronometer;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Serializable;

public class TimerViewModel extends ViewModel{

    private  int deafultTime = 10*60*1000;
    private final String TAG = "Timer_View";
    private  MutableLiveData<Integer> targetTime;
    private  MutableLiveData<Integer> remainingTime;
    public String timeText;
    public int progress;

    private boolean isStart;
    private boolean isPause;

    public void resetTime(){
        targetTime.setValue(deafultTime);
        remainingTime.setValue(deafultTime);
    }

    public String getTimeText(){
        return this.timeText;
    }

    public int getProgress(){
        return this.progress;
    }

    public void setDeafultTime(int time){
        deafultTime = time;
        remainingTime.setValue(time);
        targetTime.setValue(time);
        updateTimeText();
        updateProgress();
    }
    private void updateTimeText(){
        this.timeText = timeToText(this.remainingTime.getValue());
    }

    public MutableLiveData<Integer> getTime() {
        if(targetTime == null){
                targetTime = new MutableLiveData<>();
                targetTime.setValue(deafultTime);
                remainingTime = new MutableLiveData<>();
                remainingTime.setValue(deafultTime);
                updateTimeText();
        }
        return targetTime;
    }

    public void updateProgress(){
        int passTime = targetTime.getValue() - remainingTime.getValue();
        this.progress =  passTime * 100 / targetTime.getValue();
    }


    public void setRemainingTime(String timeString){
        int remain = textToTime(timeString)==0? deafultTime : textToTime(timeString);
        remainingTime.setValue(remain);
        updateProgress();
        updateTimeText();
    }

    public String timeToText(long time){
        int seconds = (int) (time/1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

    }

    public int textToTime(String timeString){
        String[] leftTime = timeString.split(":");
        int minute = Integer.parseInt(leftTime[0]);
        int second = Integer.parseInt(leftTime[1]);
        if(minute<=0 && second==0){
            return 0;
        }else{
            return (minute * 60 + second) * 1000;
        }
    }


    public int getRemainingTime(){
        return remainingTime.getValue();
    }


    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public void setPause(boolean pause){
        isPause = pause;
    }

    public boolean isPause(){
        return isPause;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "cleared");
    }

}
