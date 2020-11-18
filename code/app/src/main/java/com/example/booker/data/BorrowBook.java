package com.example.booker.data;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BorrowBook extends Book {
    public BorrowBook(String author, String title, String ISBN, String status, String owner,
                      String borrower, ArrayList<String> requests) {
        super(author, title, ISBN, status, owner, borrower, requests);
    }


}
