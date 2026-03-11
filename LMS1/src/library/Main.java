package library;

import library.dao.BookDao;
import library.entity.*;
import library.exception.CustomException;
import library.service.impl.BorrowService;
import library.service.impl.BookService;
import library.service.impl.UserService;
import library.util.Result;

import static library.dao.BorrowDao.userBorrowMap;
import static library.dao.UserDao.exists;
import static library.dao.UserDao.getUserById;
import static library.service.impl.UserService.overdueLabelStatic;
import static library.util.Authentication.login;
import static library.util.Authentication.stock;
import static library.util.FilesManager.*;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            initSystem();
            overdueLabelStatic();

            Scanner sc = new Scanner(System.in);
            BorrowService borrowService = new BorrowService();
            BookService bookService = new BookService();
            UserService userService = new UserService();

            System.out.println("欢迎使用图书系统");

            while (true) {
                try {
                    System.out.println("1.注册 2.登录 3.退出");

                    int ins;
                    try {
                        ins = sc.nextInt();
                        sc.nextLine();
                    } catch (Exception e) {
                        System.out.println("输入错误，请重新输入数字指令");
                        //重置Scanner
                        sc = new Scanner(System.in);
                        continue;
                    }

                    if (ins == 1) {
                        login(1);
                        System.out.println("注册成功！");
                        outfitSystem();

                    } else if (ins == 2) {
                        try {
                            User user = login(2);
                            if (user == null) {
                                continue;
                            }

                        System.out.println("登录成功！欢迎 " + user.getName());
                        System.out.println("请选择业务");

                        switch (user.getRole()) {
                            case STUDENT:
                            case TEACHER:
                                handleUserMenu(sc, user, borrowService, bookService);
                                break;
                            case ADMIN:
                                handleAdminMenu(sc, user, borrowService, bookService, userService);
                                break;
                        }
                    } catch (IllegalArgumentException e) {
                            System.out.println("职业输入错误");
                        }

                    } else if (ins == 3) {
                        System.out.println("感谢使用，再见!!!");
                        break;
                    } else {
                        System.out.println("错误的指令，请重输");
                    }

                } catch (CustomException e) {
                    System.out.println("操作失败：" + e.getMessage());
                } catch (Exception e) {
                    System.out.println("系统错误：" + e.getMessage());
                    sc = new Scanner(System.in);
                }
            }

            sc.close();
            outfitSystem();

        } catch (CustomException e) {
            System.out.println("系统初始化失败：" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("严重系统错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleUserMenu(Scanner sc, User user, BorrowService borrowService, BookService bookService) {
        USER_MENU:
        while (true) {
            try {
                System.out.println("1.借阅 2.归还 3.查看借阅图书 4.退出");
                int ins = sc.nextInt();
                sc.nextLine();

                if (ins == 1) {
                    Result<List<Book>> booksResult = bookService.getAllBooks();
                    if (booksResult.isSuccess()) {
                        System.out.println("\n=== 可借阅图书 ===");
                        for (Book book : booksResult.getData()) {
                            System.out.println(book.getBookId() + " : " + book.getState());
                        }
                    }

                    System.out.println("请输入想要借阅图书的编号");
                    String bookId = sc.nextLine();

                    Result<BorrowRecord> result = borrowService.addBorrowRecordWithResult(bookId, user.getUserId());
                    if (result.isSuccess()) {
                        System.out.println("借阅成功！");
                        outfitSystem();
                    } else {
                        System.out.println("借阅失败：" + result.getMessage());
                    }

                } else if (ins == 2) {
                    System.out.println("请输入归还的图书编号");
                    String bookId = sc.nextLine();

                    Result<Boolean> result = borrowService.deleteBorrowRecordWithResult(bookId, user.getUserId());
                    if (result.isSuccess()) {
                        System.out.println("归还成功！");
                        outfitSystem();
                    } else {
                        System.out.println("归还失败：" + result.getMessage());
                    }

                } else if (ins == 3) {
                    Result<List<BorrowRecord>> recordsResult = borrowService.getUserBorrowRecords(user.getUserId());
                    if (recordsResult.isSuccess()) {
                        List<BorrowRecord> records = recordsResult.getData();
                        if (records.isEmpty()) {
                            System.out.println("您还没有借阅图书哦 ");
                        } else {
                            System.out.println("\n=== 您的借阅记录 ===");
                            for (BorrowRecord record : records) {
                                Result<String> timeResult = borrowService.remainTimeWithResult(record.getBookId());
                                String remainTime = timeResult.isSuccess() ? timeResult.getData() : "未知";
                                System.out.println("图书编号：" + record.getBookId() +
                                        " | 应还日期：" + record.getDueTime() +
                                        " | " + remainTime);
                            }
                        }
                    }
                } else if (ins == 4) {
                    System.out.println("感谢使用，再见");
                    break USER_MENU;
                } else {
                    System.out.println("错误的指令，请重输");
                }
            } catch (Exception e) {
                System.out.println("操作失败：" + e.getMessage());
                sc = new Scanner(System.in);
            }
        }
    }

    private static void handleAdminMenu(Scanner sc, User user, BorrowService borrowService, BookService bookService, UserService userService) {
        ADMIN_MENU:
        while (true) {
            try {
                System.out.println("1.添加书籍 2.修正书名 3.文件查阅 4.异常标记 5.退出");
                int ins = sc.nextInt();
                sc.nextLine();

                if (ins == 1) {
                    stock();
                    outfitSystem();

                } else if (ins == 2) {
                    System.out.println("请输入要修正的图书编号：");
                    String bookId = sc.nextLine();

                    Result<Book> bookResult = bookService.getBook(bookId);
                    if (!bookResult.isSuccess()) {
                        System.out.println(bookResult.getMessage());
                        continue;
                    }

                    System.out.println("请输入新的书名：");
                    String title = sc.nextLine();
                    if (title == null || title.trim().isEmpty()) {
                        System.out.println("书名不能为空");
                        continue;
                    }

                    bookResult.getData().setTitle(title);
                    System.out.println("书名修改成功！");
                    outfitSystem();

                } else if (ins == 3) {
                    System.out.println("请输入你希望查询的文件：");
                    System.out.println("1.user.dat  2.book.dat  3.borrow.dat  4.AllDataFiles");
                    try {
                        ins = sc.nextInt();
                        sc.nextLine();
                        switch (ins) {
                            case 1:
                                printFile("library_data/user.dat");
                                break;
                            case 2:
                                printFile("library_data/book.dat");
                                break;
                            case 3:
                                printFile("library_data/borrow.dat");
                                break;
                            case 4:
                                printAllDataFiles();
                                break;
                            default:
                                System.out.println("错误的指令，请重输");
                        }
                    } catch (Exception e) {
                        System.out.println("输入错误，请重新输入");
                        sc = new Scanner(System.in);
                    }

                } else if (ins == 4) {
                    System.out.println("请输入要禁用的用户ID：");
                    String userId = sc.nextLine();

                    Result<User> userResult = userService.getUser(userId);
                    if (userResult.isSuccess()) {
                        userResult.getData().markDisable();
                        System.out.println("用户已禁用");
                        outfitSystem();
                    } else {
                        System.out.println("还没有这个用户哦 👤");
                    }

                } else if (ins == 5) {
                    System.out.println("感谢使用，再见");
                    break ADMIN_MENU;
                } else {
                    System.out.println("错误的指令，请重输");
                }
            } catch (CustomException e) {
                System.out.println("操作失败：" + e.getMessage());
            } catch (Exception e) {
                System.out.println("系统错误：" + e.getMessage());
                sc = new Scanner(System.in);
            }
        }
    }
}