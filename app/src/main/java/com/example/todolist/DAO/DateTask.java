package com.example.todolist.DAO;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

//a class which serve as the data type of one day in the calendar.
public class DateTask implements Serializable{


    private Date date;
    private HashMap<String,Task> tasks;

    public DateTask(Date date){
        this.date = date;
    }

    public boolean addTask(Task task){
        //slow start, considering
        if(tasks==null){
            tasks = new HashMap<>();
        }
        if (tasks.containsKey(task.getID())){
            return false;
        }else{
            tasks.put(task.getID(),task);
            return true;
        }
    }

    public boolean deleteTask(String id){
        return tasks.remove(id)!=null?true:false;
    }


    @Override
    public String toString() {
        return "DateTask{" +
                "date=" + date +
                "\n tasks=" + tasks +
                '}';
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

}
