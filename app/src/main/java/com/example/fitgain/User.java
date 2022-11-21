package com.example.fitgain;

public class User {
    private String name;
    private String email;
    private String firstTimeLogIn;
    private String manager;
    private String currentWeight;
    private String gender;
    private String targetWeight;


    public User(String name, String email){
        this.email = email;
        this.name = name;
        this.firstTimeLogIn = "1";
        this.manager = "0";
        this.currentWeight = "0";
        this.targetWeight = "0";
        this.gender = "male";

    }

    public String getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(String currentWeight) {
        this.currentWeight = currentWeight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(String targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getFirstTimeLogIn() {
        return firstTimeLogIn;
    }

    public void setFirstTimeLogIn(String firstTimeLogIn) {
        this.firstTimeLogIn = firstTimeLogIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
