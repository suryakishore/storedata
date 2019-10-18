package com.storedata.com.notepad;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */

public class NotepadItemPojo implements Serializable {

    private String title;
    private String date;
    private ArrayList<String> notedata;
    private int noteType;
    private String fireBasedocIndex;

    public String getFireBasedocIndex() {
        return fireBasedocIndex;
    }

    public void setFireBasedocIndex(String fireBasedocIndex) {
        this.fireBasedocIndex = fireBasedocIndex;
    }

    public int getNoteType() {
        return noteType;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }



    public ArrayList<String> getNotedata() {
        return notedata;
    }

    public void setNotedata(ArrayList<String> notedata) {
        this.notedata = notedata;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "NotepadItemPojo{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", notedata=" + notedata +
                ", noteType=" + noteType +
                '}';
    }
}
