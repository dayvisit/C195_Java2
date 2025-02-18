package davis.c195.DAO;

import davis.c195.helpers.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import davis.c195.model.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object class for Contact-related database operations
 */
public class ContactDAO {

    /**
     * Retrieves all contacts from the database
     * @return ObservableList of Contact objects
     * @throws SQLException if database error occurs
     */
    public static ObservableList<Contact> getAllContacts() throws SQLException {
        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        String sql = "SELECT Contact_ID, Contact_Name, Email FROM contacts";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Contact contact = new Contact(
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name"),
                        rs.getString("Email")
                );
                contacts.add(contact);
            }
        }
        return contacts;
    }

    /**
     * Gets a contact by their ID
     * @param contactId the ID to search for
     * @return Contact object if found, null if not
     * @throws SQLException if database error occurs
     */
    public static Contact getContact(int contactId) throws SQLException {
        String sql = "SELECT Contact_ID, Contact_Name, Email FROM contacts WHERE Contact_ID = ?";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ps.setInt(1, contactId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Contact(
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name"),
                        rs.getString("Email")
                );
            }
        }
        return null;
    }
}