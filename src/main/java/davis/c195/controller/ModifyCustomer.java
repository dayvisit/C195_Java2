package davis.c195.controller;

import davis.c195.DAO.CustomerDAO;
import davis.c195.DAO.LocationDAO;
import davis.c195.model.Country;
import davis.c195.model.Customer;
import davis.c195.model.FirstLevelDivision;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifyCustomer implements Initializable {
    @FXML private TextField customerIdField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField postalCodeField;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private ComboBox<String> divisionComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Customer customerToModify;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            customerIdField.setDisable(true);

            // Load countries into combo box
            LocationDAO.getAllCountries().forEach(country ->
                    countryComboBox.getItems().add(country.getCountry())
            );

            // Add listener to country combo box to update divisions
            countryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    updateDivisions(newVal);
                }
            });

        } catch (SQLException e) {
            showAlert("Error", "Failed to load location data", Alert.AlertType.ERROR);
        }
    }

    public void setCustomer(Customer customer) {
        this.customerToModify = customer;
        populateFields();
    }

    private void populateFields() {
        try {
            customerIdField.setText(String.valueOf(customerToModify.getCustomerID()));
            nameField.setText(customerToModify.getCustomerName());
            addressField.setText(customerToModify.getAddress());
            phoneField.setText(customerToModify.getPhone());
            postalCodeField.setText(customerToModify.getPostalCode());

            // Get division and country
            FirstLevelDivision division = LocationDAO.getDivision(customerToModify.getDivisionID());
            if (division != null) {
                Country country = LocationDAO.getCountry(division.getCountryID());
                if (country != null) {
                    // Set country first (this will trigger division update)
                    countryComboBox.setValue(country.getCountry());

                    // Wait briefly for divisions to load then set division
                    divisionComboBox.setValue(division.getDivisionName());
                }
            }

        } catch (SQLException e) {
            showAlert("Error", "Failed to load customer data", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onSave(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        try {
            // Get selected division ID
            String selectedDivision = divisionComboBox.getValue();
            FirstLevelDivision division = LocationDAO.getAllDivisions().filtered(
                    d -> d.getDivisionName().equals(selectedDivision)
            ).get(0);

            // Update customer object
            customerToModify.setCustomerName(nameField.getText());
            customerToModify.setAddress(addressField.getText());
            customerToModify.setPhone(phoneField.getText());
            customerToModify.setPostalCode(postalCodeField.getText());
            customerToModify.setDivisionID(division.getDivisionID());

            // Save to database
            CustomerDAO.updateCustomer(customerToModify);

            // Close window
            closeWindow();

            showAlert("Success", "Customer updated successfully", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Error", "Failed to update customer: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        if (showConfirmation("Cancel?", "Are you sure you want to cancel? Changes will not be saved.")) {
            closeWindow();
        }
    }

    @FXML
    void onActionCountry(ActionEvent event) {
        String selectedCountry = countryComboBox.getValue();
        if (selectedCountry != null) {
            updateDivisions(selectedCountry);
        }
    }

    private void updateDivisions(String countryName) {
        try {
            divisionComboBox.getItems().clear();

            // Get country ID
            Country selectedCountry = LocationDAO.getAllCountries().filtered(
                    c -> c.getCountry().equals(countryName)
            ).get(0);

            // Load divisions for selected country
            LocationDAO.getDivisionsByCountry(selectedCountry.getCountryID()).forEach(division ->
                    divisionComboBox.getItems().add(division.getDivisionName())
            );
        } catch (SQLException e) {
            showAlert("Error", "Failed to load divisions", Alert.AlertType.ERROR);
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText().isEmpty()) {
            errors.append("Name is required\n");
        }
        if (addressField.getText().isEmpty()) {
            errors.append("Address is required\n");
        }
        if (phoneField.getText().isEmpty()) {
            errors.append("Phone number is required\n");
        }
        if (postalCodeField.getText().isEmpty()) {
            errors.append("Postal code is required\n");
        }
        if (countryComboBox.getValue() == null) {
            errors.append("Country must be selected\n");
        }
        if (divisionComboBox.getValue() == null) {
            errors.append("State/Province must be selected\n");
        }

        if (errors.length() > 0) {
            showAlert("Validation Error", errors.toString(), Alert.AlertType.ERROR);
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