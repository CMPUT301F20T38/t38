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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;


public class ProfileFragment extends Fragment {

    private LinearLayout changeProfile;
    private TextView textView, profile_borrow;
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnSignOut;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        textView = (TextView) root.findViewById(R.id.show_user_name);
        profile_borrow = (TextView) root.findViewById(R.id.profile_borrow);
        changeProfile = (LinearLayout) root.findViewById(R.id.user_profile);
        btnLogin = (Button) root.findViewById(R.id.profile_login);
        btnSignUp = (Button) root.findViewById(R.id.profile_sign_up);
        btnSignOut = (Button) root.findViewById(R.id.profile_sign_out);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Clickable TextView to borrowed book activity
        profile_borrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check whether there's user logged in first, if there is, then intent
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null){
                    Toast.makeText(getContext(), "Please log in first!", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(view.getContext(), BorrowedBookListActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null){
                    Toast.makeText(getContext(), "You have already logined", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(view.getContext(), UserSignUp.class);
                    startActivity(intent);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null){
                    Toast.makeText(getContext(), "You have already logged in", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getContext(), UserLogin.class);
                    startActivity(intent);
                }
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null){
                    Toast.makeText(getContext(), "Please Log in", Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseAuth.getInstance().signOut();
                }
            }
        });


        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null){
                    Toast.makeText(getContext(), "Please Log in first!", Toast.LENGTH_SHORT).show();
                }

                else {
                    Intent intent = new Intent(getContext(), ChangeProfile.class);
                    startActivity(intent);
                }
            }
        });

        return root;
    }


}
