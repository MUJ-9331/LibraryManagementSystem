package library.dao;

import library.entity.BorrowRecord;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static library.entity.BorrowRecord.parseLineToBorrow;

public class BorrowDao {
    private static Map<String, String> bookBorrowerMap = new HashMap<>();
    public static Map<String, List<BorrowRecord>> userBorrowMap = new HashMap<>();
    public static Map<String, BorrowRecord> borrowMap = new HashMap<>();
    public static Map<LocalDate, List<String>> dueDateMap = new HashMap<>();
    private static final String BORROW_FILE = "library_data/borrow.dat";

    public static boolean exists(String bookId, String userId) {
        return bookBorrowerMap.containsKey(bookId) && userId.equals(bookBorrowerMap.get(bookId));
    }

    public static void add(BorrowRecord borrow) {
        String bookId = borrow.getBookId();
        String userId = borrow.getUserId();
        LocalDate dueTime = borrow.getDueTime();

        borrowMap.put(bookId, borrow);
        bookBorrowerMap.put(bookId, userId);
        /***
         * computeIfAbsent：如果 key (userId) 不存在，就计算一个值放进去;
         * k (key) -> (去执行) new ArrayList<>()
           (创建一个新的 ArrayList 作为默认值):如果没找到就执行的代码块
         */
        userBorrowMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(borrow);
        dueDateMap.computeIfAbsent(dueTime, k -> new ArrayList<>()).add(bookId);
    }

    public static void deleteRecord(String bookId) {
        BorrowRecord record = borrowMap.get(bookId);
        if (record != null) {
            String userId = record.getUserId();
            LocalDate dueTime = record.getDueTime();

            borrowMap.remove(bookId);
            bookBorrowerMap.remove(bookId);

            List<BorrowRecord> userRecords = userBorrowMap.get(userId);
            if (userRecords != null) {
                userRecords.removeIf(r -> r.getBookId().equals(bookId));
                if (userRecords.isEmpty()) {
                    userBorrowMap.remove(userId);
                }
            }

            List<String> bookIds = dueDateMap.get(dueTime);
            if (bookIds != null) {
                bookIds.remove(bookId);
                if (bookIds.isEmpty()) {
                    dueDateMap.remove(dueTime);
                }
            }
        }
    }

    public static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BORROW_FILE))) {
            for (BorrowRecord borrow : borrowMap.values()) {
                writer.write(formatBorrowToLine(borrow));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("保存借阅数据失败：" + e.getMessage());
        }
    }

    public static void loadFromFile() {
        File file = new File(BORROW_FILE);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                System.out.println("创建新数据文件: " + BORROW_FILE);
                return;
            } catch (IOException e) {
                throw new RuntimeException("文件创建失败");
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(BORROW_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                BorrowRecord borrow = parseLineToBorrow(line);
                if (borrow != null) {
                    String bookId = borrow.getBookId();
                    String userId = borrow.getUserId();

                    borrowMap.put(bookId, borrow);
                    bookBorrowerMap.put(bookId, userId);
                    userBorrowMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(borrow);

                    if (borrow.getReturnTime() == null) {
                        dueDateMap.computeIfAbsent(borrow.getDueTime(), k -> new ArrayList<>()).add(bookId);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("加载失败：" + e.getMessage());
        }
    }

    private static String formatBorrowToLine(BorrowRecord borrow) {
        return String.join("|",
                borrow.getBookId(),
                borrow.getUserId(),
                borrow.getBorrowTime().toString(),
                borrow.getReturnTime() == null ? "null" : borrow.getReturnTime().toString(),
                borrow.getDueTime().toString()
        );
    }

    public static BorrowRecord getRecordById(String bookId) {
        return borrowMap.get(bookId);
    }
}