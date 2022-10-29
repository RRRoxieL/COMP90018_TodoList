package com.example.todolist.ui.analysis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.todolist.DAO.DateTask;
import com.example.todolist.MainActivity;
import com.example.todolist.databinding.FragmentAnalysisBinding;
import com.example.todolist.ui.timer.FocusTask;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AnalysisFragment extends Fragment {

    private FragmentAnalysisBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AnalysisViewModel analysisViewModel =
                new ViewModelProvider(this).get(AnalysisViewModel.class);

        binding = FragmentAnalysisBinding.inflate(inflater, container, false);
        binding.buttonYesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //read data from firebase
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            String text="0 workloads completed\nCompare to last period"+"-90%";
                            DataSnapshot dateTaskTable = task.getResult().child("DateTaskTable");
                            DataSnapshot focusTaskTable = task.getResult().child("Focus");
                            String currentDate = getCalculatedDate("dd-MM-yyyy", 0);
                            String yesterdayDate = getCalculatedDate("dd-MM-yyyy", -1);
                            ArrayList<FocusTask> focusTasks = new ArrayList<>();
                            for(DataSnapshot ds : focusTaskTable.getChildren()) {
                                String taskName = ds.child("taskName").getValue(String.class);
                                String date = ds.child("date").getValue(String.class);
                                Integer minutesFocused = ds.child("minutesFocused").getValue(Integer.class);
                                String taskID = ds.child("taskID").getValue(String.class);
                                String time = ds.child("time").getValue(String.class);
                                focusTasks.add(new FocusTask(taskName, taskID, minutesFocused, date, time));
                            }

                            int todayMin = 0;
                            int yesterdayMin = 0;
                            int todayNumFocus = 0;
                            int yesterdayNumFocus = 0;
                            for (int i = 0; i < focusTasks.size(); i++) {
                                if(focusTasks.get(i).getDate().equals(getCalculatedDate("yyyy-MM-dd", 0))){
                                    todayMin += focusTasks.get(i).getMinutesFocused();
                                    todayNumFocus ++;
                                }else if(focusTasks.get(i).getDate().equals(getCalculatedDate("yyyy-MM-dd", -1))){
                                    yesterdayMin += focusTasks.get(i).getMinutesFocused();
                                    yesterdayNumFocus ++;
                                }
                            }

                            Object value = dateTaskTable.child(currentDate).getValue();
                            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create();
                            DateTask dateTask = gson.fromJson(value.toString(), DateTask.class);
                            if(dateTask==null || dateTask.getTasks()==null){
                                Toast.makeText(getContext(), "read data == null", Toast.LENGTH_SHORT).show();
                            }
                            Log.d("firebase", text);
                            //set the textview to show events statistics
                            binding.textView1.setText(currentDate + "all task:" + dateTask.getAllTaskNum() + " finished:" + dateTask.getFinishedTaskNum());
                            binding.textView2.setText(todayMin + "m focus time completed today\n"+ yesterdayMin+"m focus time completed yesterday\n");
                        }
                    }
                });



                //set visibility
                binding.barChart.setVisibility(view.VISIBLE);
                binding.lineChart.setVisibility(view.VISIBLE);
                binding.barChart1.setVisibility(view.VISIBLE);
                binding.pieChart.setVisibility(view.GONE);

                //draw barchart
                drawBarChart(binding.barChart,"Focused time(h)", 7);

                //draw line chart
                drawLineChart(binding.lineChart, "Workload trend", 7);

                //draw bar chart2
                drawBarChart(binding.barChart1, "Yesterday focus time distribution", 24);

            }

        });

        binding.buttonEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //set visibility
                binding.barChart1.setVisibility(view.GONE);
                binding.barChart.setVisibility(view.VISIBLE);
                binding.lineChart.setVisibility(view.VISIBLE);
                binding.pieChart.setVisibility(view.VISIBLE);

                //read data from firebase

                //set the textview to show events statistics
                binding.textView1.setText("9 Events completed\nCompare to last period"+"+90%");
                binding.textView2.setText("9 workloads completed\nCompare to last period"+"+60%");

                //redraw bar chart
                drawBarChart(binding.barChart, "Events distribution last week", 7);

                //redraw line chart
                drawLineChart(binding.lineChart, "Workload trend", 7);

                //pie chart
                drawPieChart(binding.pieChart, "Event completion rate");

            }
        });

        binding.buttonTimers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //read data from firebase

                //set visibility
                binding.pieChart.setVisibility(view.GONE);
                binding.barChart1.setVisibility(view.VISIBLE);
                binding.barChart.setVisibility(view.VISIBLE);
                binding.lineChart.setVisibility(view.VISIBLE);

                //set textview
                binding.textView1.setText("3 tomatoes harvested\nCompare to last period"+"+50%");
                binding.textView2.setText("10h focus time completed\nCompare to last period"+"+30%");

                //draw line chart
                drawLineChart(binding.lineChart, "Tomato Focus Trend", 14);


                //draw bar chart1
                drawBarChart(binding.barChart1, "Focus time distribution", 24);


            }
        });

        View root = binding.getRoot();

