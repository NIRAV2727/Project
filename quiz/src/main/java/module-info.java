module com.mycompany.quiz_project {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.mycompany.quiz_project to javafx.fxml;
    opens controllers to javafx.fxml;
    opens models to javafx.base, javafx.fxml;
    opens constants to javafx.fxml;

    exports com.mycompany.quiz_project;
    exports controllers;
    exports models;
    exports constants;
}