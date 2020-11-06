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
import androidx.fragment.app.Fragment;

import com.example.booker.R;
import com.example.booker.activities.BorrowedBookListActivity;
import com.example.booker.activities.ChangeProfile;
import com.example.booker.activities.UserLogin;
import com.example.booker.activities.UserSignUp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 *  Yee's Part
 *  The third fragment of the main activity named Profile
 *  TextView textView: contain user name
 *  TextView borrowRequest: clickable textview which jump to request list activity
 *  Button btnLogin: clickable Button which jump to Login activty
 *  Button btnSignUp: Button which navigate to Sign up activity
 *  Button btnSignOut: Button which sign out current user
 *  FirebaseUser: get current login user
 *
 *  RequestButton onClickListener:
 *  Sign Up Button onClicklistener: Excute sign up activity if there is no user loginning
 *  Sign In Button onClickListener: Excute sign in activity if there is no user loginning
 *  Sign Out Button onClickListner: Excute sign out activity if there is a loginning user
 *  Change Porfile TextView onClickListener: Excute change profile activity if there is a loginning user
 *  Firebase user state listener: Detect if there is user login or sign out
 *
 */

public class ProfileFragment extends Fragment {

    private LinearLayout changeProfile;
    private TextView textView, borrowRequest;
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnSignOut;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        textView = (TextView) root.findViewById(R.id.show_user_name);
        borrowRequest = (TextView) root.findViewById(R.id.profile_borrow);
        changeProfile = (LinearLayout) root.findViewById(R.id.user_profile);
        btnLogin = (Button) root.findViewById(R.id.profile_login);
        btnSignUp = (Button) root.findViewById(R.id.profile_sign_up);
        btnSignOut = (Button) root.findViewById(R.id.profile_sign_out);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Clickable TextView to borrowed book activity
        borrowRequest.setOnClickListener(new View.OnClickListener() {
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

        // When the button is click, trigger even
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If there is a logging user, raise notice information
                if (user != null){
                    Toast.makeText(getContext(), "You have already logined", Toast.LENGTH_SHORT).show();
                }

                // If there is no logging user, navigate to the sign up activity
                else {
                    Intent intent = new Intent(view.getContext(), UserSignUp.class);
                    startActivity(intent);
                }
            }
        });

        // When the button is click, trigger event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If there is user logging, notify user their logging state
                if (user != null){
                    Toast.makeText(getContext(), "You have already logged in", Toast.LENGTH_SHORT).show();
                }

                // If there is no user logging, navigate to the login in activity
                else {
                    Intent intent = new Intent(getContext(), UserLogin.class);
                    startActivity(intent);
                }
            }
        });

        // WHen the button is trigger, start event
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If there is no user logging, notify user
                if (user == null){
                    Toast.makeText(getContext(), "Please Log in", Toast.LENGTH_SHORT).show();
                }

                // If there is user logging, take him/her off-line
                else {
                    FirebaseAuth.getInstance().signOut();
                }
            }
        });

        // When the textview is trigger, start event
        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If there is no logging user, notify user
                if (user == null){
                    Toast.makeText(getContext(), "Please Log in first!", Toast.LENGTH_SHORT).show();
                }

                // Navigate to change profile activity
                else {
                    Intent intent = new Intent(getContext(), ChangeProfile.class);
                    startActivity(intent);
                }
            }
        });

        // If the fireauth state changing, sync the data change to local
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = FirebaseAuth.getInstance().getCurrentUser();
            }
        });

        return root;
    }


}
