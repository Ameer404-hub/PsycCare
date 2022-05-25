package com.example.psyccare.DataModels;

public class checkInModel {
    String checkInDate, checkInTime, Type, Description, ClassifiedAs, perCent = "";
    public boolean visibility;

    public checkInModel() {
    }

    public checkInModel(String checkInDate, String checkInTime, String type, String description, String classifiedAs, String perCent, boolean visibility) {
        this.checkInDate = checkInDate;
        this.checkInTime = checkInTime;
        Type = type;
        Description = description;
        ClassifiedAs = classifiedAs;
        this.perCent = perCent;
        this.visibility = visibility;
    }

    public checkInModel(String checkInDate, String checkInTime, String type, String description, String classifiedAs, String perCent) {
        this.checkInDate = checkInDate;
        this.checkInTime = checkInTime;
        Type = type;
        Description = description;
        ClassifiedAs = classifiedAs;
        this.perCent = perCent;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getClassifiedAs() {
        return ClassifiedAs;
    }

    public void setClassifiedAs(String classifiedAs) {
        ClassifiedAs = classifiedAs;
    }

    public String getPerCent() {
        return perCent;
    }

    public void setPerCent(String perCent) {
        this.perCent = perCent;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public boolean isVisibility() {
        return visibility;
    }
}