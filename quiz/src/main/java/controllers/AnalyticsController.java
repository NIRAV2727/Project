package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import models.DataStore;

import java.net.URL;
import java.util.ResourceBundle;

public class AnalyticsController implements Initializable {

    @FXML private Label totalStudentsLabel;
    @FXML private Label totalQuizzesLabel;
    @FXML private Label totalQuestionsLabel;
    @FXML private Label passRateLabel;
    @FXML private Label avgScoreLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        totalStudentsLabel.setText(String.valueOf(DataStore.students.size()));
        totalQuizzesLabel.setText(String.valueOf(DataStore.quizzes.size()));
        totalQuestionsLabel.setText(String.valueOf(DataStore.questions.size()));
        passRateLabel.setText("75%");
        avgScoreLabel.setText("2.5 / 3");
    }
}
