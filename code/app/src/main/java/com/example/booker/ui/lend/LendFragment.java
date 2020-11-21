package com.example.booker.ui.lend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.booker.R;
import com.example.booker.activities.AddOwnerBook;
import com.example.booker.activities.EditDeleteOwnerBook;
import com.example.booker.data.Book;
import com.example.booker.data.OwnerListViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Yee's Part
 *  The second fragment of the main activity named Lend
 *  Button btnAdd: clickable Button which jump to AddOwnerBook activity
 *
 *  Add Button onClicklistener: Execute add book activity if there is an user loginning
 *  ListView onItemClickListener: When the item in list view, navigate to the edit and delete book activity
 *
 */


public class LendFragment extends Fragment {

    private Button btnAdd;
    private Spinner filter;
    private ListView ownerList;
    private OwnerListViewAdapter ownerAdapter;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private ArrayList<Book> bookList;
    private String selectBookTitle;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_lend, container, false);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        btnAdd = (Button) root.findViewById(R.id.owner_book_add);
        filter = (Spinner) root.findViewById(R.id.owner_book_filter);
        ownerList = (ListView) root.findViewById(R.id.owner_book_list);

        bookList = new ArrayList<>();
        ownerAdapter = new OwnerListViewAdapter(getContext(), bookList);
        ownerList.setAdapter(ownerAdapter);

        final String[] categories = {"all", "avaliable", "accepted", "requested", "borrowed"};
        filter.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, categories));

        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                if (user != null){
                    final String selectedStatus = filter.getItemAtPosition(i).toString();
                    final String userId = user.getUid();
                    CollectionReference collectionReference = db.collection("User").document(userId).collection("Lend");
                    if (!selectedStatus.equals("all")) {
                        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    bookList.clear();
                                    Log.d("Filter", "Begins to Select");
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                        if (documentSnapshot.get("status").toString().equals(selectedStatus)){
                                            Book book = new Book(documentSnapshot.getString("author"), documentSnapshot.getString("title"), documentSnapshot.getString("isbn"),
                                                    documentSnapshot.getString("status"), userId, documentSnapshot.getString("borrower"), new ArrayList<>());
                                            bookList.add(book);

                                            Log.d(documentSnapshot.get("title").toString(), "added");
                                        }
                                    }
                                    ownerAdapter.notifyDataSetChanged();
                                    Log.d("Filter", "Selection Finished");
                                }
                            }
                        });
                    }
                    else {
                        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    bookList.clear();
                                    Log.d("Filter", "Begin Fetching All");
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        Book book = new Book(documentSnapshot.getString("author"), documentSnapshot.getString("title"), documentSnapshot.getString("isbn"),
                                                documentSnapshot.getString("status"), userId, documentSnapshot.getString("borrower"), new ArrayList<>());
                                        bookList.add(book);
                                        Log.d(documentSnapshot.get("title").toString(), "added");
                                    }
                                    ownerAdapter.notifyDataSetChanged();
                                    Log.d("Filter", "Fetching Finishded");
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // When the item in list view is trigger, navigate to the edit or delete book activity
        ownerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book book = (Book) ownerList.getItemAtPosition(i);
                selectBookTitle = book.getTitle();
                Intent intent = new Intent(view.getContext(), EditDeleteOwnerBook.class);
                intent.putExtra("YeeSkywalker", book);
                startActivity(intent);
            }
        });

        // When the button is trigger, navigate to the new book activity
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null){
                    Toast.makeText(getContext(), "Please log in first!", Toast.LENGTH_LONG).show();
                }

                else{
                    Intent intent = new Intent(view.getContext(), AddOwnerBook.class);
                    startActivity(intent);
                }
            }
        });


        if (user != null) {
            final String userId = user.getUid();
            CollectionReference collectionReference = db.collection("User").document(userId).collection("Lend");
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    bookList.clear();
                    Log.d("AddSnapshotListener", "Notify Data changed");
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value) {
                            Book book = new Book(documentSnapshot.getString("author"), documentSnapshot.getString("title"), documentSnapshot.getString("isbn"),
                                    documentSnapshot.getString("status"), userId, documentSnapshot.getString("borrower"), new ArrayList<>());
                            bookList.add(book);
                            Log.d(documentSnapshot.get("title").toString(), "added");
                        }
                    }
                    filter.setSelection(0);
                    ownerAdapter.notifyDataSetChanged();
                    Log.d("Owner Adapter", "Loaded");
                }
            });

        }

        return root;
    }
}
