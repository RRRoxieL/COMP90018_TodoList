package com.example.todolist;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    Button loginBtn;
    Button registerBtn;

    @BindView(R.id.btn_test)
    Button test;

    @OnClick(R.id.btn_test)
    public void onClick(){
        Intent intent =  new Intent(getApplicationContext(),TestActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

//        loginBtn = findViewById(R.id.btn_login);
//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent  = new Intent();
//                intent.setClass(LoginActivity.this,MainActivity.class);
//                startActivity(intent);
//            }
//        });


//        loginBtn = findViewById(R.id.btn_register);
//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent  = new Intent();
//                intent.setClass(LoginActivity.this,MainActivity.class);
//                startActivity(intent);
//            }
//        });


    }
}