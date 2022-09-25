package com.example.todolist.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.todolist.MainActivity;
import com.example.todolist.R;

public class LoginFragment extends Fragment {

    private View root;
    Button loginBtn;
    Button registerBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Button loginBtn;
//        loginBtn = getView().findViewById(R.id.btn_login);
//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavController controller = Navigation.findNavController(view);
//                controller.navigate(R.id.action_loginFragment_to_registerFragment);
//            }
//        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(root == null){
            root = inflater.inflate(R.layout.fragment_login, container, false);
        }

        registerBtn = root.findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController controller = Navigation.findNavController(view);
                controller.navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });

        loginBtn = root.findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent();
                intent.setClass(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }


}