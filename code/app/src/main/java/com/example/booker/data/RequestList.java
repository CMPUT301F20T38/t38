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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RequestList extends ArrayAdapter<Request> {
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

        //Log.e("+++++++++++++++++", request .getUser_name()+"--");
        TextView request_username = view.findViewById(R.id.request_username);
        TextView request_accept = view.findViewById(R.id.request_accept);
        TextView request_decline = view.findViewById(R.id.request_decline);

        request_username.setText(request.getUser_name());
        //accept, accept the current user and decline all other users
        request_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                //the document path for book will be change later
                final CollectionReference collectionReference = db.collection("User")
                        .document(mAuth.getCurrentUser().getUid()).collection("Lend")
                        .document("aaa").collection("Requests");
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
                        .document("aaa").collection("Requests");

                collectionReference.document(requests.get(position).getUser_name()).delete();
            }
        });


        return view;
    }
}
