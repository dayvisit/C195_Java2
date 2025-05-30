module davis.c {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens davis.c195 to javafx.fxml;
    opens davis.c195.controller to javafx.fxml;
    opens davis.c195.model to javafx.base;

    exports davis.c195;
    exports davis.c195.controller;
    exports davis.c195.model;
}