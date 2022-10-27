package com.example.todolist.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.todolist.R;
import com.example.todolist.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseDatabase mDatabase;

    Button logoutBtn;
    Button edit;
    TextView userName;
    TextView gender;
    TextView email;
    TextView password;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);


        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mDatabase = FirebaseDatabase.getInstance();

        logoutBtn = root.findViewById(R.id.logOut);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController controller = Navigation.findNavController(view);
                controller.navigate(R.id.action_logoutFragment_to_loginFragment);
            }
        });

        userName = root.findViewById(R.id.userName);
        gender = root.findViewById(R.id.gender);
        email = root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);

//        userName.setText("Leah");
//        gender.setText("Female");
//        email.setText("kexinwen@gmail.com");
//        password.setText("123456");

        mDatabase.getReference().child("aV3rQVEdexZXWqfc0cGAlB0sQjh1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
//                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    userName.setText("Leah");
                    gender.setText("Female");
                    email.setText("leahkexinwen@gmail.com");
                    password.setText("123456");
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}