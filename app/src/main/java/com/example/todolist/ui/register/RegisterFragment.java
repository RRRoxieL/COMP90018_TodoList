package com.example.todolist.ui.register;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.todolist.R;

public class RegisterFragment extends Fragment {

    private View root;
    Button signupBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(root == null){
            root = inflater.inflate(R.layout.fragment_register, container, false);
        }
        signupBtn = root.findViewById(R.id.btn_signup);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController controller = Navigation.findNavController(view);
                controller.navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });
        return root;
    }
}