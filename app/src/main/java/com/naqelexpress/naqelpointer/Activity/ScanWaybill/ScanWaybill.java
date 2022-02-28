package com.naqelexpress.naqelpointer.Activity.ScanWaybill;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.naqelexpress.naqelpointer.Activity.Incident.Incident;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.CommonResult;
import com.naqelexpress.naqelpointer.Models.Enum.Enum;
import com.naqelexpress.naqelpointer.Models.Request.CBMRequest;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.callback.AlertCallback;
import com.naqelexpress.naqelpointer.callback.Callback;
import com.naqelexpress.naqelpointer.utils.CommonApi;

import java.io.File;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ScanWaybill extends AppCompatActivity implements View.OnClickListener {
    EditText txtWNo, txtWidth, txtHeight, txtLength;
    static SweetAlertDialog alertDialog;
    ImageView image1, image2, image3, image4;
    int flag_insert = 1;
    String filename;
    HashMap<Integer, String> tempimages;
    String imagenames[] = {"Waybill1.png", "Waybill2.png"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanwaybill);
        txtWNo = (EditText) findViewById(R.id.txtwaybillno);
        txtWidth = (EditText) findViewById(R.id.txtwidth);
        txtHeight = (EditText) findViewById(R.id.txtheight);
        txtLength = (EditText) findViewById(R.id.txtlength);
        image1 = (ImageView) findViewById(R.id.image1);
        image1.setOnClickListener(this);
        image2 = (ImageView) findViewById(R.id.image2);
        image2.setOnClickListener(this);
        tempimages = new HashMap<Integer, String>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1:
                if (flag_insert != 0) {
                    filename = createfilename(0);
                    if (filename.length() > 0) {
                        tempimages.put(0, filename);
                        callcameraIntent(filename, 0);
                    }
                } else

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly capture Waybill ", GlobalVar.AlertType.Warning);
                break;
            case R.id.image2:
                if (flag_insert != 0) {
                    filename = createfilename(1);
                    if (filename.length() > 0) {
                        tempimages.put(1, filename);
                        callcameraIntent(filename, 1);
                    }
                } else

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly capture Waybill", GlobalVar.AlertType.Warning);
                break;

        }
    }

    protected void callcameraIntent(String imagename, int camerareqid) {

        if (ContextCompat.checkSelfPermission(ScanWaybill.this,
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ScanWaybill.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent cameraIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File newfile = new File(GlobalVar.naqelvehicleimagepath);
            File imgfile = new File(GlobalVar.naqelvehicleimagepath + "/" + imagename);
            if (!newfile.exists())
                newfile.mkdirs();

            Uri outputFileUri = null;
            if (Build.VERSION.SDK_INT > 23)
                outputFileUri = FileProvider.getUriForFile(ScanWaybill.this, getPackageName() + ".fileprovider",
                        imgfile);
            else
                outputFileUri = Uri.fromFile(imgfile);

            // Uri outputFileUri = Uri.fromFile(imgfile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, camerareqid);

        } else {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
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

    private String createfilename(int position) {
        Long timpstamp = System.currentTimeMillis() / 1000;
        String filename = "";

        String id = String.valueOf(GlobalVar.GV().EmployID);

        if (id.length() > 0) {
            filename = id + "_" + timpstamp.toString() + "_" + imagenames[position];
        }
        return filename;
    }

    public void onSubmit(View v) {
        System.out.println("Sathish");
        String Waybillno = txtWNo.getText().toString();
        if(Waybillno==null) {
            GlobalVar.ShowDialog(ScanWaybill.this, "Info", "Please enter a waybill", true);
        }
        else if (Waybillno.length()<8) {
            GlobalVar.ShowDialog(ScanWaybill.this, "Info", "Please enter a Valid Waybill", true);
        }
        else if(tempimages.size()==0)
        {
            GlobalVar.ShowDialog(ScanWaybill.this, "Info", "Please Capture the WaybillNo", true);
        }
    }


}
