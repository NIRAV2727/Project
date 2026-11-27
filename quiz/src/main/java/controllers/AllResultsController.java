package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.DataStore;
import models.DataStore.ResultRecord;

import java.net.URL;
import java.util.ResourceBundle;

public class AllResultsController implements Initializable {

    @FXML private TableView<ResultRecord>          resultsTable;
    @FXML private TableColumn<ResultRecord, String> studentCol;
    @FXML private TableColumn<ResultRecord, String> rollCol;
    @FXML private TableColumn<ResultRecord, String> quizCol;
    @FXML private TableColumn<ResultRecord, String> scoreCol;
    @FXML private TableColumn<ResultRecord, String> percentCol;
    @FXML private TableColumn<ResultRecord, String> gradeCol;
    @FXML private Label totalAttemptsLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        rollCol   .setCellValueFactory(new PropertyValueFactory<>("rollNo"));
        quizCol   .setCellValueFactory(new PropertyValueFactory<>("quizName"));
        scoreCol  .setCellValueFactory(new PropertyValueFactory<>("score"));
        percentCol.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        gradeCol  .setCellValueFactory(new PropertyValueFactory<>("grade"));

        refreshTable();
    }

    @FXML
    private void refreshTable() {
        resultsTable.getItems().setAll(DataStore.results);
        totalAttemptsLabel.setText("Total Attempts: " + DataStore.results.size());
    }
}
