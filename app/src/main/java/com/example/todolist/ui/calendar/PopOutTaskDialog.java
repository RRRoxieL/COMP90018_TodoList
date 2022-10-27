package com.example.todolist.ui.calendar;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.todolist.DAO.DateTask;
import com.example.todolist.DAO.Task;
import com.example.todolist.MainActivity;
import com.example.todolist.R;

import com.example.todolist.databinding.DialogPopouttaskBinding;
import com.example.todolist.databinding.FragmentDateBinding;
import com.example.todolist.tools.TomToolkit;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;


public class PopOutTaskDialog extends DialogFragment {

    private DialogPopouttaskBinding binding;
    private String date;
    private Handler handler;
    private Task task;
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private ActivityResultLauncher<Intent> intentActivityResultLauncher1;
    private Uri imageUri;

    public PopOutTaskDialog(String date, Handler handler) {
        this.date = date;
        this.handler=handler;

    }

    public PopOutTaskDialog(String date,Handler handler,Task task){
        this.date = date;
        this.handler = handler;
        this.task = task;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //registerForActivityResult has to be registered in oncreate function.
        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode == RESULT_OK && data != null) {
                    imageUri = data.getData();
                    ImageView imageView = (ImageView)getView().findViewById(R.id.img);
                    if(imageView==null || imageUri==null){
                        Toast.makeText(getContext(), "please select a picture", Toast.LENGTH_SHORT).show();
                    }else{
                        String filename = createFileName();
                        TomToolkit.savePicture(imageUri,filename,getContext());
                        imageView.setImageURI(imageUri);
                        if(task!=null){
                            task.setPicPath(filename);
                        }else{
                            task = new Task(getActivity(),date,1,1,"name","desc",null,null);
                            task.setPicPath(filename);
                        }
                    }

                }
            }
        });
        intentActivityResultLauncher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap img = (Bitmap)data.getExtras().get("data");
                    ImageView imageView = (ImageView)getView().findViewById(R.id.img);
                    imageUri = Uri.parse("default");
                    if(imageView==null || imageUri==null){
                        Toast.makeText(getContext(), "please select a picture", Toast.LENGTH_SHORT).show();
                    }else{
                        String filename = createFileName();
                        TomToolkit.savePicture(img,filename,getContext());
                        imageView.setImageBitmap(img);
                        if(task!=null){
                            task.setPicPath(filename);
                        }else{
                            task = new Task(getActivity(),date,1,1,"name","desc",null,null);
                            task.setPicPath(filename);
                        }
                    }

                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window win = getDialog().getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        win.setGravity(Gravity.BOTTOM);
        PopOutTaskViewModel popOutTaskViewModel = new ViewModelProvider(this).get(PopOutTaskViewModel.class);
        binding = DialogPopouttaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setContents();


        binding.btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                setTask();
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putChar("actionTag",'a');
                bundle.putSerializable("task",task);
                message.setData(bundle);
                handler.sendMessage(message);
                dismiss();
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putChar("actionTag",'d');
                if(task!=null){
                    bundle.putString("ID",task.getID());
                }
                message.setData(bundle);
                handler.sendMessage(message);
                dismiss();
            }
        });

        binding.img.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(),binding.img);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String title = item.getTitle().toString();
                        switch (title){
                            case "FROM LOCAL FILE":
                                Intent galleryIntent = new Intent();
                                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                                galleryIntent.setType("image/*");
                                intentActivityResultLauncher.launch(galleryIntent);
                                break;
                            case "FROM CAMERA":
                                Intent galleryIntent1 = new Intent();
                                galleryIntent1.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                                galleryIntent1.setType("image/*");
                                intentActivityResultLauncher1.launch(galleryIntent1);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.popout_img_selector);
                popupMenu.show();

            }
        });

        binding.importance.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        binding.timepicker.setIs24HourView(true);

        return root;
    }

    private String createFileName(){
        if(date==null || imageUri==null){
            return null;
        }
        return date.toString()+imageUri.getLastPathSegment()+".jpg";
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setContents(){
        if(task!=null){
            binding.nameText.setText(task.getName());
            binding.timepicker.setHour(task.getHour());
            binding.timepicker.setMinute(task.getMinute());
            binding.descrptText.setText(task.getDescription());
            if(task.getPicPath()!=null){
                TomToolkit.getPicture(task.getPicPath(), getContext(),binding.img);
            }
            if(task.getImportance()!=null){
                int position = Task.Importance.getPosition(task.getImportance());
                binding.importance.setSelection(position);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTask(){
        if(task==null){
            Toast.makeText(getContext(),binding.importance.getSelectedItem().toString(),Toast.LENGTH_LONG);
            task = new Task(getActivity(),date,binding.timepicker.getHour(),binding.timepicker.getMinute(),binding.nameText.getText().toString(),binding.descrptText.getText().toString(),binding.importance.getSelectedItem().toString(),null);
        }else{
            task.setName(binding.nameText.getText().toString());
            task.setDescription(binding.descrptText.getText().toString());
            task.setHour(binding.timepicker.getHour());
            task.setMinute(binding.timepicker.getMinute());
            task.setImportance(binding.importance.getSelectedItem().toString());
            Toast.makeText(getContext(),binding.importance.getSelectedItem().toString(),Toast.LENGTH_LONG);
        }

    }
}