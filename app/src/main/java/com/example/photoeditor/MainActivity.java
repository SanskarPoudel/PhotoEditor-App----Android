package com.example.photoeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private static  final int REQUEST_PERMISSIONS = 1234;
    private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int PERMISSIONS_COUNT = 2;
    @SuppressLint("NewApi")
    private boolean notPermission (){
        for(int i=0;i<PERMISSIONS_COUNT;i++){
            if(checkSelfPermission(PERMISSIONS[i])!= PackageManager.PERMISSION_GRANTED){
                return  true;
            }
        }
        return false;
    }

    @Override
    protected  void  onResume(){
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notPermission()) {
            requestPermissions(PERMISSIONS,REQUEST_PERMISSIONS);
        }
    }

    @Override
    public  void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode ==REQUEST_PERMISSIONS && grantResults.length > 0){
            if(notPermission()){
                ((ActivityManager)this.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
                recreate();
            }
        }
    }

    private static final int REQUEST_PICK_IMAGE = 12345;

    private void init(){
        if(!MainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            findViewById(R.id.takePhotoButton).setVisibility(View.GONE);
        }

        final Button selectImageButton  = findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                final Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                final Intent chooserIntent = Intent.createChooser(intent, "Select Image");
                startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
            }
        });

        final Button takePhotoButton = findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                   if(takePictureIntent.resolveActivity(getPackageManager())!=null){

                   }
                   else{
                       Toast.makeText(MainActivity.this,"Your camera app is not compatible",
                               Toast.LENGTH_SHORT).show();
                   }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != RESULT_OK){
            return;
        }

    }
}