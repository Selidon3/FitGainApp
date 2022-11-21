package com.example.fitgain;

public class UserForManaging {
    String name, email, Gender,UID;

    public UserForManaging(String name, String email, String gender, String UID) {
        this.name = name;
        this.email = email;
        this.Gender = gender;
        this.UID = UID;
    }
    public UserForManaging(String name, String email, String UID) {
        this.name = name;
        this.email = email;
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
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

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }
}
