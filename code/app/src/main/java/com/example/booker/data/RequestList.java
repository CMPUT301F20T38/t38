package com.example.booker.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.booker.R;
import com.example.booker.activities.MapsActivity;
import com.example.booker.activities.ScanCodeActivity;
import com.google.android.gms.maps.model.LatLng;
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

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

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

    private String borrower;
    private AlertDialog.Builder builder;


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

        TextView request_username = view.findViewById(R.id.request_username);
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

        //user profile dialog
        request_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //find the owner uid, thus use it for mauth.email
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(request.getUser_name())){//owner name match the owner, alert box

                                    String email = document.get("Email").toString();
                                    builder = new AlertDialog.Builder(context);
                                    builder.setMessage("Email: "+email);
                                    //Creating dialog box
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });

        //accept, accept the current user and decline all other users
        request_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //find the path to the correspond Request
                final DocumentReference documentRef = db.collection("User")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("Lend")
                        .document(request.getBook_name());
                DocumentReference owner_path = db.collection("User")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("Lend")
                        .document(request.getBook_name());


                //start the intent to scan isbn
                Intent scanIntent = new Intent(context, ScanCodeActivity.class);

                //put extra the values that may use in scan and map activity
                scanIntent.putExtra("bookName", request.getBook_name());
                scanIntent.putExtra("borrowerName", request.getUser_name());
                scanIntent.putExtra("requests",requests);
                scanIntent.putExtra("event","owner_scan");
                ((Activity) context).startActivityForResult(scanIntent,25);

                /*//the map has already added location
                if(owner_path.collection("location").document("latLon")!=null){
                    //change book status for owner if the return result is valid
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
                }else{
                    //toast didn't accept
                    Toast.makeText(context,"Failed to accept the request",Toast.LENGTH_SHORT);
                }

*/
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
                //show notification
                builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Successfully declined")
                        .setCancelable(false)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((Activity) context).finish();
                            }
                        })  ;
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        return view;
    }
}
