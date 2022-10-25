package com.example.todolist.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.todolist.MainActivity;
import com.example.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

                FirebaseAuth auth = FirebaseAuth.getInstance();
                String email = "dikaiz@student.unimelb.edu.au";
                String password = "12345678Zdk";
                final FirebaseUser[] currentUser = {null};
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            currentUser[0] = FirebaseAuth.getInstance().getCurrentUser();
                            Intent intent  = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getContext(), "login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
//                Toast.makeText(getContext(), currentUser[0].toString(), Toast.LENGTH_SHORT).show();

                    }
                });
//                Intent intent  = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent);

            }
        });

        return root;
    }


}