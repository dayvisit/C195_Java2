package davis.c195.DAO;

import davis.c195.helpers.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import davis.c195.model.Appointment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ReportsDAO {

    /**
     * Gets appointment counts by type and month
     * @return Map with type-month key and count value
     * @throws SQLException if database error occurs
     */
    public static Map<String, Integer> getAppointmentsByTypeAndMonth() throws SQLException {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT Type, MONTH(Start) as Month, COUNT(*) as Count " +
                "FROM appointments " +
                "GROUP BY Type, MONTH(Start)";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String type = rs.getString("Type");
                int month = rs.getInt("Month");
                int count = rs.getInt("Count");
                counts.put(type + "-" + month, count);
            }
        }
        return counts;
    }

    /**
     * Gets schedule for a specific contact
     * @param contactId contact to get schedule for
     * @return list of appointments for the contact
     * @throws SQLException if database error occurs
     */
    public static ObservableList<Appointment> getContactSchedule(int contactId) throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments WHERE Contact_ID = ? ORDER BY Start";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ps.setInt(1, contactId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getInt("Appointment_ID"),
                        rs.getInt("Customer_ID"),
                        rs.getInt("User_ID"),
                        rs.getInt("Contact_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Type"),
                        rs.getTimestamp("Start").toLocalDateTime(),
                        rs.getTimestamp("End").toLocalDateTime()
                ));
            }
        }
        return appointments;
    }

    /**
     * Gets total appointments by customer
     * Custom report that shows how many appointments each customer has
     * @return Map with customer ID and their appointment count
     * @throws SQLException if database error occurs
     */
    public static Map<Integer, Integer> getAppointmentsByCustomer() throws SQLException {
        Map<Integer, Integer> counts = new HashMap<>();
        String sql = "SELECT Customer_ID, COUNT(*) as Count " +
                "FROM appointments " +
                "GROUP BY Customer_ID";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int customerId = rs.getInt("Customer_ID");
                int count = rs.getInt("Count");
                counts.put(customerId, count);
            }
        }
        return counts;
    }
}