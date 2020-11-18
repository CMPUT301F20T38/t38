package com.example.booker.data;

import java.util.ArrayList;

/**
 * Stores borrowed book data including title, author, owner name, and status
 * src is the link of resource img
 */
public class BorrowedBooks extends Book{
    private String src;


    public BorrowedBooks(String author, String title, String ISBN, String status, String owner,
                         String borrower, ArrayList<String> requests, String src) {
        super(author, title, ISBN, status, owner, borrower, requests);
        this.src = src;

    }

    public BorrowedBooks(){}

    public String getSrc() {
        return src;
    }



}
