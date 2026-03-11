package library.service;

import library.entity.BorrowRecord;
import library.util.Result;
import java.util.Map;
import java.util.List;

public interface IBorrowService {
    void addBorrowRecord(String bookId, String userId);
    void deleteBorrowRecord(String bookId, String userId);
    Map<String, String> getOverdueBooks();
    String remainTime(String bookId);
    String timeDetail(String bookId);

    Result<BorrowRecord> addBorrowRecordWithResult(String bookId, String userId);
    Result<Boolean> deleteBorrowRecordWithResult(String bookId, String userId);
    Result<Map<String, String>> getOverdueBooksWithResult();
    Result<String> remainTimeWithResult(String bookId);
    Result<String> timeDetailWithResult(String bookId);
    Result<List<BorrowRecord>> getUserBorrowRecords(String userId);
}