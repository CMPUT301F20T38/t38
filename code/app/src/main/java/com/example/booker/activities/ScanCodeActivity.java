package com.example.booker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.example.booker.MainActivity;
import com.example.booker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
//copy


public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    int MY_PERMISSIONS_REQUEST_CAMERA=0;
    ZXingScannerView scannerView;
    private final String TAG="Scanner";

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
        if(event.equals("owner_scan")){
            Intent return_intent = new Intent(getApplicationContext(), MainActivity.class);
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