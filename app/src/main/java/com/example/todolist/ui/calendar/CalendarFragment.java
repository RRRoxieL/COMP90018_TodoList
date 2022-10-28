package com.example.todolist.ui.calendar;

import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolist.R;
import com.example.todolist.databinding.FragmentCalendarBinding;
import com.example.todolist.tools.GlobalValues;
import com.example.todolist.tools.TomToolkit;
import com.google.rpc.context.AttributeContext;

import java.text.SimpleDateFormat;

/**
 *  this class is the main fragment of the calendar panel.
 *  this class is responsible for display and select dates from a calendar.
 */
public class CalendarFragment extends Fragment {
    /**
     * binding: view binder
     */
    private FragmentCalendarBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textCalendar;
        CalendarView calendar = binding.Calender;

        //set the date on the header.
        calendarViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //when a date is selected, create a new DateFragment enable users to manage the selected date.
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = ""+dayOfMonth+"-"+(month+1)+"-"+year;
                TomToolkit.startDataFragment(getParentFragmentManager(),date);
//                Bundle bundle = new Bundle();
//                //save selected date into bundle and send to the new datefragment
//                bundle.putString(GlobalValues.BUNDLE_INFO_TIME,date);
//                DateFragment dateFragment = new DateFragment();
//                dateFragment.setArguments(bundle);
//                FragmentManager fm = getParentFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.replace(R.id.nav_host_fragment_activity_main,dateFragment);
//                ft.commit();
//                Toast.makeText(getContext(),"You selected :"+dayOfMonth+"/"+(month+1)+"/"+year,Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}
