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

import java.text.SimpleDateFormat;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textCalendar;
        calendarViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        CalendarView calendar = binding.Calender;
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                CharSequence date = ""+dayOfMonth+"-"+(month+1)+"-"+year;
                Bundle bundle = new Bundle();
                bundle.putCharSequence("time",date);
                DateFragment dateFragment = new DateFragment();
                dateFragment.setArguments(bundle);
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment_activity_main,dateFragment);
                ft.commit();
                Toast.makeText(getContext(),"You selected :"+dayOfMonth+"/"+(month+1)+"/"+year,Toast.LENGTH_SHORT).show();
            }
        });

//        calendarViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        textView.setText("10-06-2022");
//
//        textView.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View v) {

//            }
//        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}
