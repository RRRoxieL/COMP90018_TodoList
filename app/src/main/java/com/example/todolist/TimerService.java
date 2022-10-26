package com.example.todolist;

import static com.example.todolist.ui.timer.TimePickerDialogFragment.MINUTESTOSECONDS;
import static com.example.todolist.ui.timer.TimePickerDialogFragment.SECONDSTOMILLIS;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class TimerService extends Service {

    static ServiceCallBack serviceCallBack;
    private CountDownTimer countDownTimer;
    private static final int INTERVAL = 1000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Register call back function
    public void setCallBack(ServiceCallBack serviceCallBack) {
        this.serviceCallBack = serviceCallBack;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get the remaining time from TimerFragment
        long millisInFuture = intent.getIntExtra("remainingTime",0);

        // Set timer according to the remaining time
        countDownTimer = new CountDownTimer(millisInFuture, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / SECONDSTOMILLIS);
                int minutes = seconds / MINUTESTOSECONDS;
                seconds = seconds % MINUTESTOSECONDS;
                String time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
                Log.d("SERVICE", time);
                serviceCallBack.callback(time);
            }

            @Override
            public void onFinish() {
                //TODO: firebase related code
                Toast timesUpToast = Toast.makeText(getApplicationContext(), "TIMES UP", Toast.LENGTH_SHORT);
                timesUpToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                timesUpToast.show();
                Log.d("SERVICE", "end counting time");

            }
        }.start();

        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

}
