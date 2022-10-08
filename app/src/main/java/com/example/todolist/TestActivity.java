package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.btn_calenderTest)
    Button calenderTest;

    @BindView(R.id.btn_homeTest)
    Button homeTest;

    @OnClick({R.id.btn_calenderTest,R.id.btn_homeTest})
    public void onClick(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.btn_calenderTest:
                intent = new Intent(getApplicationContext(), CalenderActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_homeTest:
                intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
    }
}