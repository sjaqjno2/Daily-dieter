package com.example.doo.dailydieter;


public class ListDiet {

    public int getId() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }
    public String getUserId() { return userid;}

    public void setUserId(String userid) { this.userid = userid;}

    public String getNamefood1() {return namefood1;}

    public void setNamefood1(String namefood1) {this.namefood1=namefood1;}

    public String getNamefood2() {return namefood2;}

    public void setNamefood2(String namefood2) {this.namefood2=namefood2;}

    public String getNamefood3() {return namefood3;}

    public void setNamefood3(String namefood3) {this.namefood3=namefood3;}

    public String getNamefood4() {return namefood4;}

    public void setNamefood4(String namefood4) {this.namefood4=namefood4;}

    public String getNamefood5() {return namefood5;}

    public void setNamefood5(String namefood5) {this.namefood5=namefood5;}

    public Float getCalorie1() {return calorie1;}

    public void setCalorie1(Float calorie1) {this.calorie1=calorie1;}

    public Float getCalorie2() {return calorie2;}

    public void setCalorie2(Float calorie2) {this.calorie2=calorie2;}

    public Float getCalorie3() {return calorie3;}

    public void setCalorie3(Float calorie3) {this.calorie3=calorie3;}

    public Float getCalorie4() {return calorie4;}

    public void setCalorie4(Float calorie4) {this.calorie4=calorie4;}

    public Float getCalorie5() {return calorie5;}

    public int getEatenWeight1() {return eaten_weight1;}

    public void setEatenWeight1(int eaten_weight1) {this.eaten_weight1=eaten_weight1;}

    public int getEatenWeight2() {return eaten_weight2;}

    public void setEatenWeight2(int eaten_weight2) {this.eaten_weight2=eaten_weight2;}

    public int getEatenWeight3() {return eaten_weight3;}

    public void setEatenWeight3(int eaten_weight3) {this.eaten_weight3=eaten_weight3;}

    public int getEatenWeight4() {return eaten_weight4;}

    public void setEatenWeight4(int eaten_weight4) {this.eaten_weight4=eaten_weight4;}

    public int getEatenWeight5() {return eaten_weight5;}

    public void setEatenWeight5(int eaten_weight5) {this.eaten_weight5=eaten_weight5;}

    public Float getSum_prot() {return sum_prot;}

    public void setSum_prot(Float sum_prot) {this.sum_prot=sum_prot;}

    public Float getSum_carb() {return sum_carb;}

    public void setSum_carb(Float sum_carb) {this.sum_carb=sum_carb;}

    public Float getSum_fat() {return sum_fat;}

    public void setSum_fat(Float sum_fat) {this.sum_fat=sum_fat;}

    public Float getSum_salt() {return sum_salt;}

    public void setSum_salt(Float sum_salt) {this.sum_salt=sum_salt;}

    public String getDate() {return date;}

    public void setDate(String date) {this.date=date;}

    public String getMealtime() {return mealtime;}

    public void setMealtime(String mealtime) {this.mealtime=mealtime;}

    //추가한 변수
    public int id;
    public String userid;
    public String namefood1;
    public String namefood2;
    public String namefood3;
    public String namefood4;
    public String namefood5;
    public Float calorie1;
    public Float calorie2;
    public Float calorie3;
    public Float calorie4;
    public Float calorie5;
    public Float sum_prot;
    public Float sum_carb;
    public Float sum_fat;
    public Float sum_salt;
    public String date;
    public String mealtime;
    public int eaten_weight1;
    public int eaten_weight2;
    public int eaten_weight3;
    public int eaten_weight4;
    public int eaten_weight5;

    public ListDiet( int id, String userid, String namefood1, Float calorie1, int eaten_weight1,
                     String namefood2, Float calorie2, int eaten_weight2,
                     String namefood3, Float calorie3, int eaten_weight3,
                     String namefood4, Float calorie4, int eaten_weight4,
                     String namefood5, Float calorie5, int eaten_weight5,
                     Float sum_carb, Float sum_prot,
                     Float sum_fat, Float sum_salt, String date, String mealtime) {
        this.id = id;
        this.userid=userid;
        this.namefood1=namefood1;
        this.calorie1=calorie1;
        this.eaten_weight1=eaten_weight1;
        this.namefood2=namefood2;
        this.calorie2=calorie2;
        this.eaten_weight2=eaten_weight2;
        this.namefood3=namefood3;
        this.calorie3=calorie3;
        this.eaten_weight3= eaten_weight3;
        this.namefood4=namefood4;
        this.calorie4=calorie4;
        this.eaten_weight4 = eaten_weight4;
        this.namefood5=namefood5;
        this.calorie5=calorie5;
        this.eaten_weight5 = eaten_weight5;
        this.sum_carb=sum_carb;
        this.sum_prot=sum_prot;
        this.sum_fat=sum_fat;
        this.sum_salt=sum_salt;
        this.date=date;
        this.mealtime=mealtime;
    }

}