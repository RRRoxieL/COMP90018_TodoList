package com.example.todolist.ui.calendar;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.todolist.DAO.DateTask;
import com.example.todolist.DAO.Task;
import com.example.todolist.MainActivity;
import com.example.todolist.R;

import com.example.todolist.databinding.DialogPopouttaskBinding;
import com.example.todolist.databinding.FragmentDateBinding;
import com.example.todolist.tools.GlobalValues;
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
import java.util.zip.Inflater;

/**
 * this is a manager dialog for a specific task.
 */
public class PopOutTaskDialog extends DialogFragment {

    private DialogPopouttaskBinding binding;
    private String date;
    private Handler handler;
    private Task task;
    private ActivityResultLauncher<Intent> localActivityResultLauncher;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private Uri imageUri;
    private Object[] imgData = new Object[1];
    private String userID;
    /**
     * constructor without a task
     * used for adding new tasks.
     * @param date: date of the task
     * @param handler: a handler used for send message
     */
    public PopOutTaskDialog(String date, Handler handler) {
        this.date = date;
        this.handler=handler;
        this.userID = TomToolkit.getCurrentUserID();

    }

    /**
     * constructor with a task
     * used for manage existed tasks.
     * @param date: date of the task
     * @param handler: a handler used for send message
     * @param task: a task that is under management
     */
    public PopOutTaskDialog(String date,Handler handler,Task task){
        this.date = date;
        this.handler = handler;
        this.task = task;
        this.userID = TomToolkit.getCurrentUserID();
    }

    /**
     * rewrite the onCreate method to initialize resultLaunchers for local file fetch and pictures from cametra.
     * @param savedInstanceState: infomation from the fragment manager, not used
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //registerForActivityResult has to be registered in oncreate function.

        localActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode == RESULT_OK && data != null) {
                    imageUri = data.getData();
                    imgData[0] = imageUri;
                    actionReceivePic(imgData,imageUri);
                }else if(resultCode == RESULT_CANCELED || data == null){
                    imageUri = null;
                    imgData[0] = null;
                    actionReceivePic(imgData,imageUri);
                }
            }
        });
        cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode == RESULT_OK && data != null) {
                    imgData[0] = (Bitmap)data.getExtras().get("data");
                    imageUri = Uri.parse("default");
                    actionReceivePic(imgData,imageUri);
                }else if(resultCode == RESULT_CANCELED || data == null){
                    imageUri = null;
                    imgData[0] = null;
                    actionReceivePic(imgData,imageUri);
                }
            }
        });

    }

    /**
     *
     * @param data: image data, can be Uri or Bitmap
     * @param imageUri: the imageUri field, null stands for no data.
     */
    private void actionReceivePic(Object[] data,Uri imageUri){
        //if the data collected is not null, save the image to database and set the imageview to the image
        ImageView imageView = (ImageView)getView().findViewById(R.id.img);
        if(imageView==null || imageUri==null){
            Toast.makeText(getContext(), "please select a picture", Toast.LENGTH_SHORT).show();
        }
        setPicture(data,imageView);
    }

    /**
     * save the picture to database if imageUri field is not null
     * @param data: the picture data to be stored, can be Uri or Bitmap
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void savePicture(Object data){
        if(imageUri!=null){
            if(task==null){
                Toast.makeText(getContext(),binding.importance.getSelectedItem().toString(),Toast.LENGTH_LONG);
                task = new Task(getActivity(),date,binding.timepicker.getHour(),binding.timepicker.getMinute(),binding.nameText.getText().toString(),binding.descrptText.getText().toString(),binding.importance.getSelectedItem().toString(),null);
            }
            String filename = createFileName();
            task.setPicPath(filename);
            if(data instanceof Uri){
                Uri dataUri = (Uri) data;
                TomToolkit.savePicture(dataUri,filename,getContext());
            }else if(data instanceof Bitmap){
                Bitmap dataBitmap = (Bitmap) data;
                TomToolkit.savePicture(dataBitmap,filename,getContext());
            }
        }
    }

    /**
     * set the picture of a imageView, set default upload pic if data is null
     * @param data: the picture data, can be Uri or Bitmap or null
     * @param imageView: the imageview to be set
     */
    private void setPicture(Object[] data, ImageView imageView){
        if(data == null || data[0] == null){
            imageView.setImageResource(R.drawable.ic_frag_import);
            imageView.setTag(null);
        }else if(data[0] instanceof Bitmap){
            Bitmap dataBitmap = (Bitmap) data[0];
            imageView.setImageBitmap(dataBitmap);
            imageView.setTag("not_empty");
        }else if(data[0] instanceof Uri){
            Uri dataUri = (Uri) data[0];
            imageView.setImageURI(dataUri);
            imageView.setTag("not_empty");
        }

    }


    /**
     * constructor of the view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
        binding.timepicker.setIs24HourView(true);

        //simple end the popoutdialog when cancle button clicked
        binding.btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //send the request of save the updated data to the DateFragment when save button clicked.
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                savePicture(imgData[0]);
                setTask();
                TomToolkit.sendMessage(handler,task,GlobalValues.BUNDLE_INFO_ACTIONTAG,GlobalValues.ACTIONTAG_ADD);
                dismiss();
            }
        });

        //send the request of delete the under manage task to the DateFragment when delete button clicked.
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TomToolkit.sendMessage(handler,task, GlobalValues.BUNDLE_INFO_ACTIONTAG,GlobalValues.ACTIONTAG_DEL);
                dismiss();
            }
        });

        /**
         * when long click the picture, a menu would popup ask the user to
         * select picture from the file or from camera.
         */
        binding.img.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public boolean onLongClick(View v) {
                popoutMenu();
                return true;
            }
        });

        binding.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.img.getTag()==null){
                    popoutMenu();
                }else{
                    View imgBigView = inflater.inflate(R.layout.image_layout,container);
                    PopupWindow popupWindow = new PopupWindow(imgBigView,LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,true);
                    ImageView imgView = (ImageView) imgBigView.findViewById(R.id.img_big);
                    setPicture(imgData,imgView);
                    imgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                    popupWindow.showAtLocation(v.getRootView(),Gravity.CENTER,0,0);

                }
            }
        });


        return root;
    }

    private void popoutMenu(){
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
                        localActivityResultLauncher.launch(galleryIntent);
                        break;
                    case "FROM CAMERA":
                        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},101);
                        }
                        Intent galleryIntent1 = new Intent();
                        galleryIntent1.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraActivityResultLauncher.launch(galleryIntent1);
                        break;
                }
                return false;
            }
        });
        popupMenu.inflate(R.menu.popout_img_selector);
        popupMenu.show();
    }

    /**
     * create a filename for the image, name came from imageUri
     * @return a filename String
     */
    private String createFileName(){
        if(date==null || imageUri==null){
            return null;
        }
        return date+imageUri.getLastPathSegment()+".jpg";
    }


    /**
     * set the contents on the view as in the task
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setContents(){
        if(task!=null){
            binding.nameText.setText(task.getName());
            binding.timepicker.setHour(task.getHour());
            binding.timepicker.setMinute(task.getMinute());
            binding.descrptText.setText(task.getDescription());
            if(task.getPicPath()!=null){
                TomToolkit.getPicture(task.getPicPath(),getContext(),binding.img,imgData);
            }
            if(task.getImportance()!=null){
                int position = Task.Importance.getPosition(task.getImportance());
                binding.importance.setSelection(position);
            }

        }
    }

    /**
     * set the task as user updated in the contents.
     * except for task.picPath, which would be automatically set when savePicture() called
     */
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