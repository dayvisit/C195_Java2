package davis.c195.controller;

import davis.c195.DAO.*;
import davis.c195.model.Appointment;
import davis.c195.model.Contact;
import davis.c195.model.Customer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {
    @FXML private TableView<Map.Entry<String, Integer>> typeMonthTable;
    @FXML private TableColumn<Map.Entry<String, Integer>, String> typeCol;
    @FXML private TableColumn<Map.Entry<String, Integer>, String> monthCol;
    @FXML private TableColumn<Map.Entry<String, Integer>, Integer> countCol;

    @FXML private ComboBox<Contact> contactComboBox;
    @FXML private TableView<Appointment> contactScheduleTable;
    @FXML private TableColumn<Appointment, Integer> appointmentIdCol;
    @FXML private TableColumn<Appointment, String> titleCol;
    @FXML private TableColumn<Appointment, String> typeScheduleCol;
    @FXML private TableColumn<Appointment, String> descriptionCol;
    @FXML private TableColumn<Appointment, String> startCol;
    @FXML private TableColumn<Appointment, String> endCol;
    @FXML private TableColumn<Appointment, Integer> customerIdCol;

    @FXML private TableView<Map.Entry<Integer, Integer>> customerTotalsTable;
    @FXML private TableColumn<Map.Entry<Integer, Integer>, String> customerCol;
    @FXML private TableColumn<Map.Entry<Integer, Integer>, Integer> totalAppointmentsCol;

    @FXML private Button backButton;

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String[] MONTHS = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTypeMonthTable();
        setupContactScheduleTable();
        setupCustomerTotalsTable();
        loadAllReports();
    }

    /**
     * Sets up the type and month table with appropriate column mappings.
     *<p>
     * Lambda Expression #1: Uses lambda in typeCol.setCellValueFactory to extract and display
     * the appointment type from the composite map key. This improves code by enabling direct
     * string manipulation within the cell factory.
     *</p>
     *<p>
     * Lambda Expression #2: Uses lambda in monthCol.setCellValueFactory to convert numeric
     * month values to month names. This improves code by providing a clean way to transform
     * data for display without separate formatting methods.
     *</p>
     *<p>
     * Lambda Expression #3: Uses lambda in countCol.setCellValueFactory to convert the Integer
     * count value into a property that can be displayed. This improves code by simplifying
     * the process of making non-property data observable.
     *</p>
     */
    private void setupTypeMonthTable() {
        typeCol.setCellValueFactory(data -> {
            String[] parts = data.getValue().getKey().split("-");
            return new SimpleStringProperty(parts[0]);
        });

        monthCol.setCellValueFactory(data -> {
            String[] parts = data.getValue().getKey().split("-");
            int monthNum = Integer.parseInt(parts[1]);
            return new SimpleStringProperty(MONTHS[monthNum - 1]);
        });

        countCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getValue()).asObject());
    }

    /**
     * Sets up the contact schedule table with appropriate column mappings.
     *<p>
     * Lambda Expression #1: Uses lambda in contactComboBox.setOnAction to trigger the
     * loadContactSchedule method when a contact is selected. This improves code by providing
     * a clean event handling mechanism without requiring a separate handler method.
     *</p>
     *<p>
     * Lambda Expression #2-8: Uses lambdas in setCellValueFactory calls to convert appointment
     * data to appropriate property types for display. This improves code by allowing direct
     * transformation of data within the cell factories and reducing the amount of boilerplate code.
     *</p>
     */
    private void setupContactScheduleTable() {
        try {
            // Populate contact combo box
            contactComboBox.setItems(ContactDAO.getAllContacts());
            contactComboBox.setOnAction(e -> loadContactSchedule());

            // Setup table columns
            appointmentIdCol.setCellValueFactory(cellData ->
                    new SimpleIntegerProperty(cellData.getValue().getAppointmentId()).asObject());
            titleCol.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getTitle()));
            typeScheduleCol.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getType()));
            descriptionCol.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getDescription()));
            startCol.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getStart().format(DATE_TIME_FORMAT)));
            endCol.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getEnd().format(DATE_TIME_FORMAT)));
            customerIdCol.setCellValueFactory(cellData ->
                    new SimpleIntegerProperty(cellData.getValue().getCustomerId()).asObject());
        } catch (SQLException e) {
            showAlert("Error loading contacts", e.getMessage());
        }
    }

    /**
     * Sets up the customer totals table with appropriate column mappings.
     *<p>
     * Lambda Expression #1: Uses lambda in customerCol.setCellValueFactory to dynamically retrieve
     * and format customer names from their IDs. This improves code by allowing direct data
     * transformation within the cell factory instead of requiring a separate formatting method.
     *</p>
     *<p>
     * Lambda Expression #2: Uses lambda in totalAppointmentsCol.setCellValueFactory to transform
     * the raw map entry values into table cell data. This improves code by providing an efficient
     * way to convert data between different formats without additional helper methods.
     *</p>
     */
    private void setupCustomerTotalsTable() {
        customerCol.setCellValueFactory(data -> {
            try {
                Customer customer = CustomerDAO.getCustomerList().filtered(c ->
                        c.getCustomerID() == data.getValue().getKey()).get(0);
                return new SimpleStringProperty(customer.getCustomerName());
            } catch (SQLException e) {
                return new SimpleStringProperty("Unknown");
            }
        });

        totalAppointmentsCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getValue()).asObject());
    }

    private void loadAllReports() {
        try {
            // Load appointments by type and month
            Map<String, Integer> typeMonthData = ReportsDAO.getAppointmentsByTypeAndMonth();
            typeMonthTable.setItems(FXCollections.observableArrayList(typeMonthData.entrySet()));

            // Load customer totals
            Map<Integer, Integer> customerTotals = ReportsDAO.getAppointmentsByCustomer();
            customerTotalsTable.setItems(FXCollections.observableArrayList(customerTotals.entrySet()));

            // Load initial contact schedule if contacts exist
            if (!contactComboBox.getItems().isEmpty()) {
                contactComboBox.getSelectionModel().selectFirst();
                loadContactSchedule();
            }
        } catch (SQLException e) {
            showAlert("Error loading reports", e.getMessage());
        }
    }

    private void loadContactSchedule() {
        Contact selectedContact = contactComboBox.getValue();
        if (selectedContact != null) {
            try {
                ObservableList<Appointment> schedule = ReportsDAO.getContactSchedule(selectedContact.getContactID());
                contactScheduleTable.setItems(schedule);
            } catch (SQLException e) {
                showAlert("Error loading contact schedule", e.getMessage());
            }
        }
    }

    @FXML
    private void onActionBack(ActionEvent event) throws IOException {
        // Get the current stage
        Stage currentStage = (Stage) backButton.getScene().getWindow();

        // Load the main view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/davis/c195/main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Set the scene on the current stage
        currentStage.setScene(scene);
        currentStage.setTitle("Scheduling Application");
        currentStage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}