package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.DataStore;
import models.Question;
import models.Quiz;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for AddQuizFXML.fxml
 */
public class AddQuizFXMLController implements Initializable {

    // Quiz form fields
    @FXML private TextField titleField;
    @FXML private TextField subjectField;
    @FXML private TextField timeLimitField;
    @FXML private ComboBox<String> difficultyBox;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private ListView<String> quizListView;

    // Question form fields
    @FXML private ComboBox<String> quizSelectBox;
    @FXML private TextField questionField;
    @FXML private TextField optAField;
    @FXML private TextField optBField;
    @FXML private TextField optCField;
    @FXML private TextField optDField;
    @FXML private ComboBox<String> correctAnswerBox;
    @FXML private Label qErrorLabel;
    @FXML private Label qSuccessLabel;
    @FXML private ListView<String> questionListView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (difficultyBox != null) {
            difficultyBox.getItems().setAll("Easy", "Medium", "Hard");
            difficultyBox.setValue("Easy");
        }
        if (correctAnswerBox != null) {
            correctAnswerBox.getItems().setAll("Option A", "Option B", "Option C", "Option D");
            correctAnswerBox.setValue("Option A");
        }
        refreshQuizList();
    }

    private void refreshQuizList() {
        DataStore.loadQuizzes();
        if (quizListView != null) {
            quizListView.getItems().clear();
            for (Quiz q : DataStore.quizzes) {
                quizListView.getItems().add(q.getTitle() + " (" + q.getSubject() + " - " + q.getTimeLimit() + " mins)");
            }
        }
        if (quizSelectBox != null) {
            quizSelectBox.getItems().clear();
            for (Quiz q : DataStore.quizzes) {
                quizSelectBox.getItems().add(q.getTitle());
            }
            if (!quizSelectBox.getItems().isEmpty()) {
                quizSelectBox.setValue(quizSelectBox.getItems().get(0));
            }
        }
        refreshQuestionList();
    }

    private void refreshQuestionList() {
        if (questionListView != null) {
            questionListView.getItems().clear();
            String selQuiz = quizSelectBox != null ? quizSelectBox.getValue() : null;
            if (selQuiz != null) {
                for (Question q : DataStore.getQuestionsForQuiz(selQuiz)) {
                    questionListView.getItems().add(q.getQuestionText() + " [Ans: " + q.getCorrectOption() + "]");
                }
            }
        }
    }

    @FXML
    private void addQuiz(ActionEvent event) {
        if (errorLabel != null) errorLabel.setText("");
        if (successLabel != null) successLabel.setText("");

        String title = titleField != null ? titleField.getText().trim() : "";
        String subject = subjectField != null ? subjectField.getText().trim() : "";
        String timeStr = timeLimitField != null ? timeLimitField.getText().trim() : "";
        String diff = difficultyBox != null ? difficultyBox.getValue() : "Easy";

        if (title.isEmpty() || subject.isEmpty() || timeStr.isEmpty()) {
            if (errorLabel != null) errorLabel.setText("Please fill all quiz details!");
            return;
        }

        int timeLimit;
        try {
            timeLimit = Integer.parseInt(timeStr);
        } catch (NumberFormatException e) {
            if (errorLabel != null) errorLabel.setText("Time limit must be a number!");
            return;
        }

        Quiz q = new Quiz(title, subject, timeLimit, diff);
        DataStore.addQuiz(q);

        if (successLabel != null) successLabel.setText("Quiz added successfully!");
        if (titleField != null) titleField.clear();
        if (subjectField != null) subjectField.clear();
        if (timeLimitField != null) timeLimitField.clear();

        refreshQuizList();
    }

    @FXML
    private void deleteQuiz(ActionEvent event) {
        if (quizListView == null) return;
        int idx = quizListView.getSelectionModel().getSelectedIndex();
        if (idx >= 0 && idx < DataStore.quizzes.size()) {
            Quiz q = DataStore.quizzes.get(idx);
            DataStore.deleteQuiz(q);
            refreshQuizList();
        }
    }

    @FXML
    private void onQuizSelectChanged(ActionEvent event) {
        refreshQuestionList();
    }

    @FXML
    private void addQuestion(ActionEvent event) {
        if (qErrorLabel != null) qErrorLabel.setText("");
        if (qSuccessLabel != null) qSuccessLabel.setText("");

        String quizTitle = quizSelectBox != null ? quizSelectBox.getValue() : null;
        String text = questionField != null ? questionField.getText().trim() : "";
        String a = optAField != null ? optAField.getText().trim() : "";
        String b = optBField != null ? optBField.getText().trim() : "";
        String c = optCField != null ? optCField.getText().trim() : "";
        String d = optDField != null ? optDField.getText().trim() : "";
        String choice = correctAnswerBox != null ? correctAnswerBox.getValue() : "Option A";

        if (quizTitle == null || text.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
            if (qErrorLabel != null) qErrorLabel.setText("Please fill all question fields!");
            return;
        }

        String correctValue = a;
        if ("Option B".equals(choice)) correctValue = b;
        else if ("Option C".equals(choice)) correctValue = c;
        else if ("Option D".equals(choice)) correctValue = d;

        Question q = new Question(quizTitle, text, a, b, c, d, correctValue);
        DataStore.addQuestion(q);

        if (qSuccessLabel != null) qSuccessLabel.setText("Question added!");
        if (questionField != null) questionField.clear();
        if (optAField != null) optAField.clear();
        if (optBField != null) optBField.clear();
        if (optCField != null) optCField.clear();
        if (optDField != null) optDField.clear();

        refreshQuestionList();
    }

    @FXML
    private void deleteQuestion(ActionEvent event) {
        if (questionListView == null || quizSelectBox == null) return;
        String selQuiz = quizSelectBox.getValue();
        if (selQuiz == null) return;

        int idx = questionListView.getSelectionModel().getSelectedIndex();
        var questions = DataStore.getQuestionsForQuiz(selQuiz);
        if (idx >= 0 && idx < questions.size()) {
            Question q = questions.get(idx);
            DataStore.deleteQuestion(q);
            refreshQuestionList();
        }
    }
}
