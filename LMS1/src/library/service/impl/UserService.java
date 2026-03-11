package library.service.impl;

import library.dao.UserDao;
import library.entity.User;
import library.exception.CustomException;
import library.service.IUserService;
import library.util.Result;
import library.util.Validator;

import java.util.Map;

import static library.dao.UserDao.getUserById;
import static library.dao.UserDao.userMap;

public class UserService implements IUserService {

    @Override
    public void addUser(String name, User.userRole role, String userId) {
        Validator.validateUser(name, userId, role);
        User user = User.createUser(name, userId, role);
        UserDao.add(user);
    }

    @Override
    public Result<User> addUserWithResult(String name, User.userRole role, String userId) {
        try {
            Validator.validateUser(name, userId, role);

            if (UserDao.exists(userId)) {
                return Result.error("用户已存在", 1001);
            }

            User user = User.createUser(name, userId, role);
            UserDao.add(user);
            return Result.success("注册成功", user);
        } catch (CustomException e) {
            return Result.fromException(e);
        } catch (Exception e) {
            return Result.error("系统错误：" + e.getMessage(), 500);
        }
    }

    @Override
    public Result<User> getUser(String userId) {
        try {
            User user = UserDao.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在", 404);
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage(), 500);
        }
    }

    public static void overdueLabelStatic() {
        for (User user : userMap.values()) {
            if (!"DISABLE".equals(user.getState())) {
                user.markNormal();
            }
        }
        BorrowService borrowService = new BorrowService();
        Map<String, String> overdueMap = borrowService.getOverdueBooks();
        for (String userId : overdueMap.values()) {
            User user = getUserById(userId);
            if (user != null) {
                user.markAbnormal();
            }
        }
    }

    @Override
    public void overdueLabel() {
        overdueLabelStatic();
    }

    @Override
    public Result<Void> overdueLabelWithResult() {
        try {
            overdueLabelStatic();
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("逾期标记失败：" + e.getMessage(), 500);
        }
    }

    public static User.userRole getRoleById(String userId) {
        User user = getUserById(userId);
        return (user != null) ? user.getRole() : null;
    }
}