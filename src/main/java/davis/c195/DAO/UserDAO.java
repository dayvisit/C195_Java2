package davis.c195.DAO;

import davis.c195.helpers.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import davis.c195.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Data Access Object class for User-related database operations
 */
public class UserDAO {
    private static User currentUser;

    /**
     * Gets the currently logged-in user
     * @return current User object
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the currently logged-in user
     * @param user User to set as current
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Validates user credentials against the database
     * @param username username to check
     * @param password password to check
     * @return User object if valid, null if not
     * @throws SQLException if database error occurs
     */
    public static User validateUser(String username, String password) throws SQLException {
        String sql = "SELECT User_ID, User_Name, Password FROM users WHERE User_Name = ? AND Password = ?";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("User_ID"),
                        rs.getString("User_Name"),
                        rs.getString("Password")
                );
            }
        }
        return null;
    }

    /**
     * Gets all users from the database
     * @return ObservableList of User objects
     * @throws SQLException if database error occurs
     */
    public static ObservableList<User> getAllUsers() throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT User_ID, User_Name, Password FROM users";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getInt("User_ID"),
                        rs.getString("User_Name"),
                        rs.getString("Password")
                );
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Gets a user by their ID
     * @param userId ID to search for
     * @return User object if found, null if not
     * @throws SQLException if database error occurs
     */
    public static User getUser(int userId) throws SQLException {
        String sql = "SELECT User_ID, User_Name, Password FROM users WHERE User_ID = ?";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("User_ID"),
                        rs.getString("User_Name"),
                        rs.getString("Password")
                );
            }
        }
        return null;
    }
}