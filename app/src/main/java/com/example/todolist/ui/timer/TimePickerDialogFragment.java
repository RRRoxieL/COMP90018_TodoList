package com.example.todolist.ui.timer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.todolist.databinding.FragmentTimePickerDialogBinding;

import java.util.Arrays;
import java.util.stream.IntStream;

//DialogFragment as time picker
@RequiresApi(api = Build.VERSION_CODES.N)
public class TimePickerDialogFragment extends DialogFragment {
    private static final int MINTIME = 10;
    private static final int MAXTIME = 60;
    private static final int DEFAULTTIME = 15;
    public  static final int SECONDSTOMILLIS = 1000;
    public static final int MINUTESTOSECONDS = 60;

    // Use view binding
    private FragmentTimePickerDialogBinding binding;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Binding view
        binding = FragmentTimePickerDialogBinding.inflate(LayoutInflater.from(getContext()));

        // Build view and init number picker
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(binding.getRoot());
        builder.setTitle("Focus Time");

        int[] array = IntStream.range(MINTIME, MAXTIME + 1).toArray();
        String[] strArray = Arrays.stream(array).mapToObj(String::valueOf).toArray(String[]::new);
        binding.numberPicker.setMaxValue(MAXTIME);
        binding.numberPicker.setMinValue(MINTIME);
        binding.numberPicker.setValue(DEFAULTTIME);
        binding.numberPicker.setDisplayedValues(strArray);

        binding.setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getParentFragment() != null) {
                    // Pass chosen time back to TimerFragment
                    ((TimerFragment) getParentFragment()).setTime(binding.numberPicker.getValue() * MINUTESTOSECONDS * SECONDSTOMILLIS );
                }
                // Hide dialog afterwards
                getDialog().hide();
            }
        });

        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide dialog
                getDialog().hide();
            }
        });

        return builder.create();
    }
}