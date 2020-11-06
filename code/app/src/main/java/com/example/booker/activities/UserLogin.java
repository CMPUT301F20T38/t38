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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

 /**
  * Yee's Part
  * The activity allow user to login
  * EditText userEmail: enable user to input email
  * EditText userPassword: enable user to input password
  * Button btnSubmit: sumbit the form
  */
 public class UserLogin extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPassword;
    private Button btnSubmit;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);

        userEmail = (EditText) findViewById(R.id.login_email);
        userPassword = (EditText) findViewById(R.id.login_password);
        btnSubmit = (Button) findViewById(R.id.login_submit);

        mAuth = FirebaseAuth.getInstance();

        // When button is click, excute the login method
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    /*
        Execute the login event
        return: void
     */
    private void login() {

        Intent intent = getIntent();
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (email.isEmpty()){
            userEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            userPassword.requestFocus();
            return;
        }

        
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d("Login", "Success");
                }
                else {
                    Log.d("Login","Fail");
                }
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        setResult(1);
        finish();
    }
}
