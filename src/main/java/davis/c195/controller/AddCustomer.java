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

public class AddCustomer implements Initializable {
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField postalCodeField;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private ComboBox<String> divisionComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML
    void onActionCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setContentText("Are you sure you want to cancel? Any unsaved data will be lost.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.close();
            }
        });
    }

    @FXML
    void onActionSave(ActionEvent event) {
        try {
            if (validateInput()) {
                String name = nameField.getText();
                String address = addressField.getText();
                String postal = postalCodeField.getText();
                String phone = phoneField.getText();

                // Get the selected division to get its ID
                String selectedDivision = divisionComboBox.getValue();
                int divisionId = -1;

                // Find the division ID that matches the selected division name
                for (FirstLevelDivision division : LocationDAO.getAllDivisions()) {
                    if (division.getDivisionName().equals(selectedDivision)) {
                        divisionId = division.getDivisionID();
                        break;
                    }
                }

                // Create new customer
                Customer newCustomer = new Customer(
                        0,  // ID will be auto-generated
                        name,
                        address,
                        postal,
                        phone,
                        divisionId
                );

                // Insert customer
                CustomerDAO.insertCustomer(newCustomer);

                // Show success message and close window
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Customer successfully added!");
                alert.showAndWait();

                Stage stage = (Stage) saveButton.getScene().getWindow();
                stage.close();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error adding customer: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void onActionCountry(ActionEvent event) {
        try {
            // Clear the division combo box first
            divisionComboBox.getItems().clear();

            // Get selected country
            String selectedCountry = countryComboBox.getValue();

            if (selectedCountry != null) {
                // Find country ID
                int countryId = -1;
                for (Country country : LocationDAO.getAllCountries()) {
                    if (country.getCountry().equals(selectedCountry)) {
                        countryId = country.getCountryID();
                        break;
                    }
                }

                // Get divisions for selected country and populate combo box
                LocationDAO.getDivisionsByCountry(countryId).forEach(division ->
                        divisionComboBox.getItems().add(division.getDivisionName())
                );
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error loading divisions: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameField.getText().isEmpty()) {
            errorMessage.append("Name is required\n");
        }
        if (addressField.getText().isEmpty()) {
            errorMessage.append("Address is required\n");
        }
        if (postalCodeField.getText().isEmpty()) {
            errorMessage.append("Postal code is required\n");
        }
        if (phoneField.getText().isEmpty()) {
            errorMessage.append("Phone number is required\n");
        }
        if (countryComboBox.getValue() == null) {
            errorMessage.append("Country must be selected\n");
        }
        if (divisionComboBox.getValue() == null) {
            errorMessage.append("Division/State must be selected\n");
        }

        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Disable ID field as it's auto-generated
            idField.setDisable(true);
            idField.setText("Auto-Generated");

            // Load countries into combo box
            LocationDAO.getAllCountries().forEach(country ->
                    countryComboBox.getItems().add(country.getCountry())
            );

            // Set division combo box prompt text
            divisionComboBox.setPromptText("Select a Country First");

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error initializing form: " + e.getMessage());
            alert.showAndWait();
        }
    }
}