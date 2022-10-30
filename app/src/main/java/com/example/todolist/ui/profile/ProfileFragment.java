package com.example.todolist.ui.profile;

import android.os.Bundle;
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

import com.example.todolist.MainActivity;
import com.example.todolist.R;
import com.example.todolist.databinding.FragmentProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private String userID;

    Button logoutBtn;
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

        // initialise firebase realtime database
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        // get current user ID from MainActivity
        userID = ((MainActivity)getActivity()).getUid();

        // define user information textview
        userName = root.findViewById(R.id.userName);
        gender = root.findViewById(R.id.gender);
        email = root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);

        // create navigation from logout button to login fragment
        logoutBtn = root.findViewById(R.id.logOut);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController controller = Navigation.findNavController(view);
                controller.navigate(R.id.action_logoutFragment_to_loginFragment);
            }
        });

        // read user data
        readUser();

        return root;
    }

    // read user data from Firebase realtime database based on user ID
    private void readUser(){
        mReference.child("Users").child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get initial user value as a string
                Object value = snapshot.getValue();

                // parse string data
                String[] userData = value.toString().split(",");
                String[] parsed = userData;
                for (int i = 0; i < 4; i++) {
                    parsed[i] = userData[i].split("=")[1];
                }
                userName.setText(parsed[3].replace("}",""));
                gender.setText(parsed[1]);
                email.setText(parsed[2]);
                password.setText(parsed[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
