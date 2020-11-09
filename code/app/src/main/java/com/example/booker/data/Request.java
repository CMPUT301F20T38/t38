package com.example.booker.data;

import java.util.ArrayList;

/**
 * Request class is used for owner book request list
 * It stores data of each request
 */
public class Request {
    private String user_name;
    private String book_name;

    public Request() {
    }

    public Request(String user_name, String book_name) {
        this.user_name = user_name;
        this.book_name = book_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getBook_name() {
        return book_name;
    }
}
