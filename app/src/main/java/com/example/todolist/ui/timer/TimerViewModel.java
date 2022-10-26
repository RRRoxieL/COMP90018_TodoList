package com.example.todolist.ui.timer;


import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Chronometer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimerViewModel extends ViewModel{

    private int deafultTime = 25*60*1000;
    private final static String TAG = "Timer_View";
    private MutableLiveData<Integer> targetTime;
    private MutableLiveData<Integer> remainingTime;
    private long timeWhenStopped;
    private Chronometer backup;
    private boolean isStart;
    private boolean isPause;

    public void resetTime(){
        targetTime.setValue(deafultTime);
        remainingTime.setValue(deafultTime);
    }

    public void setDeafultTime(int time){
        deafultTime = time;
    }

    public MutableLiveData<Integer> getTime() {
        if(targetTime == null){
            targetTime = new MutableLiveData<>();
            targetTime.setValue(deafultTime);
            remainingTime = new MutableLiveData<>();
            remainingTime.setValue(deafultTime);
        }
        return targetTime;
    }



    public int getProgress(){
        int passTime = targetTime.getValue() - remainingTime.getValue();
        return passTime * 100 / targetTime.getValue();
    }

    public void passTimer(Chronometer chronometer){
        backup = chronometer;
        backup.start();
    }

    public void setRemainingTime(Chronometer chronometer){
        int remain = chronometerToTime(chronometer)==0? deafultTime : chronometerToTime(chronometer);
        remainingTime.setValue(remain);
    }

    public int chronometerToTime(Chronometer chronometer){
        String[] leftTime = chronometer.getText().toString().split(":");
        int minute = Integer.parseInt(leftTime[0]);
        int second = Integer.parseInt(leftTime[1]);
        if(minute<=0 && second==0){
            return 0;
        }else{
            return (minute * 60 + second) * 1000;
        }
    }

    public int getTimer(){
        return chronometerToTime(backup);
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

    public long getStopTime() {
        return timeWhenStopped;
    }

    public void setStopTime(long timeWhenStopped) {
        this.timeWhenStopped = timeWhenStopped;
    }
}
