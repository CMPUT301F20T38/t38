package com.example.booker.data;

import android.util.Log;

import java.io.Serializable;

/**
 * Book class that have attribute
 * @param : String author
 * @param : String title
 * @param : String ISBN
 * @param : String status
 * @param : String owner
 * @param : String borrower
 *
 * This class has getter and setter method as well as constructor
 */
public class Book implements Serializable {
    private String author;
    private String title;
    private String ISBN;
    private String status;
    private String owner;
    private String borrower;

    public Book(String author, String title, String ISBN, String status, String owner, String borrower) {
        this.author = author;
        this.title = title;
        this.ISBN = ISBN;
        this.status = status;
        this.owner = owner;
        this.borrower = borrower;
    }

    public Book(){}

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getStatus() {
        return status;
    }

    public String getOwner() {
        return owner;
    }

    public String getBorrower() {
        return borrower;
    }
}
