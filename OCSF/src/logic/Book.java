
package logic;

/**
 * Represents a book in the library system.
 * Contains details about the book such as title, author, genre, description, and availability.
 */
public class Book {

    private String title; // The title of the book
    private String author; // The author of the book
    private String genre; // The genre of the book
    private String description; // A brief description of the book
    private String availableCopies; // The number of available copies of the book
    private String totalCopies; // The total number of copies of the book
    private String upcomigReturnDate; // The upcoming return date of the book
    private String location; // The location of the book in the library

    /**
     * Constructs a new Book with the specified details.
     *
     * @param title the title of the book
     * @param author the author of the book
     * @param genre the genre of the book
     * @param description a brief description of the book
     * @param availableCopies the number of available copies of the book
     * @param totalCopies the total number of copies of the book
     * @param upcomigReturnDate the upcoming return date of the book
     * @param location the location of the book in the library
     */
    public Book(String title, String author, String genre, String description, String availableCopies, String totalCopies, String upcomigReturnDate, String location) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.availableCopies = availableCopies;
        this.totalCopies = totalCopies;
        this.upcomigReturnDate = upcomigReturnDate;
        this.location = location;
    }

    /**
     * Gets the title of the book.
     *
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the author of the book.
     *
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Gets the genre of the book.
     *
     * @return the genre of the book
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Gets the description of the book.
     *
     * @return the description of the book
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the number of available copies of the book.
     *
     * @return the number of available copies of the book
     */
    public String getAvailableCopies() {
        return availableCopies;
    }

    /**
     * Gets the total number of copies of the book.
     *
     * @return the total number of copies of the book
     */
    public String getTotalCopies() {
        return totalCopies;
    }

    /**
     * Gets the upcoming return date of the book.
     *
     * @return the upcoming return date of the book
     */
    public String getUpcomigReturnDate() {
        return upcomigReturnDate;
    }

    /**
     * Gets the location of the book in the library.
     *
     * @return the location of the book in the library
     */
    public String getLocation() {
        return location;
    }
}
