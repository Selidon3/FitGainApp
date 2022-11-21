package com.example.fitgain;

public class Food {
    private String type;
    private String calories;

    public Food(String type, String calories) {
        this.type = type;
        this.calories = calories;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
}
