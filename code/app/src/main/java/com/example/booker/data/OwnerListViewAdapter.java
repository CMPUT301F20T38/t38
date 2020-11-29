package com.example.booker.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.booker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter that extends arrayadapter
 * ArrayList book that maintaint the book class
 * context that get current activity
 */
public class OwnerListViewAdapter extends ArrayAdapter<Book> {

    private ArrayList<Book> books;
    private Context context;
    private FirebaseFirestore db;

    public OwnerListViewAdapter(Context context, ArrayList<Book> books){
        super(context, 0, books);
        this.context = context;
        this.books = books;
    }



    // getView that inflate the view of each item in the arraylist
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.owner_listview, parent, false);
        }

        Book book = books.get(position);

        TextView author = view.findViewById(R.id.owner_book_author);
        TextView title = view.findViewById(R.id.owner_book_title);
        TextView ISBN = view.findViewById(R.id.owner_book_ISBN);
        TextView borrower = view.findViewById(R.id.owner_borrower_name);
        TextView status = view.findViewById(R.id.owner_book_status);

        ImageView image = view.findViewById(R.id.owner_book_image);

        db = FirebaseFirestore.getInstance();

        View finalView = view;
        db.collection("UploadImages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e("image","begin");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.e("imageID",document.getId());
                        if (document.getId().equals(book.getISBN())) {

                            Log.e("image",book.getISBN());
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
        author.setText(book.getAuthor());
        title.setText(book.getTitle());
        borrower.setText(book.getBorrower());
        ISBN.setText(book.getISBN());
        status.setText(book.getStatus());

        return view;
    }
}
