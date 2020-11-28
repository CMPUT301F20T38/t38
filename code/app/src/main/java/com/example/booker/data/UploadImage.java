package com.example.booker.data;

/**
 * Jason'part
 * In this class, we create a uploadImage class for the purpose of uploading and getting the key for uploaded image.
 * And this class is used in Images.activity and the ImageAdapter
 *
 * Modified and referenced to
 * https://codinginflow.com/tutorials/android/firebase-storage-upload-and-retrieve-images/part-2-image-chooser
 *
 */

public class UploadImage {

    private String mName;
    private String mImageUrl;
    private String mKey;

        public UploadImage() {
            //empty constructor needed
        }

        public UploadImage(String name, String imageUrl) {
            if (name.trim().equals("")) {
                name = "No Name";
            }

            mName = name;
            mImageUrl = imageUrl;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getImageUrl() {
            return mImageUrl;
        }

        public void setImageUrl(String imageUrl) {
            mImageUrl = imageUrl;
        }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
