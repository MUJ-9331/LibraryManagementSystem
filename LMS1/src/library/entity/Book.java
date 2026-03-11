package library.entity;

public class Book {
    private String ISBN;
    private String title;
    private double price;
    private final String bookId;
    private String state;

    public static Book createBook(String ISBN, String title, double price, String bookId) {
        return new Book(ISBN, title, price, bookId, "AVAILABLE");
    }

    private Book(String ISBN, String title, double price, String bookId, String state) {
        this.ISBN = ISBN;
        this.title = title;
        this.price = price;
        this.bookId = bookId;
        this.state = state;
    }

    public static Book parseLineToBook(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 5) return null;
        return new Book(
                parts[0],
                parts[1],
                Double.parseDouble(parts[2]),
                parts[3],
                parts[4]
        );
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBookId() {
        return bookId;
    }

    public String getState() {
        return state;
    }

    public void markAsUnavailable() {
        this.state = "UNAVAILABLE";
    }

    public void markAvailable() {
        this.state = "AVAILABLE";
    }
}