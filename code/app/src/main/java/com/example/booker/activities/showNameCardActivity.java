package com.example.booker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.booker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class showNameCardActivity extends AppCompatActivity {
    private String docID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_name_card);

        final Intent intent = getIntent();
        final String currrentUserName = intent.getStringExtra("User Name");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
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

        final TextView namecard_name = findViewById(R.id.namecard_name);
        final TextView namecard_phone = findViewById(R.id.namecard_phone);
        final TextView namecard_email = findViewById(R.id.namecard_email);

        db.collection("User").document(currrentUserName).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                String username = (String) document.get("Name");
                                String email = (String) document.get("Email");
                                String phone = (String) document.get("Phone");
                                namecard_name.setText(username);
                                namecard_phone.setText("Email:" + email);
                                namecard_email.setText("Phone: " + phone);
                            }
                        }
                    }
                });
    }
}