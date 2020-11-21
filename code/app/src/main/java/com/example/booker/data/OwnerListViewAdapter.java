package com.example.booker.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.booker.R;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
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

        author.setText(book.getAuthor());
        title.setText(book.getTitle());
        ISBN.setText(book.getISBN());
        status.setText(book.getStatus());

        return view;
    }
}
