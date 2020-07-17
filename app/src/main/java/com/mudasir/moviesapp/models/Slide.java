package com.mudasir.moviesapp.models;

public class Slide {

    private String thumbnail;
    private String title;
    private String streamingLink;
    private String key;

    public Slide(String thumbnail, String title, String streamingLink, String key) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.streamingLink = streamingLink;
        this.key = key;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStreamingLink() {
        return streamingLink;
    }

    public void setStreamingLink(String streamingLink) {
        this.streamingLink = streamingLink;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Slide() {
    }

}
