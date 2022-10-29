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
        mReference = mDatabase.getReference();
        userID = "pK5mJ29nbGbFNGIMB2fOde31tlw1";

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

        readUser();

        return root;
    }

    private void readUser(){
        mReference.child("Users").child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object value = snapshot.getValue();
                userName.setText(value.toString());
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
