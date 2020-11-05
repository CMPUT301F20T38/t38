package com.example.booker.ui.lend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.booker.R;
import com.example.booker.activities.AddOwnerBook;
import com.example.booker.data.OwnerListViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class LendFragment extends Fragment {

    private Button btnAdd;
    private ListView ownerList;
    private OwnerListViewAdapter ownerAdapter;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_lend, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        btnAdd = (Button) root.findViewById(R.id.owner_book_add);
        ownerList = (ListView) root.findViewById(R.id.owner_book_list);


        final List<Map<String, Object>> bookList = new ArrayList<Map<String, Object>>();

        if (userId != null) {
            CollectionReference collectionReference = db.collection("User").document(userId).collection("Lend");

            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("author", document.getString("author"));
                            map.put("title", document.getString("title"));
                            map.put("ISBN", document.getString("ISBN"));
                            Log.d("data", document.getString("author"));
                            bookList.add(map);
                        }

                    }
                    else {
                        Log.d("Hello", "Fail");
                    }

                }
            });
        }
        else {
        }

        Log.d("Hel", "HJasdfasdf");

        ownerAdapter = new OwnerListViewAdapter(getContext(), bookList);
        ownerList.setAdapter(ownerAdapter);
        Log.d("Adaper", "Miracle");
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

        return root;

    }

}
