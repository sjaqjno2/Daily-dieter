package com.example.doo.dailydieter;

import java.util.Date;

public class list_item {
    public int getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(int profile_image) {
        this.profile_image = profile_image;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWrite_date() {
        return date;
    }

    public void setWrite_date(Date write_date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    //추가한 변수
    public int profile_image;
    public String nickname;
    public String title;
    public String date;
    public String content;
    public int count = 0;

    public list_item( String title,  String content, String id, String date, int count) {
        this.title = title;
        this.content = content;
        this.nickname = id;
        this.date = date;
        this.count = count;
    }

    public list_item() {
    }

}