package library.service;

import library.entity.Book;
import library.util.Result;
import java.util.List;

public interface IBookService {
    void addBook(String ISBN, String title, double price);

    Result<Book> addBookWithResult(String ISBN, String title, double price);
    Result<Book> getBook(String bookId);
    Result<List<Book>> getAllBooks();
}