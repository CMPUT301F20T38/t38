package com.example.booker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booker.R;
import com.example.booker.data.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditDeleteOwnerBook extends AppCompatActivity {

    private EditText editTitle, editAuthor, editISBN;
    private Button btnEdit, btnDelete, btnRequest;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_edit_delete_book);

        final Intent intent = getIntent();
        final Book book = (Book) intent.getSerializableExtra("YeeSkywalker");

        String author = book.getAuthor();
        String title = book.getTitle();
        String ISBN = book.getISBN();



        editTitle = (EditText) findViewById(R.id.owner_ed_title);
        editAuthor = (EditText) findViewById(R.id.owner_ed_author);
        editISBN = (EditText) findViewById(R.id.owner_ed_ISBN);

        btnEdit = (Button) findViewById(R.id.owner_edit_btn);
        btnDelete = (Button) findViewById(R.id.owner_delete_btn);
        btnRequest = (Button) findViewById(R.id.owner_show_request);

        editTitle.setText(title);
        editAuthor.setText(author);
        editISBN.setText(ISBN);

        editTitle.addTextChangedListener(textWatcher);
        editAuthor.addTextChangedListener(textWatcher);
        editISBN.addTextChangedListener(textWatcher);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                book.setAuthor(editAuthor.getText().toString());
                book.setTitle(editTitle.getText().toString());
                book.setISBN(editISBN.getText().toString());

                Log.d("Owner's edit book", "Is Click");
                intent.putExtra("YeeSkywalker", book);
                setResult(0, intent);
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Owner's delete book", "Is Clicked");
                intent.putExtra("YeeSkywalker", book);
                setResult(1, intent);
                finish();
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Owner's check request", "Under Construction");
                Intent intent_request = new Intent(view.getContext(),RequestListActivity.class);
                intent_request.putExtra("BookName",book.getTitle());
                startActivity(intent_request);
            }
        });

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (editTitle.getText().toString().trim().isEmpty() ||
                    editAuthor.getText().toString().trim().isEmpty() ||
                    editISBN.getText().toString().isEmpty()) {
                Log.d("TextWatcher", "Null detected");
                btnEdit.setEnabled(false);
            }
            else {
                btnEdit.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
