package com.example.booker.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booker.R;
import com.example.booker.data.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Yee's Part
 * The activity allow user to add new book as owner
 * EditText author: Enable user to input author name when attend to add a book
 * EditText title: Enable user to input title name when attend to add a boook
 * EditText ISBN: Enable user to input ISBN when attend to add a boook
 * Button btnComfirm: sumbit the form
 * Action bar deketed
 *
 * FirebaseAuth mAuth: the token of firebasemAuth reference
 * FirebaseFirestore db: the token of firebasefirestore reference
 */

public class AddOwnerBook extends AppCompatActivity {
    private EditText author;
    private EditText title;
    private EditText ISBN;
    private Button btnComfirm;

    private ImageView photo;
    private ImageView addISBN;
    private Uri filePath;

    final static String TAG ="image";
    private final int PICK_IMAGE_REQUEST = 22;
    private final int GET_ISBN = 33;//request code for isbn

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_add_book);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        Intent intent = getIntent();

        author = (EditText) findViewById(R.id.owner_add_author);
        title = (EditText) findViewById(R.id.owner_add_title);
        ISBN = (EditText) findViewById(R.id.owner_add_ISBN);
        btnComfirm = (Button) findViewById(R.id.owner_add_confirm);
        photo = findViewById(R.id.photoView);
        addISBN = findViewById(R.id.add_isbn_button);



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
                CollectionReference collectionReference = db.collection("User")
                        .document(userId).collection("Lend");
                Book book = new Book(addAuthor, addTitle, addISBN, "available",
                        userId, "", new ArrayList<>());


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



        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"  pick the pic");
                SelectImage();
                Log.d(TAG,"  finshed the picking");
            }
        });

        addISBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to scan isbn activity
                Intent intent = new Intent(AddOwnerBook.this, ScanCodeActivity.class);
                //notify the event is add isbn
                intent.putExtra("event", "owner_add_isbn");
                startActivityForResult(intent, GET_ISBN);//Activity is started with requestCode 33
            }
        });

    }

    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                photo.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }else if(requestCode == GET_ISBN  && data != null ){
            ISBN.setText(data.getStringExtra("ISBN"));
        }
    }

}
