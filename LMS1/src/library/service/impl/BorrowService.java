package library.service.impl;

import library.dao.BookDao;
import library.dao.BorrowDao;
import library.dao.UserDao;
import library.entity.Book;
import library.entity.BorrowRecord;
import library.entity.User;
import library.exception.CustomException;
import library.exception.ErrorCode;
import library.service.IBorrowService;
import library.util.Result;
import library.util.Validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static library.dao.BookDao.getBookById;
import static library.dao.BorrowDao.*;
import static library.dao.UserDao.getUserById;

public class BorrowService implements IBorrowService {

    @Override
    public void addBorrowRecord(String bookId, String userId) {
        Validator.validateUserId(userId);

        User user = UserDao.getUserById(userId);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Book book = BookDao.getBookById(bookId);
        if (book == null) {
            throw new CustomException(ErrorCode.BOOK_NOT_FOUND);
        }

        Validator.validateBorrow(bookId, user.getState(), book.getState(), userId);
        BorrowRecord borrow = BorrowRecord.createBorrow(bookId, userId);
        BorrowDao.add(borrow);
        book.markAsUnavailable();
        user.incrementBorrowCount();
    }

    @Override
    public Result<BorrowRecord> addBorrowRecordWithResult(String bookId, String userId) {
        try {
            Validator.validateUserId(userId);

            User user = UserDao.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在", 404);
            }

            Book book = BookDao.getBookById(bookId);
            if (book == null) {
                return Result.error("图书不存在", 404);
            }

            if ("ABNORMAL".equals(user.getState()) || "DISABLE".equals(user.getState())) {
                return Result.error("账户已冻结，无法借阅", 403);
            }
            if ("UNAVAILABLE".equals(book.getState())) {
                return Result.error("图书已被借出", 403);
            }
            if (user.getBorrowCounted() >= user.getMaxBorrowCount()) {
                return Result.error("超过最大借阅数量", 403);
            }

            BorrowRecord borrow = BorrowRecord.createBorrow(bookId, userId);
            BorrowDao.add(borrow);
            book.markAsUnavailable();
            user.incrementBorrowCount();

            return Result.success("借阅成功", borrow);
        } catch (CustomException e) {
            return Result.fromException(e);
        } catch (Exception e) {
            return Result.error("系统错误：" + e.getMessage(), 500);
        }
    }

    @Override
    public void deleteBorrowRecord(String bookId, String userId) {
        if (bookId == null) {
            throw new CustomException(ErrorCode.INVALID_BOOK_ID);
        }

        if (!BorrowDao.exists(bookId, userId)) {
            throw new CustomException(ErrorCode.BORROW_NOT_FOUND);
        }
        deleteRecord(bookId);
        User user = getUserById(userId);
        if (user != null) {
            user.decrementBorrowCount();
        }
        Book book = getBookById(bookId);
        if (book != null) {
            book.markAvailable();
        }
    }

    @Override
    public Result<Boolean> deleteBorrowRecordWithResult(String bookId, String userId) {
        try {
            if (bookId == null) {
                return Result.error("图书ID不能为空", 400);
            }

            if (!BorrowDao.exists(bookId, userId)) {
                return Result.error("借阅记录不存在", 404);
            }

            deleteRecord(bookId);
            User user = getUserById(userId);
            if (user != null) {
                user.decrementBorrowCount();
            }
            Book book = getBookById(bookId);
            if (book != null) {
                book.markAvailable();
            }

            return Result.success("归还成功", true);
        } catch (CustomException e) {
            return Result.fromException(e);
        } catch (Exception e) {
            return Result.error("系统错误：" + e.getMessage(), 500);
        }
    }

    @Override
    public Map<String, String> getOverdueBooks() {
        Map<String, String> overdueMap = new HashMap<>();
        LocalDate now = LocalDate.now();
        for (Map.Entry<LocalDate, List<String>> entry : dueDateMap.entrySet()) {
            LocalDate dueDate = entry.getKey();
            if (dueDate.isBefore(now)) {
                for (String bookId : entry.getValue()) {
                    BorrowRecord record = borrowMap.get(bookId);
                    if (record != null && record.getReturnTime() == null) {
                        overdueMap.put(bookId, record.getUserId());
                    }
                }
            }
        }
        return overdueMap;
    }

    @Override
    public Result<Map<String, String>> getOverdueBooksWithResult() {
        try {
            return Result.success(getOverdueBooks());
        } catch (Exception e) {
            return Result.error("获取逾期图书失败：" + e.getMessage(), 500);
        }
    }

    @Override
    public String remainTime(String bookId) {
        BorrowRecord record = getRecordById(bookId);
        if (record == null) {
            return "无借阅记录";
        }
        LocalDate dueTime = record.getDueTime();
        LocalDate now = LocalDate.now();
        long days = ChronoUnit.DAYS.between(now, dueTime);

        if (days > 0) {
            return "剩余 " + days + " 天到期";
        } else if (days == 0) {
            return "今天到期";
        } else {
            return "已逾期 " + Math.abs(days) + " 天";
        }
    }

    @Override
    public Result<String> remainTimeWithResult(String bookId) {
        try {
            BorrowRecord record = getRecordById(bookId);
            if (record == null) {
                return Result.error("无借阅记录", 404);
            }

            LocalDate dueTime = record.getDueTime();
            LocalDate now = LocalDate.now();
            long days = ChronoUnit.DAYS.between(now, dueTime);

            if (days > 0) {
                return Result.success("剩余 " + days + " 天到期");
            } else if (days == 0) {
                return Result.success("今天到期");
            } else {
                return Result.success("已逾期 " + Math.abs(days) + " 天");
            }
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage(), 500);
        }
    }

    @Override
    public String timeDetail(String bookId) {
        BorrowRecord record = getRecordById(bookId);
        if (record == null) {
            throw new CustomException(ErrorCode.BORROW_NOT_FOUND);
        }
        return "出借时间：" + record.getBorrowTime() + "\n截止时间：" + record.getDueTime();
    }

    @Override
    public Result<String> timeDetailWithResult(String bookId) {
        try {
            BorrowRecord record = getRecordById(bookId);
            if (record == null) {
                return Result.error("借阅记录不存在", 404);
            }
            return Result.success("出借时间：" + record.getBorrowTime() + "\n截止时间：" + record.getDueTime());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage(), 500);
        }
    }

    @Override
    public Result<List<BorrowRecord>> getUserBorrowRecords(String userId) {
        try {
            List<BorrowRecord> records = userBorrowMap.get(userId);
            if (records == null || records.isEmpty()) {
                return Result.success(new ArrayList<>());
            }
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("获取借阅记录失败：" + e.getMessage(), 500);
        }
    }
}