//        final TextView textView = binding.textAnalysis;
//        analysisViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    /**
     * Pass your date format and no of days for minus from current
     * If you want to get previous date then pass days with minus sign
     * else you can pass as it is for next date
     * @param dateFormat
     * @param days
     * @return Calculated Date
     */
    public String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

    private void drawPieChart(PieChart pieChart, String pieChartLabel){
        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setRotationAngle(0);
//                binding.pieChart.setExtraOffsets(0f, 20.0f, 0f, 15.0f);

        ArrayList<PieEntry> entries = new ArrayList<>();
        //set color
        ArrayList<Integer> colors = new ArrayList<>();
        entries.add(new PieEntry(1f, "unfinished"));
        colors.add(Color.RED);
        entries.add(new PieEntry(11f, "finished"));
        colors.add(Color.GREEN);
        PieDataSet dataSet = new PieDataSet(entries, pieChartLabel);
        dataSet.setColors(colors);
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(5f);
        
        //draw solid pie
        binding.pieChart.setHoleRadius(0f);
        binding.pieChart.setTransparentCircleRadius(0f);

        //show outside of pie chart
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(1f);
        dataSet.setValueLinePart2Length(1f);
        dataSet.setValueLineColor(Color.parseColor("#287DFD"));
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        //show percent
        dataSet.setDrawValues(dataSet.isDrawValuesEnabled());
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(binding.pieChart));
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.BLACK);

        binding.pieChart.setData(data);
        binding.pieChart.highlightValues(null);
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.animateY(1400);
        binding.pieChart.animateY(1400, Easing.EaseInQuad);
        binding.pieChart.invalidate();

//      legend setting
        Legend ll = binding.pieChart.getLegend();
        ll.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        ll.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        ll.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        ll.setDrawInside(false);
        ll.setEnabled(true);

//      label setting
        binding.pieChart.setEntryLabelColor(Color.BLACK);
        binding.pieChart.setEntryLabelTextSize(15f);
    }

    private void drawLineChart(LineChart lineChart, String lineChartLabel, int dotNumber){
        ArrayList<Entry> line = new ArrayList<Entry>();
        ArrayList<String> xAxisLabel = new ArrayList<String>();
        for (int i = 0; i < dotNumber; i++) {
            int j = dotNumber-i;
//                    String date = getCalculatedDate("MMdd" , -j);
//                    int d = Integer.valueOf(date);
            line.add(new Entry(i, j*i%dotNumber));
            xAxisLabel.add(getCalculatedDate("dd-MMM" , -j));
        }

        LineDataSet lineDataSet = new LineDataSet(line, lineChartLabel);
//                lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(16f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
//                xAxis.setTextSize(8f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));


        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setText(lineChartLabel);
        lineChart.animateY(2000);
    }

    private void drawBarChart(BarChart barChart, String barChartLabel, int barNumber){
        //draw barchart
        ArrayList<BarEntry> arr = new ArrayList<>();
        ArrayList<String> xAxisLabel = new ArrayList<String>();
        for (int i = 0; i < barNumber; i++) {
            int j = barNumber-i;
//                    String date = getCalculatedDate("MMdd" , -j);
//                    int d = Integer.valueOf(date);
            arr.add(new BarEntry(i, i*i%barNumber));
            if(barNumber == 7 && barChartLabel.endsWith("last week")){
                xAxisLabel.add(getCalculatedDate("EEE" , -j));
            }else if (barNumber == 7){
                xAxisLabel.add(getCalculatedDate("dd-MMM" , -j));
            }else if (barNumber == 24){
                xAxisLabel.add(String.valueOf(i));
            }
        }


        BarDataSet barDataSet = new BarDataSet(arr, barChartLabel);
        //barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);

        //change x label
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        barChart.getDescription().setText(barChartLabel);
        barChart.animateY(2000);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
