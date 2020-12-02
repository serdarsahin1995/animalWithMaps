package com.example.petswithmaps.Models;

public class FcmModel {
    public static final String TOKEN ="token" ;
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_MESSAGE = "message";
    public static final String NOTIFICATION_TO = "to";
    public static final String NOTIFICATION_DATA = "data";
    public static final String CHANNEL_ID = "animal_With_Map";
    public static final String CHANNEL_NAME ="animalWithMap";
    public static final String CHANNEL_DESC ="animal With Map";
    String title;
    String message;
    String konum1;
    String konum2;
    public FcmModel(String title, String message,String konum1,String konum2) {
        this.title = title;
        this.message = message;
        this.konum1=konum1;
        this.konum2=konum2;
    }

    public FcmModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKonum1() {
        return konum1;
    }

    public void setKonum1(String konum1) {
        this.konum1 = konum1;
    }

    public String getKonum2() {
        return konum2;
    }

    public void setKonum2(String konum2) {
        this.konum2 = konum2;
    }
}
