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
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.todolist.DAO.DateTask;
import com.example.todolist.R;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Map;
import java.util.Set;

public class TomToolkit {
    private static final DatabaseReference dateTaskTable = FirebaseDatabase.getInstance().getReference("DateTaskTable");
    private static final StorageReference fireStorage = FirebaseStorage.getInstance().getReference("Picture");
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"picture not found",Toast.LENGTH_LONG);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public static StorageReference getImgRef(String filename){
        return fireStorage.child("image/" + filename);
    }

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

    public static void saveToFireBase(String date,DateTask dateTask){
        dateTaskTable.child(date).setValue(new Gson().toJson(dateTask));
    }

    public static DatabaseReference getDatabaseTable(){
        return dateTaskTable;
    }




}
