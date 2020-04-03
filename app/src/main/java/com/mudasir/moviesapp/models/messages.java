package com.mudasir.moviesapp.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class messages {

    private String username;
    private String message;

    public messages() {
    }

    public messages(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username",username);
        result.put("message",message);
        return result;
    }

}
