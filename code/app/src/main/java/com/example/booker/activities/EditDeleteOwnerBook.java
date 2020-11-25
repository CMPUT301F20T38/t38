package com.example.booker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booker.R;
import com.example.booker.data.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.utils.L;

import org.w3c.dom.Document;

import java.util.ArrayList;

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
    private Button btnEdit, btnDelete;
    private ImageView btnPhoto, btnReturn, btnLocation, btnRequest;
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
        btnRequest = (ImageView) findViewById(R.id.owner_show_request);
        btnPhoto = (ImageView) findViewById(R.id.owner_add_photo);
        btnLocation = (ImageView) findViewById(R.id.owner_map_change);
        btnReturn = (ImageView) findViewById(R.id.owner_return);

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

                collectionReference.document(prevTitle).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (documentSnapshot.exists()){
                                ArrayList<String> uidList = (ArrayList<String>) documentSnapshot.get("requests");
                                Log.d("Edit Request", "Loaded");

                                if (uidList.size() != 0){
                                    for (int a = 0; a < uidList.size(); a++){
                                        Log.d("Loop", "Begin");
                                        CollectionReference userBorrowed = db
                                                .collection("User")
                                                .document(uidList.get(a))
                                                .collection("Borrowed");
                                        userBorrowed
                                                .document(prevTitle)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                Log.d("Getting Document", "Success");

                                                DocumentSnapshot document = task.getResult();
                                                if (document != null) {
                                                    Log.d("Document", "Is Not Null");
                                                    userBorrowed
                                                            .document(prevTitle)
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d("Borrower Edit", "Successes");
                                                                    userBorrowed
                                                                            .document(book.getTitle())
                                                                            .set(document.getData());

                                                                    userBorrowed
                                                                            .document(editTitle.getText().toString())
                                                                            .update(
                                                                                    "author", editAuthor.getText().toString(),
                                                                                    "ISBN", editISBN.getText().toString(),
                                                                                    "title", editTitle.getText().toString()
                                                                            )
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.d("Prev book", "Deleted");
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.d("Prev book", "Not Deleted");
                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d("Borrower Edit", "Fails");
                                                                }
                                                            });
                                                }
                                            }
                                        });
                                    }
                                    collectionReference
                                            .document(prevTitle)
                                            .delete();

                                    collectionReference
                                            .document(book.getTitle())
                                            .set(book);

                                    finish();
                                }
                            }
                        }
                    }
                });
            }
        });

        // when the delete button is clicked, begin to delete book from firestore
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Owner's delete book", "Is Clicked");
                String preTile = book.getTitle();

                collectionReference.document(preTile).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d("Asap", "Rocky");
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){
                                ArrayList<String> uidList = (ArrayList<String>) documentSnapshot.get("requests");
                                Log.d("Request Users", uidList.toString());

                                if (uidList.size() != 0) {
                                    for (int i = 0; i < uidList.size(); i++) {
                                        CollectionReference userBorrowed = db
                                                .collection("User")
                                                .document(uidList.get(i))
                                                .collection("Borrowed");
                                        userBorrowed
                                                .document(preTile)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("Borrower Book", "Deleted");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("Borrower Book", "Delete Fail");
                                                    }
                                                });
                                    }
                                }

                                collectionReference
                                        .document(preTile)
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
                        }
                    }
                });
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
