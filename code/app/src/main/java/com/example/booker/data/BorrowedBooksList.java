package com.example.booker.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.booker.MainActivity;
import com.example.booker.R;
import com.example.booker.activities.DisplayMapActivity;
import com.example.booker.activities.MapsActivity;
import com.example.booker.activities.ScanCodeActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * BorrowedBooksList class is the adapter for Borrowed Book List, the function of it
 * is to customize the Borrowed Book list view and decide the buttons pattern
 * for each status
 */
public class BorrowedBooksList extends ArrayAdapter<BorrowedBooks>  {
    private ArrayList<BorrowedBooks> borrowedBooks;
    private Context context;
    private final String TAG="BorrowLookList";
    private AlertDialog.Builder builder;

    public BorrowedBooksList(Context context, ArrayList<BorrowedBooks> borrowedBooks) {
        super(context, 0, borrowedBooks);
        this.borrowedBooks = borrowedBooks;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;

        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.borrowed_book_content, parent,false);
        }

        BorrowedBooks borrowedBook = borrowedBooks.get(position);

        ImageView borrowed_img = view.findViewById(R.id.borrowed_img);
        ImageView map_img = view.findViewById(R.id.borrowed_map);
        TextView borrowed_title = view.findViewById(R.id.borrowed_title);
        TextView borrowed_author = view.findViewById(R.id.borrowed_author);
        TextView borrowed_status = view.findViewById(R.id.borrowed_status);
        TextView borrowed_owner_username = view.findViewById(R.id.borrowed_owner_username);
        Button accept_book = view.findViewById(R.id.accept_borrowed_book);
        Button return_book = view.findViewById(R.id.return_borrowed_book);
        Button cancel_book = view.findViewById(R.id.cancel_borrowed_book);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //find the borrower's path, check if it exists
        db.collection("User").document(borrowedBook.getOwner()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"SUccessfull find document");
                            String userName = task.getResult().get("Name").toString();
                            //the img resourse will be changed later, but now it will just use sample
                            borrowed_img.setImageResource(R.mipmap.testimg);
                            borrowed_title.setText(borrowedBook.getTitle());
                            borrowed_author.setText(borrowedBook.getAuthor());
                            borrowed_status.setText(borrowedBook.getStatus());
                            borrowed_owner_username.setText(userName);

                            borrowed_owner_username.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String ownerName = borrowedBook.getOwner();

                                    db.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                //find the owner uid, thus use it for mauth.email
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.getId().equals(ownerName)){//owner name match the owner, alert box

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

                        }else{
                            Log.d(TAG,"Fail to find document");
                        }

                    }
                });

        //set the button change and visibility of map
        if(borrowedBook.getStatus().equals("borrowed")){//borrowed
            accept_book.setVisibility(View.GONE);
            return_book.setVisibility(View.VISIBLE);
            cancel_book.setVisibility(View.GONE);
            map_img.setVisibility(View.INVISIBLE);

            //set the click listener on return button
            return_book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("User")
                            .document(mAuth.getCurrentUser().getUid()).collection("Borrowed")
                            .document(borrowedBook.getTitle())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(context.getApplicationContext(), ScanCodeActivity.class);
                                //set the path to the user's book, also delete borrower's document
                                intent.putExtra("event","return_book");
                                intent.putExtra("book",borrowedBook.getTitle());
                                intent.putExtra("owner",borrowedBook.getOwner());
                                intent.putExtra("borrower",mAuth.getCurrentUser().getUid());
                                intent.putExtra("isbn",borrowedBook.getISBN());
                                context.startActivity(intent);
                            }
                        }
                    });
                }
            });
        }else if (borrowedBook.getStatus().equals("accepted")){//accepted
            accept_book.setVisibility(View.VISIBLE);
            return_book.setVisibility(View.GONE);
            cancel_book.setVisibility(View.GONE);
            map_img.setVisibility(View.VISIBLE);


            //for Map
            map_img.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View view) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("User")
                            .document(mAuth.getCurrentUser().getUid()).collection("Borrowed")
                            .document(borrowedBook.getTitle())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                // Document found in the offline cache
                                DocumentSnapshot document = task.getResult();

                                Map data = (Map) document.get("location");

                                Double lat = (Double) data.get("latitude");
                                Double lon = (Double) data.get("longitude");

                                Log.d(TAG, "Cached document data lat: " + lat);
                                Log.d(TAG, "Cached document data lon : " + lon);

                                Intent goToDisplayMap = new Intent(context, DisplayMapActivity.class);
                                goToDisplayMap.putExtra("LAT", lat);
                                Log.d(TAG, "putExtra: Lattt:  "+lat);

                                goToDisplayMap.putExtra("LON", lon);
                                Log.d(TAG, "putExtra: LONGG:"+lon);

                                context.startActivity(goToDisplayMap);



                            } else {
                                Log.d(TAG, "Cached get failed: ", task.getException());
                            }
                        }
                    });

                }
            });


            //end of map interaction
            //for ISBN, scan to confirm borrow the book and set status to borrowed
            accept_book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    Intent intent = new Intent(context.getApplicationContext(), ScanCodeActivity.class);
                    //set the path to the user's book
                    intent.putExtra("event","accept_book");
                    intent.putExtra("book",borrowedBook.getTitle());
                    intent.putExtra("owner",borrowedBook.getOwner());
                    intent.putExtra("borrower",mAuth.getCurrentUser().getUid());
                    context.startActivity(intent);
                }
            });


        }else{//requested, but not accept
            accept_book.setVisibility(View.GONE);
            return_book.setVisibility(View.GONE);
            cancel_book.setVisibility(View.VISIBLE);
            map_img.setVisibility(View.GONE);
            cancel_book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//cancel the book that requested
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    //delete it in borrower's list
                    db.collection("User")
                            .document(mAuth.getCurrentUser().getUid()).collection("Borrowed")
                            .document(borrowedBook.getTitle()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull  Task<Void> task) {
                            if(task.isSuccessful()){Log.d(TAG,"delete request in borrower's list");}
                            else{Log.d(TAG,"Fail to delete request in borrower's list");}
                        }
                    });

                    //delete the borrower in owner's list
                    db.collection("User")
                            .document(borrowedBook.getOwner()).collection("Lend")
                            .document(borrowedBook.getTitle()).update("requests", FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){Log.d(TAG,"Delete borrower in owner's list");}
                                    else{Log.d(TAG,"Fail to delete borrower in owner's list");}
                                }
                            });
                }
            });
        }


        return view;
    }
}
