package library.util;

import library.dao.UserDao;
import library.entity.User;
import library.exception.CustomException;
import library.exception.ErrorCode;
import library.service.impl.BookService;
import library.service.impl.UserService;

import java.util.Scanner;

public class Authentication {

    public static User login(int ins) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入姓名，职业和对应的学号/教师工号/教职工号（中间用空格隔开）");
        System.out.println("职业可选：STUDENT, TEACHER, ADMIN");

        try {
            String[] information = sc.nextLine().split(" +");
            if (information.length != 3) {
                throw new IllegalArgumentException("输入格式错误！请按照格式输入：姓名 角色 编号");
            }

            String name = information[0];
            String roleStr = information[1].toUpperCase();
            String userId = information[2];

            User.userRole role;
            try {
                role = User.userRole.valueOf(roleStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("错误的职业输入！请输入：STUDENT, TEACHER, ADMIN");
            }

            if (ins == 1) {
                UserService userService = new UserService();
                userService.addUser(name, role, userId);
                return null;
            }

            if (ins == 2) {
                Validator.validateUserId(userId, role);
                User user = UserDao.getUserById(userId);
                if (user == null || !user.getName().equals(name) || user.getRole() != role) {
                    throw new CustomException(ErrorCode.USER_NOT_FOUND);
                }
                return user;
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("输入出错：" + e.getMessage());
        }
        return null;
    }

    public static void stock() {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入图书的ISBN码，书名和书籍的价格（中间用空格隔开）");

        try {
            String[] information = sc.nextLine().split(" +");
            if (information.length != 3) {
                throw new IllegalArgumentException("输入格式错误！请按照格式输入：ISBN 书名 价格");
            }

            String ISBN = information[0];
            String title = information[1];
            double price = Double.parseDouble(information[2]);

            BookService bookService = new BookService();
            bookService.addBook(ISBN, title, price);
            System.out.println("图书添加成功！");
        } catch (Exception e) {
            System.out.println("输入出错：" + e.getMessage());
        }
    }
}