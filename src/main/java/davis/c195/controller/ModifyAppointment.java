package davis.c195.controller;

import davis.c195.DAO.*;
import davis.c195.model.Appointment;
import davis.c195.model.Contact;
import davis.c195.model.Customer;
import davis.c195.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.ResourceBundle;

public class ModifyAppointment implements Initializable {
    @FXML private TextField appointmentIdField;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField locationField;
    @FXML private TextField typeField;
    @FXML private ComboBox<String> contactComboBox;
    @FXML private ComboBox<String> customerComboBox;
    @FXML private ComboBox<String> userComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<LocalTime> startTimeComboBox;
    @FXML private ComboBox<LocalTime> endTimeComboBox;
    @FXML private Label timeZoneLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Appointment appointmentToModify;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Set time zone label
            timeZoneLabel.setText("Time Zone: " + ZoneId.systemDefault());

            // Load combo boxes
            loadContactComboBox();
            loadCustomerComboBox();
            loadUserComboBox();
            loadTimeComboBoxes();

        } catch (SQLException e) {
            showAlert("Error", "Failed to initialize form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void setAppointment(Appointment appointment) {
        this.appointmentToModify = appointment;
        populateFields();
    }

    private void populateFields() {
        try {
            appointmentIdField.setText(String.valueOf(appointmentToModify.getAppointmentId()));
            titleField.setText(appointmentToModify.getTitle());
            descriptionArea.setText(appointmentToModify.getDescription());
            locationField.setText(appointmentToModify.getLocation());
            typeField.setText(appointmentToModify.getType());

            // Set contact
            Contact contact = ContactDAO.getContact(appointmentToModify.getContactId());
            if (contact != null) {
                contactComboBox.setValue(contact.getContactID() + ": " + contact.getContactName());
            }

            // Set customer
            Customer customer = CustomerDAO.getCustomerList().filtered(c ->
                    c.getCustomerID() == appointmentToModify.getCustomerId()).get(0);
            customerComboBox.setValue(customer.getCustomerID() + ": " + customer.getCustomerName());

            // Set user
            User user = UserDAO.getUser(appointmentToModify.getUserId());
            if (user != null) {
                userComboBox.setValue(user.getUserID() + ": " + user.getUserName());
            }

            // Set dates and times
            startDatePicker.setValue(appointmentToModify.getStart().toLocalDate());
            endDatePicker.setValue(appointmentToModify.getEnd().toLocalDate());
            startTimeComboBox.setValue(appointmentToModify.getStart().toLocalTime());
            endTimeComboBox.setValue(appointmentToModify.getEnd().toLocalTime());

        } catch (SQLException e) {
            showAlert("Error", "Failed to load appointment data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadContactComboBox() throws SQLException {
        contactComboBox.getItems().clear();
        ContactDAO.getAllContacts().forEach(contact ->
                contactComboBox.getItems().add(contact.getContactID() + ": " + contact.getContactName())
        );
    }

    private void loadCustomerComboBox() throws SQLException {
        customerComboBox.getItems().clear();
        CustomerDAO.getCustomerList().forEach(customer ->
                customerComboBox.getItems().add(customer.getCustomerID() + ": " + customer.getCustomerName())
        );
    }

    private void loadUserComboBox() throws SQLException {
        userComboBox.getItems().clear();
        UserDAO.getAllUsers().forEach(user ->
                userComboBox.getItems().add(user.getUserID() + ": " + user.getUserName())
        );
    }

    private void loadTimeComboBoxes() {
        // Populate time combo boxes with business hours (8:00 AM to 10:00 PM EST)
        startTimeComboBox.getItems().clear();
        endTimeComboBox.getItems().clear();

        LocalTime time = LocalTime.of(8, 0);
        while (!time.isAfter(LocalTime.of(22, 0))) {
            startTimeComboBox.getItems().add(time);
            endTimeComboBox.getItems().add(time);
            time = time.plusMinutes(30); // 30-minute intervals
        }
    }

    @FXML
    void onSave(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        try {
            // Get IDs from combo box selections
            int contactId = Integer.parseInt(contactComboBox.getValue().split(":")[0].trim());
            int customerId = Integer.parseInt(customerComboBox.getValue().split(":")[0].trim());
            int userId = Integer.parseInt(userComboBox.getValue().split(":")[0].trim());

            // Get start and end times
            LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(), startTimeComboBox.getValue());
            LocalDateTime endDateTime = LocalDateTime.of(endDatePicker.getValue(), endTimeComboBox.getValue());

            // Update appointment object
            appointmentToModify.setTitle(titleField.getText());
            appointmentToModify.setDescription(descriptionArea.getText());
            appointmentToModify.setLocation(locationField.getText());
            appointmentToModify.setType(typeField.getText());
            appointmentToModify.setStart(startDateTime);
            appointmentToModify.setEnd(endDateTime);
            appointmentToModify.setCustomerId(customerId);
            appointmentToModify.setUserId(userId);
            appointmentToModify.setContactId(contactId);

            // Save to database
            AppointmentDAO.update(appointmentToModify);

            showAlert("Success", "Appointment updated successfully", Alert.AlertType.INFORMATION);
            closeWindow();

        } catch (SQLException e) {
            showAlert("Error", "Failed to update appointment: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        if (showConfirmation("Cancel?", "Are you sure you want to cancel? Changes will not be saved.")) {
            closeWindow();
        }
    }

    private boolean validateFields() {
        StringBuilder errorMessage = new StringBuilder();

        if (titleField.getText().isEmpty()) {
            errorMessage.append("Title is required\n");
        }
        if (descriptionArea.getText().isEmpty()) {
            errorMessage.append("Description is required\n");
        }
        if (locationField.getText().isEmpty()) {
            errorMessage.append("Location is required\n");
        }
        if (typeField.getText().isEmpty()) {
            errorMessage.append("Type is required\n");
        }
        if (contactComboBox.getValue() == null) {
            errorMessage.append("Contact must be selected\n");
        }
        if (customerComboBox.getValue() == null) {
            errorMessage.append("Customer must be selected\n");
        }
        if (userComboBox.getValue() == null) {
            errorMessage.append("User must be selected\n");
        }
        if (startDatePicker.getValue() == null) {
            errorMessage.append("Start date must be selected\n");
        }
        if (endDatePicker.getValue() == null) {
            errorMessage.append("End date must be selected\n");
        }
        if (startTimeComboBox.getValue() == null) {
            errorMessage.append("Start time must be selected\n");
        }
        if (endTimeComboBox.getValue() == null) {
            errorMessage.append("End time must be selected\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
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

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}