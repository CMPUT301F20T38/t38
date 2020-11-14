package com.example.booker.activities;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class ImagesActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
        private RecyclerView mRecyclerView;
        private ImageAdapter mAdapter;
        private ProgressBar mProgressCircle;
        private FirebaseStorage mStorage;
        private DatabaseReference mDatabaseRef;
        private ValueEventListener mDBListener;
        private List<UploadImage> mUploads;
        private String bookISBN;
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
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploadImage").child(bookISBN);
            mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUploads.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UploadImage upload = postSnapshot.getValue(UploadImage.class);
                        upload.setmKey(postSnapshot.getKey());
                        mUploads.add(upload);
                    }
                    mAdapter.notifyDataSetChanged();
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressCircle.setVisibility(View.INVISIBLE);
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
                    mDatabaseRef.child(selectedKey).removeValue();
                    Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        protected void onDestroy() {
            super.onDestroy();
            mDatabaseRef.removeEventListener(mDBListener);
        }
    }
//    private RecyclerView mRecyclerView;
//    private ImageAdapter mAdapter;
//    private ProgressBar mProgressCircle;
//    private FirebaseStorage mStorage;
//    private DatabaseReference mDatabaseRef;
//    private ValueEventListener mDBListener;
//    private List<UploadImage> mUploads;
//    private String bookISBN;
//    final String TAG="image Activity";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_images);
//        Intent get =getIntent();
//        bookISBN =get.getStringExtra("ISBN");
//        mRecyclerView = findViewById(R.id.recycler_view);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mProgressCircle = findViewById(R.id.progress_circle);
//        mUploads = new ArrayList<>();
//        mAdapter = new ImageAdapter(ImagesActivity.this, mUploads);
//        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(ImagesActivity.this);
//        mStorage = FirebaseStorage.getInstance();
//        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploadImage");
//        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                mUploads.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    UploadImage upload = postSnapshot.getValue(UploadImage.class);
//                    upload.setmKey(postSnapshot.getKey());
//                    mUploads.add(upload);
//                }
//                mAdapter.notifyDataSetChanged();
//                mProgressCircle.setVisibility(View.INVISIBLE);
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                mProgressCircle.setVisibility(View.INVISIBLE);
//            }
//        });
//    }
//    @Override
//    public void onItemClick(int position) {
//        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
//    }
//    @Override
//    public void onWhatEverClick(int position) {
//        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
//    }
//    @Override
//    public void onDeleteClick(int position) {
//        UploadImage selectedItem = mUploads.get(position);
//        final String selectedKey = selectedItem.getmKey();
//        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
//        Log.d(TAG,"delete Ref is "+imageRef );
//        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                mDatabaseRef.child(selectedKey).removeValue();
//                Log.d(TAG,"selectedkey  is "+selectedKey );
//
//                Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mDatabaseRef.removeEventListener(mDBListener);
//    }
//}
