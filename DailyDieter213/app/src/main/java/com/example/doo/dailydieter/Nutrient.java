package com.example.doo.dailydieter;


public class Nutrient {

    float carbohydrate;
    float protein;
    float fat;
    float salt;
    float calorie;
    float weightfood;
    String namefood;
    @Override
    public String toString() {
        return "Nutrient{" +
                "name = " + namefood + '\'' +
                "carbohydrate='" + carbohydrate + '\'' +
                ", protein='" + protein + '\'' +
                ", fat='" + fat + '\'' +
                ", salt='" + salt + '\'' +
                ", calorie='" + calorie + '\'' +
                ", weightfood='" + weightfood + '\'' +
                '}';
    }

    public Nutrient(String namefood, float calorie) {
        this.namefood = namefood;
        this. calorie = calorie;
    }
    public Nutrient(String namefood, Float carbohydrate, Float protein, Float fat, Float salt, Float calorie, Float weightfood) {
        this.namefood = namefood;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
        this.salt = salt;
        this.calorie = calorie;
        this.weightfood = weightfood;
    }
    public Nutrient(){
    }
    public String getNamefood() {
        return this.namefood;
    }

    public void setNamefood(String namefood) {
        this.namefood = namefood;
    }

    public float getCarbohydrate() {
        return this.carbohydrate;
    }

    public void setCrabohydrate(float carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public float getProtein() {
        return this.protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFat() {
        return this.protein;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getSalt() {
        return this.salt;
    }

    public void setSalt(float salt) {
        this.salt = salt;
    }

    public float getCalorie() {
        return this.calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public float getWeightfood() {
        return this.weightfood;
    }

    public void setWeightfood(float weightfood) {
        this.weightfood = weightfood;
    }
}