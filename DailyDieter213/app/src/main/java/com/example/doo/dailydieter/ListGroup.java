package com.example.doo.dailydieter;

public class ListGroup {

    //추가한 변수
    public String id;
    public String groupName;
    public String goal;
    public String userCount;

    public ListGroup() {

    }

    public ListGroup(String groupName, String userCount) {
        this.groupName=groupName;
        this.userCount=userCount;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id=id;}

    public String getGroupname() {return groupName;}

    public void setGroupname(String groupName) {this.groupName=groupName;}

    public String getGoal() {return goal;}

    public void setGoal(String goal) {this.goal=goal;}

    public String getUserCount() {return userCount;}

    public void setUserCount(String userCount) {this.userCount=userCount;}

    public ListGroup( String groupName, String goal, String userCount){
        this.groupName=groupName;
        this.goal=goal;
        this.userCount=userCount;
    }
}
