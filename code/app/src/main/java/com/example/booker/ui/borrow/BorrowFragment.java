package com.example.booker.ui.borrow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.booker.R;
import com.example.booker.activities.UserSignUp;
import com.example.booker.data.OwnerListViewAdapter;
import com.example.booker.data.SearchListViewAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a page for user to search book and request to borrow book
 * Button btnSearch: clickable Button which will give a list of book search by keywords
 *
 * Search Button onClickListener: Refresh the BookList according to the search content
 * and clear the search content
 *
 */

public class BorrowFragment extends Fragment {

    private Button btnSearch;
    private EditText searchEditText;
    private ListView searchList;
    private SearchListViewAdapter searchAdapter;
    private FirebaseFirestore db;
    private String search_content;
    private String ownerusername;
    private ArrayList<String> userids;
    private ArrayList<Map<String, Object>> booklist;
    private LatLng locaiton = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View root = inflater.inflate(R.layout.fragment_borrow, container, false);



        db = FirebaseFirestore.getInstance();



        /*
        db.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    //add all userids to userids list

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userids.add(document.getId());
                    }


                    for (final String uid : userids) {

                        //get usernames

                        final Map<String, Object> usermap = new HashMap<String,Object>();

                        db.collection("User").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.e("userid", uid);
                                    String username = task.getResult().getString("Name");
                                    Log.e("username", username);
                                    usermap.put(uid, username);
                                }
                            }
                        });


                        //initial page booklist add

                        db.collection("User").document(uid).collection("Lend")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                //if (task.isSuccessful()) {
                                    Log.e("Initial this user", uid);
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("author", document.getString("author"));
                                        map.put("title", document.getString("title"));
                                        map.put("ISBN", document.getString("isbn"));

                                        //get id for now, should be fix to username later
                                        //map.put("owner", document.getString("owner"));
                                        map.put("owner", usermap.get(uid));
                                        map.put("status", document.getString("status"));

                                        booklist.add(map);
                                    }

                                    searchAdapter = new SearchListViewAdapter(getContext(), booklist);
                                    searchList.setAdapter(searchAdapter);
                                //}
                            }

                        });
                    }

                } else {
                    Log.d("Retrieve Data", "Fail");
                }
            }
        });
        */


        //get ID and Usernames Map and initial page booklist add

        Map<String, Object> usermap = new HashMap<String,Object>();

        usermap = getIDAndName();


        //get search content

        btnSearch = root.findViewById(R.id.search_button);
        searchList = root.findViewById(R.id.search_book_list);

        searchEditText = (EditText) root.findViewById(R.id.search_content);

        booklist = new ArrayList<Map<String, Object>>();


        //search button

        Map<String, Object> finalUsermap1 = usermap;
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Map<String, Object>> booklist = new ArrayList<Map<String, Object>>();

                search_content = searchEditText.getText().toString();


                for (String uid : finalUsermap1.keySet()) {
                    //if content is empty show nothing

                    if (search_content.equals("")){
                        booklist.clear();
                        searchAdapter = new SearchListViewAdapter(getContext(),booklist);
                        searchList.setAdapter(searchAdapter);
                        break;
                    };

                    //search


                    db.collection("User").document(uid).collection("Lend")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    Log.e("addbook uid", uid);
                                    Log.e("addbook name", finalUsermap1.get(uid).toString());


                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Log.e("book", document.getString("title"));

                                        String thisauthor =  document.getString("author");
                                        String thistitle = document.getString("title");
                                        String thisISBN = document.getString("isbn");
                                        String thisstatus = document.getString("status");

                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("author", document.getString("author"));
                                        map.put("title", document.getString("title"));
                                        map.put("ISBN", document.getString("isbn"));
                                        map.put("location",document.getString("status") );

                                        map.put("owner",document.getString("owner"));
                                        map.put("owner_name", finalUsermap1.get(uid).toString());
                                        map.put("status",document.getString("status"));





                                        if ((thisauthor.contains(search_content) || thistitle.contains(search_content) || thisISBN.contains(search_content))
                                                && (thisstatus.equals("available")  || thisstatus.equals("requested"))){
                                            booklist.add(map);
                                        }

                                        //upadate booklist



                                        searchAdapter = new SearchListViewAdapter(getContext(),booklist);
                                        searchList.setAdapter(searchAdapter);

                                    }

                                }
                            });


                }

                //clear the search text after the search

                searchEditText.setText("");

            }

        });

        /*
            Request Approve Notification
            By Yee
         */


        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference userNotification =  db.collection("User")
                    .document(userID)
                    .collection("Notification");

            userNotification.whereEqualTo("type", "request")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            Log.d("Book notify Listener", "Accepted");
                            if (error != null){
                                Log.d("Accept Notify", "Failed", error);
                            }

                            for (DocumentChange dc : value.getDocumentChanges()){
                                switch (dc.getType()) {
                                    case ADDED:
                                        Log.d("Book notify", "Modified");
                                        if (getActivity() != null) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setMessage("Your book request has benn accepted")
                                                    .setPositiveButton("GotCha",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                                }
                                                            });
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                        userNotification.document(dc.getDocument().getId())
                                                .delete();
                                        break;
                                }
                            }
                        }
                    });
        }

        return root;
    }

    public Map<String, Object> getIDAndName(){
        Log.e("getID", "start");
        ArrayList<String> userIDs = new ArrayList<String>();
        final Map<String, Object> userMAP = new HashMap<String, Object>();
        db.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e("getID", "success");

                    //add all userids to userids list

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.e("addID", document.getId());
                        Log.e("addIDName",document.get("Name").toString());
                        userMAP.put(document.getId(),document.get("Name").toString());
                    }

                    for (String uid : userMAP.keySet()) {
                        db.collection("User").document(uid).collection("Lend")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Log.e("Initial uid", uid);
                                Log.e("Initial name", userMAP.get(uid).toString());

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.e("Initial book", document.getString("title"));

                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("author", document.getString("author"));
                                    map.put("title", document.getString("title"));
                                    map.put("ISBN", document.getString("isbn"));

                                    map.put("owner",document.getString("owner"));
                                    map.put("owner_name", userMAP.get(uid).toString());
                                    map.put("status", document.getString("status"));

                                    booklist.add(map);

                                    //upadate booklist

                                    searchAdapter = new SearchListViewAdapter(getContext(), booklist);
                                    searchList.setAdapter(searchAdapter);

                                }

                            }
                        });
                    }
                } else {
                    Log.d("Retrieve Data", "Fail");
                }
            }
        });
        Log.e("getID", "end");
        return userMAP;
    }

/*
    public Map<String, Object> getIDAndName(ArrayList<String> userIDs){
        Log.e("getIDAndName", "start");
        final Map<String, Object> userMAP = new HashMap<String, Object>();

        for (String uid : userIDs) {
            db.collection("User").document(uid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String username =  documentSnapshot.get("Name").toString();
                            Log.e("getUsername", username);
                            userMAP.put(uid, username);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Usernamefail", uid);
                        }
                    });
        }

            Log.e("getIDandName", "end");
        return userMAP;
    }

 */
}
