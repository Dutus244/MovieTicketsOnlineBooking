package com.example.movieticketsonlinebooking.viewmodels;

import java.util.Date;

public class ReviewModel {
    private String name;
    private String review;
    private Date timeStamp;
    private double totalStarGiven;

    public ReviewModel(String name, String review, Date timeStamp, double totalStarGiven) {
        this.name = name;
        this.review = review;
        this.timeStamp = timeStamp;
        this.totalStarGiven = totalStarGiven;
    }

    public ReviewModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getTotalStarGiven() {
        return totalStarGiven;
    }

    public void setTotalStarGiven(double totalStarGiven) {
        this.totalStarGiven = totalStarGiven;
    }
}
