package library.service;

import library.entity.User;
import library.util.Result;

public interface IUserService {
    void addUser(String name, User.userRole role, String userId);
    void overdueLabel();

    Result<User> addUserWithResult(String name, User.userRole role, String userId);
    Result<User> getUser(String userId);
    Result<Void> overdueLabelWithResult();
}