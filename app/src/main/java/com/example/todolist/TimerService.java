package com.example.todolist;

import static com.example.todolist.ui.timer.TimePickerDialogFragment.MINUTESTOSECONDS;
import static com.example.todolist.ui.timer.TimePickerDialogFragment.SECONDSTOMILLIS;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.time.LocalDate;
import java.time.LocalTime;
import com.example.todolist.ui.timer.FocusTask;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        int minutesTotal = intent.getIntExtra("targetTime", 60000) / (MINUTESTOSECONDS * SECONDSTOMILLIS);
        long millisInFuture = intent.getIntExtra("remainingTime", 0);
        String taskName = intent.getStringExtra("taskName");
        String taskID = intent.getStringExtra("taskID");

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

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onFinish() {
                // Display toast message when time's up
                Toast timesUpToast = Toast.makeText(getApplicationContext(), "TIMES UP", Toast.LENGTH_SHORT);
                timesUpToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                timesUpToast.show();

                // Create FocusTask object and save in realtime database
                String time = String.valueOf(LocalTime.now()).substring(0, 8);
                String date = String.valueOf(LocalDate.now());
                String nodeID = time + "@" + date + "@" + taskID;
                FocusTask currentFocus = new FocusTask(taskName, taskID, minutesTotal, date, time);
                DatabaseReference nodeRef = FirebaseDatabase.getInstance().getReference("Focus").child(nodeID);
                nodeRef.setValue(currentFocus);
            }
        }.start();

        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}


