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
    private int rank;
    private String username;
    private String difficultyToString;

    public Result(){

    }

    public Result(int id, int userid, ArrayList<Integer> result, int difficulty, int category, int total, int rank, String username) {
        this.id = id;
        this.userid = userid;
        this.result = result;
        this.difficulty = difficulty;
        this.category = category;
        this.total = total;
        this.rank = rank;
        this.username = username;
    }

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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCategoryToString() {
        String sReturn = "";
        if(this.category==1){
            sReturn = "Film";
        }else if(this.category==2){
            sReturn = "Musik";
        }else if(this.category==3){
            sReturn = "Sport";
        }
        return sReturn;
    }

    public String getDifficultyToString() {
        String sReturn = "";
        if(this.difficulty==1){
            sReturn = "Lätt";
        }else if(this.difficulty==2){
            sReturn = "Medel";
        }else if(this.difficulty==3){
            sReturn = "Svår";
        }
        return sReturn;
    }
}
