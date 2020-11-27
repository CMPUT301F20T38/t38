package com.example.booker.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.booker.R;
import com.example.booker.data.Book;
import com.example.booker.data.UploadImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Yee's Part
 * The activity allow user to add new book as owner
 * EditText author: Enable user to input author name when attend to add a book
 * EditText title: Enable user to input title name when attend to add a boook
 * EditText ISBN: Enable user to input ISBN when attend to add a boook
 * Button btnComfirm: sumbit the form
 * Action bar deketed
 *
 * FirebaseAuth mAuth: the token of firebasemAuth reference
 * FirebaseFirestore db: the token of firebasefirestore reference
 */

public class AddOwnerBook extends AppCompatActivity {
    private EditText author;
    private EditText title;
    private EditText ISBN;
    private Button btnComfirm;

    private String currentPhotoPath;

    private ImageView photo;
    private ImageView addISBN;
    private Uri filePath;
    private StorageTask mUploadTask;
    private String downloadUrl;
    private DatabaseReference mDatabaseRef;
    private EditText mEditTextFileName;
    private ProgressBar mProgressBar;
    private String bookISBN;

    final static String TAG ="image";
    private final int PICK_IMAGE_REQUEST = 22;
    private final int CAMERA = 10;
    private final int GET_ISBN = 33;//request code for isbn
    int MY_PERMISSIONS_REQUEST_CAMERA=0;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_add_book);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        author = (EditText) findViewById(R.id.owner_add_author);
        title = (EditText) findViewById(R.id.owner_add_title);
        ISBN = (EditText) findViewById(R.id.owner_add_ISBN);
        btnComfirm = (Button) findViewById(R.id.owner_add_confirm);
        photo = findViewById(R.id.photoView);
        addISBN = findViewById(R.id.add_isbn_button);

        mEditTextFileName = findViewById(R.id.owner_add_title);
        mProgressBar = findViewById(R.id.add_progress_bar);

        storageReference = FirebaseStorage.getInstance().getReference("uploadImage");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploadImage");



        if (author.toString().isEmpty()){
            author.requestFocus();
        }

        if (title.toString().isEmpty()){
            title.requestFocus();
        }

        if (ISBN.toString().isEmpty()){
            ISBN.requestFocus();
        }

        // when button is trigger, begin to interacet with firestore
        btnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (filePath == null){
                    uploadInf();
                }
                if (filePath!=null){
                    bookISBN = ISBN.getText().toString();

                    uploadImage();
                }
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopueWindow();
                //Log.d(TAG,"  pick the pic");
                //SelectImage();
                //Log.d(TAG,"  finshed the picking");
            }
        });

        addISBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to scan isbn activity
                Intent intent = new Intent(AddOwnerBook.this, ScanCodeActivity.class);
                //notify the event is add isbn
                intent.putExtra("event", "owner_add_isbn");
                startActivityForResult(intent, GET_ISBN);//Activity is started with requestCode 33
            }
        });
    }

    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    private void TakeCamera(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA);








    }

    private void showPopueWindow(){
        View popView = View.inflate(this,R.layout.popup_window,null);
        Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);
        //get height and width
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels*1/3;

        final PopupWindow popupWindow = new PopupWindow(popView,width,height);

        popupWindow.setFocusable(true);
        //popWindow dismiss if click outside
        popupWindow.setOutsideTouchable(true);

        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
                popupWindow.dismiss();
            }
        });

        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakeCamera();
                popupWindow.dismiss();
            }
        });

        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        //popupWindow not translucent
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

        //translucent
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,50);

    }




    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference imageRef
                    = storageReference
                    .child(
                            bookISBN+"/"
                                    + System.currentTimeMillis()
                                    + "." + getFileExtension(filePath));

            // adding listeners on upload
            // or failure of image
            mUploadTask=imageRef.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){

                                    // Image uploaded successfully
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProgressBar.setProgress(0);
                                        }
                                    }, 500);
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    uploadInf();

                                    Toast.makeText(AddOwnerBook.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            downloadUrl = uri.toString();
                                            //push to database
                                            UploadImage upload = new UploadImage( mEditTextFileName.getText().toString().trim(),
                                                    downloadUrl);

//                                            Log.d(TAG, "name   "+mEditTextFileName.getText().toString().trim());
//                                            Log.d(TAG, "URL   "+taskSnapshot.getUploadSessionUri().toString());


                                            String uploadId =  System.currentTimeMillis()+"Key";
                                            Log.d(TAG, "uploadId   "+uploadId);

                                            Map<String, Object> docData = new HashMap<>();

                                            Map<String, Object> nestedData = new HashMap<>();

                                            nestedData.put("Url",downloadUrl);
                                            nestedData.put("Name",mEditTextFileName.getText().toString().trim());

                                            docData.put(uploadId,nestedData);

                                            db.collection("UploadImages").document(bookISBN)
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {

                                                            db.collection("UploadImages").document(bookISBN)
                                                                    .update(docData)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d(TAG, "URl and name  successfully written!");
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.w(TAG, "Error writing uploading URl and name", e);
                                                                        }
                                                                    });

                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                            db.collection("UploadImages").document(bookISBN)
                                                                    .set(docData)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d(TAG, "URl and name  successfully written!");
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.w(TAG, "Error writing uploading URl and name", e);
                                                                        }
                                                                    });
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });


                                            db.collection("UploadImages").document(bookISBN)
                                                    .update(docData)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "URl and name  successfully written!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error writing uploading URl and name", e);
                                                        }
                                                    });

//                                            mDatabaseRef.child(bookISBN).child(uploadId).setValue(upload);

                                        }
                                    });

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(AddOwnerBook.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                                    mProgressBar.setProgress((int) progress);
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });

        }
        else{
            Toast.makeText(this,"No file selected", Toast.LENGTH_LONG).show();
        }

    }

    private void uploadInf(){
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        String addAuthor = author.getText().toString();
        String addTitle = title.getText().toString();
        String addISBN = ISBN.getText().toString();
        CollectionReference collectionReference = db.collection("User")
                .document(userId).collection("Lend");
        Book book = new Book(addAuthor, addTitle, addISBN, "available",
                userId, "", new ArrayList<>());
        bookISBN = ISBN.getText().toString();



        collectionReference
                .document(addTitle)
                .set(book)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Add Data Firestore", "Failed");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();

            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);

                photo.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }

        if(requestCode == CAMERA){


            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            photo.setImageBitmap(bitmap);

            filePath = getImageUri(getApplicationContext(), bitmap);


        }

        else if(requestCode == GET_ISBN  && data != null ){
            ISBN.setText(data.getStringExtra("ISBN"));
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }





}
