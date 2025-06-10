
package logic;

/**
 * Represents the history of a book loan in the library system.
 * Contains details about the loan such as book ID, book name, loan date, return date, late status, and other information.
 */
public class BookLoanHistory {
    private int bookId; // The ID of the book
    private String bookName; // The name of the book
    private String loanDate; // The date the book was loaned
    private String returnDate; // The date the book is due to be returned
    private String isLate; // Indicates if the book was returned late
    private String other; // Other relevant information about the loan

    /**
     * Constructs a new BookLoanHistory with the specified details.
     *
     * @param bookId the ID of the book
     * @param bookName the name of the book
     * @param loanDate the date the book was loaned
     * @param returnDate the date the book is due to be returned
     * @param isLate indicates if the book was returned late
     * @param other other relevant information about the loan
     */
    public BookLoanHistory(int bookId, String bookName, String loanDate, String returnDate, String isLate, String other) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.isLate = isLate;
        this.other = other;
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
     * Gets the late status of the book return.
     *
     * @return the late status of the book return
     */
    public String getIsLate() {
        return isLate;
    }

    /**
     * Sets the late status of the book return.
     *
     * @param isLate the late status of the book return
     */
    public void setIsLate(String isLate) {
        this.isLate = isLate;
    }

    /**
     * Gets other relevant information about the loan.
     *
     * @return other relevant information about the loan
     */
    public String getOther() {
        return other;
    }

    /**
     * Sets other relevant information about the loan.
     *
     * @param other other relevant information about the loan
     */
    public void setOther(String other) {
        this.other = other;
    }
}
