package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.DataStore;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentResultController implements Initializable {

    @FXML private Label quizNameLabel;
    @FXML private Label scoreLabel;
    @FXML private Label percentageLabel;
    @FXML private Label gradeLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int score = DataStore.lastScore;
        int total = DataStore.lastTotal;
        double pct = total > 0 ? (score * 100.0 / total) : 0;

        scoreLabel.setText(score + " / " + total);
        percentageLabel.setText(String.format("%.1f%%", pct));

        String grade;
        if      (pct >= 90) grade = "A+";
        else if (pct >= 75) grade = "A";
        else if (pct >= 60) grade = "B";
        else if (pct >= 40) grade = "C";
        else                grade = "F";

        gradeLabel.setText(grade);

        if (grade.equals("A+") || grade.equals("A")) {
            gradeLabel.getStyleClass().setAll("badge-easy");
        } else if (grade.equals("B") || grade.equals("C")) {
            gradeLabel.getStyleClass().setAll("badge-medium");
        } else {
            gradeLabel.getStyleClass().setAll("badge-hard");
        }

        if (DataStore.selectedQuiz != null) {
            quizNameLabel.setText(DataStore.selectedQuiz.getTitle());
        } else {
            quizNameLabel.setText("Quiz Assessment Result");
        }
    }

    @FXML
    private void backToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/studentDashboard.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("QuizHub - Student Dashboard");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void viewLeaderboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/leaderboard.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("QuizHub - Global Rankings");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
