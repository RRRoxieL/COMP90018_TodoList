package com.example.todolist.ui.timer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.todolist.R;
import com.example.todolist.databinding.FragmentTimePickerDialogBinding;

import java.util.Arrays;
import java.util.stream.IntStream;


@RequiresApi(api = Build.VERSION_CODES.N)
public class TimePickerDialogFragment extends DialogFragment {

    private FragmentTimePickerDialogBinding binding;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = FragmentTimePickerDialogBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(binding.getRoot());
        builder.setTitle("Focus Time");

        int[] array = IntStream.range(10, 60 + 1).toArray();
        String[] strArray = Arrays.stream(array).mapToObj(String::valueOf).toArray(String[]::new);
        binding.numberPicker.setMaxValue(60);
        binding.numberPicker.setMinValue(10);
        binding.numberPicker.setValue(25);
        binding.numberPicker.setDisplayedValues(strArray);

        binding.setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getParentFragment() != null) {
                    Log.d("Dialog", "onClick: "+ binding.numberPicker.getValue());
                    ((TimerFragment)getParentFragment()).setTime(binding.numberPicker.getValue());
                }
            }
        });

    return builder.create();
    }
}