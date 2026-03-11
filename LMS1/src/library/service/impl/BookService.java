package library.service.impl;

import library.dao.BookDao;
import library.entity.Book;
import library.exception.CustomException;
import library.service.IBookService;
import library.util.Result;
import library.util.Validator;

import java.util.ArrayList;
import java.util.List;

import static library.dao.BookDao.getTitleCount;

public class BookService implements IBookService {

    @Override
    public void addBook(String ISBN, String title, double price) {
        Validator.validateBook(ISBN, title, price);
        String bookId = generateBookId(title);
        Book book = Book.createBook(ISBN, title, price, bookId);
        BookDao.add(book);
    }

    @Override
    public Result<Book> addBookWithResult(String ISBN, String title, double price) {
        try {
            Validator.validateBook(ISBN, title, price);
            String bookId = generateBookId(title);
            Book book = Book.createBook(ISBN, title, price, bookId);
            BookDao.add(book);
            return Result.success("图书添加成功", book);
        } catch (CustomException e) {
            return Result.fromException(e);
        } catch (Exception e) {
            return Result.error("系统错误：" + e.getMessage(), 500);
        }
    }

    @Override
    public Result<Book> getBook(String bookId) {
        try {
            Book book = BookDao.getBookById(bookId);
            if (book == null) {
                return Result.error("图书不存在", 404);
            }
            return Result.success(book);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage(), 500);
        }
    }

    @Override
    public Result<List<Book>> getAllBooks() {
        try {
            List<Book> books = new ArrayList<>(BookDao.bookMap.values());
            return Result.success(books);
        } catch (Exception e) {
            return Result.error("获取图书列表失败：" + e.getMessage(), 500);
        }
    }

    private String generateBookId(String title) {
        int count = getTitleCount(title) + 1;
        return title + String.format("-%03d", count);
    }
}