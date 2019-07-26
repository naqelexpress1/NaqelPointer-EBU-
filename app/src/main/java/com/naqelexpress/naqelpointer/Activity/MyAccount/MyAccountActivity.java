package com.naqelexpress.naqelpointer.Activity.MyAccount;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class MyAccountActivity
        extends AppCompatActivity {
    TextView lbEmployID, lbEmployName, lbEmployMobileNo, lbEmployStation, lbVersion;
    Button btnExtractDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myaccount);

        lbEmployID = (TextView) findViewById(R.id.lbEmployID);
        lbEmployName = (TextView) findViewById(R.id.lbEmployName);
        lbEmployMobileNo = (TextView) findViewById(R.id.lbMobileNo);
        lbEmployStation = (TextView) findViewById(R.id.lbStation);
        lbVersion = (TextView) findViewById(R.id.lbVersion);

        lbEmployID.setText(String.valueOf(GlobalVar.GV().EmployID));
        lbEmployName.setText(GlobalVar.GV().EmployName);
        lbEmployMobileNo.setText(GlobalVar.GV().EmployMobileNo);
        lbEmployStation.setText(GlobalVar.GV().EmployStation);
        lbVersion.setText(getString(R.string.lbVersion) + GlobalVar.GV().AppVersion);

        btnExtractDB = (Button) findViewById(R.id.btnExtractDB);
        if (GlobalVar.GV().EmployID == 19127 || GlobalVar.GV().EmployID == -1)//|| GlobalVar.GV().EmployID == 18110)
            btnExtractDB.setVisibility(View.VISIBLE);
        else
            btnExtractDB.setVisibility(View.INVISIBLE);
        btnExtractDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File sd = Environment.getExternalStorageDirectory();
                    if (!GlobalVar.GV().checkPermission(MyAccountActivity.this, GlobalVar.PermissionType.Storage)) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(),
                                getString(R.string.NeedStoragePermision),
                                GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(MyAccountActivity.this, GlobalVar.PermissionType.Storage);
                    } else {
                        if (sd.canWrite()) {
                            String currentDBPath = "/data/data/" + getPackageName() + "/databases/NaqelPointerDB.db";
                            String backupDBPath = "NaqelPointerDB.db";
                            File currentDB = new File(currentDBPath);
                            File backupDB = new File(sd, backupDBPath);

                            if (currentDB.exists()) {
                                FileChannel src = new FileInputStream(currentDB).getChannel();
                                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                dst.transferFrom(src, 0, src.size());
                                src.close();
                                dst.close();
                                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Extracted Successfully", GlobalVar.AlertType.Info);
                            }
                        } else
                            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedStoragePermision), GlobalVar.AlertType.Info);
                    }
                } catch (Exception e) {
                }
            }
        });
    }
}
