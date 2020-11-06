package com.example.booker.ui.borrow;

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

import com.example.booker.R;
import com.example.booker.activities.UserSignUp;
import com.example.booker.data.OwnerListViewAdapter;
import com.example.booker.data.SearchListViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BorrowFragment extends Fragment {

    private Button btnSearch;
    private EditText searchEditText;
    private ListView searchList;
    private SearchListViewAdapter searchAdapter;
    private FirebaseFirestore db;
    private String search_content;
    private String ownerusername;
    private ArrayList<String> userids;
    private List<Map<String, Object>> booklist;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View root = inflater.inflate(R.layout.fragment_borrow, container, false);

        Log.e("Start","start");

        db = FirebaseFirestore.getInstance();

        //get userIDs
        userids = new ArrayList<String>();

        db.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    Log.e("adduser", "add");

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userids.add(document.getId());
                    }
                    //get usernames

                    final Map<String, Object> usermap = new HashMap<String,Object>();

                    for (final String uid : userids) {
                        db.collection("User").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Log.e("userid", uid);
                                String username = task.getResult().getString("Name");
                                Log.e("username", username);
                                usermap.put(uid,username);
                            }
                        });
                        db.collection("User").document(uid).collection("Lend")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Log.e("Initial this user", uid);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String thisauthor = document.getString("author");
                                    String thistitle = document.getString("title");
                                    String thisISBN = document.getString("isbn");
                                    String thisstatus = document.getString("status");

                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("author", document.getString("author"));
                                    map.put("title", document.getString("title"));
                                    map.put("ISBN", document.getString("isbn"));

                                    map.put("owner", usermap.get(uid));
                                    //map.put("owner",ownerusername);
                                    map.put("status", document.getString("status"));

                                    booklist.add(map);
                                }

                                searchAdapter = new SearchListViewAdapter(getContext(), booklist);
                                searchList.setAdapter(searchAdapter);
                            }


                        });
                    }

                    if (userids.size() == 0){Log.e("length","0");}

                    Log.e("adduser", "adddone");

                } else {
                    Log.d("Retrieve Data", "Fail");
                }
            }
        });

        //get usernames

        final List<Map<String, Object>> usernamelist = new ArrayList<Map<String, Object>>();

        for (final String uid : userids) {
            db.collection("User").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String username = task.getResult().getString("Name");
                    Map<String, Object> usermap = new HashMap<String, Object>();
                    usermap.put("Username",username);
                    usermap.put("Userid",uid);
                    usernamelist.add(usermap);
                }
            });
        }

        if (userids.size() == 0){Log.e("length","0");}

        //initial stage

        //get search content

        btnSearch = root.findViewById(R.id.search_button);
        searchList = root.findViewById(R.id.search_book_list);

        searchEditText = (EditText) root.findViewById(R.id.search_content);

        booklist = new ArrayList<Map<String, Object>>();

        //search button

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Map<String, Object>> booklist = new ArrayList<Map<String, Object>>();

                search_content = searchEditText.getText().toString();


                for (String uid : userids) {
                    if (search_content.equals("")){
                        booklist.clear();
                        searchAdapter = new SearchListViewAdapter(getContext(),booklist);
                        searchList.setAdapter(searchAdapter);
                        break;
                    };

                    Log.e("user", uid);
                    Log.e("for", "for start");

                    Log.e("searchcontent", search_content);

                    db.collection("User").document(uid).collection("Lend")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    Log.e("addbook", "add");

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

                                        map.put("owner",document.getString("owner"));
                                        //map.put("owner",ownerusername);
                                        map.put("status",document.getString("status"));




                                        if ((thisauthor.contains(search_content) || thistitle.contains(search_content) || thisISBN.contains(search_content))
                                                && (thisstatus.equals("avaliable")  || thisstatus.equals("requested"))){
                                            booklist.add(map);
                                        }

                                        searchAdapter = new SearchListViewAdapter(getContext(),booklist);
                                        searchList.setAdapter(searchAdapter);

                                    }

                                }
                            });


                }


                searchEditText.setText("");

            }

        });


        return root;
    }

    public void setbook(){
        Log.e("setbook","start");

        for (String uid : userids) {
            Log.e("forstart", "1");
            Log.e("user", uid);


            db.collection("User").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    ownerusername = task.getResult().getString("Name");
                }
            });


            db.collection("User").document(uid).collection("Lend")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.e("addbook", "add");

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Log.e("book", document.getString("title"));

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("author", document.getString("author"));
                        map.put("title", document.getString("title"));
                        map.put("ISBN", document.getString("isbn"));

                        map.put("owner", ownerusername);
                        map.put("status", document.getString("status"));


                        booklist.add(map);

                    }

                    searchAdapter = new SearchListViewAdapter(getContext(), booklist);
                    searchList.setAdapter(searchAdapter);

                    searchEditText.setText("");
                }
            });
        }
    }
    
}
