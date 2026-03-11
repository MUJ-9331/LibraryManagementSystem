package library.entity;

import java.time.LocalDate;

import static library.dao.UserDao.getUserById;

public class BorrowRecord {
    private String bookId;
    private String userId;
    private final LocalDate borrowTime;
    private LocalDate returnTime;
    private final LocalDate dueTime;

    public static BorrowRecord createBorrow(String bookId, String userId) {
        return new BorrowRecord(bookId, userId, LocalDate.now(), null,
                LocalDate.now().plusDays(getUserById(userId).getMaxBorrowTime()));
    }

    private BorrowRecord(String bookId, String userId, LocalDate borrowTime, LocalDate returnTime, LocalDate dueTime) {
        this.bookId = bookId;
        this.userId = userId;
        this.borrowTime = borrowTime;
        this.returnTime = returnTime;
        this.dueTime = dueTime;
    }

    public static BorrowRecord parseLineToBorrow(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 5) return null;
        return new BorrowRecord(
                parts[0],
                parts[1],
                LocalDate.parse(parts[2]),
                "null".equals(parts[3]) || parts[3].trim().isEmpty() ? null : LocalDate.parse(parts[3]),
                LocalDate.parse(parts[4])
        );
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getBorrowTime() {
        return borrowTime;
    }

    public LocalDate getDueTime() {
        return dueTime;
    }

    public LocalDate getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(LocalDate returnTime) {
        this.returnTime = returnTime;
    }
}