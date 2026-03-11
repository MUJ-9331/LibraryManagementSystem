package library.exception;

public enum ErrorCode {
    // 用户模块 1000-1999
    USER_ALREADY_EXISTS(1001, "用户已存在"),

    // 图书模块 2000-2999
    INVALID_PRICE(2001, "价格必须为正数"),

    // 借阅模块 3000-3999
    USER_NOT_FOUND(3001, "用户不存在"),
    USER_LIMIT_EXCEED(3003, "超过最大借阅数量"),
    USER_FROZEN(3004, "账户已冻结, 请归还逾期图书后再进行借阅"),
    BOOK_NOT_FOUND(3005, "图书不存在"),
    BOOK_NOT_AVAILABLE(3006, "图书不可借阅"),
    BOOK_ALREADY_BORROWED(3007, "图书已被借出"),
    BORROW_NOT_FOUND(3008, "借阅记录不存在"),

    // 输入验证 4000-4999
    INVALID_USER_NAME(4001, "用户名不能为空"),
    INVALID_USER_ROLE(4002, "未输入身份"),
    INVALID_USER_ID(4003, "用户ID不正确,学生ID为10位数字，教师ID为8位数字"),
    INVALID_BOOK_ID(4004, "图书ID不正确,图书ID格式：书名-三位数字"),
    INVALID_ISBN(4005, "ISBN是10或13位(带-)数字"),
    INVALID_TITLE(4006, "书名不能为空"),
    INVALID_INPUT(4007, "错误的指令"),

    // 文件 5000-5999
    FILE_NOT_FOUND(5001, "文件不存在");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}