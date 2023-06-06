package com.example.regagest.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

@SuppressWarnings("unused")
public class User implements Serializable {
    String id, user, pass;
    int quota;

    public User(String id, String user) {
        this.id = id;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", quota=" + quota +
                '}';
    }
}
