package com.example.booker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ChangeProfile extends AppCompatActivity {

    private EditText changeEmail, changePassword;
    private Button cancelBtn;
    private Button comfirmBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_profile);

        changeEmail = (EditText) findViewById(R.id.change_email);
        changePassword = (EditText) findViewById(R.id.change_password);
        comfirmBtn = (Button) findViewById(R.id.change_confirm_btn);
        cancelBtn = (Button) findViewById(R.id.change_cancel_btn);

        db = FirebaseFirestore.getInstance();

        final Intent intent = getIntent();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference collectionReference = db.collection("User");
        final String userEmail = user.getEmail();
        changeEmail.setText(userEmail);

        changeEmail.addTextChangedListener(textWatcher);
        changePassword.addTextChangedListener(textWatcher);

        comfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(userEmail, changePassword.getText().toString().trim());

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("User reauthentication", "successful");
                            user.updateEmail(changeEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("User Email", "Update");
                                        finish();
                                    }
                                    else {
                                        Log.d("User Email", "Fail Update");
                                    }
                                }
                            });
                        }
                        else {
                            Log.d("User Reauthentication", "fail");
                        }
                    }
                });

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("Email", changeEmail.getText().toString().trim());
                collectionReference
                        .document(user.getUid())
                        .update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Change email", "Success");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Change email", "Fail");
                            }
                        });
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (changeEmail.getText().toString().trim().isEmpty() ||
                    changePassword.getText().toString().isEmpty()) {
                comfirmBtn.setEnabled(false);
            }
            else {
                comfirmBtn.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
