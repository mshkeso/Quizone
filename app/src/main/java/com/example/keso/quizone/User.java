package com.example.keso.quizone;

/**
 * Created by KESO on 05/02/2017.
 */

public class User {
    private int id;
    private String username;
    private String email;
    private int quizcoin;

    public User(int id, String username, String email, int quizcoin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.quizcoin = quizcoin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getQuizcoin() {
        return quizcoin;
    }

    public void setQuizcoin(int quizcoin) {
        this.quizcoin = quizcoin;
    }

}
