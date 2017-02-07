package com.example.keso.quizone;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KESO on 02/02/2017.
 */

public class Question implements Parcelable{
    private String text;
    private int id;
    private String answer;
    private String badAnswer1;
    private String badAnswer2;
    private String badAnswer3;

    public Question(String text, int id, String answer, String badAnswer1, String badAnswer2, String badAnswer3) {
        this.text = text;
        this.id = id;
        this.answer = answer;
        this.badAnswer1 = badAnswer1;
        this.badAnswer2 = badAnswer2;
        this.badAnswer3 = badAnswer3;
    }

    public Question(Parcel parcel) {
        this.text = parcel.readString();
        this.id = parcel.readInt();
        this.answer = parcel.readString();
        this.badAnswer1 = parcel.readString();
        this.badAnswer2 = parcel.readString();
        this.badAnswer3 = parcel.readString();

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getBadAnswer1() {
        return badAnswer1;
    }

    public void setBadAnswer1(String badAnswer1) {
        this.badAnswer1 = badAnswer1;
    }

    public String getBadAnswer2() {
        return badAnswer2;
    }

    public void setBadAnswer2(String badAnswer2) {
        this.badAnswer2 = badAnswer2;
    }

    public String getBadAnswer3() {
        return badAnswer3;
    }

    public void setBadAnswer3(String badAnswer3) {
        this.badAnswer3 = badAnswer3;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeInt(id);
        dest.writeString(answer);
        dest.writeString(badAnswer1);
        dest.writeString(badAnswer2);
        dest.writeString(badAnswer3);
    }

    // Method to recreate a Question from a Parcel
    public static Creator<Question> CREATOR = new Creator<Question>() {

        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }

    };
}
