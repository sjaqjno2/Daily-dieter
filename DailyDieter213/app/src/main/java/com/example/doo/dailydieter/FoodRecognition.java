package com.example.doo.dailydieter;

public class FoodRecognition {
    String namefood;
    float score;
    @Override
    public String toString() {
        return "FoodRecognition{" +
                "namefood='" + namefood + '\'' +
                ", score='" + score + '\'' +
                '}';
    }

    public FoodRecognition(float score, String namefood) {
        this.score = score;
        this.namefood = namefood;
    }

    public String getNamefood() {
        return this.namefood;
    }

    public void setNamefood(String namefood) {
        this.namefood = namefood;
    }

    public float getScore() {
        return this.score;
    }

    public void setScore(float score) {
        this.score = score;
    }

}
