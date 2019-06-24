package com.example.doo.dailydieter;

public class ListUpdateDiet {

    public ListUpdateDiet (String namefood, float calorie){
        this.namefood = namefood;
        this.calorie = calorie;
    }

    public String namefood;
    public float calorie;

    public String getNamefood() {
        return namefood;
    }

    public void setNamefood(String namefood) {
        this.namefood = namefood;
    }

    public float getCalorie() {
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }
}
