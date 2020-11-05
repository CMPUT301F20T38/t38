package com.example.booker.data;

/**
 * Stores borrowed book data including title, author, owner name, and status
 * src is the link of resource img
 */
public class BorrowedBooks {
    private String src;
    private String title;
    private String author;
    private String owner;
    private String status;

    public BorrowedBooks(String src, String title, String author, String owner, String status) {
        this.src = src;
        this.title = title;
        this.author = author;
        this.owner = owner;
        this.status = status;
    }

    public BorrowedBooks(){}

    public String getSrc() {
        return src;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getOwner_name() {
        return owner;
    }

    public String getStatus() {
        return status;
    }
}
