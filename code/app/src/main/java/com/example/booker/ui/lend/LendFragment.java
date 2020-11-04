package com.example.booker.ui.lend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booker.R;
import com.example.booker.activities.AddOwnerBook;
import com.example.booker.data.OwnerListViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LendFragment extends Fragment {

    private Button btnAdd;
    private ListView ownerList;
    private OwnerListViewAdapter ownerAdapter;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lend, container, false);

        btnAdd = (Button) root.findViewById(R.id.owner_book_add);
        ownerList = (ListView) root.findViewById(R.id.owner_book_list);
        List<Map<String, Object>> bookList = dataMapping();
        ownerAdapter = new OwnerListViewAdapter(bookList, getContext());
        ownerList.setAdapter(ownerAdapter);

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

    private List<Map<String, Object>> dataMapping() {
        List<Map<String, Object>> currentList = new ArrayList<Map<String, Object>>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return currentList;
        }

        else {
            return currentList;
        }

    }
}
