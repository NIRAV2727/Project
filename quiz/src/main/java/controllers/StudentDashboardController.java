package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.DataStore;
import models.Quiz;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StudentDashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label rollNoLabel;
    @FXML private Label totalQuizzesLabel;
    @FXML private Label attemptsLabel;
    @FXML private Label topScoreLabel;
    @FXML private TextField searchField;
    @FXML private ListView<String> quizListView;

    private List<Quiz> filteredQuizzes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DataStore.loadQuizzes();
        DataStore.loadResults();

        if (DataStore.currentStudent != null) {
            welcomeLabel.setText("Welcome, " + DataStore.currentStudent.getName() + "!");
            rollNoLabel.setText("Roll No: " + DataStore.currentStudent.getRollNo());
        }

        totalQuizzesLabel.setText(String.valueOf(DataStore.quizzes.size()));
        
        // Calculate student stats
        calculateStudentStats();

        // Search field listener
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshQuizList(newVal));
        }

        refreshQuizList("");
    }

    private void calculateStudentStats() {
        if (DataStore.currentStudent == null) return;
        
        int myAttempts = 0;
        String bestGrade = "N/A";
        
        for (DataStore.ResultRecord r : DataStore.results) {
            if (r.getRollNo().equalsIgnoreCase(DataStore.currentStudent.getRollNo()) ||
                r.getStudentName().equalsIgnoreCase(DataStore.currentStudent.getName())) {
                myAttempts++;
                if (bestGrade.equals("N/A") || r.getGrade().startsWith("A")) {
                    bestGrade = r.getGrade();
                }
            }
        }
        
        attemptsLabel.setText(String.valueOf(myAttempts));
        topScoreLabel.setText(bestGrade);
    }

    private void refreshQuizList(String filter) {
        quizListView.getItems().clear();
        filteredQuizzes.clear();
        
        String query = filter == null ? "" : filter.toLowerCase().trim();

        for (Quiz q : DataStore.quizzes) {
            if (query.isEmpty() || 
                q.getTitle().toLowerCase().contains(query) || 
                q.getSubject().toLowerCase().contains(query) ||
                q.getDifficulty().toLowerCase().contains(query)) {
                
                filteredQuizzes.add(q);
                
                int questionCount = DataStore.getQuestionsForQuiz(q.getTitle()).size();
                String itemText = String.format("📌  %-30s  |  Subject: %-18s  |  ⏱ %d Mins  |  ❓ %d Questions  |  Difficulty: [%s]",
                        q.getTitle(), q.getSubject(), q.getTimeLimit(), questionCount, q.getDifficulty().toUpperCase());
                
                quizListView.getItems().add(itemText);
            }
        }
    }

    @FXML
    private void startQuiz(javafx.event.ActionEvent event) {
        int idx = quizListView.getSelectionModel().getSelectedIndex();
        if (idx < 0 || idx >= filteredQuizzes.size()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Quiz Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a quiz from the platform catalog first.");
            alert.showAndWait();
            return;
        }

        DataStore.selectedQuiz = filteredQuizzes.get(idx);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/quizAttempt.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("QuizHub Assessment - " + DataStore.selectedQuiz.getTitle());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    @FXML
    private void viewResults(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/studentResult.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("QuizHub - Assessment History");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    @FXML
    private void viewLeaderboard(javafx.event.ActionEvent event) {
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

    @FXML
    private void logout(javafx.event.ActionEvent event) {
        DataStore.currentStudent = null;
        DataStore.selectedQuiz   = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HelloView.fxml"));
            Scene scene = new Scene(loader.load(), 950, 650);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("QuizHub Platform");
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.show();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
}
