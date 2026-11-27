package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.DataStore;
import models.Question;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class QuizAttemptController implements Initializable {

    @FXML private Label quizTitleLabel;
    @FXML private Label subjectLabel;
    @FXML private Label timerLabel;
    @FXML private Label questionNumberLabel;
    @FXML private Label progressPctLabel;
    @FXML private ProgressBar quizProgressBar;
    @FXML private Label questionTextLabel;
    
    @FXML private RadioButton optionARadio;
    @FXML private RadioButton optionBRadio;
    @FXML private RadioButton optionCRadio;
    @FXML private RadioButton optionDRadio;
    @FXML private ToggleGroup optionsGroup;

    @FXML private Button prevBtn;
    @FXML private Button nextBtn;
    @FXML private Button submitBtn;

    private List<Question> quizQuestions = new ArrayList<>();
    private String[] selectedAnswers;
    private int currentIndex = 0;
    private int secondsRemaining = 600; // default 10 minutes
    private Timeline timerTimeline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (DataStore.selectedQuiz != null) {
            quizTitleLabel.setText(DataStore.selectedQuiz.getTitle());
            subjectLabel.setText("Subject: " + DataStore.selectedQuiz.getSubject());
            
            // Set timer from quiz time limit (convert minutes to seconds)
            int minutes = DataStore.selectedQuiz.getTimeLimit();
            if (minutes <= 0) minutes = 10;
            secondsRemaining = minutes * 60;
            
            // Load questions specifically for this quiz
            quizQuestions = DataStore.getQuestionsForQuiz(DataStore.selectedQuiz.getTitle());
        }

        if (quizQuestions.isEmpty()) {
            quizQuestions = DataStore.questions;
        }

        selectedAnswers = new String[quizQuestions.size()];

        startTimer();
        loadQuestion(currentIndex);
    }

    private void startTimer() {
        updateTimerLabel();
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsRemaining--;
            updateTimerLabel();
            if (secondsRemaining <= 0) {
                timerTimeline.stop();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Time Expired");
                alert.setHeaderText("Time's Up!");
                alert.setContentText("Your time limit for this quiz has expired. Submitting answers automatically.");
                alert.showAndWait();
                finishQuiz(null);
            }
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void updateTimerLabel() {
        int m = secondsRemaining / 60;
        int s = secondsRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", m, s));
        if (secondsRemaining < 60) {
            timerLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    private void loadQuestion(int index) {
        if (index < 0 || index >= quizQuestions.size()) return;
        
        Question q = quizQuestions.get(index);
        questionNumberLabel.setText("Question " + (index + 1) + " of " + quizQuestions.size());
        
        double progress = (double) (index + 1) / quizQuestions.size();
        quizProgressBar.setProgress(progress);
        progressPctLabel.setText(String.format("%.0f%% Completed", progress * 100));

        questionTextLabel.setText(q.getQuestionText());
        optionARadio.setText("A) " + q.getOptionA());
        optionBRadio.setText("B) " + q.getOptionB());
        optionCRadio.setText("C) " + q.getOptionC());
        optionDRadio.setText("D) " + q.getOptionD());

        optionsGroup.selectToggle(null);

        // Restore previously selected option if any
        if (selectedAnswers[index] != null) {
            String sel = selectedAnswers[index];
            if (sel.startsWith("A)")) optionARadio.setSelected(true);
            else if (sel.startsWith("B)")) optionBRadio.setSelected(true);
            else if (sel.startsWith("C)")) optionCRadio.setSelected(true);
            else if (sel.startsWith("D)")) optionDRadio.setSelected(true);
        }

        prevBtn.setDisable(index == 0);
        nextBtn.setVisible(index < quizQuestions.size() - 1);
    }

    private void saveCurrentSelection() {
        RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
        if (selected != null) {
            selectedAnswers[currentIndex] = selected.getText();
        }
    }

    @FXML
    private void nextQuestion(ActionEvent event) {
        saveCurrentSelection();
        if (currentIndex < quizQuestions.size() - 1) {
            currentIndex++;
            loadQuestion(currentIndex);
        }
    }

    @FXML
    private void previousQuestion(ActionEvent event) {
        saveCurrentSelection();
        if (currentIndex > 0) {
            currentIndex--;
            loadQuestion(currentIndex);
        }
    }

    @FXML
    private void confirmFinishQuiz(ActionEvent event) {
        saveCurrentSelection();
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Submit Assessment");
        alert.setHeaderText("Confirm Submission");
        alert.setContentText("Are you sure you want to finish and submit your quiz answers now?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            finishQuiz(event);
        }
    }

    private void finishQuiz(ActionEvent event) {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }

        int score = 0;
        for (int i = 0; i < quizQuestions.size(); i++) {
            Question q = quizQuestions.get(i);
            String selectedText = selectedAnswers[i];
            if (selectedText != null) {
                String val = "";
                if (selectedText.startsWith("A)")) val = q.getOptionA();
                else if (selectedText.startsWith("B)")) val = q.getOptionB();
                else if (selectedText.startsWith("C)")) val = q.getOptionC();
                else if (selectedText.startsWith("D)")) val = q.getOptionD();

                if (val.equalsIgnoreCase(q.getCorrectOption()) || selectedText.contains(q.getCorrectOption())) {
                    score++;
                }
            }
        }

        DataStore.lastScore = score;
        DataStore.lastTotal = quizQuestions.size();

        if (DataStore.currentStudent != null && DataStore.selectedQuiz != null) {
            DataStore.saveResult(
                    DataStore.currentStudent.getName(),
                    DataStore.currentStudent.getRollNo(),
                    DataStore.selectedQuiz.getTitle(),
                    score,
                    quizQuestions.size()
            );
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/studentResult.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) quizTitleLabel.getScene().getWindow();
            stage.setTitle("QuizHub - Result Summary");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
