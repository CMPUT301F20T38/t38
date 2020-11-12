package com.example.booker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booker.R;
import com.example.booker.data.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Yee's Part
 * The activity allow user to edit or delete book
 * EditText author: Enable user to input author name when attend to edit a book
 * EditText title: Enable user to input title name when attend to edit a boook
 * EditText ISBN: Enable user to input ISBN when attend to edit a boook
 * Button btnEdit: sumbit the form
 * Button btnDelete: delete the book from intent extract
 * Button btnRequest: jump to request activities
 * FirebaseAuth mAuth: the token of firebasemAuth reference
 * FirebaseFirestore db: the token of firebasefirestore reference
 */

public class EditDeleteOwnerBook extends AppCompatActivity {

    private EditText editTitle, editAuthor, editISBN;
    private Button btnPhoto, btnEdit, btnDelete, btnRequest;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_edit_delete_book);

        final Intent intent = getIntent();
        final Book book = (Book) intent.getSerializableExtra("YeeSkywalker");

        String author = book.getAuthor();
        String title = book.getTitle();
        String ISBN = book.getISBN();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        editTitle = (EditText) findViewById(R.id.owner_ed_title);
        editAuthor = (EditText) findViewById(R.id.owner_ed_author);
        editISBN = (EditText) findViewById(R.id.owner_ed_ISBN);

        btnEdit = (Button) findViewById(R.id.owner_edit_btn);
        btnDelete = (Button) findViewById(R.id.owner_delete_btn);
        btnRequest = (Button) findViewById(R.id.owner_show_request);
        btnPhoto = (Button) findViewById(R.id.owner_add_photo);

        editTitle.setText(title);
        editAuthor.setText(author);
        editISBN.setText(ISBN);

        editTitle.addTextChangedListener(textWatcher);
        editAuthor.addTextChangedListener(textWatcher);
        editISBN.addTextChangedListener(textWatcher);

        final String userId = user.getUid();
        final CollectionReference collectionReference = db.collection("User").document(userId).collection("Lend");

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToPhoto = new Intent (EditDeleteOwnerBook.this, Photograph.class);
                goToPhoto.putExtra("ISBN",ISBN );
//                goToPhoto.putExtra("BookName",title);
                Log.d("photo: BOOK ISBN", ISBN );

                startActivity(goToPhoto);



            }
        });

        // when the edit button is clicked, begin to submit book information to firestore
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String prevTitle = book.getTitle();
                book.setAuthor(editAuthor.getText().toString());
                book.setTitle(editTitle.getText().toString());
                book.setISBN(editISBN.getText().toString());

                collectionReference
                        .document(prevTitle)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(prevTitle, "Edited");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(prevTitle, "Failed Edit");
                            }
                        });

                collectionReference
                        .document(book.getTitle())
                        .set(book)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(book.getTitle(), "Updated");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(book.getTitle(), "Failed Updated");
                            }
                        });
                finish();
            }
        });

        // when the delete button is clicked, begin to delete book from firestore
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Owner's delete book", "Is Clicked");
                collectionReference
                        .document(book.getTitle())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(book.getTitle(), "Delete");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(book.getTitle(), "Delete Fail");
                            }
                        });
                finish();
            }
        });

        // when the user request to see their book request of the current book
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Owner's check request", "Under Construction");
                Intent intent_request = new Intent(view.getContext(),RequestListActivity.class);
                intent_request.putExtra("BookName",book.getTitle());
                startActivity(intent_request);
            }
        });

    }

    // TextWatcher that ensures user have input all needed information brefore submit
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (editTitle.getText().toString().trim().isEmpty() ||
                    editAuthor.getText().toString().trim().isEmpty() ||
                    editISBN.getText().toString().isEmpty()) {
                Log.d("TextWatcher", "Null detected");
                btnEdit.setEnabled(false);
            }
            else {
                btnEdit.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
