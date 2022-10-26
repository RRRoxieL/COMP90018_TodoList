package com.example.todolist.ui.timer;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolist.R;
import com.example.todolist.databinding.FragmentTimerBinding;

public class TimerFragment extends Fragment {

    private final String TAG= "Timer";

    private FragmentTimerBinding binding;
    private TimerViewModel timerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        timerViewModel = new ViewModelProvider(getActivity()).get(TimerViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false);
        binding.setData(timerViewModel);
        binding.setLifecycleOwner(getActivity());

        if(timerViewModel.isStart()) {
            binding.timeTimer.setBase(SystemClock.elapsedRealtime() + timerViewModel.getTimer());
            binding.startBtnTimer.setVisibility(View.INVISIBLE);
            if(timerViewModel.isPause()){
                binding.pauseBtnTimer.setVisibility(View.INVISIBLE);
            }else {
                binding.timeTimer.start();
                binding.resumeBtnTimer.setVisibility(View.INVISIBLE);
            }
            binding.progressBar.setProgress(timerViewModel.getProgress());
        }else{
            binding.resumeBtnTimer.setVisibility(View.INVISIBLE);
            binding.timeTimer.setBase(SystemClock.elapsedRealtime() + timerViewModel.getTime().getValue());
        }

        /**
         * SEEK BAR
         */
        binding.seekBarTimer.setProgress(timerViewModel.getTime().getValue()/60000);
        binding.seekBarTimer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                timerViewModel.getTime().setValue(i*60*1000);
                binding.timeTimer.setBase(SystemClock.elapsedRealtime() + timerViewModel.getTime().getValue());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        /**
         * CHRONOMETER
         */
        binding.timeTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                timerViewModel.setRemainingTime(binding.timeTimer);
                binding.progressBar.setProgress(timerViewModel.getProgress());
                if(chronometer.getText().equals("00:00")){
                    chronometer.stop();
                    Log.d(TAG, "Timer Stop");
                    Toast.makeText(getActivity(), "Time reached", Toast.LENGTH_SHORT).show();
                    binding.startBtnTimer.setVisibility(View.VISIBLE);
                    timerViewModel.resetTime();
                    timerViewModel.setStart(false);
                }
            }
        });

        binding.timeTimer.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                TimePickerDialogFragment dialog = new TimePickerDialogFragment();
                dialog.show(getActivity().getSupportFragmentManager(), "TIME PICKER");
            }
        });


        /**
         * START BUTTON
         */
        binding.startBtnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.timeTimer.setBase(SystemClock.elapsedRealtime() + timerViewModel.getTime().getValue()+999);
                binding.timeTimer.start();
                timerViewModel.setStart(true);
                binding.startBtnTimer.setVisibility(View.INVISIBLE);
            }
        });


        /**
         * PAUSE BUTTON
         */
        binding.pauseBtnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.timeTimer.stop();
                timerViewModel.setStopTime(binding.timeTimer.getBase() - SystemClock.elapsedRealtime());
                timerViewModel.setPause(true);
                binding.pauseBtnTimer.setVisibility(View.INVISIBLE);
                binding.resumeBtnTimer.setVisibility(View.VISIBLE);
            }
        });

        /**
         * RESUME BUTTON
         */
        binding.resumeBtnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.timeTimer.setBase(SystemClock.elapsedRealtime() + timerViewModel.getStopTime());
                binding.timeTimer.start();
                timerViewModel.setPause(false);
                binding.resumeBtnTimer.setVisibility(View.INVISIBLE);
                binding.pauseBtnTimer.setVisibility(View.VISIBLE);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    public void setTime(int time){
        timerViewModel.setDeafultTime(time);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        timerViewModel.setRemainingTime(binding.timeTimer);
        timerViewModel.passTimer(binding.timeTimer);
    }

}