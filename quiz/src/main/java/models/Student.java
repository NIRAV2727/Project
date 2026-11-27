/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author KSC67
 */
public class Student {

    private String name;
    private String email;
    private String password;
    private String rollno;
    
    public Student(String name, String email, String password, String rollno) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.rollno = rollno;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getRollno() { return rollno; }

    public void setRollno(String rollno) { this.rollno = rollno; }

    public String getRollNo() {
        return rollno;
    }
}
