package com.mycompany.quiz_project;

import constants.adminEmailPassword;
import models.DataStore;
import models.Student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class HelloViewController {

    @FXML private TextField     adminEmail;
    @FXML private PasswordField adminPassword;
    @FXML private Label         adminError;

    @FXML private TextField     studentEmail;
    @FXML private PasswordField studentPassword;
    @FXML private Label         studentError;

    @FXML
    private void LoginAdmin(javafx.event.ActionEvent event) {
        String email    = adminEmail.getText().trim();
        String password = adminPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            adminError.setText("Please fill all fields."); return;
        }
        if (email.equalsIgnoreCase(adminEmailPassword.email) &&
                password.equals(adminEmailPassword.password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminHomeScreen.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Admin Dashboard");
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                adminError.setText("Error loading admin screen.");
            }
        } else {
            adminError.setText("Invalid email or password.");
        }
    }

    @FXML
    private void LoginStudent(javafx.event.ActionEvent event) {
        String email    = studentEmail.getText().trim();
        String password = studentPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            studentError.setText("Please fill all fields."); return;
        }
        DataStore.loadStudents();

        Student found = null;
        for (Student s : DataStore.students) {
            if (s.getEmail().equalsIgnoreCase(email) && s.getPassword().equals(password)) {
                found = s; break;
            }
        }

        if (found != null) {
            DataStore.currentStudent = found;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/studentDashboard.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Student Dashboard");
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                studentError.setText("Error loading student screen.");
            }
        } else {
            studentError.setText("Invalid email or password.");
        }
    }

    @FXML
    private void goToRegister(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/studentRegister.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Student Registration");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) { 
            e.printStackTrace();
        }
    }
}
