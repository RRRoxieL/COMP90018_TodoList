package com.example.todolist.tools;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.example.todolist.DAO.DateTask;
import com.example.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.sql.Date;
import java.util.Map;
import java.util.Set;

public class TomToolkit {
    private static final DatabaseReference dateTaskTable = FirebaseDatabase.getInstance().getReference("DateTaskTable");
    private static Long ID;
    static {
        FirebaseDatabase.getInstance().getReference("GlobalTaskID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    ID = (Long)task.getResult().getValue();
                }
            }

        });
        if(ID==null){
            FirebaseDatabase.getInstance().getReference("GlobalTaskID").setValue(1);
        }
    }



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

//    public static DateTask addReader(String dateString,DateTask dateTask){
//        dateTaskTable.child(dateString).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Object value = snapshot.getValue();
//                dateTask = new Gson().fromJson(value.toString(), DateTask.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w(TAG, "loadPost:onCancelled", error.toException());
//            }
//        });
//    }

    public static void saveToFireBase(String date,DateTask dateTask){
        dateTaskTable.child(date).setValue(new Gson().toJson(dateTask));
    }
    public static void getIDFromFirebase(){

    }

    public static DateTask getFromFireBase(String date){
//        final DateTask[] dateTask = {null};
//        dateTaskTable.child(date).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(task.isSuccessful()){
//                     dateTask[0] = task.getResult().getValue(DateTask.class);
//                }
//            }
//        });
//
//        return dataSnapshotTask.getResult().getValue(DateTask.class);
        return null;
    }

    public static DatabaseReference getDatabaseTable(){
        return dateTaskTable;
    }




}
