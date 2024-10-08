module davis.c195 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens davis.c195 to javafx.fxml;
    exports davis.c195;
}