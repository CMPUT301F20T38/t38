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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
    private OwnerListViewAdapter searchAdapter;
    private FirebaseFirestore db;
    private String search_content;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View root = inflater.inflate(R.layout.fragment_borrow, container, false);

        Log.e("Start","start");

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();


        final ArrayList<String> userids = new ArrayList<String>();

        db.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    Log.e("adduser", "add");

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userids.add(document.getId());
                    }

                    Log.e("adduser", "adddone");

                } else {
                    Log.d("Retrieve Data", "Fail");
                }
            }
        });



        btnSearch = root.findViewById(R.id.search_button);
        searchList = root.findViewById(R.id.search_book_list);

        searchEditText = (EditText) root.findViewById(R.id.search_content);




        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Map<String, Object>> booklist = new ArrayList<Map<String, Object>>();

                search_content = searchEditText.getText().toString();


                for (String uid : userids) {
                    Log.e("user", uid);
                    Log.e("searchcontent", search_content );

                    db.collection("User").document(uid).collection("Lend")
                            .whereEqualTo("title",search_content)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    Log.e("addbook", "add");

                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Log.e("book", document.getString("title"));

                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("author", document.getString("author"));
                                        map.put("title", document.getString("title"));
                                        map.put("ISBN", document.getString("ISBN"));
                                        Log.d("data", document.getString("author"));
                                        booklist.add(map);
                                    }

                                    searchAdapter = new OwnerListViewAdapter(getContext(),booklist);
                                    searchList.setAdapter(searchAdapter);

                                    searchEditText.setText("");
                                }
                            });
                }
            }

        });


        return root;
    }

    
}
