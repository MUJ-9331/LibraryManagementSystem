package library.util;

import library.dao.BookDao;
import library.dao.BorrowDao;
import library.dao.UserDao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FilesManager {

    public static void printAllDataFiles() {
        System.out.println("\n========== 打印所有数据文件 ==========");
        printFile("library_data/user.dat");
        printFile("library_data/book.dat");
        printFile("library_data/borrow.dat");
        System.out.println("=====================================\n");
    }

    public static void printFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("文件不存在: " + filePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            System.out.println("\n--- 文件: " + filePath + " ---");
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                System.out.printf("第%2d行: %s%n", lineNumber++, line);
            }
            System.out.println("--- 共 " + (lineNumber - 1) + " 行 ---");
        } catch (IOException e) {
            System.err.println("读取文件失败: " + filePath);
        }
    }

    public static void initSystem() {
        new File("library_data").mkdir();
        UserDao.loadFromFile();
        BookDao.loadFromFile();
        BorrowDao.loadFromFile();
    }

    public static void outfitSystem() {
        UserDao.saveToFile();
        BookDao.saveToFile();
        BorrowDao.saveToFile();
    }
}