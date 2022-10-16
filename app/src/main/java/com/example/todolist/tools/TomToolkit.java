package com.example.todolist.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson2.JSON;
import com.example.todolist.DAO.DateTask;
import com.example.todolist.DAO.Task;
import com.example.todolist.R;
import com.google.gson.Gson;

import java.sql.Date;
import java.util.Map;
import java.util.Set;

public class TomToolkit {

    public static boolean saveData(Activity activity,String date,DateTask dateTask){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(date, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("data", new Gson().toJson(dateTask));
//        Toast.makeText(activity,"addData"+JSON.toJSONString(dateTask),Toast.LENGTH_LONG).show();
        edit.commit();
        return true;
        }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static DateTask readData(Activity activity, String date){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(date, Context.MODE_PRIVATE);
        if(sharedPreferences==null){
            return null;
        }else{
            String data = sharedPreferences.getString("data", "{}");
            if (data.equals("null") || data.equals("") || data.equals("{}") ){
                Toast.makeText(activity, "datanull:"+data, Toast.LENGTH_SHORT).show();
                return null;
            }
//            Toast.makeText(activity, "dataY:"+data, Toast.LENGTH_SHORT).show();
            DateTask dateTask = new Gson().fromJson(data, DateTask.class);
            return dateTask;
        }

    }

    public static Long readID(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("ID", Context.MODE_PRIVATE);
        Long data = new Long(0);
        data = sharedPreferences.getLong("ID", data);
        if (data==null || data==0){
            data+=1;
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putLong("ID", data);
            return data;
        }else{
            return data;
        }
    }


}
