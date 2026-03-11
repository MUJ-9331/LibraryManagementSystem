package library.dao;

import library.entity.User;
import library.exception.CustomException;
import library.exception.ErrorCode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static library.entity.User.parseLineToUser;

public class UserDao {
    public static Map<String, User> userMap = new HashMap<>();
    private static Map<String, String> idToNameMap = new HashMap<>();
    private static final String USER_FILE = "library_data/user.dat";

    public static boolean exists(String name, User.userRole role, String userId) {
        User user = getUserById(userId);
        return user != null && user.getRole() == role && user.getName().equals(name);
    }

    public static boolean exists(String userId) {
        return userMap.containsKey(userId);
    }

    public static void add(User user) {
        userMap.put(user.getUserId(), user);
        System.out.println(user.getName() + "注册成功！");
    }

    public static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User user : userMap.values()) {
                writer.write(formatUserToLine(user));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("保存用户数据失败：" + e.getMessage());
        }
    }

    public static void loadFromFile() {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                System.out.println("创建新数据文件: " + USER_FILE);
                return;
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND);
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = parseLineToUser(line);
                if (user != null) {
                    userMap.put(user.getUserId(), user);
                    idToNameMap.put(user.getName(), user.getUserId());
                }
            }
        } catch (IOException e) {
            System.err.println("加载失败：" + e.getMessage());
        }
    }

    private static String formatUserToLine(User user) {
        return String.join("|",
                user.getName(),
                user.getUserId(),
                user.getRole().name(),
                user.getState(),
                String.valueOf(user.getBorrowCounted())
        );
    }

    public static User getUserById(String userId) {
        return userMap.get(userId);
    }
}