package davis.c195.DAO;

import davis.c195.helpers.DBConnection;
import davis.c195.model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data Access Object for handling Appointment records in the database.
 * Contains methods for CRUD operations on appointments.
 */
public class AppointmentDAO {

    /**
     * Retrieves all appointments from the database
     * @return ObservableList of Appointment objects
     * @throws SQLException if database access error occurs
     */
    public static ObservableList<Appointment> getAllAppointments() throws SQLException {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Appointment appointment = new Appointment(
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
                );
                appointmentList.add(appointment);
            }
        }
        return appointmentList;
    }

    /**
     * Inserts a new appointment into the database
     * @param appointment The appointment to insert
     * @return true if insert was successful
     * @throws SQLException if database access error occurs
     */
    public static boolean insert(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, " +
                "Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ps.setInt(1, appointment.getAppointmentId());
            ps.setString(2, appointment.getTitle());
            ps.setString(3, appointment.getDescription());
            ps.setString(4, appointment.getLocation());
            ps.setString(5, appointment.getType());
            ps.setTimestamp(6, Timestamp.valueOf(appointment.getStart()));
            ps.setTimestamp(7, Timestamp.valueOf(appointment.getEnd()));
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, "admin"); // Replace with actual user from UserDAO.getCurrentUser().getUsername()
            ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(11, "admin"); // Replace with actual user from UserDAO.getCurrentUser().getUsername()
            ps.setInt(12, appointment.getCustomerId());
            ps.setInt(13, appointment.getUserId());
            ps.setInt(14, appointment.getContactId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Updates an existing appointment in the database
     * @param appointment The appointment to update
     * @return true if update was successful
     * @throws SQLException if database access error occurs
     */
    public static boolean update(Appointment appointment) throws SQLException {
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, " +
                "Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, " +
                "User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ps.setString(1, appointment.getTitle());
            ps.setString(2, appointment.getDescription());
            ps.setString(3, appointment.getLocation());
            ps.setString(4, appointment.getType());
            ps.setTimestamp(5, Timestamp.valueOf(appointment.getStart()));
            ps.setTimestamp(6, Timestamp.valueOf(appointment.getEnd()));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, "admin"); // Replace with actual user from UserDAO.getCurrentUser().getUsername()
            ps.setInt(9, appointment.getCustomerId());
            ps.setInt(10, appointment.getUserId());
            ps.setInt(11, appointment.getContactId());
            ps.setInt(12, appointment.getAppointmentId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Deletes an appointment from the database
     * @param appointmentId The ID of the appointment to delete
     * @return true if deletion was successful
     * @throws SQLException if database access error occurs
     */
    public static boolean delete(int appointmentId) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Gets appointments for the next 7 days
     * @return ObservableList of Appointment objects within the next week
     * @throws SQLException if database access error occurs
     */
    public static ObservableList<Appointment> getAppointmentsThisWeek() throws SQLException {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments WHERE YEARWEEK(Start, 1) = YEARWEEK(CURDATE(), 1)";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Appointment appointment = new Appointment(
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
                );
                appointmentList.add(appointment);
            }
        }
        return appointmentList;
    }

    /**
     * Gets appointments for the current month
     * @return ObservableList of Appointment objects within the current month
     * @throws SQLException if database access error occurs
     */
    public static ObservableList<Appointment> getAppointmentsThisMonth() throws SQLException {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments WHERE MONTH(Start) = MONTH(CURDATE()) AND YEAR(Start) = YEAR(CURDATE())";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Appointment appointment = new Appointment(
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
                );
                appointmentList.add(appointment);
            }
        }
        return appointmentList;
    }

    /**
     * Gets the next available appointment ID
     * @return next available appointment ID
     * @throws SQLException if database access error occurs
     */
    public static int getNextAppointmentId() throws SQLException {
        String sql = "SELECT MAX(Appointment_ID) as maxId FROM appointments";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("maxId") + 1;
            }
            return 1; // Return 1 if no appointments exist
        }
    }
}