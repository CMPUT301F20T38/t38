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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RequestListActivity extends AppCompatActivity {
    final String TAG = "owner requests tag";
    private ListView request_list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Request> requestList;
    private ArrayAdapter<Request> requestAdapter;
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

        //change the path of the book(document) here in later coding
        final CollectionReference collectionReference = db.collection("User")
                .document(mAuth.getCurrentUser().getUid()).collection("Lend")
                .document("aaa").collection("Requests");


        //get each request for each book
        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                requestList.add(new Request(document.getId()));
                                //Log.e(TAG, "added");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        requestAdapter = new RequestList(this, requestList);
        request_list.setAdapter(requestAdapter);

        //real-time change
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                requestList.clear();

                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    String user_name = (String)doc.getId();
                    requestList.add(new Request(user_name));
                }
                requestAdapter.notifyDataSetChanged();
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