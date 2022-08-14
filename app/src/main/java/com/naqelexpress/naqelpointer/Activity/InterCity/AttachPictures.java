package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AttachPictures extends AppCompatActivity implements View.OnClickListener{

    TextView back, next;
    Button uploadFile;
    String filename;
    String imagesuffix = "";

    List<String> sendImages = new ArrayList<String>();
    List<String> tempImages = new ArrayList<String>();

//    public HashMap<Integer, String> sendimages;
//    HashMap<Integer, String> ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_pictures);

        findViewById();
    }

    public void findViewById(){
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);

        uploadFile = findViewById(R.id.uploadFile);

        back.setOnClickListener(this);
        next.setOnClickListener(this);
        uploadFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.uploadFile:
                Long timpstamp = System.currentTimeMillis() / 1000;
                String id = String.valueOf(GlobalVar.GV().EmployID);
                imagesuffix = "FleetSafety";
                filename = id + "_" + timpstamp.toString() + "_" + imagesuffix + "_" + ".png";
                CreatefileName(view);
                break;

            case R.id.back:
                onBackPressed();
                break;

            case R.id.next:
                Intent intent = new Intent(getApplicationContext(), FleetSafety.class);
                startActivity(intent);
                break;

        }
    }

    protected void CreatefileName(View view) {

        if (filename.length() > 0) {
            sendImages.add(filename);
            callcameraIntent(filename, 0, view);
        }

    }

    protected void callcameraIntent(String imagename, int camerareqid, View view) {

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent cameraIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            File newfile = new File(GlobalVar.naqelvehicleimagepath);
            File imgfile = new File(GlobalVar.naqelvehicleimagepath + "/" + imagename);
            if (!newfile.exists())
                newfile.mkdirs();

            Uri outputFileUri = null;
            if (Build.VERSION.SDK_INT > 23)
                outputFileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider",
                        imgfile);
            else
                outputFileUri = Uri.fromFile(imgfile);

            // Uri outputFileUri = Uri.fromFile(imgfile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, camerareqid);

        } else {
            GlobalVar.GV().ShowSnackbar(view, getString(R.string.ErrorWhileSaving),
                    GlobalVar.AlertType.Error);
            try {
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(),
                        null);
                intent.setData(uri);
                startActivity(intent);
            } catch (Exception e) {

            }
        }
    }
}
