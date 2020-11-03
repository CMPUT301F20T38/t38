package com.example.booker.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.booker.MainActivity;
import com.example.booker.R;
import com.example.booker.activities.ChangeProfile;
import com.example.booker.activities.UserSignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;


public class ProfileFragment extends Fragment {

    private LinearLayout changeProfile;
    private TextView textView;
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnSignOut;
    private String currentUser;
    private boolean isFirstLoading;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        isFirstLoading = true;
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeProfile = (LinearLayout) getActivity().findViewById(R.id.user_profile);
        textView = (TextView) getActivity().findViewById(R.id.user_name);
        btnLogin = (Button) getActivity().findViewById(R.id.profile_login);
        btnSignUp = (Button) getActivity().findViewById(R.id.profile_sign_up);
        btnSignOut = (Button) getActivity().findViewById(R.id.profile_sign_out);

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
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
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
                textView.setText(data.getStringExtra("User Name"));
            }
        }
        if (requestCode == 1){
            if (resultCode == 0){
                textView.setText(data.getStringExtra("New Name"));
            }
        }
    }
}
