package com.example.booker.activities;

import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PhotographEdit extends AppCompatActivity {

    private ImageView bookImage;
    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagesRef;
    private Dialog loadingBar;
    private DatabaseReference RootRef;
    final private String TAG ="photoEDITing";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photograph_edit);

        bookImage = (ImageView) findViewById(R.id.EditImage);
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("book image");


        bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "  : Imgae clicked");

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
//                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(galleryIntent.createChooser(galleryIntent, "Select Picture"),GalleryPick);
                Log.d(TAG, "  : Gallery in");

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            Uri ImageUri = data.getData();


            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK)
            {
//                loadingBar.setTitle("Set Profile Image");
//
//                loadingBar.setCanceledOnTouchOutside(false);
//                loadingBar.show();

                Uri resultUri = result.getUri();

                Log.d(TAG, resultUri+" :is the resultUri");


                StorageReference filePath = UserProfileImagesRef.child(  "1.jpg");

                Log.d(TAG, filePath+" :is the filePath");


                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(PhotographEdit.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                            final String downloaedUrl = task.getResult().toString();
                            Log.d(TAG, downloaedUrl+" :is the the final URi");

                            RootRef.child("image")
                                    .setValue(downloaedUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(PhotographEdit.this, "Image save in Database, Successfully...", Toast.LENGTH_SHORT).show();
//                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                Toast.makeText(PhotographEdit.this, "Error: " + message, Toast.LENGTH_SHORT).show();
//                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(PhotographEdit.this, "Error: " + message, Toast.LENGTH_SHORT).show();
//                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Log.d("in", "onresult");
//        if (requestCode==GalleryPick && requestCode==RESULT_OK && data!=null){
//            Log.d("in", "crop");
//            Uri ImageUri = data.getData();
//
//            // start picker to get image for cropping and then use the image in cropping activity
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .start(this);
//            // start cropping activity for pre-acquired image saved on the device
//            CropImage.activity(ImageUri)
//                    .start(this);
//
//// for fragment (DO NOT use `getActivity()`)
//            CropImage.activity()
//                    .start( this);
//
//
//        }

//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (resultCode == RESULT_OK){
//                Uri resultUri =result.getUri();
//                //change the name
//                StorageReference filePath = ImageRef.child("1.jpg");
//
//                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()){
//                            Toast.makeText(PhotographEdit.this,"Book image uploead Success", Toast.LENGTH_LONG).show();
//                        }
//                        else
//                        {
//                            String message = task.getException().toString();
//                            Toast.makeText(PhotographEdit.this,message, Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                });


//            }

//        }



//
//    }
}
