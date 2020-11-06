package com.example.booker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.booker.R;
import com.example.booker.data.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

/**
 * Yee's Part
 * The activity allow user to add new book as owner
 * EditText author: Enable user to input author name when attend to add a book
 * EditText title: Enable user to input title name when attend to add a boook
 * EditText ISBN: Enable user to input ISBN when attend to add a boook
 * Button btnComfirm: sumbit the form
 *
 * FirebaseAuth mAuth: the token of firebasemAuth reference
 * FirebaseFirestore db: the token of firebasefirestore reference
 */

public class AddOwnerBook extends AppCompatActivity {
    private EditText author;
    private EditText title;
    private EditText ISBN;
    private Button btnComfirm;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_add_book);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        Intent intent = getIntent();

        author = (EditText) findViewById(R.id.owner_add_author);
        title = (EditText) findViewById(R.id.owner_add_title);
        ISBN = (EditText) findViewById(R.id.owner_add_ISBN);
        btnComfirm = (Button) findViewById(R.id.owner_add_confirm);

        if (author.toString().isEmpty()){
            author.requestFocus();
        }

        if (title.toString().isEmpty()){
            title.requestFocus();
        }

        if (ISBN.toString().isEmpty()){
            ISBN.requestFocus();
        }

        // when button is trigger, begin to interacet with firestore
        btnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String addAuthor = author.getText().toString();
                String addTitle = title.getText().toString();
                String addISBN = ISBN.getText().toString();
                CollectionReference collectionReference = db.collection("User").document(userId).collection("Lend");
                Book book = new Book(addAuthor, addTitle, addISBN, "avaliable", userId, "");


                collectionReference
                        .document(addTitle)
                        .set(book)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Add Data Firestore", "Failed");
                            }
                        });

            }
        });
    }

}
