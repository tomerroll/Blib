
package logic;

import java.io.Serializable;

/**
 * Represents a user in the library system.
 * Contains details about the user such as user ID, username, password hash, first name, last name, phone number, email, subscription status, and role.
 */
public class User implements Serializable {
    private int userId; // The ID of the user
    private String username; // The username of the user
    private String passwordHash; // The hashed password of the user
    private String firstName; // The first name of the user
    private String lastName; // The last name of the user
    private String phoneNumber; // The phone number of the user
    private String email; // The email address of the user
    private String subscriptionStatus; // The subscription status of the user (Active or Frozen)
    private String role; // The role of the user (Librarian or User)

    /**
     * Constructs a new User with default values.
     */
    public User() {
    }

    /**
     * Constructs a new User with the specified details.
     *
     * @param userId the ID of the user
     * @param username the username of the user
     * @param passwordHash the hashed password of the user
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param phoneNumber the phone number of the user
     * @param email the email address of the user
     * @param subscriptionStatus the subscription status of the user (Active or Frozen)
     * @param role the role of the user (Librarian or User)
     */
    public User(int userId, String username, String passwordHash, String firstName, String lastName,
                String phoneNumber, String email, String subscriptionStatus, String role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.subscriptionStatus = subscriptionStatus;
        this.role = role;
    }

    /**
     * Gets the ID of the user.
     *
     * @return the ID of the user
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user.
     *
     * @param userId the ID of the user
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the hashed password of the user.
     *
     * @return the hashed password of the user
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the hashed password of the user.
     *
     * @param passwordHash the hashed password of the user
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Gets the first name of the user.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName the first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the user.
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName the last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the phone number of the user.
     *
     * @return the phone number of the user
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber the phone number of the user
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the email address of the user.
     *
     * @return the email address of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the email address of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the subscription status of the user.
     *
     * @return the subscription status of the user
     */
    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    /**
     * Sets the subscription status of the user.
     *
     * @param subscriptionStatus the subscription status of the user
     */
    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    /**
     * Gets the role of the user.
     *
     * @return the role of the user
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role the role of the user
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns a string representation of the User.
     *
     * @return a string representation of the User
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", subscriptionStatus='" + subscriptionStatus + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
