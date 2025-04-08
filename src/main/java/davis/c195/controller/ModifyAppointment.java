package davis.c195.controller;

import davis.c195.DAO.*;
import davis.c195.helpers.AppointmentValidation;
import davis.c195.model.Appointment;
import davis.c195.model.Contact;
import davis.c195.model.Customer;
import davis.c195.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.ResourceBundle;
import java.util.stream.Stream;

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
            timeZoneLabel.setText("Time Zone: " + ZoneId.systemDefault());
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

            Contact contact = ContactDAO.getContact(appointmentToModify.getContactId());
            if (contact != null) {
                contactComboBox.setValue(contact.getContactID() + ": " + contact.getContactName());
            }

            Customer customer = CustomerDAO.getCustomerList().filtered(c ->
                    c.getCustomerID() == appointmentToModify.getCustomerId()).get(0);
            customerComboBox.setValue(customer.getCustomerID() + ": " + customer.getCustomerName());

            User user = UserDAO.getUser(appointmentToModify.getUserId());
            if (user != null) {
                userComboBox.setValue(user.getUserID() + ": " + user.getUserName());
            }

            startDatePicker.setValue(appointmentToModify.getStart().toLocalDate());
            endDatePicker.setValue(appointmentToModify.getEnd().toLocalDate());
            startTimeComboBox.setValue(appointmentToModify.getStart().toLocalTime());
            endTimeComboBox.setValue(appointmentToModify.getEnd().toLocalTime());

        } catch (SQLException e) {
            showAlert("Error", "Failed to load appointment data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Loads contact data into the contact combo box.
     *<p>
     * Lambda Expression: Uses forEach to efficiently process each contact and add it to the combo box.
     * This improves code by eliminating the need for explicit iteration and index tracking,
     * resulting in more concise and readable code.
     *</p>
     * @throws SQLException if database access error occurs
     */
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

    /**
     * Loads user data into the user combo box.
     *<p>
     * Lambda Expression: Uses forEach to efficiently populate the combo box with user data.
     * This improves code readability and reduces line count compared to traditional for loops.
     *</p>
     * @throws SQLException if database access error occurs
     */
    private void loadUserComboBox() throws SQLException {
        userComboBox.getItems().clear();
        UserDAO.getAllUsers().forEach(user ->
                userComboBox.getItems().add(user.getUserID() + ": " + user.getUserName())
        );
    }

    private void loadTimeComboBoxes() {
        startTimeComboBox.getItems().clear();
        endTimeComboBox.getItems().clear();

        LocalTime time = LocalTime.of(8, 0);
        while (!time.isAfter(LocalTime.of(22, 0))) {
            startTimeComboBox.getItems().add(time);
            endTimeComboBox.getItems().add(time);
            time = time.plusMinutes(30);
        }
    }

    /**
     * Saves appointment data and returns user to schedule screen. Performs various validations including
     * checking for appointment time overlaps and business hours compliance.
     *<p>
     * Lambda Expression #1: Validates form fields using Stream.anyMatch(). This improves code by replacing
     * multiple if-statements with a single, maintainable stream operation that can be easily modified
     * if validation rules change.
     *</p>
     *<p>
     * Lambda Expression #2: Uses removeIf to filter appointment list. This improves code by providing
     * a clean, functional way to remove elements based on a condition without requiring an explicit
     * loop or temporary collection.
     *</p>
     * @param event Save button event
     */
    @FXML
    void onSave(ActionEvent event) {
        try {
            // Lambda #1: Field validation
            boolean hasEmptyFields = Stream.of(
                            titleField.getText(),
                            descriptionArea.getText(),
                            locationField.getText(),
                            typeField.getText(),
                            contactComboBox.getValue(),
                            customerComboBox.getValue(),
                            userComboBox.getValue())
                    .anyMatch(field -> field == null || field.trim().isEmpty());

            if (hasEmptyFields) {
                showAlert("Error", "All fields must be filled out", Alert.AlertType.ERROR);
                return;
            }

            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null ||
                    startTimeComboBox.getValue() == null || endTimeComboBox.getValue() == null) {
                showAlert("Error", "Date and time must be selected", Alert.AlertType.ERROR);
                return;
            }

            // Get IDs from combo box selections
            int contactId = Integer.parseInt(contactComboBox.getValue().split(":")[0].trim());
            int customerId = Integer.parseInt(customerComboBox.getValue().split(":")[0].trim());
            int userId = Integer.parseInt(userComboBox.getValue().split(":")[0].trim());

            // Get start and end times
            LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(), startTimeComboBox.getValue());
            LocalDateTime endDateTime = LocalDateTime.of(endDatePicker.getValue(), endTimeComboBox.getValue());

            // Check for overlapping appointments
            ObservableList<Appointment> appointments = AppointmentDAO.getAllAppointments();

            // Check for appointment time validity (business hours)
            AppointmentValidation AppointmentValidation = null;
            if (!AppointmentValidation.isWithinBusinessHours(startDateTime, endDateTime)) {
                showAlert("Error", "Appointment must be scheduled within business hours (8:00 AM to 10:00 PM EST), Monday through Friday",
                        Alert.AlertType.ERROR);
                return;
            }

            // Check for customer appointment overlaps, excluding the current appointment
            if (!AppointmentValidation.isValidAppointmentTime(
                    appointments,
                    customerId,
                    startDateTime,
                    endDateTime,
                    appointmentToModify.getAppointmentId())) {
                showAlert("Error", "This appointment overlaps with another appointment for the same customer",
                        Alert.AlertType.ERROR);
                return;
            }

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