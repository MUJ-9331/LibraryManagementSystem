package library.util;

import library.entity.User;
import library.exception.CustomException;
import library.exception.ErrorCode;

import static library.dao.UserDao.getUserById;

public class Validator {

    private static void notEmpty(String str, ErrorCode errorCode) {
        if (str == null || str.trim().isEmpty()) {
            throw new CustomException(errorCode);
        }
    }

    public static void validateUserName(String name) {
        notEmpty(name, ErrorCode.INVALID_USER_NAME);
    }

    public static void validateUserId(String userId) {
        notEmpty(userId, ErrorCode.INVALID_USER_ID);
    }

    public static void validateUserId(String userId, User.userRole role) {
        validateUserId(userId);
        switch (role) {
            case STUDENT:
                if (!userId.matches("^\\d{10}$")) {
                    throw new CustomException(ErrorCode.INVALID_USER_ID);
                }
                break;
            case TEACHER:
                if (!userId.matches("^\\d{8}$")) {
                    throw new CustomException(ErrorCode.INVALID_USER_ID);
                }
                break;
            case ADMIN:
                if (!userId.matches("^\\d{1,10}$")) {
                    throw new CustomException(ErrorCode.INVALID_USER_ID);
                }
                break;
        }
    }

    public static void validateUser(String name, String userId, User.userRole role) {
        validateUserName(name);
        validateUserId(userId, role);
    }

    public static void validateISBN(String isbn) {
        notEmpty(isbn, ErrorCode.INVALID_ISBN);
        String cleaned = isbn.replace("-", "");
        if (!cleaned.matches("^\\d{9}[\\dX]$|^\\d{13}$")) {
            throw new CustomException(ErrorCode.INVALID_ISBN);
        }
    }

    public static void validateTitle(String title) {
        notEmpty(title, ErrorCode.INVALID_TITLE);
    }

    public static void validatePrice(double price) {
        if (price <= 0) {
            throw new CustomException(ErrorCode.INVALID_PRICE);
        }
    }

    public static void validateBookId(String bookId) {
        notEmpty(bookId, ErrorCode.INVALID_BOOK_ID);
        if (!bookId.matches("^[\\u4e00-\\u9fa5a-zA-Z]+-\\d{3}$")) {
            throw new CustomException(ErrorCode.INVALID_BOOK_ID);
        }
    }

    public static void validateBook(String ISBN, String title, double price) {
        validateISBN(ISBN);
        validateTitle(title);
        validatePrice(price);
    }

    public static void validateBorrow(String bookId, String userState, String bookState, String userId) {
        if ("ABNORMAL".equals(userState) || "DISABLE".equals(userState)) {
            throw new CustomException(ErrorCode.USER_FROZEN);
        }

        if ("UNAVAILABLE".equals(bookState)) {
            throw new CustomException(ErrorCode.BOOK_ALREADY_BORROWED);
        }

        int num = getUserById(userId).getBorrowCounted();
        if (num >= getUserById(userId).getMaxBorrowCount()) {
            throw new CustomException(ErrorCode.USER_LIMIT_EXCEED);
        }
    }

    public static void validateRange(int value, int min, int max, ErrorCode errorCode) {
        if (value < min || value > max) {
            throw new CustomException(errorCode);
        }
    }

    public static int validateMenuChoice(String input, int min, int max) {
        try {
            int choice = Integer.parseInt(input);
            if (choice < min || choice > max) {
                throw new CustomException(ErrorCode.INVALID_INPUT);
            }
            return choice;
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }
}