package com.example.myapplication;

public class getImage {

    private String date;
    private String url;
    private String title;
    private long id;

    public getImage(String date, String url, String title, long id) {
        this.date = date;
        this.url = url;
        this.title = title;
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }
}