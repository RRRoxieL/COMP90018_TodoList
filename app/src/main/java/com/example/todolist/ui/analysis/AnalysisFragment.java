package com.example.todolist.ui.analysis;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.IntStream;

public class AnalysisFragment extends Fragment {

    private FragmentAnalysisBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AnalysisViewModel analysisViewModel =
                new ViewModelProvider(this).get(AnalysisViewModel.class);

        binding = FragmentAnalysisBinding.inflate(inflater, container, false);
        int period = 7; //one period has 7 days
        int numDays = 2*period;
        binding.buttonYesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set visibility
                binding.barChart.setVisibility(view.VISIBLE);
                binding.lineChart.setVisibility(view.VISIBLE);
                binding.barChart1.setVisibility(view.VISIBLE);
                binding.pieChart.setVisibility(view.GONE);
                //read data from firebase
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {

                            //read focus data
                            ArrayList<FocusTask> focusTasks = readFocusTask(task);
                            int[][] stat = getFocusStatistics(focusTasks, numDays);
                            int[] focusMin = stat[0];
                            int[] focusNum = stat[1];
                            int[] yesterdayFocusDistribution = stat[2];

                            //read event/date data
                            ArrayList<DateTask> dateTasks = readDateTask(task, numDays);
                            int[] workload = getWorkload(dateTasks, numDays);

                            DateTask todayTask = dateTasks.get(dateTasks.size()-1);
                            DateTask yesterdayTask = dateTasks.get(dateTasks.size()-2);

                            //set the textview to show statistics
                            binding.textView1.setText(todayTask.getFinishedTaskNum() + " task(s) finished today\n"
                                    +yesterdayTask.getFinishedTaskNum() + " task(s) finished yesterday");
                            binding.textView2.setText(focusMin[focusMin.length-1] + " min focused today\n"
                                    + focusMin[focusMin.length-2]+" min focused yesterday");

                            //draw barchart
                            drawBarChart(binding.barChart,"Focused time(m)", period, focusMin);

                            //draw line chart
                            drawLineChart(binding.lineChart, "Workload trend last 7 days", period, workload);

                            //draw bar chart2
                            drawBarChart(binding.barChart1, "Yesterday 24h focus time(min) distribution", 24, yesterdayFocusDistribution);
                        }
                    }
                });
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
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {

                            //read event/date data

                            ArrayList<DateTask> dateTasks = readDateTask(task, numDays);
                            int[] workload = getWorkload(dateTasks, numDays);

                            int[] lastPeriodFinished = new int[period];
                            int[] lastPeriodAll = new int[period];
                            int[] thisPeriodFinished = new int[period];
                            int[] thisPeriodAll = new int[period];
                            for (int i = 0; i < period; i++) {
                                lastPeriodFinished[i] = dateTasks.get(i).getFinishedTaskNum();
                                lastPeriodAll[i] = dateTasks.get(i).getAllTaskNum();
                                thisPeriodFinished[i] = dateTasks.get(period+i).getFinishedTaskNum();
                                thisPeriodAll[i] = dateTasks.get(period+i).getAllTaskNum();
                            }

                            //set the textview to show events statistics
                            binding.textView2.setText("Last period:\n"
                                    +sum(lastPeriodAll) + " task(s) generated\n"
                                    +sum(lastPeriodFinished) + " task(s) finished");

                            binding.textView1.setText("This period(latest 7 days):\n"
                                    +sum(thisPeriodAll) + " task(s) generated\n"
                                    +sum(thisPeriodFinished) + " task(s) finished");

                            //redraw bar chart
                            drawBarChart(binding.barChart, "Events distribution this period including unfinished"
                                    , period, thisPeriodAll);

                            //redraw line chart
                            drawLineChart(binding.lineChart, "Workload trend last 14 days"
                                    , numDays, workload);

                            //pie chart
                            drawPieChart(binding.pieChart, "Event completion rate this period"
                                    , sum(thisPeriodFinished), sum(thisPeriodAll)-sum(thisPeriodFinished));

                        }
                    }
                });


            }
        });

        binding.buttonTimers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set visibility
                binding.pieChart.setVisibility(view.GONE);
                binding.barChart1.setVisibility(view.VISIBLE);
                binding.barChart.setVisibility(view.VISIBLE);
                binding.lineChart.setVisibility(view.VISIBLE);

                //read data from firebase
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            //read focus data
                            ArrayList<FocusTask> focusTasks = readFocusTask(task);
                            int[][] stat = getFocusStatistics(focusTasks, numDays);
                            int[] focusMin = stat[0];
                            int[] focusNum = stat[1];
                            int[] yesterdayFocusDistribution = stat[2];

                            int[] thisPeriodTomato = new int[period];
                            int[] thisPeriodMin = new int[period];
                            int[] lastPeriodTomato = new int[period];
                            int[] lastPeriodMin = new int[period];
                            for (int i = 0; i < period; i++) {
                                lastPeriodTomato[i] = focusNum[i];
                                thisPeriodTomato[i] = focusNum[i+period];
                                lastPeriodMin[i] = focusMin[i];
                                thisPeriodMin[i] = focusMin[i+period];
                            }
                            //set the textview to show statistics
                            binding.textView1.setText("This period(latest 7 days):\n"
                                    +sum(thisPeriodTomato) + " tomato(es) harvested\n"
                                    +sum(thisPeriodMin) + " min focused");

                            binding.textView2.setText("Last period:\n"
                                    +sum(lastPeriodTomato) + " tomato(es) harvested\n"
                                    +sum(lastPeriodMin) + " min focused");


                            //draw bar chart
                            drawBarChart(binding.barChart, "Tomatoes distribution this period", period, thisPeriodTomato);

                            //draw line chart
                            drawLineChart(binding.lineChart, "Focus time(min) trend last 14 days", numDays, focusMin);


                            //draw bar chart1
                            drawBarChart(binding.barChart1, "Yesterday 24h focus time(min) distribution", 24, yesterdayFocusDistribution);
                        }
                    }
                });


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

    private int[] getWorkload(ArrayList<DateTask> dateTasks, int numDays){
        int[] workload = new int[numDays];
        for (int i = 0; i < workload.length; i++) {
            workload[i] = dateTasks.get(i).getFinishedTaskNum();
        }
        return workload;
    }

    private int[][] getFocusStatistics(ArrayList<FocusTask> focusTasks, int numDays){
        ArrayList<String> lastDays = new ArrayList<String>();
        //last14Days.get(13) is today, last14Days.get(12) is yesterday and so on
        for (int i = 0; i < numDays; i++) {
            int j = numDays - 1 - i;
            lastDays.add(getCalculatedDate("dd-MM-yyyy" , -j));
        }

        String yesterdayDate = lastDays.get(lastDays.size()-2);
        int[][] stat = new int[3][];
        stat[0] = new int[lastDays.size()];//FocusMin
        stat[1] = new int[lastDays.size()];//FocusNum
        stat[2] = new int[24];//yesterday 24h distribution

        for (int j = 0; j < lastDays.size(); j++) {
            //get focus min and focus number for each day in the last 14 days
            for (int i = 0; i < focusTasks.size(); i++) {
                if(focusTasks.get(i).getDate().equals(lastDays.get(j))){
                    stat[0][j] += focusTasks.get(i).getMinutesFocused();
                    stat[1][j] ++;
                }

            }
        }
        for (int i = 0; i < focusTasks.size(); i++) {
            //get yesterday 24h focus time distribution
            if(focusTasks.get(i).getDate().equals(yesterdayDate)){
                int minFocused = focusTasks.get(i).getMinutesFocused();
                String[] time = focusTasks.get(i).getTime().split(":");
                int hour = Integer.parseInt(time[0]);
                int min = Integer.parseInt(time[1]);
                if(min+minFocused>59){
                    stat[2][hour] += 60 - min;
                    hour++;
                    min = min + minFocused - 60;
                    if (hour < 24){
                        stat[2][hour] += min;
                    }
                }else{
                    stat[2][hour] += minFocused;
                }


            }
        }
        return stat;
    }

    private ArrayList<FocusTask> readFocusTask(Task<DataSnapshot> task){
        //get current user id
        String userID = ((MainActivity)getActivity()).getUid();
        DataSnapshot focusTaskTable = task.getResult().child("Focus").child(userID);
        //read all focus tasks
        ArrayList<FocusTask> focusTasks = new ArrayList<>();
        for(DataSnapshot ds : focusTaskTable.getChildren()) {
            String taskName = ds.child("taskName").getValue(String.class);
            String date = ds.child("date").getValue(String.class);
            Integer minutesFocused = ds.child("minutesFocused").getValue(Integer.class);
            String taskID = ds.child("taskID").getValue(String.class);
            String time = ds.child("time").getValue(String.class);
            String uid = ds.child("uid").getValue(String.class);
            focusTasks.add(new FocusTask(uid, taskName, taskID, minutesFocused, date, time));
        }
        return focusTasks;
    }

    //get the number of all tasks and finished tasks for each day in the last numDays
    private ArrayList<DateTask> readDateTask(Task<DataSnapshot> task, int numDays){
        //get current user id
        String userID = ((MainActivity)getActivity()).getUid();
        DataSnapshot dateTaskTable = task.getResult().child("DateTaskTable").child(userID);
        //read event/date data
        ArrayList<DateTask> dateTasks = new ArrayList<>();
        ArrayList<String> lastDays = new ArrayList<String>();
        //last14Days.get(13) is today, last14Days.get(12) is yesterday and so on
        for (int i = 0; i < numDays; i++) {
            int j = numDays - 1 - i;
            lastDays.add(getCalculatedDate("dd-MM-yyyy" , -j));
        }
        for (int i = 0; i < lastDays.size(); i++) {
            if(dateTaskTable.hasChild(lastDays.get(i))){
                Object value = dateTaskTable.child(lastDays.get(i)).getValue();
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create();
                DateTask dateTask = gson.fromJson(value.toString(), DateTask.class);
                if(dateTask==null || dateTask.getTasks()==null){
                    Toast.makeText(getContext(), "read data == null", Toast.LENGTH_SHORT).show();
                }
                dateTasks.add(dateTask);
            }else {
                DateTask dateTask = new DateTask();
                dateTask.setAllTaskNum(0);
                dateTask.setFinishedTaskNum(0);
                dateTasks.add(dateTask);
            }
        }
        return dateTasks;

    }

    private void drawPieChart(PieChart pieChart, String pieChartLabel, int finished, int unfinished){
        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setRotationAngle(0);
//                binding.pieChart.setExtraOffsets(0f, 20.0f, 0f, 15.0f);

        ArrayList<PieEntry> entries = new ArrayList<>();
        //set color
        ArrayList<Integer> colors = new ArrayList<>();
        entries.add(new PieEntry(unfinished, "unfinished"));
        colors.add(Color.RED);
        entries.add(new PieEntry(finished, "finished"));
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

    private void drawLineChart(LineChart lineChart, String lineChartLabel, int dotNumber, int[] y){
        ArrayList<Entry> line = new ArrayList<Entry>();
        ArrayList<String> xAxisLabel = new ArrayList<String>();
        for (int i = 0; i < dotNumber; i++) {
            //dotNumber - 1 includes today
            int j = dotNumber-1-i;
            line.add(new Entry(i, y[y.length-dotNumber+i]));
            xAxisLabel.add(getCalculatedDate("dd-MMM" , -j));
        }

        LineDataSet lineDataSet = new LineDataSet(line, lineChartLabel);
//                lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(16f);
        //set float to int
        lineDataSet.setValueFormatter(new DefaultValueFormatter(0));

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

    private int sum(int[] a){
        int sum = 0;
        for (int i : a) {
            sum += i;
        }
        return sum;
    }

    private void drawBarChart(BarChart barChart, String barChartLabel, int barNumber, int[] y){
        //draw barchart
        ArrayList<BarEntry> arr = new ArrayList<>();
        ArrayList<String> xAxisLabel = new ArrayList<String>();
        for (int i = 0; i < barNumber; i++) {
            // barNumber-1 includes today
            int j = barNumber-1-i;
            if(y.length < 24){
                //last barNumber days
                arr.add(new BarEntry(i, y[y.length-barNumber+i]));
            }else{
                //last 24 hours
                arr.add(new BarEntry(i, y[i]));
            }
            if(barNumber == 7 && barChartLabel.contains("this period")){
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
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueFormatter(new DefaultValueFormatter(0));

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
