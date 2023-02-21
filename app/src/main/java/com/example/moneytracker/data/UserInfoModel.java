package com.example.moneytracker.data;

import com.example.moneytracker.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class UserInfoModel {
    private String uid;
    private String name;
    private String email;

    public UserInfoModel() {
        // Required default constructor for Firebase Realtime Database
    }

    public UserInfoModel(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.NODE_UID, uid);
        result.put(Constants.NODE_NAME, name);
        result.put(Constants.NODE_EMAIL, email);
        return result;
    }

}
