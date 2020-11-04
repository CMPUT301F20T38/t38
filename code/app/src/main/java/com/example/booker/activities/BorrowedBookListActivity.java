package com.example.booker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class BorrowedBookListActivity extends AppCompatActivity {
    final String TAG = "borrowed book tag";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListView borrowed_list;
    private ArrayList<BorrowedBooks> borrowedBooksList;
    private ArrayAdapter<BorrowedBooks> borrowedBooksAdapter;
    private Button accept_book, return_book;
    private ImageView borrowed_img;
    private TextView borrowed_title, borrowed_author, borrowed_status, borrowed_owner_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrowed_book_list);

        borrowed_list = findViewById(R.id.borrowed_list);

        borrowedBooksList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Log.e(TAG, mAuth.getCurrentUser().getUid());
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

                                /*String borrowed_img = borrowedBooks.getSrc();
                                String borrowed_title = borrowedBooks.getTitle();
                                String borrowed_author = borrowedBooks.getAuthor();
                                String borrowed_status = borrowedBooks.getStatus();
                                String borrowed_owner_name = borrowedBooks.getOwner_name();*/
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
    }
}