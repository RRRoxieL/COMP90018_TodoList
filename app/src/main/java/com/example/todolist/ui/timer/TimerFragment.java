package com.example.todolist.ui.timer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolist.MainActivity;
import com.example.todolist.TimerService;
import com.example.todolist.R;
import com.example.todolist.ServiceCallBack;
import com.example.todolist.databinding.FragmentTimerBinding;

public class TimerFragment extends Fragment implements ServiceCallBack {
    private final int INTERVAL = 1000;
    private final String TAG = "Timer";

    private FragmentTimerBinding binding;
    private TimerViewModel timerViewModel;
    private TimerService timerService;
    private String taskID;
    private String taskName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        timerViewModel = new ViewModelProvider(getActivity()).get(TimerViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false);
        binding.setData(timerViewModel);
        binding.setLifecycleOwner(getActivity());

        try {
            this.taskName = getArguments().getString("taskName");
            this.timerViewModel.setTaskName(taskName);
            this.taskID = getArguments().getString("taskID");

            if (taskName != null) {
                binding.taskNameText.setText(this.taskName);
            }
        } catch (RuntimeException e) {
            Log.d(TAG, "no context");
        }

        // Init timer service
        timerService = new TimerService();
        timerService.setCallBack(this);

        // Set widgets' visibility according to timer's status
        timerViewModel.getTime();
        if (timerViewModel.isStart()) {
            binding.taskNameText.setText(this.timerViewModel.getTaskName());
            binding.startBtnTimer.setVisibility(View.INVISIBLE);
            if (timerViewModel.isPause()) {
                binding.taskNameText.setText(this.timerViewModel.getTaskName());
                binding.timeText.setText(timerViewModel.getTimeText());
                binding.pauseBtnTimer.setVisibility(View.INVISIBLE);
            } else {
                binding.resumeBtnTimer.setVisibility(View.INVISIBLE);
            }
        } else {
            binding.cancelBtnTimer.setVisibility(View.INVISIBLE);
            binding.resumeBtnTimer.setVisibility(View.INVISIBLE);
            binding.pauseBtnTimer.setVisibility(View.INVISIBLE);
            binding.timeText.setText(timerViewModel.getTimeText());
        }

        // Implement pop-up dialog as time picker
        binding.timeText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                TimePickerDialogFragment dialog = new TimePickerDialogFragment();
                dialog.show(getChildFragmentManager(), "TIME PICKER");
            }
        });


        // START BUTTON
        binding.startBtnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Init intent and start service
                initIntentAndStartService();

                // Change timer's status and widget's visibility
                timerViewModel.setStart(true);
                binding.startBtnTimer.setVisibility(View.INVISIBLE);
                binding.pauseBtnTimer.setVisibility(View.VISIBLE);
                binding.cancelBtnTimer.setVisibility(View.VISIBLE);
            }
        });

        // CANCEL BUTTON
        binding.cancelBtnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop service
                getActivity().stopService(new Intent(getActivity(), TimerService.class));
                timerViewModel.resetTime();
                timerViewModel.setStart(false);
                resetUI();
            }
        });

        // PAUSE BUTTON
        binding.pauseBtnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop service
                getActivity().stopService(new Intent(getActivity(), TimerService.class));

                // Change timer's status and widget's visibility
                timerViewModel.setPause(true);
                binding.pauseBtnTimer.setVisibility(View.INVISIBLE);
                binding.resumeBtnTimer.setVisibility(View.VISIBLE);
            }
        });

        // RESUME BUTTON
        binding.resumeBtnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Init intent and start service
                initIntentAndStartService();
                // Change widgets' visibility accordingly
                timerViewModel.setPause(false);
                binding.resumeBtnTimer.setVisibility(View.INVISIBLE);
                binding.pauseBtnTimer.setVisibility(View.VISIBLE);

            }
        });

        return binding.getRoot();
    }

    // Implement callback function
    @Override
    public void callback(String str) {
        binding.timeText.setText(str);
        timerViewModel.setRemainingTime(str);
        binding.progressBar.setProgress(timerViewModel.getProgress());

        if (str.equals("00:00")) {
            Log.d(TAG, "TIMES UP FROM TIMER FRAGMENT");
            timerViewModel.resetTime();
            resetUI();
            timerViewModel.setStart(false);
        }
    }

    // Reset UI after timer is canceled or time is reset
    private void resetUI() {
        binding.timeText.setText(timerViewModel.getTimeText());
        binding.progressBar.setProgress(timerViewModel.getProgress());
        binding.startBtnTimer.setVisibility(View.VISIBLE);
        binding.cancelBtnTimer.setVisibility(View.INVISIBLE);
        binding.pauseBtnTimer.setVisibility(View.INVISIBLE);
        binding.resumeBtnTimer.setVisibility(View.INVISIBLE);
    }

    // Function for DialogFragment to setting time
    public void setTime(int time) {
        // Stop service
        getActivity().stopService(new Intent(getActivity(), TimerService.class));

        // Set default time in ViewModel
        timerViewModel.setDeafultTime(time);

        resetUI();
        Log.d("set default time", "" + time);
    }

    public void initIntentAndStartService() {
        Intent intent = new Intent(getActivity(), TimerService.class);
        intent.putExtra("uid", ((MainActivity)getActivity()).getUid());
        intent.putExtra("remainingTime", timerViewModel.getRemainingTime());
        intent.putExtra("targetTime", timerViewModel.getTargetTime());
        if (taskName != null & taskID != null) {
            intent.putExtra("taskName", this.taskName);
            intent.putExtra("taskID", this.taskID);
        }
        getActivity().startService(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        // Save remaining time as backup data
        timerViewModel.setRemainingTime((String) binding.timeText.getText());
    }
}