package com.storedata.com;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */
public class Note {
    int id;
    String note;

    public Note(int id, String note) {
        this.id = id;
        this.note = note;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", note='" + note + '\'' +
                '}';
    }
// getters an setters
}