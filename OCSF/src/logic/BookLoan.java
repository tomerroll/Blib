
package logic;

/**
 * Represents a book loan in the library system.
 * Contains details about the loan such as book ID, loan date, return date, book name, librarian user, and extension date.
 */
public class BookLoan {
    private int bookId; // The ID of the book
    private String loanDate; // The date the book was loaned
    private String returnDate; // The date the book is due to be returned
    private String bookName; // The name of the book
    private String librarianUser; // The user ID of the librarian who processed the loan
    private String extendedAt; // The date the loan was extended

    /**
     * Constructs a new BookLoan with the specified details.
     *
     * @param bookId the ID of the book
     * @param loanDate the date the book was loaned
     * @param returnDate the date the book is due to be returned
     * @param bookName the name of the book
     * @param librarianUser the user ID of the librarian who processed the loan
     * @param extendedAt the date the loan was extended
     */
    public BookLoan(int bookId, String loanDate, String returnDate, String bookName, String librarianUser, String extendedAt) {
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.bookName = bookName;
        this.librarianUser = librarianUser;
        this.extendedAt = extendedAt;
    }

    /**
     * Gets the ID of the book.
     *
     * @return the ID of the book
     */
    public int getBookId() {
        return bookId;
    }

    /**
     * Sets the ID of the book.
     *
     * @param bookId the ID of the book
     */
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    /**
     * Gets the date the book was loaned.
     *
     * @return the date the book was loaned
     */
    public String getLoanDate() {
        return loanDate;
    }

    /**
     * Sets the date the book was loaned.
     *
     * @param loanDate the date the book was loaned
     */
    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    /**
     * Gets the date the book is due to be returned.
     *
     * @return the date the book is due to be returned
     */
    public String getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the date the book is due to be returned.
     *
     * @param returnDate the date the book is due to be returned
     */
    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Gets the name of the book.
     *
     * @return the name of the book
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * Sets the name of the book.
     *
     * @param bookName the name of the book
     */
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    /**
     * Gets the user ID of the librarian who processed the loan.
     *
     * @return the user ID of the librarian who processed the loan
     */
    public String getLibrarianUser() {
        return librarianUser;
    }

    /**
     * Sets the user ID of the librarian who processed the loan.
     *
     * @param librarianUser the user ID of the librarian who processed the loan
     */
    public void setLibrarianUser(String librarianUser) {
        this.librarianUser = librarianUser;
    }

    /**
     * Gets the date the loan was extended.
     *
     * @return the date the loan was extended
     */
    public String getExtendedAt() {
        return extendedAt;
    }

    /**
     * Sets the date the loan was extended.
     *
     * @param extendedAt the date the loan was extended
     */
    public void setExtendedAt(String extendedAt) {
        this.extendedAt = extendedAt;
    }
}
