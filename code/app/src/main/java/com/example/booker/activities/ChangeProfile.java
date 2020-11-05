package com.example.booker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booker.R;
import com.example.booker.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ChangeProfile extends AppCompatActivity {

    private EditText changeemail;
    private EditText changeephone;
    private Button cancelBtn;
    private Button comfirmBtn;
    private FirebaseFirestore db;
    private String docID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_profile);

        changeemail = (EditText) findViewById(R.id.change_email);
        changeephone = (EditText) findViewById(R.id.change_phone);

        comfirmBtn = (Button) findViewById(R.id.change_confirm_btn);
        cancelBtn = (Button) findViewById(R.id.change_cancel_btn);

        db = FirebaseFirestore.getInstance();

        final Intent intent = getIntent();
        final String currrentUserName = intent.getStringExtra("User Name");
//        changeemail.setText(currrentUserName);


        db.collection("User").whereEqualTo("Name",currrentUserName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                docID = document.getId();
                            }

                        }
                    }
                });

        TextView contactinfo = findViewById(R.id.show_email);
        TextView contactinfo2 = findViewById(R.id.show_phone);

        comfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String newName = changeemail.getText().toString();
//                intent.putExtra("New Name", newName);
//                setResult(0, intent);
//                finish();
                String new_email = changeemail.getText().toString();
                String new_phone = changeephone.getText().toString();
                Map<String, Object> data = new HashMap<>();
                if (!new_email.equals("")) {
                    data.put("Email", new_email);
                }
                if (!new_phone.equals("")) {
                    data.put("Phone", new_phone);
                }
                db.collection("User").document(docID)
                        .update(data);

                intent.putExtra("email",new_email);
                intent.putExtra("phone",new_phone);
//                contactinfo.setText(new_email);
//                contactinfo2.setText(new_phone);
                setResult(0,intent);
                finish();

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
