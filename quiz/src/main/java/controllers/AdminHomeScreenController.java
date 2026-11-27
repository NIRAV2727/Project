package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.DataStore;
import models.Question;
import models.Quiz;
import models.Student;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminHomeScreenController implements Initializable {

    // Tab 1: Manage Quizzes
    @FXML private TextField quizTitleInput;
    @FXML private TextField quizSubjectInput;
    @FXML private TextField quizTimeLimitInput;
    @FXML private ComboBox<String> quizDifficultyInput;
    @FXML private Label quizStatusLabel;
    @FXML private TableView<Quiz> quizTable;
    @FXML private TableColumn<Quiz, String> qTitleCol;
    @FXML private TableColumn<Quiz, String> qSubjectCol;
    @FXML private TableColumn<Quiz, Integer> qTimeCol;
    @FXML private TableColumn<Quiz, String> qDiffCol;

    // Tab 2: Manage Questions
    @FXML private ComboBox<String> questionQuizSelect;
    @FXML private TextArea questionTextInput;
    @FXML private TextField optAInput;
    @FXML private TextField optBInput;
    @FXML private TextField optCInput;
    @FXML private TextField optDInput;
    @FXML private ComboBox<String> correctOptChoice;
    @FXML private Label questionStatusLabel;
    @FXML private TableView<Question> questionsTable;
    @FXML private TableColumn<Question, String> qBankQuizCol;
    @FXML private TableColumn<Question, String> qBankTextCol;
    @FXML private TableColumn<Question, String> qBankCorrectCol;

    // Tab 3: Manage Students
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, String> stuNameCol;
    @FXML private TableColumn<Student, String> stuEmailCol;
    @FXML private TableColumn<Student, String> stuRollCol;

    // Tab 4: Analytics & Results
    @FXML private Label statTotalStudents;
    @FXML private Label statTotalQuizzes;
    @FXML private Label statTotalAttempts;
    @FXML private TableView<DataStore.ResultRecord> allResultsTable;
    @FXML private TableColumn<DataStore.ResultRecord, String> resStuNameCol;
    @FXML private TableColumn<DataStore.ResultRecord, String> resRollCol;
    @FXML private TableColumn<DataStore.ResultRecord, String> resQuizCol;
    @FXML private TableColumn<DataStore.ResultRecord, String> resScoreCol;
    @FXML private TableColumn<DataStore.ResultRecord, String> resPctCol;
    @FXML private TableColumn<DataStore.ResultRecord, String> resGradeCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load fresh data
        DataStore.loadQuizzes();
        DataStore.loadQuestions();
        DataStore.loadStudents();
        DataStore.loadResults();

        setupQuizTab();
        setupQuestionTab();
        setupStudentTab();
        setupAnalyticsTab();
    }

    private void setupQuizTab() {
        quizDifficultyInput.getItems().setAll("Easy", "Medium", "Hard");
        quizDifficultyInput.setValue("Easy");

        qTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        qSubjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        qTimeCol.setCellValueFactory(new PropertyValueFactory<>("timeLimit"));
        qDiffCol.setCellValueFactory(new PropertyValueFactory<>("difficulty"));

        refreshQuizTable();
    }

    private void refreshQuizTable() {
        quizTable.setItems(javafx.collections.FXCollections.observableArrayList(DataStore.quizzes));
        quizTable.refresh();
        
        // Update quiz dropdown in questions tab
        questionQuizSelect.getItems().clear();
        for (Quiz q : DataStore.quizzes) {
            questionQuizSelect.getItems().add(q.getTitle());
        }
        if (!questionQuizSelect.getItems().isEmpty()) {
            questionQuizSelect.setValue(questionQuizSelect.getItems().get(0));
        }
    }

    @FXML
    private void createQuiz(ActionEvent event) {
        String title = quizTitleInput.getText() != null ? quizTitleInput.getText().trim() : "";
        String subject = quizSubjectInput.getText() != null ? quizSubjectInput.getText().trim() : "";
        String timeStr = quizTimeLimitInput.getText() != null ? quizTimeLimitInput.getText().trim() : "";
        String diff = quizDifficultyInput.getValue() != null ? quizDifficultyInput.getValue() : "Easy";

        if (title.isEmpty() || subject.isEmpty() || timeStr.isEmpty()) {
            quizStatusLabel.setStyle("-fx-text-fill: #EF4444;");
            quizStatusLabel.setText("Please fill all quiz details!");
            return;
        }

        int timeLimit;
        try {
            timeLimit = Integer.parseInt(timeStr);
        } catch (NumberFormatException e) {
            quizStatusLabel.setStyle("-fx-text-fill: #EF4444;");
            quizStatusLabel.setText("Time limit must be a valid number!");
            return;
        }

        Quiz newQuiz = new Quiz(title, subject, timeLimit, diff);
        DataStore.addQuiz(newQuiz);

        quizStatusLabel.setStyle("-fx-text-fill: #10B981;");
        quizStatusLabel.setText("Quiz '" + title + "' added successfully!");
        
        quizTitleInput.clear();
        quizSubjectInput.clear();
        quizTimeLimitInput.clear();

        refreshQuizTable();
        updateAnalytics();
    }

    @FXML
    private void deleteSelectedQuiz(ActionEvent event) {
        Quiz selected = quizTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a quiz from the table to delete.");
            alert.showAndWait();
            return;
        }
        DataStore.deleteQuiz(selected);
        refreshQuizTable();
        updateAnalytics();
    }

    private void setupQuestionTab() {
        correctOptChoice.getItems().setAll("Option A", "Option B", "Option C", "Option D");
        correctOptChoice.setValue("Option A");

        qBankQuizCol.setCellValueFactory(new PropertyValueFactory<>("quizTitle"));
        qBankTextCol.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        qBankCorrectCol.setCellValueFactory(new PropertyValueFactory<>("correctOption"));

        refreshQuestionsTable();
    }

    private void refreshQuestionsTable() {
        questionsTable.setItems(javafx.collections.FXCollections.observableArrayList(DataStore.questions));
        questionsTable.refresh();
    }

    @FXML
    private void createQuestion(ActionEvent event) {
        String quizTitle = questionQuizSelect.getValue();
        String text = questionTextInput.getText() != null ? questionTextInput.getText().trim() : "";
        String a = optAInput.getText() != null ? optAInput.getText().trim() : "";
        String b = optBInput.getText() != null ? optBInput.getText().trim() : "";
        String c = optCInput.getText() != null ? optCInput.getText().trim() : "";
        String d = optDInput.getText() != null ? optDInput.getText().trim() : "";
        String correctChoice = correctOptChoice.getValue() != null ? correctOptChoice.getValue() : "Option A";

        if (quizTitle == null || text.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
            questionStatusLabel.setStyle("-fx-text-fill: #EF4444;");
            questionStatusLabel.setText("Please complete all question fields!");
            return;
        }

        String correctValue = a;
        if ("Option B".equals(correctChoice)) correctValue = b;
        else if ("Option C".equals(correctChoice)) correctValue = c;
        else if ("Option D".equals(correctChoice)) correctValue = d;

        Question q = new Question(quizTitle, text, a, b, c, d, correctValue);
        DataStore.addQuestion(q);

        questionStatusLabel.setStyle("-fx-text-fill: #10B981;");
        questionStatusLabel.setText("Question added to '" + quizTitle + "'!");

        questionTextInput.clear();
        optAInput.clear();
        optBInput.clear();
        optCInput.clear();
        optDInput.clear();

        refreshQuestionsTable();
    }

    @FXML
    private void deleteSelectedQuestion(ActionEvent event) {
        Question selected = questionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a question from the table to delete.");
            alert.showAndWait();
            return;
        }
        DataStore.deleteQuestion(selected);
        refreshQuestionsTable();
    }

    private void setupStudentTab() {
        stuNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        stuEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        stuRollCol.setCellValueFactory(new PropertyValueFactory<>("rollno"));

        refreshStudentsTable();
    }

    private void refreshStudentsTable() {
        studentsTable.setItems(javafx.collections.FXCollections.observableArrayList(DataStore.students));
        studentsTable.refresh();
    }

    @FXML
    private void deleteSelectedStudent(ActionEvent event) {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a student account to remove.");
            alert.showAndWait();
            return;
        }
        DataStore.deleteStudent(selected);
        refreshStudentsTable();
        updateAnalytics();
    }

    private void setupAnalyticsTab() {
        resStuNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        resRollCol.setCellValueFactory(new PropertyValueFactory<>("rollNo"));
        resQuizCol.setCellValueFactory(new PropertyValueFactory<>("quizName"));
        resScoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        resPctCol.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        resGradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));

        updateAnalytics();
    }

    private void updateAnalytics() {
        statTotalStudents.setText(String.valueOf(DataStore.students.size()));
        statTotalQuizzes.setText(String.valueOf(DataStore.quizzes.size()));
        statTotalAttempts.setText(String.valueOf(DataStore.results.size()));
        allResultsTable.setItems(javafx.collections.FXCollections.observableArrayList(DataStore.results));
        allResultsTable.refresh();
    }

    @FXML
    private void logout(ActionEvent event) {
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
