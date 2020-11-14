package com.example.booker.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.booker.data.Request;
import com.example.booker.data.RequestList;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import com.example.booker.data.RequestList;
//copy


public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    int MY_PERMISSIONS_REQUEST_CAMERA=0;
    ZXingScannerView scannerView;
    private final String TAG="Scanner";

    private String selectedBookname, selectedBorrower,selectedOwner;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LatLng pickedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

    }

    @Override
    public void handleResult(Result result) {//handle the ISBN  result
        //first check which event it is
        Intent prev_intent = getIntent();
        String event = prev_intent.getStringExtra("event");
        Double lat = prev_intent.getExtras().getDouble("LAT1");
        Double lon = prev_intent.getExtras().getDouble("LON1");
        selectedBookname = prev_intent.getExtras().getString("bookName");
        selectedBorrower = prev_intent.getExtras().getString("borrowerName");
        pickedLocation = new LatLng(lat,lon);

        Log.d(TAG, "Data received  "+ selectedBookname+ "   borrower  "+ selectedBorrower);



        if(event.equals("owner_scan")){

                            //find the path to the correspond Request
                final DocumentReference documentRef = db.collection("User")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("Lend")
                        .document(selectedBookname);
                DocumentReference owner_path = db.collection("User")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("Lend")
                        .document(selectedBookname);
                //change book status for owner
                owner_path.update("status","accepted");
                owner_path.update("requests", FieldValue.arrayRemove());

                //change borrow status for accepted user
                db.collection("User").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //find a matched name, update the status of its borrowed book
                                        if(document.getId().equals(selectedBorrower)){
                                            //change status to accepted
                                            db.collection("User").document(document.getId()).collection("Borrowed")
                                                    .document(selectedBookname).update("status","accepted")
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "Correspond user accept status successfully updated!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "Correspond user accept status failed to updated!");
                                                        }
                                                    });



                                        }
                                    }
                                }else{
                                    Log.d(TAG, "Fail to find user col/doc!");
                                }
                            }
                        });
                //update status for other users, loop all users
                for(final Request user_request : requests){
                    if(!user_request.getUser_name().equals(selectedBorrower)) {
                        db.collection("User").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //find a matched name, update the status of its borrowed book
                                                if (document.getId().equals(user_request.getUser_name())) {
                                                    //if decline a request, then for the user trying to borrow the book, it will disappear and send notification
                                                    //delete the correspond book in borrower's borrowed list
                                                    db.collection("User").document(document.getId()).collection("Borrowed")
                                                            .document(user_request.getBook_name()).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    //notification code write here
                                                                    Log.d(TAG, "Correspond user accept status successfully updated!");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d(TAG, "Correspond user accept status failed to updated!");
                                                                }
                                                            });
                                                }
                                            }
                                        } else {
                                            Log.d(TAG, "Fail to find user col/doc!");
                                        }
                                    }
                                });
                    }else{//change the borrower field in owner's book
                        db.collection("User").document(mAuth.getCurrentUser().getUid()).collection("Lend")
                                .document(selectedBookname).update("borrower",user_request.getUser_name())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Correspond user accept status successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Correspond user accept status failed to updated!");
                                    }
                                });
                    }
                }


                //delete all elements in Request array(update request list array) ???
                documentRef.update("requests",null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Remove all users successfully! ");
                        }else{
                            Log.d(TAG, "Fail to remove all users!");
                        }
                    }
                });


            db.collection("User")
                            .document(selectedBorrower)
                            .collection("Borrowed").document(selectedBookname)
                            .collection("location").document("latLon")
                            .set(pickedLocation)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Correspond user accept status successfully updated!");

                                    Intent scan_intent = new Intent(getApplicationContext(), ScanCodeActivity.class);
                                    scan_intent.putExtra("event","owner_scan");
                                    startActivity(scan_intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Correspond user accept status failed to updated!");
                                }
                            });




            Intent return_intent = new Intent(getApplicationContext(), RequestList.class);
            return_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(return_intent);
        }else if(event.equals("owner_add_isbn")){
            //Log.e("========================",result.getText());
            Intent return_intent = new Intent();
            return_intent.putExtra("ISBN",result.getText());
            setResult(33,return_intent);
            finish();
        }else if(event.equals("accept_book")){
            //get data from intent
            String owner  = prev_intent.getStringExtra("owner");
            String borrower  = prev_intent.getStringExtra("borrower");
            String book  = prev_intent.getStringExtra("book");
            Log.e("================================================",owner+borrower+book);
            //firebase path
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //path to the ISBN of owner
            db.collection("User").document(owner)
                    .collection("Lend").document(book).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                String ISBN = task.getResult().get("isbn").toString();
                                //decide if the ISBN code is the same as the owner's isbn
                                if(result.getText().equals(ISBN)){
                                    Log.e("======================================================",result.getText()+"   "+ISBN);
                                    //change owner book status
                                    db.collection("User").document(owner)
                                            .collection("Lend").document(book).update("status","borrowed")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull  Task<Void> task1) {
                                                    if (task1.isSuccessful()) {Log.d(TAG, "Succeed change owner status ");}
                                                    else{Log.d(TAG, "Change owner status failed: ", task.getException());}
                                                }
                                            });

                                    //change borrower book status
                                    db.collection("User").document(borrower)
                                            .collection("Borrowed").document(book).update("status","borrowed")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull  Task<Void> task1) {
                                                    if (task1.isSuccessful()) {Log.d(TAG, "Succeed change borrower status ");}
                                                    else{Log.d(TAG, "Change borrower status failed: ", task.getException());}
                                                }
                                            });
                                }else{Log.e("======================================================",result.getText()+"   "+ISBN);}
                            }else{

                                Log.d(TAG, "The accepted book didn't match: ", task.getException());
                            }
                            finish();//end activity
                        }
                    });

        }

        //onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        scannerView.setResultHandler(this);
//        scannerView.startCamera();
//    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}