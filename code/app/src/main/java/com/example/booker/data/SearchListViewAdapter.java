package com.example.booker.data;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.booker.R;
import com.example.booker.ui.borrow.BorrowFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchListViewAdapter extends ArrayAdapter<Map<String, Object>>{
    final String TAG = "borrow/search list tag";
    private ArrayList<Map<String, Object>> bookList;
    private LayoutInflater layoutInflater;
    private Button request_button;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Map<String, Object> null_field;//in order to create a document with null field
    private String owner;
    StorageReference storageReference;
    private DatabaseReference mDatabaseRef;

    private AlertDialog.Builder builder;

    public SearchListViewAdapter(Context context, ArrayList<Map<String, Object>> bookList) {
        super(context,0,bookList);
        this.bookList = bookList;
        this.context = context;



    }


    public LatLng location;


    @Override
    public View getView(int i, View view, ViewGroup parent) {

        //set the request button reaction

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.search_listview, parent, false);
        }

        db = FirebaseFirestore.getInstance();

        TextView author = view.findViewById(R.id.search_book_author);
        TextView title = view.findViewById(R.id.search_book_title);
        TextView ISBN = view.findViewById(R.id.search_book_ISBN);
        TextView ownerName = view.findViewById(R.id.search_book_owner);
        TextView status = view.findViewById(R.id.search_book_status);

        ImageView image = view.findViewById(R.id.search_book_image);

        // imageRef = storageReference.child((String)bookList.get(i).get("ISBN")+"/");
        View finalView = view;

        db.collection("UploadImages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e("image","begin");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.e("imageID",document.getId());
                       if (document.getId().equals((String)bookList.get(i).get("ISBN"))) {

                           Log.e("image",(String)bookList.get(i).get("ISBN"));
                           //Log.e("image",document.getData().);

                           Map<String, Object> map = new HashMap<String, Object>();
                           map = (Map) document.getData();
                           for(String i: map.keySet()){
                               Map<String, Object> map1 = new HashMap<String, Object>();
                               map1 = (Map) map.get(i);

                               Log.e("imagefind",map1.get("Url").toString());


                               Glide.with(finalView)
                                       .load(map1.get("Url").toString())
                                       .into(image);
                           }
                        }
                    }
                }

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("uploadImage");

        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploadImage");

        StorageReference imageRef = storageReference.child("1/1606439309622.jpg");
        //StorageReference imageRef = FirebaseStorage.getInstance().getReference();


        /*
        Glide.with(view)
                .load("https://firebasestorage.googleapis.com/v0/b/team38-5a204.appspot.com/o/uploadImage%2F222%2F1606439346049.jpg?alt=media&token=a737c89e-9c8e-43f1-baa9-7f1a859f1965")
                .into(image);


         */

        author.setText((String)bookList.get(i).get("author"));
        title.setText((String)bookList.get(i).get("title"));
        ISBN.setText((String)bookList.get(i).get("ISBN"));
        ownerName.setText((String)bookList.get(i).get("owner_name"));
        status.setText((String)bookList.get(i).get("status"));

        ownerName.setOnClickListener(new View.OnClickListener() {
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

        mAuth = FirebaseAuth.getInstance();


        request_button = view.findViewById(R.id.search_request_button);
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
