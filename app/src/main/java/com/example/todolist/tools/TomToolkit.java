package com.example.todolist.tools;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.todolist.DAO.DateTask;
import com.example.todolist.MainActivity;
import com.example.todolist.R;
import com.example.todolist.ui.calendar.DateFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;

/**
 * a toolkit that includes all neccessary functions for calendar and home fragment.
 */
public class TomToolkit {
    final static private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static final DatabaseReference dateTaskTable_main;
    private static final StorageReference fireStorage_main;
    private static Long ID;
    private static DatabaseReference dateTaskTable;
    private static StorageReference fireStorage;
    private static String userID;




    /**
     * test the connection to firebase
     */
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        dateTaskTable_main= FirebaseDatabase.getInstance().getReference("DateTaskTable");
        fireStorage_main = FirebaseStorage.getInstance().getReference("Picture");
//        FirebaseDatabase.getInstance().getReference("GlobalTaskID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                    ID = (Long)task.getResult().getValue();
//                }
//            }
//
//        });
//        if(ID==null){
//            FirebaseDatabase.getInstance().getReference("GlobalTaskID").setValue(1);
//        }
    }

    public static void initializeUser(String userID){
        TomToolkit.userID = userID;
        dateTaskTable = dateTaskTable_main.child(TomToolkit.userID);
        fireStorage = fireStorage_main.child(TomToolkit.userID);
        dateTaskTable.keepSynced(true);
    }

    public static String getCurrentUserID(){
        if(TomToolkit.userID == null){

        }
        return TomToolkit.userID;
    }

    /**
     * send a message through handler
     * @param handler: the handler that used for sending messge
     * @param task: the task that fills the data
     * @param actiontag: a global value for the key for actionTag in the message
     * @param action: the type of actions
     */
    public static void sendMessage(Handler handler, com.example.todolist.DAO.Task task, String actiontag, char action){
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putChar(actiontag,action);
        if(action!=GlobalValues.ACTIONTAG_DEL){
            bundle.putSerializable(GlobalValues.BUNDLE_INFO_TASK,task);
        }else{
            if(task != null){
                bundle.putString(GlobalValues.BUNDLE_INFO_TASKID,task.getID());
            }else{
                bundle.putString(GlobalValues.BUNDLE_INFO_TASKID,null);
            }

        }
        message.setData(bundle);
        handler.sendMessage(message);
    }

    /**
     * read global task id from firebase
     * @deprecated
     * @param activity
     * @return
     */
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

    /**
     * fetch picture from firebase, and show it in the imageview
     * default fetch path: Picture/image/filename
     * @param filename: the name of the ficture to be fetched
     * @param context: the context where the error messge shown
     * @param imageView: the place fetched picture to be shown
     */
    public static void getPicture(String filename,Context context,ImageView imageView){
        StorageReference fileRef = fireStorage.child("image/" + filename);
        File tempimg = null;
        if(true){
            try {
                tempimg = File.createTempFile(filename,".jpg");
                File finalTempimg = tempimg;
                fileRef.getFile(finalTempimg).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(finalTempimg.getAbsolutePath());
                        imageView.setImageBitmap(bitmap);
                        imageView.setClickable(true);
                        imageView.setLongClickable(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"picture not found",Toast.LENGTH_LONG);
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                        imageView.setImageResource(R.drawable.ic_pic_load);
                        imageView.setClickable(false);
                        imageView.setLongClickable(false);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * get the referenced file from the database
     * @deprecated
     * @param filename: the filename to be get
     * @return: file reference
     */
    public static StorageReference getImgRef(String filename){
        return fireStorage.child("image/" + filename);
    }

    /**
     * save picture to fire base
     * @param imageUri: the picture Uri
     * @param filename: the name of the picture on the firebase
     * @param context: the context where error message shown
     */
    public static void savePicture(Uri imageUri,String filename,Context context){
        fireStorage.child("image/"+filename).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG,"save "+filename+" to database");
                Toast.makeText(context, "success!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * save picture to fire base
     * @param bitmap: the picture in bitmap
     * @param filename: the name of the picture on the firebase
     * @param context: the context where error message shown
     */
    public static void savePicture(Bitmap bitmap,String filename,Context context){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        fireStorage.child("image/"+filename).putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG,"save "+filename+" to database");
                Toast.makeText(context, "success!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * save a dateTask to firebase
     * @param date: the date of the dateTask
     * @param dateTask the dateTask to be stored
     */
    public static void saveToFireBase(String date,DateTask dateTask){
        dateTaskTable.child(date).setValue(new Gson().toJson(dateTask));
    }

    /**
     * get the dateTaskTable refrerence
     * @return static field DatabaseReference dateTaskTable
     */
    public static DatabaseReference getDatabaseTable(){
        return dateTaskTable;
    }

    /**
     * get date from a date String
     * @param dateString: a String that to be convert to date
     * @return: a date that equal to that in the date string.
     * @throws ParseException: when date string is not valid
     */
    public static Date getDate(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }

//    public static getTool(){
//        CharSequence date = ""+dayOfMonth+"-"+(month+1)+"-"+year;
//        Bundle bundle = new Bundle();
//        bundle.putCharSequence("time",date);
//        DateFragment dateFragment = new DateFragment();
//        dateFragment.setArguments(bundle);
//        FragmentManager fm = getParentFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.replace(R.id.nav_host_fragment_activity_main,dateFragment);
//        ft.commit();
//    }

    // 获取当前时间
    public static Date dateCalculator(Date date, int days){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        // 把日期往后增加一天,整数  往后推,负数往前移动
        calendar.add(Calendar.DATE, days);
        // 这个时间就是日期往后推一天的结果
        return calendar.getTime();
    }

    public static String toDateString(Date date){
        return dateFormat.format(date);
    }

    public static void startDataFragment(FragmentManager fm, String dateString){
        Bundle bundle = new Bundle();
        //save selected date into bundle and send to the new datefragment
        bundle.putCharSequence(GlobalValues.BUNDLE_INFO_TIME,dateString);
        DateFragment dateFragment = new DateFragment();
        dateFragment.setArguments(bundle);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.nav_host_fragment_activity_main,dateFragment);
        ft.commit();
    }




}
