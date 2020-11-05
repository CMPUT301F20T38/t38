package com.example.booker.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.booker.R;

import java.util.List;
import java.util.Map;

public class OwnerListViewAdapter extends BaseAdapter {

    private List<Map<String, Object>> bookList;
    private LayoutInflater layoutInflater;
    private Context context;

    public OwnerListViewAdapter(Context context, List<Map<String, Object>> bookList) {
        this.bookList = bookList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);

    }

    public class Component{
        public TextView title;
        public TextView author;
        public TextView ISBN;
        public TextView borrowerTag;
        public TextView borrowerName;
        public TextView status;
    }

    @Override
    public int getCount() {
        Log.d("BOOOK SIZE", bookList.toString());
        return bookList.size();
    }

    @Override
    public Object getItem(int i) {
        return bookList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d("cs", "go");
        Component component = null;
        if (view == null){

            component = new Component();
            view = layoutInflater.inflate(R.layout.owner_listview, null);
            component.author = (TextView) view.findViewById(R.id.owner_book_author);
            component.title = (TextView) view.findViewById(R.id.owner_book_title);
            component.ISBN = (TextView) view.findViewById(R.id.owner_book_ISBN);
            component.borrowerTag = (TextView) view.findViewById(R.id.owner_borrower_tag);
            component.borrowerName = (TextView) view.findViewById(R.id.owner_borrower_name);
            component.status = (TextView) view.findViewById(R.id.owner_book_status);
            Log.d("hello","123");
            view.setTag(component);
        }

        else {
            component = (Component) view.getTag();
            Log.d("null", "hello");
        }

        component.author.setText((String)bookList.get(i).get("author"));
        component.title.setText((String)bookList.get(i).get("title"));
        component.ISBN.setText((String)bookList.get(i).get("ISBN"));
        component.borrowerTag.setText((String)bookList.get(i).get("tag"));
        component.borrowerName.setText((String)bookList.get(i).get("name"));
        component.status.setText((String)bookList.get(i).get("status"));

        return view;
    }
}
