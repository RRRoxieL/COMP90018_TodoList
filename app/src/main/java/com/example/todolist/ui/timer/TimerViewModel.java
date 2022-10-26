package com.example.todolist.ui.timer;



import static com.example.todolist.ui.timer.TimePickerDialogFragment.MINUTESTOSECONDS;
import static com.example.todolist.ui.timer.TimePickerDialogFragment.SECONDSTOMILLIS;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimerViewModel extends ViewModel{
    //    private static final int DEFAULTTIME = 10;
    private final String TAG = "Timer_View";

    //    private  int deafultTime = DEFAULTTIME * MINUTESTOSECONDS * SECONDSTOMILLIS;
    private  int deafultTime = 5000;
    private  MutableLiveData<Integer> targetTime;
    private  MutableLiveData<Integer> remainingTime;
    public String timeText;
    public int progress;

    private boolean isStart;
    private boolean isPause;

    public void setDeafultTime(int time){
        deafultTime = time;
        remainingTime.setValue(time);
        targetTime.setValue(time);
        updateTimeText();
        updateProgress();
    }

    public void resetTime(){
        targetTime.setValue(deafultTime);
        remainingTime.setValue(deafultTime);
        updateProgress();
        updateTimeText();
    }

    public String getTimeText(){
        return this.timeText;
    }

    public int getProgress(){
        return this.progress;
    }

    private void updateTimeText(){
        this.timeText = timeToText(this.remainingTime.getValue());
    }

    public void updateProgress(){
        int passTime = targetTime.getValue() - remainingTime.getValue();
        this.progress =  passTime * 100 / targetTime.getValue();
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

    public void setRemainingTime(String timeString){
        int remain = textToTime(timeString)==0? deafultTime : textToTime(timeString);
        remainingTime.setValue(remain);
        updateProgress();
        updateTimeText();
    }

    public String timeToText(long time){
        int seconds = (int) (time / SECONDSTOMILLIS);
        int minutes = seconds / MINUTESTOSECONDS;
        seconds = seconds % MINUTESTOSECONDS;
        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

    }

    public int textToTime(String timeString){
        String[] leftTime = timeString.split(":");
        int minute = Integer.parseInt(leftTime[0]);
        int second = Integer.parseInt(leftTime[1]);
        if(minute <= 0 && second == 0){
            return 0;
        }else{
            return (minute * MINUTESTOSECONDS + second) * SECONDSTOMILLIS;
        }
    }


    public int getRemainingTime(){
        return remainingTime.getValue();
    }


    public boolean isStart() {
        return isStart;
    }
    public boolean isPause(){
        return isPause;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public void setPause(boolean pause){
        isPause = pause;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "cleared");
    }

}
