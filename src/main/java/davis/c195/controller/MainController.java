package davis.c195.controller;

import davis.c195.DAO.*;
import davis.c195.model.*;
import javafx.beans.property.SimpleStringProperty;
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

    /**
     * Initializes the controller class and sets up all tables and controls.
     *<p>
     * Lambda Expression #1: Uses lambda in contactCol.setCellValueFactory to dynamically fetch
     * and display contact names based on contact IDs. This improves code by enabling direct data
     * lookup and transformation within the table display logic.
     *</p>
     * <p>
     * Lambda Expression #2: Uses lambda in divisionCol.setCellValueFactory to format division
     * and country information for display. This improves code by combining multiple database
     * lookups into a single operation that produces formatted output.
     * </p>
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize appointment table columns
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(data -> {
            try {
                Contact contact = ContactDAO.getContact(data.getValue().getContactId());
                return new SimpleStringProperty(contact != null ? contact.getContactName() : "");
            } catch (SQLException e) {
                return new SimpleStringProperty("");
            }
        });
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
        divisionCol.setCellValueFactory(data -> {
            try {
                FirstLevelDivision division = LocationDAO.getDivision(data.getValue().getDivisionID());
                if (division != null) {
                    Country country = LocationDAO.getCountry(division.getCountryID());
                    return new SimpleStringProperty(division.getDivisionName() +
                            (country != null ? ", " + country.getCountry() : ""));
                }
                return new SimpleStringProperty("");
            } catch (SQLException e) {
                return new SimpleStringProperty("");
            }
        });
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/davis/c195/AddAppointment.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Appointment");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshAppointmentTable();
        } catch (IOException e) {
            showAlert("Error", "Could not open add appointment form: " + e.getMessage(), Alert.AlertType.ERROR);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/davis/c195/ModifyAppointment.fxml"));
            Parent root = loader.load();

            ModifyAppointment controller = loader.getController();
            controller.setAppointment(selected);

            Stage stage = new Stage();
            stage.setTitle("Update Appointment");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshAppointmentTable();
        } catch (IOException e) {
            showAlert("Error", "Could not open update appointment form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onDeleteAppointment(ActionEvent event) {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an appointment to delete", Alert.AlertType.WARNING);
            return;
        }

        if (!showConfirmation("Delete Appointment", "Are you sure you want to delete this appointment?")) {
            return;
        }

        try {
            AppointmentDAO.delete(selected.getAppointmentId());
            refreshAppointmentTable();
            showAlert("Success", "Appointment deleted successfully\nAppointment ID: " +
                    selected.getAppointmentId() + "\nType: " + selected.getType(), Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Error", "Could not delete appointment", Alert.AlertType.ERROR);
        }
    }

    // Customer CRUD Operations
    @FXML
    void onAddCustomer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/davis/c195/AddCustomer.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Customer");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshCustomerTable();
        } catch (IOException e) {
            showAlert("Error", "Could not open add customer form: " + e.getMessage(), Alert.AlertType.ERROR);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/davis/c195/ModifyCustomer.fxml"));
            Parent root = loader.load();
            ModifyCustomer controller = loader.getController();
            controller.setCustomer(selected);
            Stage stage = new Stage();
            stage.setTitle("Update Customer");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshCustomerTable();
        } catch (IOException e) {
            showAlert("Error", "Could not open update customer form: " + e.getMessage(), Alert.AlertType.ERROR);
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

            if (!showConfirmation("Delete Customer", "Are you sure you want to delete this customer?")) {
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
    void onReports(ActionEvent event) {
        try {
            Stage currentStage = (Stage) reportsBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/davis/c195/Reports.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Reports");
            currentStage.show();
        } catch (IOException e) {
            showAlert("Error", "Could not open reports form", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onLogout(ActionEvent event) {
        try {
            if (!showConfirmation("Logout", "Are you sure you want to logout?")) {
                return;
            }

            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/davis/c195/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
            currentStage.close();
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

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        return alert.showAndWait().get() == ButtonType.OK;
    }
}