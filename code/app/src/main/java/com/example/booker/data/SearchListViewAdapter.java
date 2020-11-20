package com.example.booker.data;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.booker.R;
import com.example.booker.ui.borrow.BorrowFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchListViewAdapter extends BaseAdapter {
    final String TAG = "borrow/search list tag";
    private List<Map<String, Object>> bookList;
    private LayoutInflater layoutInflater;
    private Button request_button;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Map<String, Object> null_field;//in order to create a document with null field
    private String owner;

    private AlertDialog.Builder builder;

    public SearchListViewAdapter(Context context, List<Map<String, Object>> bookList) {
        this.bookList = bookList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);


    }

    public class Component{
        public TextView title;
        public TextView author;
        public TextView ISBN;
        public TextView ownerTag;
        public TextView ownerName;
        public TextView status;
        public LatLng location;
    }

    @Override
    public int getCount() {
        Log.d("BOOK SIZE", bookList.toString());
        return bookList.size();

    }

    @Override
    public Object getItem(int i) {
        return bookList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        //set the request button reaction
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Component component = null;
        if (view == null){

            component = new Component();
            view = layoutInflater.inflate(R.layout.search_listview, null);
            component.author = (TextView) view.findViewById(R.id.book_author);
            component.title = (TextView) view.findViewById(R.id.book_title);
            component.ISBN = (TextView) view.findViewById(R.id.book_ISBN);
            component.ownerName = (TextView) view.findViewById(R.id.owner_name);
            component.status = (TextView) view.findViewById(R.id.book_status);
            Log.d("hello","123");
            view.setTag(component);
        }

        else {
            component = (Component) view.getTag();
            Log.d("null", "hello");
        }

        component.author.setText("Author: "+(String)bookList.get(i).get("author"));
        component.title.setText("Title: "+(String)bookList.get(i).get("title"));
        component.ISBN.setText("ISBN: "+(String)bookList.get(i).get("ISBN"));
        component.ownerName.setText("owner:"+(String)bookList.get(i).get("owner_name"));
        component.status.setText("status:"+(String)bookList.get(i).get("status"));

        component.ownerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //find the owner uid, thus use it for mauth.email
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(bookList.get(i).get("owner").toString())){//owner name match the owner, alert box

                                    String email = document.get("Email").toString();
                                    builder = new AlertDialog.Builder(context);
                                    builder.setMessage("Email: "+email);
                                    //Creating dialog box
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });

        request_button = view.findViewById(R.id.request_button);
        final int where = i;
        request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser()==null){//check if the user's already logged in
                    Toast.makeText(view.getContext(), "Please log in first!", Toast.LENGTH_SHORT).show();
                }else{
                    //add the book to the borrowed books of current user
                    bookList.get(where).put("location", 0);
                    db.collection("User").document(mAuth.getCurrentUser()
                            .getUid()).collection("Borrowed").document(bookList.get(where).get("title").toString())
                            .set(bookList.get(where))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                    //update status in borrower borrowed page - requested
                    db.collection("User").document(mAuth.getCurrentUser()
                            .getUid()).collection("Borrowed").document(bookList.get(where).get("title").toString())
                            .update("status","requested")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Borrowed status changed successfully!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Borrowed status failed", e);
                                }
                            });
                    //get username of the borrower and add to owner's request list
                    db.collection("User").document(mAuth.getCurrentUser()
                            .getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            final String borrower_name = documentSnapshot.getString("Name");
                            //Log.e("================================",bookList.get(where).get("owner").toString()+bookList.get(where).get("title").toString());
                            //add to owner request list
                            db.collection("User")
                                    .document(bookList.get(where).get("owner").toString()).collection("Lend")
                                    .document(bookList.get(where).get("title").toString()).update("requests", FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG,"Successfully add username to request list");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG,"failed to add username to request list");
                                        }
                                    });
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG,"failed to add username to request list");
                                }
                            });
                    Toast.makeText(view.getContext(),"Thank you for request, please wait for response.",Toast.LENGTH_SHORT);
                }



            }
        });

        return view;
    }
}
