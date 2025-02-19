package davis.c195.controller;

import davis.c195.DAO.AppointmentDAO;
import davis.c195.DAO.CustomerDAO;
import davis.c195.DAO.UserDAO;
import davis.c195.model.Appointment;
import davis.c195.model.Customer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    // Appointment Table and Columns
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> appointmentIdCol;
    @FXML private TableColumn<Appointment, String> titleCol;
    @FXML private TableColumn<Appointment, String> descriptionCol;
    @FXML private TableColumn<Appointment, String> locationCol;
    @FXML private TableColumn<Appointment, String> contactCol;
    @FXML private TableColumn<Appointment, String> typeCol;
    @FXML private TableColumn<Appointment, LocalDateTime> startCol;
    @FXML private TableColumn<Appointment, LocalDateTime> endCol;
    @FXML private TableColumn<Appointment, Integer> aptCustomerIdCol;
    @FXML private TableColumn<Appointment, Integer> userIdCol;

    // Customer Table and Columns
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> customerIdCol;
    @FXML private TableColumn<Customer, String> customerNameCol;
    @FXML private TableColumn<Customer, String> addressCol;
    @FXML private TableColumn<Customer, String> phoneCol;
    @FXML private TableColumn<Customer, String> divisionCol;
    @FXML private TableColumn<Customer, String> postalCodeCol;

    // Radio Buttons for Appointment View
    @FXML private RadioButton allAppointmentsRadio;
    @FXML private RadioButton weekRadio;
    @FXML private RadioButton monthRadio;

    // Buttons
    @FXML private Button addAppointmentBtn;
    @FXML private Button updateAppointmentBtn;
    @FXML private Button deleteAppointmentBtn;
    @FXML private Button addCustomerBtn;
    @FXML private Button updateCustomerBtn;
    @FXML private Button deleteCustomerBtn;
    @FXML private Button reportsBtn;
    @FXML private Button logoutBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize appointment table columns
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactId")); // You might want to convert this to contact name
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        aptCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Initialize customer table columns
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        divisionCol.setCellValueFactory(new PropertyValueFactory<>("divisionID")); // You might want to convert this to division name
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        // Load initial data
        refreshAppointmentTable();
        refreshCustomerTable();
    }

    // Appointment Table View Handlers
    @FXML
    void onAllAppointments(ActionEvent event) {
        try {
            appointmentTable.setItems(AppointmentDAO.getAllAppointments());
        } catch (SQLException e) {
            showAlert("Error", "Could not load appointments", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onWeekView(ActionEvent event) {
        try {
            appointmentTable.setItems(AppointmentDAO.getAppointmentsThisWeek());
        } catch (SQLException e) {
            showAlert("Error", "Could not load appointments", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onMonthView(ActionEvent event) {
        try {
            appointmentTable.setItems(AppointmentDAO.getAppointmentsThisMonth());
        } catch (SQLException e) {
            showAlert("Error", "Could not load appointments", Alert.AlertType.ERROR);
        }
    }

    // Appointment CRUD Operations
    @FXML
    void onAddAppointment(ActionEvent event) {
        try {
            openForm("AddAppointment.fxml", "Add Appointment");
            refreshAppointmentTable();
        } catch (IOException e) {
            showAlert("Error", "Could not open add appointment form", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onUpdateAppointment(ActionEvent event) {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an appointment to update", Alert.AlertType.WARNING);
            return;
        }

        try {
            openForm("ModifyAppointment.fxml", "Update Appointment");
            refreshAppointmentTable();
        } catch (IOException e) {
            showAlert("Error", "Could not open update appointment form", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onDeleteAppointment(ActionEvent event) {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an appointment to delete", Alert.AlertType.WARNING);
            return;
        }

        try {
            AppointmentDAO.delete(selected.getAppointmentId());
            refreshAppointmentTable();
            showAlert("Success", "Appointment deleted successfully", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Error", "Could not delete appointment", Alert.AlertType.ERROR);
        }
    }

    // Customer CRUD Operations
    @FXML
    void onAddCustomer(ActionEvent event) {
        try {
            openForm("AddCustomer.fxml", "Add Customer");
            refreshCustomerTable();
        } catch (IOException e) {
            showAlert("Error", "Could not open add customer form", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onUpdateCustomer(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a customer to update", Alert.AlertType.WARNING);
            return;
        }

        try {
            openForm("ModifyCustomer.fxml", "Update Customer");
            refreshCustomerTable();
        } catch (IOException e) {
            showAlert("Error", "Could not open update customer form", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onDeleteCustomer(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a customer to delete", Alert.AlertType.WARNING);
            return;
        }

        try {
            if (!CustomerDAO.appointmentListIsEmpty(selected.getCustomerID())) {
                showAlert("Error", "Cannot delete customer with existing appointments", Alert.AlertType.ERROR);
                return;
            }

            CustomerDAO.deleteCustomer(selected.getCustomerID());
            refreshCustomerTable();
            showAlert("Success", "Customer deleted successfully", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Error", "Could not delete customer", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onLogout(ActionEvent event) {
        try {
            // Get the current stage
            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();

            // Create and show the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/davis/c195/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();

            // Close the dashboard
            currentStage.close();

            // Clear any session data if needed
            UserDAO.setCurrentUser(null);

        } catch (IOException e) {
            showAlert("Error", "Could not return to login screen", Alert.AlertType.ERROR);
        }
    }

    // Helper Methods
    private void refreshAppointmentTable() {
        try {
            ObservableList<Appointment> appointments = AppointmentDAO.getAllAppointments();
            appointmentTable.setItems(appointments);
        } catch (SQLException e) {
            showAlert("Error", "Could not refresh appointment data", Alert.AlertType.ERROR);
        }
    }

    private void refreshCustomerTable() {
        try {
            ObservableList<Customer> customers = CustomerDAO.getCustomerList();
            customerTable.setItems(customers);
        } catch (SQLException e) {
            showAlert("Error", "Could not refresh customer data", Alert.AlertType.ERROR);
        }
    }

    private void openForm(String fxmlFile, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/davis/c195/" + fxmlFile));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}