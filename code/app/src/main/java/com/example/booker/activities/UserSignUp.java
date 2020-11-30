package com.example.booker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * Yee's Part
 * The activity allow user to sign up
 * EditText userName: enable user to input username
 * EditText userEmail: enable user to input email
 * EditText userPassword: enable user to input password
 * Button btnSubmit: sumbit the form
 */

public class UserSignUp extends AppCompatActivity {

    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private Button btnSubmit;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = (EditText) findViewById(R.id.sign_up_username);
        userEmail = (EditText) findViewById(R.id.sign_up_email);
        userPassword = (EditText) findViewById(R.id.sign_up_password);

        btnSubmit = (Button) findViewById(R.id.sign_up_submit);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // When the button is triggered, the sign up event is occuring
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        });

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("User State", "Change");
            }
        });

    }

    /*
        The sign up function interat with google authenticaton
        return: void
     */
    private void signUp() {


        final Intent intent = getIntent();
        final String name = userName.getText().toString();
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (name.isEmpty()){
            userName.requestFocus();
            return;
        }

        if (email.isEmpty()){
            userEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            userPassword.requestFocus();
            return;
        }

        // When user try to create a new account, triggered the interacting event with mauth
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d("Sign up", "Good");
                    HashMap<String, String> data = new HashMap<>();

                    CollectionReference collectionReference = db.collection("User");
                    data.put("Email", email);
                    data.put("Name", name);
                    String Id = user.getUid();

                    collectionReference
                            .document(Id)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Sign Up Data Setting", "Success");
                                    intent.putExtra("User Name", name);
                                    setResult(0, intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Sign Up Data Setting", "Fail");
                                }
                            });
                }
                else {
                    Log.d("Sign up", "Fail");
                }
            }
        });

    }



}
