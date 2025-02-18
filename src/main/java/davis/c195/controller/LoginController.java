package davis.c195.controller;

import davis.c195.DAO.AppointmentDAO;
import davis.c195.DAO.UserDAO;
import davis.c195.model.Appointment;
import davis.c195.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label locationLabel;
    @FXML private Label zoneIdLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button exitButton;

    private ResourceBundle rb;
    private static final String LOG_FILE = "login_activity.txt";
    private static final ZoneId localZoneId = ZoneId.systemDefault();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        setLanguage();
        displayZoneId();
    }

    /**
     * Handles login button click
     * Validates credentials and logs attempt
     */
    @FXML
    private void onLoginClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = UserDAO.validateUser(username, password);

            if (user != null) {
                logLoginAttempt(username, true);
                UserDAO.setCurrentUser(user);
                checkUpcomingAppointments();
                loadMainScreen();
            } else {
                logLoginAttempt(username, false);
                showError(rb.getString("loginError"));
            }
        } catch (SQLException e) {
            showError(rb.getString("databaseError"));
        } catch (IOException e) {
            showError(rb.getString("logError"));
        }
    }

    /**
     * Handles exit button click
     * Closes the application
     */
    @FXML
    private void onExitClick() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Sets language based on system locale
     */
    private void setLanguage() {
        try {
            rb = ResourceBundle.getBundle("davis.c195.lang", Locale.getDefault());
            titleLabel.setText(rb.getString("title"));
            usernameLabel.setText(rb.getString("username"));
            passwordLabel.setText(rb.getString("password"));
            locationLabel.setText(rb.getString("location"));
            loginButton.setText(rb.getString("login"));
            exitButton.setText(rb.getString("exit"));
        } catch (Exception e) {
            System.err.println("Error loading language bundle: " + e.getMessage());
        }
    }

    /**
     * Displays user's zone ID
     */
    private void displayZoneId() {
        zoneIdLabel.setText(localZoneId.toString());
    }

    /**
     * Logs login attempts to file
     */
    private void logLoginAttempt(String username, boolean success) throws IOException {
        LocalDateTime timestamp = LocalDateTime.now();
        String status = success ? "SUCCESS" : "FAILURE";

        String logEntry = String.format("%s | %s | User: %s | Zone: %s%n",
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                status,
                username,
                localZoneId
        );

        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(logEntry);
        }
    }

    /**
     * Checks for appointments within 15 minutes of login
     */
    private void checkUpcomingAppointments() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime soon = now.plusMinutes(15);

            boolean found = false;
            for (Appointment appt : AppointmentDAO.getAllAppointments()) {
                LocalDateTime start = appt.getStart();
                if (start.isAfter(now) && start.isBefore(soon)) {
                    showInfo(String.format(rb.getString("upcomingAppt"),
                            appt.getAppointmentId(),
                            start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
                    found = true;
                    break;
                }
            }

            if (!found) {
                showInfo(rb.getString("noUpcomingAppt"));
            }
        } catch (SQLException e) {
            showError(rb.getString("databaseError"));
        }
    }

    /**
     * Loads the main application screen
     */
    private void loadMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/davis/c195/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError(rb.getString("loadError"));
        }
    }

    /**
     * Shows error alert
     */
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(rb.getString("error"));
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows info alert
     */
    private void showInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(rb.getString("info"));
        alert.setContentText(message);
        alert.showAndWait();
    }
}