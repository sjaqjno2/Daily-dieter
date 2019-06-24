package com.example.doo.dailydieter;

public class ListInfo {

    private String category;
    private String categoryValue;

    public ListInfo(String category, String categoryValue){
        this.category=category;
        this.categoryValue=categoryValue;
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryValue() {
        return categoryValue;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCategoryValue(String categoryValue) {
        this.categoryValue = categoryValue;
    }
}
