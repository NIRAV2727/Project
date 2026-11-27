package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import constants.DBConnection;

/**
 * DataStore - Core Data Management Layer for QuizHub Platform
 */
public class DataStore {

    public static List<Student>      students  = new ArrayList<>();
    public static List<Quiz>         quizzes   = new ArrayList<>();
    public static List<Question>     questions = new ArrayList<>();
    public static List<ResultRecord> results   = new ArrayList<>();

    public static Student currentStudent = null;
    public static Quiz    selectedQuiz   = null;
    public static int     lastScore      = 0;
    public static int     lastTotal      = 0;
    
    static {
        loadStudents();
        loadQuizzes();
        loadQuestions();
        loadResults();
    }
    
    public static void loadStudents() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name, email, password, roll_no FROM students");
             ResultSet rs = stmt.executeQuery()) {

            List<Student> dbStudents = new ArrayList<>();
            while (rs.next()) {
                dbStudents.add(new Student(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("roll_no")
                ));
            }
            students = dbStudents;
        } catch (SQLException e) {
            System.err.println("Failed to load students from DB: " + e.getMessage());
        }
    }
    
    public static boolean addStudent(Student s) {
        String sql = "INSERT INTO students (name, email, password, roll_no) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getName()); 
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPassword());
            ps.setString(4, s.getRollNo());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                boolean alreadyInList = false;
                for (Student existing : students) {
                    if (existing.getEmail().equalsIgnoreCase(s.getEmail()) ||
                        existing.getRollNo().equalsIgnoreCase(s.getRollNo())) {
                        alreadyInList = true;
                        break;
                    }
                }
                if (!alreadyInList) {
                    students.add(s);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("addStudent error: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean deleteStudent(Student s) {
        String sql = "DELETE FROM students WHERE roll_no=? OR email=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("deleteStudent error: " + e.getMessage());
        }
        students.remove(s);
        return true;
    }

    public static void loadQuizzes() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT title, subject, time_limit, difficulty FROM quizzes");
             ResultSet rs = stmt.executeQuery()) {
            List<Quiz> dbQuizzes = new ArrayList<>();
            while (rs.next()) {
                dbQuizzes.add(new Quiz(
                        rs.getString("title"),
                        rs.getString("subject"),
                        rs.getInt("time_limit"),
                        rs.getString("difficulty")
                ));
            }
            quizzes = dbQuizzes;
        } catch (Exception e) {
            System.err.println("DB loadQuizzes error: " + e.getMessage());
        }
    }
    
    public static boolean addQuiz(Quiz q) {
        if (q == null || q.getTitle() == null || q.getTitle().trim().isEmpty()) {
            return false;
        }
        String sql = "INSERT INTO quizzes (title, subject, time_limit, difficulty) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getTitle());
            ps.setString(2, q.getSubject());
            ps.setInt(3, q.getTimeLimit());
            ps.setString(4, q.getDifficulty());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("addQuiz error: " + e.getMessage());
        }
        quizzes.add(q);
        return true;
    }
    
    public static void deleteQuiz(Quiz q) {
        String sql = "DELETE FROM quizzes WHERE title=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getTitle());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("deleteQuiz error: " + e.getMessage());
        }
        quizzes.remove(q);
    }
    
    public static void loadQuestions() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT quiz_title, question_text, option_a, option_b, option_c, option_d, correct_option FROM questions");
             ResultSet rs = stmt.executeQuery()) {
            List<Question> dbQuestions = new ArrayList<>();
            while (rs.next()) {
                dbQuestions.add(new Question(
                        rs.getString("quiz_title"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option")
                ));
            }
            questions = dbQuestions;
        } catch (Exception e) {
            System.err.println("DB loadQuestions error: " + e.getMessage());
        }
    }
    
    public static List<Question> getQuestionsForQuiz(String quizTitle) {
        loadQuestions();
        List<Question> result = new ArrayList<>();
        for (Question q : questions) {
            if (q.getQuizTitle().equalsIgnoreCase(quizTitle)) {
                result.add(q);
            }
        }
        return result;
    }

    public static boolean addQuestion(Question q) {
        String sql = "INSERT INTO questions (quiz_title, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getQuizTitle());
            ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.setString(7, q.getCorrectOption());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("addQuestion error: " + e.getMessage());
        }
        questions.add(q);
        return true;
    }
    
    public static boolean deleteQuestion(Question q) {
        String sql = "DELETE FROM questions WHERE quiz_title=? AND question_text=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getQuizTitle()); 
            ps.setString(2, q.getQuestionText());
            ps.executeUpdate();
            questions.remove(q);
            return true;
        } catch (SQLException e) { 
            System.err.println("deleteQuestion error: " + e.getMessage()); 
            questions.remove(q);
            return true; 
        }
    }
    
    public static void loadResults() {
        try (Connection con = DBConnection.getConnection();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery("SELECT * FROM results ORDER BY attempt_date DESC")) {
            List<ResultRecord> dbResults = new ArrayList<>();
            while (rs.next()) {
                dbResults.add(new ResultRecord(
                        rs.getString("student_name"),
                        rs.getString("roll_no"),
                        rs.getString("quiz_name"),
                        rs.getString("score"),
                        rs.getString("percentage"),
                        rs.getString("grade")
                ));
            }
            results = dbResults;
        } catch (SQLException e) {
            System.err.println("loadResults error: " + e.getMessage());
        }
    }
    
    public static void saveResult(String studentName, String rollNo, String quizTitle, int score, int total) {
        double pct = total > 0 ? (score * 100.0 / total) : 0;
        String grade;
        if      (pct >= 90) grade = "A+";
        else if (pct >= 75) grade = "A";
        else if (pct >= 60) grade = "B";
        else if (pct >= 40) grade = "C";
        else                grade = "F";

        String scoreStr = score + "/" + total;
        String pctStr   = String.format("%.1f%%", pct);

        String sql = "INSERT INTO results (student_name, roll_no, quiz_name, score, percentage, grade) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentName); 
            ps.setString(2, rollNo);
            ps.setString(3, quizTitle);   
            ps.setString(4, scoreStr);
            ps.setString(5, pctStr);      
            ps.setString(6, grade);
            ps.executeUpdate();
        } catch (SQLException e) { 
            System.err.println("saveResult error: " + e.getMessage()); 
        }

        results.add(0, new ResultRecord(studentName, rollNo, quizTitle, scoreStr, pctStr, grade));
    }
   
    public static class ResultRecord {
        private final String studentName, rollNo, quizName, score, percentage, grade;
        public ResultRecord(String studentName, String rollNo, String quizName,
                            String score, String percentage, String grade) {
            this.studentName = studentName; 
            this.rollNo = rollNo;
            this.quizName    = quizName;    
            this.score  = score;
            this.percentage  = percentage;  
            this.grade  = grade;
        }
        public String getStudentName() { return studentName; }
        public String getRollNo()      { return rollNo;      }
        public String getQuizName()    { return quizName;    }
        public String getScore()       { return score;       }
        public String getPercentage()  { return percentage;  }
        public String getGrade()       { return grade;       }
    }
}
