package com.example.idea_reminder.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.Date;

public class Idea {
    String ideaId;
    String ideaTitle;
    String ideaText;
    boolean remindMe;
    LocalDate nextRemindDate;
    int gap;
    public String getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(String ideaId) {
        this.ideaId = ideaId;
    }

    public String getIdeaTitle() {
        return ideaTitle;
    }

    public void setIdeaTitle(String ideaTitle) {
        this.ideaTitle = ideaTitle;
    }

    public String getIdeaText() {
        return ideaText;
    }

    public void setIdeaText(String ideaText) {
        this.ideaText = ideaText;
    }

    public boolean isRemindMe() {
        return remindMe;
    }

    public void setRemindMe(boolean remindMe) {
        this.remindMe = remindMe;
    }

    public LocalDate getNextRemindDate() {
        return nextRemindDate;
    }

    public void setNextRemindDate(LocalDate nextRemindDate) {
        this.nextRemindDate = nextRemindDate;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }
}
