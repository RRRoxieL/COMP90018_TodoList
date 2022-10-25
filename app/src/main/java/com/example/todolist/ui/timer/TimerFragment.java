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

import com.example.todolist.TimerService;
import com.example.todolist.R;

import com.example.todolist.ServiceCallBack;
import com.example.todolist.databinding.FragmentTimerBinding;

public class TimerFragment extends Fragment implements ServiceCallBack {

    private final String TAG= "Timer";

    private FragmentTimerBinding binding;
    private static TimerViewModel timerViewModel;
    private final int INTERVAL = 1000;
    private TimerService timerService;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        timerViewModel = new ViewModelProvider(getActivity()).get(TimerViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false);
        binding.setData(timerViewModel);
        binding.setLifecycleOwner(getActivity());

        timerService = new TimerService();
        timerService.setCallBack(this);
        timerViewModel.getTime();

        if(timerViewModel.isStart()) {
            binding.startBtnTimer.setVisibility(View.INVISIBLE);
            if(timerViewModel.isPause()){
                binding.pauseBtnTimer.setVisibility(View.INVISIBLE);
            }else {
                binding.resumeBtnTimer.setVisibility(View.INVISIBLE);
            }
//            binding.progressBar.setProgress(timerViewModel.getProgress());
        }else{
            binding.resumeBtnTimer.setVisibility(View.INVISIBLE);
            binding.timeText.setText(timerViewModel.getTimeText());
        }




        /**
         * adjust time
         */
        binding.timeText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                TimePickerDialogFragment dialog = new TimePickerDialogFragment();
                dialog.show(getChildFragmentManager(), "TIME PICKER");
            }
        });


        /**
         * START BUTTON
         */
        binding.startBtnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getActivity(), TimerService.class);
                startIntent.putExtra("remainingTime", timerViewModel.getRemainingTime());
                getActivity().startService(startIntent);
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
                timerViewModel.setPause(true);
                getActivity().stopService(new Intent(getActivity(), TimerService.class));
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
                timerViewModel.setPause(false);
                Intent resumeIntent = new Intent(getActivity(), TimerService.class);
                resumeIntent.putExtra("remainingTime", timerViewModel.getRemainingTime() + 999);
                getActivity().startService(resumeIntent);
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
        Log.d("set default time", ""+time);
        timerViewModel.setDeafultTime(time);
        binding.timeText.setText(timerViewModel.getTimeText());
        binding.progressBar.setProgress(timerViewModel.getProgress());
        binding.startBtnTimer.setVisibility(View.VISIBLE);
        binding.pauseBtnTimer.setVisibility(View.VISIBLE);
        binding.resumeBtnTimer.setVisibility(View.INVISIBLE);
        getActivity().stopService(new Intent(getActivity(), TimerService.class));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        timerViewModel.setRemainingTime((String) binding.timeText.getText());
    }

    @Override
    public void callback(String str) {
        binding.timeText.setText(str);
        timerViewModel.setRemainingTime(str);
        binding.progressBar.setProgress(timerViewModel.getProgress());

    }
}