package com.example.booker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booker.R;
import com.example.booker.data.BorrowedBooks;
import com.example.booker.data.BorrowedBooksList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BorrowedBookListActivity extends AppCompatActivity {
    final String TAG = "borrowed book tag";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListView borrowed_list;
    private ArrayList<BorrowedBooks> borrowedBooksList;
    private ArrayAdapter<BorrowedBooks> borrowedBooksAdapter;
    private TextView return_button;
    private Button accept_book, return_book;
    private ImageView borrowed_filter;
    private int position;//filter choice pos: 0-requested, 1-accepted, 2-all
    private TextView borrowed_title, borrowed_author, borrowed_status, borrowed_owner_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrowed_book_list);
        position = 2;

        borrowed_list = findViewById(R.id.borrowed_list);
        return_button = findViewById(R.id.borrowed_return);
        borrowed_filter = findViewById(R.id.borrowed_filter);

        borrowedBooksList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        //Log.e(TAG, mAuth.getCurrentUser().getUid());
        final CollectionReference collectionReference = db.collection("User")
                .document(mAuth.getCurrentUser().getUid()).collection("Borrowed");

        //get data from db and add them to  booklist
        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BorrowedBooks borrowedBooks = document.toObject(BorrowedBooks.class);
                                borrowedBooksList.add(borrowedBooks);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        borrowedBooksAdapter = new BorrowedBooksList(this, borrowedBooksList);
        borrowed_list.setAdapter(borrowedBooksAdapter);

        //real-time change
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                borrowedBooksList.clear();

                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    String src = (String)doc.getData().get("src");
                    String title = (String)doc.getData().get("title");
                    String author = (String)doc.getData().get("author");
                    String owner_name = (String)doc.getData().get("owner");
                    String status = (String)doc.getData().get("status");
                    borrowedBooksList.add(new BorrowedBooks(src, title, author, owner_name, status));
                }
                borrowedBooksAdapter.notifyDataSetChanged();
            }
        });

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        borrowed_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BorrowedBookListActivity.this);
                //string array for dialog choose items
                String[] choiceArray = new String[]{"Requested", "Accepted", "All"};
                //set the builder title
                builder.setTitle("Book Filter");
                builder.setSingleChoiceItems(choiceArray, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        position = i;
                        if(position==0){//show requested
                            borrowedBooksList.clear();
                            collectionReference
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    //only show status==requested
                                                    String status = (String)document.getData().get("status");
                                                    if(status.equals("requested")){
                                                        BorrowedBooks borrowedBooks = document.toObject(BorrowedBooks.class);
                                                        borrowedBooksList.add(borrowedBooks);
                                                    }
                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }else if(position==1){//show accepted
                            borrowedBooksList.clear();
                            collectionReference
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    //only show status==accepted
                                                    String status = (String)document.getData().get("status");
                                                    if(status.equals("accepted")){

                                                        //Log.e("=====================================","position:  "+status);
                                                        BorrowedBooks borrowedBooks = document.toObject(BorrowedBooks.class);
                                                        borrowedBooksList.add(borrowedBooks);
                                                    }
                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }else{//show all
                            borrowedBooksList.clear();
                            collectionReference
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    //show all
                                                        BorrowedBooks borrowedBooks = document.toObject(BorrowedBooks.class);
                                                        borrowedBooksList.add(borrowedBooks);

                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }
                       dialogInterface.dismiss();//return from dialog
                        borrowedBooksAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do nothing
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}