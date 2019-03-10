package com.uyr.yusara.homelesssavermac.Modal;

public class Posts_Homeless
{

    public String uid;
    public String postImage;
    public String name;
    public String age;
    public String relationship;
    public String occupation;
    public String location;
    public String decription;
    public String reportnumber;
    public String time;
    public String date;
    public String countPosts;

    public Posts_Homeless() {}

    public Posts_Homeless(String uid, String postImage, String name, String age, String relationship, String occupation, String decription, String reportnumber, String location, String time, String date, String countPosts) {
        this.uid = uid;
        this.postImage = postImage;
        this.name = name;
        this.age = age;
        this.relationship = relationship;
        this.occupation = occupation;
        this.decription = decription;
        this.reportnumber = reportnumber;
        this.location = location;
        this.time = time;
        this.date = date;
        this.countPosts = countPosts;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public String getReportnumber() {
        return reportnumber;
    }

    public void setReportnumber(String reportnumber) {
        this.reportnumber = reportnumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCountPosts() {
        return countPosts;
    }

    public void setCountPosts(String countPosts) {
        this.countPosts = countPosts;
    }
}
