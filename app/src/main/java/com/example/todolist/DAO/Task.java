package com.example.todolist.DAO;

import android.app.Activity;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import com.example.todolist.tools.TomToolkit;

import java.io.Serializable;
import java.util.Locale;


public class Task implements Comparable<Task>, Serializable {
    private static long IDGENERATOR = 0;

    @Override
    public int compareTo(Task o) {
        if(this.hour!=o.hour){
            return this.hour-o.hour;
        }else{
            return this.minute-o.minute;
        }
    }

    public enum Importance{
        NOT_IMPOTANT("NOT IMPORATANT"),IMPORTANT("IMPORANT"),VERY_IMPORTANT("VERY IMPORTANT");
        private String s;
        private Importance(String s) {
            this.s = s;
        }

        @Override
        public String toString(){
            return s;
        }

        public static Importance toImportance(String s){
            String upS = s.toUpperCase(Locale.ROOT);
            switch (upS){
                case "NOT IMPORATANT":
                    return NOT_IMPOTANT;
                case "IMPORTANT":
                    return IMPORTANT;
                case "VERY IMPORTATNT":
                    return VERY_IMPORTANT;
                default:
                    return null;
            }

        }
    }



    private String ID;
    private int hour;
    private int minute;
    private String name;
    private String description;
    private Importance importance = Importance.NOT_IMPOTANT;
    private boolean taskDown = false;
    private String picPath;

    public Task (Activity activity, @Nullable int hour, @Nullable int minute, String name, @Nullable String description, @Nullable String importance, @Nullable String picPath){
        if(IDGENERATOR==0){
            IDGENERATOR = TomToolkit.readID(activity);
        }
        this.ID = String.valueOf(IDGENERATOR++);
        this.hour = hour;
        this.minute = minute;
        this.name = name;
        this.description = description;
        if(importance!=null){
            this.importance = Importance.toImportance(importance);
        }

        this.picPath = picPath;
    }



    @Override
    public String toString() {
        return "Task{" +
                  "ID="+ID +
//                "hour=" + hour +
//                ", minute=" + minute +
                ", name='" + name + '\'' +
//                ", description='" + description + '\'' +
//                ", importance=" + importance +
//                ", taskDown=" + taskDown +
//                ", picPath='" + picPath + '\'' +

                '}'+"\n";
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Importance getImportance() {
        return importance;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }

    public boolean isTaskDown() {
        return taskDown;
    }

    public void setTaskDown(boolean taskDown) {
        this.taskDown = taskDown;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
