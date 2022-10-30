package com.example.todolist.DAO;

import android.widget.Toast;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

//a class which serve as the data type of one day in the calendar.
public class DateTask implements Serializable{


    private Date date;
    private HashMap<String,Task> tasks;
    private int allTaskNum = 0;
    private int finishedTaskNum = 0;
    private String userID;

    public DateTask(){

    }

    public DateTask(Date date, String userID){
        this.date = date;
        this.userID = userID;
    }

    public boolean addTask(Task task){
        //slow start, considering
        if(tasks==null){
            tasks = new HashMap<>();
        }

        if (tasks.containsKey(task.getID())){
            tasks.put(task.getID(),task);
            return false;
        }else{
            tasks.put(task.getID(),task);
            this.allTaskNum+=1;
            if(task.isTaskDown()){
                this.finishedTaskNum+=1;
            }
            return true;
        }
    }

    public boolean deleteTask(String id){
        if(id == null){
            return false;
        }
        Task removedTask = tasks.remove(id);
        if(removedTask!=null){
            this.allTaskNum -= 1;
            if(removedTask.isTaskDown()){
                this.finishedTaskNum-=1;
            }
            return true;
        }else{
            return false;
        }
    }

    public void downTask(Task task){
        if(task.isTaskDown()){
            this.finishedTaskNum+=1;
        }else{
            this.finishedTaskNum-=1;
        }
    }


    @Override
    public String toString() {
        return "DateTask{" +
                "date=" + date +
                "\n tasks=" + tasks +
                '}';
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public HashMap<String, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<String, Task> tasks) {
        this.tasks = tasks;
    }

    public int getAllTaskNum() {
        return allTaskNum;
    }

    public void setAllTaskNum(int allTaskNum) {
        this.allTaskNum = allTaskNum;
    }

    public int getFinishedTaskNum() {
        return finishedTaskNum;
    }

    public void setFinishedTaskNum(int finishedTaskNum) {
        this.finishedTaskNum = finishedTaskNum;
    }
}
