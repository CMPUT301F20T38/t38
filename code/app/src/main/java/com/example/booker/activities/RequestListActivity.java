package com.example.booker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booker.R;
import com.example.booker.data.BorrowedBooks;
import com.example.booker.data.Request;
import com.example.booker.data.RequestList;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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

/**
 * RequestListActivity is the activity for the request in owner lend page
 * It is inside of the lend page, after click on specific book, there's a show requests
 * it shows all request from other users who want to borrow the book
 */
public class RequestListActivity extends AppCompatActivity {
    final String TAG = "owner requests tag";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private ListView request_list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Request> requestList;
    private ArrayAdapter<Request> requestAdapter;
    private String book_name;
    private TextView return_button, request_decline;
    private Button request_accept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lend_requests);

        request_list = findViewById(R.id.requests_list);
        return_button = findViewById(R.id.request_list_return);
//        Button request_accept = (Button)findViewById(R.id.request_accept);
        request_decline = findViewById(R.id.request_decline);

        requestList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        book_name = getIntent().getStringExtra("BookName");
        Log.e("bookname++++++++++++++++++",book_name);

        //change the path of the book(document) here in later coding
        final CollectionReference collectionReference = db.collection("User")
                .document(mAuth.getCurrentUser().getUid()).collection("Lend")
                .document(book_name).collection("Requests");


        //get each request for each book
        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                requestList.add(new Request(document.getId(),book_name));
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
                    requestList.add(new Request(user_name,book_name));
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


        if (isServicesOK()){
            init();
        }




    }
    private void init(){
        Button btnMap = (Button) findViewById(R.id.request_accept);
        request_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestListActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }




    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(RequestListActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(RequestListActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}