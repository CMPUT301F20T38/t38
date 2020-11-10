package com.example.booker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.booker.R;
import com.example.booker.data.BorrowedBooks;
import com.example.booker.data.Request;
import com.example.booker.data.RequestList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RequestListActivity is the activity for the request in owner lend page
 * It is inside of the lend page, after click on specific book, there's a show requests
 * it shows all request from other users who want to borrow the book
 */
public class RequestListActivity extends AppCompatActivity {
    final String TAG = "owner requests tag";
    private ListView request_list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Request> requestList;
    private ArrayList<String> request_users;
    private ArrayAdapter<Request> requestAdapter;
    private String book_name;
    private TextView return_button, request_accept, request_decline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lend_requests);

        request_list = findViewById(R.id.requests_list);
        return_button = findViewById(R.id.request_list_return);
        request_accept = findViewById(R.id.request_accept);
        request_decline = findViewById(R.id.request_decline);

        requestList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        book_name = getIntent().getStringExtra("BookName");

        //find the path to the owner of the book, and go into lend branch
        final DocumentReference documentReference = db.collection("User")
                .document(mAuth.getCurrentUser().getUid()).collection("Lend").document(book_name);


        //get requests array for each book
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //get each element in requests field and add them to list
                request_users = (ArrayList<String>) task.getResult().get("requests");
                if(request_users!=null){
                    for(int m=0; m<request_users.size(); m++) {
                        requestList.add(new Request(request_users.get(m), book_name));
                    }
                }

            }
        });
        requestAdapter = new RequestList(this, requestList);
        request_list.setAdapter(requestAdapter);

        //real-time change
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                requestList.clear();
                request_users = (ArrayList<String>) snapshot.get("requests");
                if(request_users!=null){
                    for(int m=0; m<request_users.size(); m++) {
                        requestList.add(new Request(request_users.get(m), book_name));
                    }
                }

            }


        });


                return_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
    }
}