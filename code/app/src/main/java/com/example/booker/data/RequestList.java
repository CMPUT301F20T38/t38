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

        TextView request_username = view.findViewById(R.id.request_username);
        TextView request_accept = view.findViewById(R.id.request_accept);
        TextView request_decline = view.findViewById(R.id.request_decline);

        request_username.setText(request.getUser_name());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        //accept, accept the current user and decline all other users
        request_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //find the path to the correspond Request
                final CollectionReference collectionReference = db.collection("User")
                        .document(mAuth.getCurrentUser().getUid()).collection("Lend")
                        .document(request.getBook_name()).collection("Requests");
                //change book status for owner
                db.collection("User").document(mAuth.getCurrentUser().getUid())
                        .collection("Lend").document(request.getBook_name()).update("status","accepted");

/*                //change borrow status for accepted user
                db.collection("User").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //find a matched name, update the status of its borrowed book
                                        if(document.get("Name").equals(requests.get(position).getUser_name())){
                                            //the book name path here will be changed later
                                            //may have some issue in get field here???
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
                //update status for other users
                for(int i=0; i<requests.size(); i++){
                    if(i != position){
                        db.collection("User").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //find a matched name, update the status of its borrowed book
                                                if(document.get("Name").equals(requests.get(i).getUser_name())){
                                                    //the book name path here will be changed later, path will change here ???
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
                    }
                }
*/

                //delete all documents in Request collection
                for(int i=requests.size()-1; i>=0; i--){
                    collectionReference.document(requests.get(i).getUser_name()).delete();
                }

            }
        });
        //decline, decline correspond user
        request_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                //the document path for book will be change later
                final CollectionReference collectionReference = db.collection("User")
                        .document(mAuth.getCurrentUser().getUid()).collection("Lend")
                        .document(request.getBook_name()).collection("Requests");
/*                db.collection("User").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //find a matched name, update the status of its borrowed book
                                        if(document.get("Name").equals(requests.get(position).getUser_name())){
                                            //the book name path here will be changed later, path will change here ???
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
*/
                collectionReference.document(requests.get(position).getUser_name()).delete();
            }
        });


        return view;
    }
}
