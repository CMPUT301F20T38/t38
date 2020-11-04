package com.example.booker.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.booker.R;
import com.example.booker.activities.BorrowedBookListActivity;
import com.example.booker.activities.ChangeProfile;
import com.example.booker.activities.UserLogin;
import com.example.booker.activities.UserSignUp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {

    private LinearLayout changeProfile;
    private TextView textView, profile_borrow;
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnSignOut;

    private ProfileViewModel profileViewModel;

    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        textView = (TextView) getActivity().findViewById(R.id.user_name);
        profile_borrow =(TextView)  getActivity().findViewById(R.id.profile_borrow);
        profileViewModel.getUserName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeProfile = (LinearLayout) getActivity().findViewById(R.id.user_profile);
        textView = (TextView) getActivity().findViewById(R.id.user_name);
        profile_borrow =(TextView)  getActivity().findViewById(R.id.profile_borrow);
        btnLogin = (Button) getActivity().findViewById(R.id.profile_login);
        btnSignUp = (Button) getActivity().findViewById(R.id.profile_sign_up);
        btnSignOut = (Button) getActivity().findViewById(R.id.profile_sign_out);

        //button to borrowed book activity
        profile_borrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check whether there's user logged in first, if there is, then intent
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null){
                    Toast.makeText(getContext(), "Please log in first!", Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(view.getContext(), BorrowedBookListActivity.class);
                    startActivity(intent);
                }

            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UserSignUp.class);
                startActivityForResult(intent, 0);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UserLogin.class);
                startActivityForResult(intent, 0);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * For test Login id, delete before submit
                 */

                Toast.makeText(getContext(), FirebaseAuth.getInstance().getCurrentUser().getUid() + "jjjj", Toast.LENGTH_LONG).show();
            }
        });

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChangeProfile.class);
                String userName = textView.getText().toString();
                intent.putExtra("User Name", userName);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            if (resultCode == 0){

            }

            if (requestCode == 1){
                Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 1){
            if (resultCode == 0){

            }
        }
    }
}