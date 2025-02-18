package davis.c195.model;

import java.time.LocalDateTime;

/**
 * Represents a user in the scheduling system.
 * Maps to the USERS table in the database.
 */
public class User {
    private int userID;
    private String userName;
    private String password;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdatedBy;

    /**
     * User class constructor with essential fields
     *
     * @param userID id
     * @param userName username
     * @param password password
     */
    public User(int userID, String userName, String password) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Full constructor with all fields
     *
     * @param userID id
     * @param userName username
     * @param password password
     * @param createDate creation date
     * @param createdBy creator
     * @param lastUpdate last update time
     * @param lastUpdatedBy last updater
     */
    public User(int userID, String userName, String password, LocalDateTime createDate,
                String createdBy, LocalDateTime lastUpdate, String lastUpdatedBy) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * @return userID id
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID id
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return userName username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return password password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return createDate creation date
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate creation date
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * @return createdBy creator
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy creator
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return lastUpdate last update time
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate last update time
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return lastUpdatedBy last updater
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * @param lastUpdatedBy last updater
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}