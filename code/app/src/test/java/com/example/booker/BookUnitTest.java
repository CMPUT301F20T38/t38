package com.example.booker;

import com.example.booker.data.Book;
import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class BookUnitTest {

    @Test
    public void testBookGetAuthor(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("Coco", book.getAuthor());
    }

    @Test
    public void testBookSetAuthor(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("Coco", book.getAuthor());

        book.setAuthor("Simpson");
        assertEquals("Simpson", book.getAuthor());
    }

    @Test
    public void testBookGetTitle(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("GoodLife", book.getTitle());
    }

    @Test
    public void testBookSetTitle(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("GoodLife", book.getTitle());

        book.setTitle("Ocean Cite");
        assertEquals("Ocean Cite", book.getTitle());
    }

    @Test
    public void testBookGetISBN(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("123456789", book.getISBN());
    }

    @Test
    public void testBookSetISBN(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("123456789", book.getISBN());

        book.setISBN("0987654321");
        assertEquals("0987654321", book.getISBN());
    }

    @Test
    public void testBookGetStatus(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("available", book.getStatus());
    }

    @Test
    public void testBookSetStatus(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("available", book.getStatus());

        book.setStatus("requested");
        assertEquals("requested", book.getStatus());
    }

    @Test
    public void testBookGetOwner(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("Yee", book.getOwner());
    }

    @Test
    public void testBookSetOwner(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("Yee", book.getOwner());

        book.setOwner("GoonerTai");
        assertEquals("GoonerTai", book.getOwner());
    }

    @Test
    public void testBookGetBorrower(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("Lin", book.getBorrower());
    }

    @Test
    public void testBookSetBorrower(){
        Book book = new Book("Coco", "GoodLife", "123456789",
                "available", "Yee", "Lin", new ArrayList<>());
        assertEquals("Lin", book.getBorrower());

        book.setBorrower("Tai");
        assertEquals("Tai", book.getBorrower());
    }
}
