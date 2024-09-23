package com.yahya.shadow.MyRatingHistory;

public class RatingObject {
    private String raterUsername,raterId;
    private float rateVal;


    public RatingObject(String raterUsername, float rateVal, String raterId) {
        this.raterUsername = raterUsername;
        this.rateVal = rateVal;
        this.raterId = raterId;
    }

    public String getRaterUsername() {
        return raterUsername;
    }

    public float getRateVal() {
        return rateVal;
    }

    public String getRaterId() {
        return raterId;
    }
}
