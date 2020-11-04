package com.example.booker.activities;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.booker.R;
import com.example.booker.ui.profile.ProfileViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

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

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    HashMap<String, String> data = new HashMap<>();

                    CollectionReference collectionReference = db.collection("User");
                    data.put("Name", name);
                    String Id = user.getUid();

                    collectionReference
                            .document(Id)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    intent.putExtra("User Name", name);
                                    setResult(0, intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.getUid();
                                }
                            });


                }
                else {

                    // For Test, Delete before submit -- Yee Lin
                    Toast.makeText(getApplicationContext(), "Bad", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
