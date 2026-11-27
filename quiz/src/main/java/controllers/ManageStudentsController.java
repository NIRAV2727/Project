package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.DataStore;
import models.Student;

import java.net.URL;
import java.util.ResourceBundle;

public class ManageStudentsController implements Initializable {

    @FXML private TableView<Student>          studentsTable;
    @FXML private TableColumn<Student, String> nameCol;
    @FXML private TableColumn<Student, String> emailCol;
    @FXML private TableColumn<Student, String> rollCol;
    @FXML private TextField searchField;
    @FXML private Label     totalStudentsLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameCol .setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        rollCol .setCellValueFactory(new PropertyValueFactory<>("rollNo"));
        loadStudents();
    }

    private void loadStudents() {
        DataStore.loadStudents();   // refresh from DB
        studentsTable.getItems().setAll(DataStore.students);
        totalStudentsLabel.setText("Total Students: " + DataStore.students.size());
    }

    @FXML
    private void searchStudent() {
        String query = searchField.getText().trim().toLowerCase();
        studentsTable.getItems().clear();
        for (Student s : DataStore.students) {
            if (s.getName().toLowerCase().contains(query) ||
                    s.getEmail().toLowerCase().contains(query) ||
                    s.getRollNo().toLowerCase().contains(query)) {
                studentsTable.getItems().add(s);
            }
        }
    }

    @FXML
    private void deleteStudent() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete student: " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    DataStore.deleteStudent(selected);
                    loadStudents();
                }
            });
        } else {
            new Alert(Alert.AlertType.WARNING, "Please select a student first.").show();
        }
    }

    @FXML
    private void refreshList() {
        searchField.clear();
        loadStudents();
    }
}
