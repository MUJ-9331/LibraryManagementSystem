package library.dao;

import library.entity.Book;
import library.exception.CustomException;
import library.exception.ErrorCode;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static library.entity.Book.parseLineToBook;

public class BookDao {
    public static Map<String, Book> bookMap = new LinkedHashMap<>();
    private static Map<String, Integer> titleCountMap = new HashMap<>();
    private static final String BOOK_FILE = "library_data/book.dat";

    public static int getTitleCount(String title) {
        return titleCountMap.getOrDefault(title, 0);
    }

    public static boolean exists(String bookId) {
        return bookMap.containsKey(bookId);
    }

    public static void add(Book book) {
        bookMap.put(book.getBookId(), book);
        String title = book.getTitle();
        titleCountMap.put(title, titleCountMap.getOrDefault(title, 0) + 1);
        System.out.println(book.getBookId() + "入库成功！");
    }

    public static void saveToFile() {
        //BufferedWriter：缓冲区写入器（草稿纸）   FileWriter：文件写入器（空本子）（专门写文件的工具）
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOK_FILE))) {
            for (Book book : bookMap.values()) {
                writer.write(formatBookToLine(book));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("保存图书数据失败：" + e.getMessage());
        }
    }

    public static void loadFromFile() {
        File file = new File(BOOK_FILE);

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                System.out.println("创建新数据文件: " + BOOK_FILE);
                return;
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND);
            }
        }

        //语法糖，小括号里的内容自动finally
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOK_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Book book = parseLineToBook(line);
                if (book != null) {
                    bookMap.put(book.getBookId(), book);
                }
            }
        } catch (IOException e) {
            System.err.println("加载失败：" + e.getMessage());
        }
    }

    private static String formatBookToLine(Book book) {
        return String.join("|",
                book.getISBN(),
                book.getTitle(),
                String.valueOf(book.getPrice()),
                book.getBookId(),
                book.getState());
    }

    public static Book getBookById(String bookId) {
        return bookMap.get(bookId);
    }
}