package com.example.keso.quizone;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KESO on 16/02/2017.
 */

public class Result implements Serializable {
    private int id;
    private int userid;
    private ArrayList<Integer> result = new ArrayList<>();
    private int difficulty;
    private int category;
    private int total;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addResult(int result){
        this.result.add(result);
    }

    public int getSpecificResult(int index){
        return result.get(index);
    }

    public ArrayList<Integer> getResult(){
        return this.result;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void calculateTotal(){
        total = 0;
        for(int i = 0; i<result.size(); i++){
            total+=result.get(i);
        }
    }
}
