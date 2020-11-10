package com.example.booker.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.booker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * RequestList class is the adapter for request list, the function of it
 * is to customize the request list view and add click event on the
 * request buttons
 */
public class RequestList extends ArrayAdapter<Request> {
    final String TAG = "request update tag";
    private ArrayList<Request> requests;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public RequestList(@NonNull Context context, @NonNull ArrayList<Request> requests) {
        super(context, 0, requests);
        this.requests = requests;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;

        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.lend_requests_content, parent,false);
        }

        final Request request = requests.get(position);

        final TextView request_username = view.findViewById(R.id.request_username);
        TextView request_accept = view.findViewById(R.id.request_accept);
        TextView request_decline = view.findViewById(R.id.request_decline);



        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db.collection("User").document(request.getUser_name()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String username = task.getResult().get("Name").toString();
                    request_username.setText(username);
                }else{
                    Log.d(TAG, "Fail to find user col/doc!");
                }


            }
        });

        //accept, accept the current user and decline all other users
        request_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //find the path to the correspond Request
                final DocumentReference documentRef = db.collection("User")
                        .document(mAuth.getCurrentUser().getUid()).collection("Lend")
                        .document(request.getBook_name());
                DocumentReference owner_path = db.collection("User").document(mAuth.getCurrentUser().getUid())
                        .collection("Lend").document(request.getBook_name());
                //change book status for owner
                owner_path.update("status","accepted");
                owner_path.update("requests", FieldValue.arrayRemove());

                //change borrow status for accepted user
                db.collection("User").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //find a matched name, update the status of its borrowed book
                                        if(document.getId().equals(request.getUser_name())){
                                            //change status to accepted
                                            db.collection("User").document(document.getId()).collection("Borrowed")
                                                    .document(request.getBook_name()).update("status","accepted")
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "Correspond user accept status successfully updated!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "Correspond user accept status failed to updated!");
                                                        }
                                                    });
                                        }
                                    }
                                }else{
                                    Log.d(TAG, "Fail to find user col/doc!");
                                }
                            }
                        });
                //update status for other users, loop all users
                for(final Request user_request : requests){
                    if(!user_request.getUser_name().equals(request.getUser_name())) {
                        db.collection("User").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //find a matched name, update the status of its borrowed book
                                                if (document.getId().equals(user_request.getUser_name())) {
                                                    //if decline a request, then for the user trying to borrow the book, it will disappear and send notification
                                                    //delete the correspond book in borrower's borrowed list
                                                    db.collection("User").document(document.getId()).collection("Borrowed")
                                                            .document(user_request.getBook_name()).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    //notification code write here
                                                                    Log.d(TAG, "Correspond user accept status successfully updated!");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d(TAG, "Correspond user accept status failed to updated!");
                                                                }
                                                            });
                                                }
                                            }
                                        } else {
                                            Log.d(TAG, "Fail to find user col/doc!");
                                        }
                                    }
                                });
                    }else{//change the borrower field in owner's book
                        db.collection("User").document(mAuth.getCurrentUser().getUid()).collection("Lend")
                                .document(request.getBook_name()).update("borrower",user_request.getUser_name())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Correspond user accept status successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Correspond user accept status failed to updated!");
                                    }
                                });
                    }
                }
/*                for(int i=0; i<requests.size(); i++){
                    //when the user is not the accepted one
                    if(i != position){
                        final int where = i;
                        db.collection("User").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //find a matched name, update the status of its borrowed book
                                                if(document.getId().equals(requests.get(where).getUser_name())){
                                                    //if decline a request, then for the user trying to borrow the book, it will disappear and send notification
                                                    //delete the correspond book in borrower's borrowed list
                                                    db.collection("User").document(document.getId()).collection("Borrowed")
                                                            .document(request.getBook_name()).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    //notification code write here
                                                                    Log.d(TAG, "Correspond user accept status successfully updated!");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d(TAG, "Correspond user accept status failed to updated!");
                                                                }
                                                            });
                                                }
                                            }
                                        }else{
                                            Log.d(TAG, "Fail to find user col/doc!");
                                        }
                                    }
                                });
                    }*/


                //delete all elements in Request array(update request list array) ???
                documentRef.update("requests",null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Remove all users successfully! ");
                        }else{
                            Log.d(TAG, "Fail to remove all users!");
                        }
                    }
                });


            }
        });
        // decline correspond single user
        request_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                //the document path for owner's book
                final DocumentReference documentRef = db.collection("User")
                        .document(mAuth.getCurrentUser().getUid()).collection("Lend")
                        .document(request.getBook_name());
                //get correspond user name
                db.collection("User").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //find a matched name, update the status of its borrowed book
                                        if(document.getId().equals(request.getUser_name())){
                                            //if decline a request, than for the user trying to borrow the book, it will disappear and send notification
                                            db.collection("User").document(document.getId()).collection("Borrowed")
                                                    .document(request.getBook_name()).delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "Correspond user accept status successfully updated!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "Correspond user accept status failed to updated!");
                                                        }
                                                    });
                                        }
                                    }
                                }else{
                                    Log.d(TAG, "Fail to find user col/doc!");
                                }
                            }
                        });
                //delete the correspond item in array
                documentRef.update("requests",FieldValue.arrayRemove(request.getUser_name())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Remove single user successfully! ");
                        }else{
                            Log.d(TAG, "Fail to remove single user!");
                        }
                    }
                });
            }
        });


        return view;
    }
}
