package com.example.todolist;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class TimerService extends Service {

    static ServiceCallBack serviceCallBack;
    private CountDownTimer countDownTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // register call back function
    public void setCallBack(ServiceCallBack serviceCallBack) {
        this.serviceCallBack = serviceCallBack;
    }

    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long millisInFuture = intent.getIntExtra("remainingTime",0);
        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished/1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                String time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
                Log.d("SERVICE", time);
                serviceCallBack.callback(time);
            }

            @Override
            public void onFinish() {
                Log.d("SERVICE", "end counting time");

            }
        }.start();

        return START_STICKY;
    }

}
