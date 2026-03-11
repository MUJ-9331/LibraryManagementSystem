package library.entity;

public class Person {
    private String name;
    private String userId;

    public Person(String name, String id) {
        this.name = name;
        this.userId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}