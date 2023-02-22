package com.example.photoeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


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
        if(!MainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
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
                       final File photoFile = createImageFile();
                       imageUri = Uri.fromFile(photoFile);
                       final SharedPreferences myPrefs = getSharedPreferences(appId,0);
                       myPrefs.edit().putString("path",photoFile.getAbsolutePath()).apply();
                       takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                       startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
                   }
                   else{
                       Toast.makeText(MainActivity.this,"Your camera app is not compatible",
                               Toast.LENGTH_SHORT).show();
                   }

            }
        });
    }

    private static  final int REQUEST_IMAGE_CAPTURE = 1012;
    private Uri imageUri;

    private static  final String appId = "photoEditor";

   private File createImageFile(){
        final String timeStamp = new SimpleDateFormat("yyyMMdd_HHmm ss").format(new Date());
        final String imageFileName = "/JPEG" + timeStamp +  ".jpg";
        final File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(storageDir+imageFileName);

   }

   private boolean editMode = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(imageUri==null){
                final SharedPreferences p = getSharedPreferences(appId,0);
                final String path = p.getString("path","");
                if(path.length()<1){
                    recreate();
                }
                imageUri = Uri.parse("file://"+path);
            }
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,imageUri));
        }
        else if(data==null){
          recreate();
          return;
        }
        else if(requestCode == REQUEST_PICK_IMAGE){
            imageUri = data.getData();
        }
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this,"Loading","Please Wait",true);

        editMode = true;

        findViewById(R.id.welcomeScreen).setVisibility(View.GONE);
        findViewById(R.id.editScreen).setVisibility(View.VISIBLE);

        dialog.cancel();

   }
}