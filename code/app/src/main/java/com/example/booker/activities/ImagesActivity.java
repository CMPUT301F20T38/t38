package com.example.booker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.booker.R;
import com.example.booker.data.Book;
import com.example.booker.data.ImageAdapter;
import com.example.booker.data.UploadImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Jason'part
 * In this activity, it handles the part of showing the uploaded images, and the deletion
 *
 * The image is loaded from the Urls stored on firestore and the actual image is store in firestorage
 *
 * Modified and referenced to
 * https://codinginflow.com/tutorials/android/firebase-storage-upload-and-retrieve-images/part-8-delete-uploads
 *
 */

public class ImagesActivity extends AppCompatActivity  implements ImageAdapter.OnItemClickListener{ //
        private RecyclerView mRecyclerView;
        private ImageAdapter mAdapter;
        private ProgressBar mProgressCircle;
        private FirebaseStorage mStorage;
//        private DatabaseReference mDatabaseRef;
        private FirebaseFirestore db;

        private ValueEventListener mDBListener;
        private List<UploadImage> mUploads;
        private String bookISBN;
        private final String TAG="Displaying uploaded Image: ";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_images);

            Intent get =getIntent();
            bookISBN =get.getStringExtra("ISBN");

            mRecyclerView = findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mProgressCircle = findViewById(R.id.progress_circle);
            mUploads = new ArrayList<>();
            mAdapter = new ImageAdapter(ImagesActivity.this, mUploads);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(ImagesActivity.this);
            mStorage = FirebaseStorage.getInstance();
            db = FirebaseFirestore.getInstance();



//            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploadImage").child(bookISBN);
//            mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            final DocumentReference docRef=db.collection("UploadImages").document(bookISBN);

           docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            mUploads.clear();
                            Map<String, Object> map = document.getData();
                            Log.d(TAG, "DocumentSnapshot data: " + map);


                            if (map != null) {
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    String k = entry.getKey();
                                    Map<String, Object> myMap = (Map<String, Object>) entry.getValue();
                                    String name = (String) myMap.get("Name");
                                    String myUrll = (String) myMap.get("Url");

                                    UploadImage upload = new UploadImage(name,myUrll);
                                    upload.setmKey(k);



                                    mUploads.add(upload);
                                    Log.d(TAG, "DocumentSnapshot data: " + upload);
                                }


                                mAdapter.notifyDataSetChanged();
                                mProgressCircle.setVisibility(View.INVISIBLE);


                            }

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }




        @Override
        public void onItemClick(int position) {
            Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onWhatEverClick(int position) {
            Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onDeleteClick(int position) {
            UploadImage selectedItem = mUploads.get(position);
            final String selectedKey = selectedItem.getmKey();
            StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                    mDatabaseRef.child(selectedKey).removeValue();
                    DocumentReference docRef=db.collection("UploadImages").document(bookISBN);
//                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                DocumentSnapshot document = task.getResult();
//                                if (document.exists()) {
//                                    Map<String, Object> map = document.getData();
//                                    Log.d(TAG, "delete Image " + map);
//
//
//                                    if (map != null) {
//                                        for (Map.Entry<String, Object> entry : map.entrySet()) {
//                                            String k = entry.getKey();
//
//
//                                            if (k.equals(selectedKey) ){
//                                                Log.d(TAG, "delete Image key is " + k);
//
//                                                map.remove(selectedKey);
//                                                Log.d(TAG, "after removing" + map);

                                                Map<String,Object> updates = new HashMap<>();
                                                updates.put(selectedKey, FieldValue.delete());
                                                docRef.update(updates)

                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "delete  successfully !");
                                                                finish();

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "delete is not succesfully", e);
                                                            }
                                                        });

//                                            };
//
//                                        }
//
//
//                                    }
//
//                                    Log.d(TAG, "line 237: " + document.getData());
//                                } else {
//                                    Log.d(TAG, "No such document");
//                                }
//                            } else {
//                                Log.d(TAG, "get failed with ", task.getException());
//                            }
//                        }
//                    });
                    Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        protected void onDestroy() {
            super.onDestroy();
//            mDatabaseRef.removeEventListener(mDBListener);
        }
    }
