package com.mudasir.moviesapp.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Group {

    private String groupName;
    private String groupCode;
    private String movieUrl;
    private String title;
    private String Status;
    private String Admin;



    public Group() {

    }

    public Group(String groupName, String groupCode,String title, String movieUrl ,String status ,String admin) {
        this.groupName = groupName;
        this.groupCode = groupCode;
        this.movieUrl = movieUrl;
        this.title=title;
        this.Status=status;
        this.Admin=admin;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }


    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public String getStatus() {
        return Status;
    }

    public String getAdmin() {
        return Admin;
    }

    public void setAdmin(String admin) {
        Admin = admin;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("movieUrl", movieUrl);
        result.put("groupName",groupName);
        result.put("groupCode",groupCode);
        result.put("title",title);
        result.put("status",Status);
        result.put("admin",Admin);

        return result;
    }


}
