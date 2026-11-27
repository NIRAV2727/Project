package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.DataStore;
import models.DataStore.ResultRecord;

import java.net.URL;
import java.util.ResourceBundle;

public class LeaderboardController implements Initializable {

    @FXML private TableView<ResultRecord> leaderboardTable;
    @FXML private TableColumn<ResultRecord, String> nameCol;
    @FXML private TableColumn<ResultRecord, String> rollNoCol;
    @FXML private TableColumn<ResultRecord, String> quizCol;
    @FXML private TableColumn<ResultRecord, String> scoreCol;
    @FXML private TableColumn<ResultRecord, String> pctCol;
    @FXML private TableColumn<ResultRecord, String> gradeCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        rollNoCol.setCellValueFactory(new PropertyValueFactory<>("rollNo"));
        quizCol.setCellValueFactory(new PropertyValueFactory<>("quizName"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        pctCol.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));

        DataStore.loadResults();
        leaderboardTable.setItems(javafx.collections.FXCollections.observableArrayList(DataStore.results));
        leaderboardTable.refresh();
    }

    @FXML
    private void backToDashboard(ActionEvent event) {
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
        }
    }
}
