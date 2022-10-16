package com.example.todolist.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolist.R;
import com.example.todolist.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private LinearLayout layout;

    int i = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);


        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        layout = binding.eventLayout;

        FloatingActionButton fab =  binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"clicked fab", Toast.LENGTH_SHORT).show();
                addView(root);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void addView(View view) {
//        TextView child = new TextView(getContext());
//        child.setTextSize(20);
//        // 获取当前的时间并转换为时间戳格式,
//        i++;
//        String currentTime = "event " + i;
//        child.setText(currentTime);
//        // 调用一个参数的addView方法
//        layout.addView(child);
        i++;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.addView(createEventView(),lp);

        // 添加分割线
        TextView splitLine = new TextView(getContext());
        splitLine.setWidth(layout.getWidth());
        splitLine.setHeight(1);
        splitLine.setBackgroundColor(Color.parseColor("#666666"));
        layout.addView(splitLine);
    }

    private LinearLayout createEventView(){
        LinearLayout.LayoutParams lp;

        // 事件主体
        LinearLayout event =  new LinearLayout(getContext());
        event.setOrientation(LinearLayout.HORIZONTAL);
        event.setBackgroundColor(Color.parseColor("#00ffff"));

        // 复选框
        CheckBox checkBox = new CheckBox(getContext());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // 选中（已完成），背景为灰色
                    event.setBackgroundColor(Color.parseColor("#d1d1e0"));
                } else {
                    // 没选中（未完成），背景为蓝色
                    event.setBackgroundColor(Color.parseColor("#00ffff"));
                }
            }
        });
        lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight=1;
        lp.leftMargin = 20;
        event.addView(checkBox,lp);

        // 事件信息layout
        LinearLayout info = new LinearLayout(getContext());
        info.setOrientation(LinearLayout.VERTICAL);

        // 事件标题
        TextView title = new TextView(getContext());
        title.setText("title"+i);
        title.getPaint().setFakeBoldText(true);
        lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin=20;
        info.addView(title,lp);

        // 事件简介
        TextView msg = new TextView(getContext());
        msg.setText("This is msg");
        lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 10;
        lp.bottomMargin=20;
        info.addView(msg,lp);

        lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.weight=6;
        event.addView(info,lp);
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"clicked "+title.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        ImageView timer =  new ImageView(getContext());
        timer.setImageResource(R.drawable.ic_timer);
        lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity= Gravity.CENTER;
        lp.rightMargin=20;
        lp.weight=1;

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You cliked timer icon", Toast.LENGTH_SHORT).show();
            }
        });
        event.addView(timer,lp);

        return event;
    }

}