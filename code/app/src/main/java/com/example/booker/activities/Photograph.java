package com.example.booker.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booker.R;
import com.example.booker.data.UploadImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

import static com.google.common.io.Files.getFileExtension;

public class Photograph extends AppCompatActivity {

    // views for button
    private Button btnSelect, btnUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;


    // view for image view
    private ImageView imageView;

    private ProgressBar mProgressBar;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    private StorageReference storageReference;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private StorageTask mUploadTask;
    private String downloadUrl;

    final static String TAG ="image";
    private String bookISBN;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mAuth = FirebaseAuth.getInstance();

        // initialise views
        btnSelect = findViewById(R.id.button_choose_image);
        btnUpload = findViewById(R.id.button_upload);
        imageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);



        Intent receiveISBN = getIntent();
        bookISBN = receiveISBN.getExtras().getString("ISBN");



        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().getReference("uploadImage");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploadImage");

        // on pressing btnSelect SelectImage() is called
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG,"  pick the pic");
                SelectImage();
                Log.d(TAG,"  finshed the picking");
            }
        });

        // on pressing btnUpload uploadImage() is called
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(Photograph.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
            }
        });


        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagesActivity();

            }
        });





    }

    private void openImagesActivity() {

        Intent intent =new Intent(this, ImagesActivity.class);
        intent.putExtra("ISBN",bookISBN);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

        } else {


//            signInAnonymously();


        }

    }




    // Select Image method
    private void SelectImage()
    {

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

    // Override onActivityResult method
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


                Picasso.get().load(filePath).into(imageView);



        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // UploadImage method
    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
//            final ProgressDialog progressDialog
//                    = new ProgressDialog(this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();

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
//                                    progressDialog.dismiss();

                                    Toast.makeText(Photograph.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            downloadUrl = uri.toString();
                                            //push to database
                                            UploadImage upload = new UploadImage( mEditTextFileName.getText().toString().trim(),
                                                    downloadUrl);

                                            Log.d(TAG, "name   "+mEditTextFileName.getText().toString().trim());
                                            Log.d(TAG, "URL   "+taskSnapshot.getUploadSessionUri().toString());
                                            String uploadId = mDatabaseRef.push().getKey();
                                            Log.d(TAG, "uploadId   "+uploadId);

                                            mDatabaseRef.child(bookISBN).child(uploadId).setValue(upload);

                                        }
                                    });

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
//                            progressDialog.dismiss();
                            Toast
                                    .makeText(Photograph.this,
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
                                    mProgressBar.setProgress((int) progress);
//                                    progressDialog.setMessage(
//                                            "Uploaded "
//                                                    + (int)progress + "%");
                                }
                            });

        }
        else{
            Toast.makeText(this,"No file selected", Toast.LENGTH_LONG).show();
        }

    }


}


