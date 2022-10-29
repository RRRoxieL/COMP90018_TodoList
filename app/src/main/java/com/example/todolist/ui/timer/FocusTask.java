package com.example.todolist.ui.timer;

public class FocusTask {
    private String taskName;
    private String taskID;
    private int minutesFocused;
    private String date;
    private String time;
    private String uid;


    public FocusTask(String uid, String taskName, String taskID, int minutesFocused, String date, String time) {
        this.uid = uid;
        if (taskName == null || taskID == null) {
            this.taskName = "None";
            this.taskID = "None";
        } else {
            this.taskName = taskName;
            this.taskID = taskID;
        }

        this.minutesFocused = minutesFocused;
        this.date = String.valueOf(date);
        this.time = String.valueOf(time);
    }

    public void setUid(String Uid){
        this.uid = Uid;
    }

    public String getUid(){
        return this.uid;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public int getMinutesFocused() {
        return minutesFocused;
    }

    public void setMinutesFocused(int minutesFocused) {
        this.minutesFocused = minutesFocused;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
