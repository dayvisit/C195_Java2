package davis.c195.DAO;

import davis.c195.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import davis.c195.helpers.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data Access Object for Customer-related database operations.
 */
public class CustomerDAO {

    /**
     * Gets list of customers from database and returns
     *
     * @return ObservableList of Customer objects
     * @throws SQLException if database error occurs
     */
    public static ObservableList<Customer> getCustomerList() throws SQLException {
        ObservableList<Customer> list = FXCollections.observableArrayList();

        String sql = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM client_schedule.customers";
        PreparedStatement ps = DBConnection.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int customerID = rs.getInt("Customer_ID");
            String name = rs.getString("Customer_Name");
            String address = rs.getString("Address");
            String postal = rs.getString("Postal_Code");
            String phone = rs.getString("Phone");
            int divisionID = rs.getInt("Division_ID");

            Customer customer = new Customer(customerID, name, address, postal, phone, divisionID);
            list.add(customer);
        }
        return list;
    }

    /**
     * Insert a customer object into the database
     *
     * @param newCustomer new customer object to insert
     * @throws SQLException if database error occurs
     */
    public static void insertCustomer(Customer newCustomer) throws SQLException {
        String sql = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, " +
                "Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = DBConnection.connection.prepareStatement(sql);

        ps.setInt(1, newCustomer.getCustomerID());
        ps.setString(2, newCustomer.getCustomerName());
        ps.setString(3, newCustomer.getAddress());
        ps.setString(4, newCustomer.getPostalCode());
        ps.setString(5, newCustomer.getPhone());
        ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(7, newCustomer.getCreatedBy());
        ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(9, newCustomer.getLastUpdatedBy());
        ps.setInt(10, newCustomer.getDivisionID());

        ps.executeUpdate();
    }

    /**
     * Updates existing customer in the database
     *
     * @param customer customer to update
     * @throws SQLException if database error occurs
     */
    public static void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, " +
                "Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? " +
                "WHERE Customer_ID = ?";

        PreparedStatement ps = DBConnection.connection.prepareStatement(sql);

        ps.setString(1, customer.getCustomerName());
        ps.setString(2, customer.getAddress());
        ps.setString(3, customer.getPostalCode());
        ps.setString(4, customer.getPhone());
        ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(6, customer.getLastUpdatedBy());
        ps.setInt(7, customer.getDivisionID());
        ps.setInt(8, customer.getCustomerID());

        ps.executeUpdate();
    }

    /**
     * Deletes customer from the database
     *
     * @param customerID id of customer to delete
     * @throws SQLException if database error occurs
     */
    public static void deleteCustomer(int customerID) throws SQLException {
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement ps = DBConnection.connection.prepareStatement(sql);
        ps.setInt(1, customerID);
        ps.executeUpdate();
    }

    /**
     * Returns true if customer has no associated appointments, false if they do
     *
     * @param customerID id of customer to check
     * @return boolean indicating if customer has appointments
     * @throws SQLException if database error occurs
     */
    public static boolean appointmentListIsEmpty(int customerID) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM appointments WHERE Customer_ID = ?";
        PreparedStatement ps = DBConnection.connection.prepareStatement(sql);
        ps.setInt(1, customerID);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("count") == 0;
        }
        return true;
    }
}