package com.example.psyccare.DataModels;

public class UserSignupData {
    String Name, Email, Pass;

    public UserSignupData() {
    }

    public UserSignupData(String name, String email, String pass) {
        Name = name;
        Email = email;
        Pass = pass;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPass() {
        return Pass;
    }

    public void setPass(String pass) {
        Pass = pass;
    }
}
