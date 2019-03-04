package com.uyr.yusara.homelesssavermac.Modal;

public class Posts
{
    public String uid;
    public String time;
    public String date;
    public String agencyname;
    public String categories;
    public String officenumber;
    public String email;
    public String website;
    public String facebook;
    public String twitter;
    public String location;

    //tags adalah categories dlm interface
    public String tags;

    public String service;
    public String schedule;
    public String postImage;
    public String countPosts;
    public String status;

    public Posts(){}

    public Posts(String uid, String time, String date, String agencyname, String categories, String officenumber, String email, String website, String facebook, String twitter, String location, String tags, String service, String schedule, String postImage, String countPosts, String status) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.agencyname = agencyname;
        this.categories = categories;
        this.officenumber = officenumber;
        this.email = email;
        this.website = website;
        this.facebook = facebook;
        this.twitter = twitter;
        this.location = location;
        this.tags = tags;
        this.service = service;
        this.schedule = schedule;
        this.postImage = postImage;
        this.countPosts = countPosts;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getAgencyname() {
        return agencyname;
    }

    public void setAgencyname(String agencyname) {
        this.agencyname = agencyname;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getOfficenumber() {
        return officenumber;
    }

    public void setOfficenumber(String officenumber) {
        this.officenumber = officenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getCountPosts() {
        return countPosts;
    }

    public void setCountPosts(String countPosts) {
        this.countPosts = countPosts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
