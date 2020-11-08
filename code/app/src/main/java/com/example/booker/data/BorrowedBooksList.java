package com.example.booker.data;

import android.content.Context;
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

import com.example.booker.R;

import java.util.ArrayList;

/**
 * BorrowedBooksList class is the adapter for Borrowed Book List, the function of it
 * is to customize the Borrowed Book list view and decide the buttons pattern
 * for each status
 */
public class BorrowedBooksList extends ArrayAdapter<BorrowedBooks> {
    private ArrayList<BorrowedBooks> borrowedBooks;
    private Context context;

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

        //the img resourse will be changed later, but now it will just use sample
        borrowed_img.setImageResource(R.mipmap.testimg);
        borrowed_title.setText(borrowedBook.getTitle());
        borrowed_author.setText(borrowedBook.getAuthor());
        borrowed_status.setText(borrowedBook.getStatus());
        borrowed_owner_username.setText(borrowedBook.getOwner());
        //set the button change and visibility of map
        if(borrowedBook.getStatus().equals("borrowed")){//borrowed
            accept_book.setVisibility(View.GONE);
            return_book.setVisibility(View.VISIBLE);
            map_img.setVisibility(View.INVISIBLE);
        }else if (borrowedBook.getStatus().equals("accepted")){//accepted
            accept_book.setVisibility(View.VISIBLE);
            return_book.setVisibility(View.GONE);
            map_img.setVisibility(View.VISIBLE);
        }else{//requested, but not accept
            accept_book.setVisibility(View.GONE);
            return_book.setVisibility(View.GONE);
            map_img.setVisibility(View.GONE);
        }

        return view;
    }
}
