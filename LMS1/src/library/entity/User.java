package library.entity;

public class User extends library.entity.Person {

    public enum userRole {
        STUDENT(30, 5),
        TEACHER(60, 10),
        ADMIN(Integer.MAX_VALUE, Integer.MAX_VALUE);

        private final int maxTime;
        private final int maxCount;

        userRole(int maxTime, int maxCount) {
            this.maxTime = maxTime;
            this.maxCount = maxCount;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public int getMaxTime() {
            return maxTime;
        }
    }

    private final userRole role;
    private String state;
    private int borrowCounted;

    public static User createUser(String name, String userId, userRole role) {
        return new User(name, userId, role, "NORMAL", 0);
    }

    private User(String name, String userId, userRole role, String state, int borrowCounted) {
        super(name, userId);
        this.role = role;
        this.state = state;
        this.borrowCounted = borrowCounted;
    }

    public static User parseLineToUser(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 5) return null;
        return new User(
                parts[0],
                parts[1],
                userRole.valueOf(parts[2]),
                parts[3],
                Integer.parseInt(parts[4])
        );
    }

    public void incrementBorrowCount() {
        this.borrowCounted++;
    }

    public void decrementBorrowCount() {
        if (this.borrowCounted > 0) {
            this.borrowCounted--;
        }
    }

    public userRole getRole() {
        return role;
    }

    public String getState() {
        return state;
    }

    public void markAbnormal() {
        this.state = "ABNORMAL";
    }

    public void markNormal() {
        this.state = "NORMAL";
    }

    public void markDisable() {
        this.state = "DISABLE";
    }

    public int getBorrowCounted() {
        return borrowCounted;
    }

    public int getMaxBorrowCount() {
        return this.role.getMaxCount();
    }

    public int getMaxBorrowTime() {
        return this.role.getMaxTime();
    }
}