package com.uyr.yusara.homelesssavermac.Modal;

public class Posts_Homeless
{

    public String uid;
    public String postImage;
    public String postImage2;
    public String postImage3;
    public String fullname;
    public String age;
    public String illness;
    public String gender;
    public String martialstatus;
    public String occupation;
    public String location;
    public String decription;
    public String reportnumber;
    public String time;
    public String date;
    public String countPosts;

    public Posts_Homeless() {}

    public Posts_Homeless(String uid, String postImage, String postImage2, String postImage3, String fullname, String age, String relationship, String occupation, String decription, String reportnumber, String location, String time, String date, String countPosts, String gender, String illness) {
        this.uid = uid;
        this.postImage = postImage;
        this.postImage2 = postImage2;
        this.postImage3 = postImage3;
        this.fullname = fullname;
        this.age = age;
        this.occupation = occupation;
        this.decription = decription;
        this.reportnumber = reportnumber;
        this.location = location;
        this.time = time;
        this.date = date;
        this.countPosts = countPosts;
        this.gender = gender;
        this.illness = illness;
    }

    public String getIllness() {
        return illness;
    }

    public void setIllness(String illness) {
        this.illness = illness;
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

    public String getPostImage2() {
        return postImage2;
    }

    public void setPostImage2(String postImage2) {
        this.postImage2 = postImage2;
    }

    public String getPostImage3() {
        return postImage3;
    }

    public void setPostImage3(String postImage3) {
        this.postImage3 = postImage3;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMartialstatus() {
        return martialstatus;
    }

    public void setMartialstatus(String martialstatus) {
        this.martialstatus = martialstatus;
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
