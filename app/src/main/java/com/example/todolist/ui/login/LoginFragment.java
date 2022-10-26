package com.example.todolist.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.todolist.MainActivity;
import com.example.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private View root;
    Button loginBtn;
    Button registerBtn;

    EditText login_username, login_password;
    Button signupBtn;

    private FirebaseAuth mAuth;

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

        //auth components
        mAuth = FirebaseAuth.getInstance();

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

        login_username = root.findViewById(R.id.login_username);
        login_password = root.findViewById(R.id.login_password);

        loginBtn = root.findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin(view);
            }

        });

        return root;
    }

    private void userLogin(View view){
        String username = login_username.getText().toString().trim();
        String password = login_password.getText().toString().trim();

        if(username.isEmpty()){
            login_username.setError("Email is required!");
            login_username.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            login_username.setError("Please enter a valid email!");
            login_username.requestFocus();
            return;
        }

        if(password.isEmpty()){
            login_password.setError("password is required!");
            login_password.requestFocus();
            return;
        }

        if(password.length() < 6){
            login_password.setError("Min password length is 6 characters!");
            login_password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    view.getContext().startActivity(intent);
                }else{

                }
            }
        });
    }

}