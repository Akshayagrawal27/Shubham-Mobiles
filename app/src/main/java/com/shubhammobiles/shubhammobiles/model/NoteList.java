package com.shubhammobiles.shubhammobiles.model;

import java.util.HashMap;

/**
 * Created by Akshay on 11-03-2018.
 */

public class NoteList {

    private String noteDate;
    private String notePadText;
    private String owner;
    private HashMap<String, Object> timeStampLastChanged;

    public NoteList() {
    }

    public NoteList(String noteDate, String notePadText, String owner, HashMap<String, Object> timeStampLastChanged) {
        this.noteDate = noteDate;
        this.notePadText = notePadText;
        this.owner = owner;
        this.timeStampLastChanged = timeStampLastChanged;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getNotePadText() {
        return notePadText;
    }

    public void setNotePadText(String notePadText) {
        this.notePadText = notePadText;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public HashMap<String, Object> getTimeStampLastChanged() {
        return timeStampLastChanged;
    }

    public void setTimeStampLastChanged(HashMap<String, Object> timeStampLastChanged) {
        this.timeStampLastChanged = timeStampLastChanged;
    }
}
