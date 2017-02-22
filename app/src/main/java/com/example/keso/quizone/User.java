package com.example.keso.quizone;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by KESO on 05/02/2017.
 */

public class User implements Serializable{
    private int id;
    private String username;
    private String email;
    private int quizcoin;
    private Date date;

    public User(int id, String username, String email, int quizcoin, Date date) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.quizcoin = quizcoin;
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
