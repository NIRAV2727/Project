package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.DataStore;
import models.Student;

public class StudentRegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField rollNoField;
    @FXML private Label errorLabel;

    @FXML
    private void registerStudent(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String rollNo = rollNoField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || rollNo.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        DataStore.loadStudents();
        for (Student s : DataStore.students) {
            if (s.getEmail().equalsIgnoreCase(email)) {
                errorLabel.setText("Email is already registered.");
                return;
            }
            if (s.getRollNo().equalsIgnoreCase(rollNo)) {
                errorLabel.setText("Roll number is already registered.");
                return;
            }
        }

        Student newStudent = new Student(name, email, password, rollNo);
        boolean success = DataStore.addStudent(newStudent);
        if (success) {
            goToLogin(event);
        } else {
            errorLabel.setText("Registration failed. Unable to save to database.");
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HelloView.fxml"));
            Scene scene = new Scene(loader.load(), 740, 520);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Quiz Application");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
