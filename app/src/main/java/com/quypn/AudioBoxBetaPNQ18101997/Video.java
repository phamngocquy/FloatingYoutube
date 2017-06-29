package com.quypn.AudioBoxBetaPNQ18101997;

import java.io.Serializable;


public class Video implements Serializable {

    private String videoId;
    private String title;
    private String url;
    private String  channelTitle;
    private String viewCount;
    private int id;


    public Video(String videoId, String title, String url, String channelTitle) {
        this.title = title;
        this.videoId = videoId;
        this.url = url;
        this.channelTitle = channelTitle;

    }

    public Video()
    {

    }
    public Video(int id ,String videoId, String title, String url,String channelTitle,String viewCount) {
        this.id = id;
        this.title = title;
        this.videoId = videoId;
        this.url = url;
        this.channelTitle = channelTitle;
        this.viewCount = viewCount;
    }

    public Video(int id ,String videoId, String title, String url,String channelTitle) {
        this.id = id;
        this.title = title;
        this.videoId = videoId;
        this.url = url;
        this.channelTitle = channelTitle;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getChannelTitle() {

        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
