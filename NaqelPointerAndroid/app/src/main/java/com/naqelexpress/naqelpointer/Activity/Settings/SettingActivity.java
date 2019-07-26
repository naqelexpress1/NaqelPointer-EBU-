package com.naqelexpress.naqelpointer.Activity.Settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserSettings;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

public class SettingActivity
        extends AppCompatActivity {
//    FloatingActionButton SaveButton, CloseButton, OptionsButton;
//    TextView txtSave, txtClose;
//    Animation ShowButtonAnimation,HideButtonAnimation,ShowLayoutAnimation,HideLayoutAnimation;

    EditText txtIPAddress;
    TextView lbLastMasterUpdate;
    CheckBox chShowScaningCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settingactivity);

        txtIPAddress = (EditText) findViewById(R.id.txtIPAddress);
        lbLastMasterUpdate = (TextView) findViewById(R.id.lbLastMasterUpdate);
        chShowScaningCamera = (CheckBox) findViewById(R.id.chShowScaningCamera);
        CheckBox mc = (CheckBox) findViewById(R.id.multiplemarker);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("naqelSettings", 0);
        boolean multiple_not = pref.getBoolean("mc", true);
        if (multiple_not)
            mc.setChecked(true);
        else
            mc.setChecked(false);

//        OptionsButton = (FloatingActionButton) findViewById(R.id.btnOptions);
//        SaveButton = (FloatingActionButton) findViewById(R.id.btnShowSelectedShipments);
//        CloseButton = (FloatingActionButton) findViewById(R.id.btnClose);
//        txtSave = (TextView) findViewById(R.id.txtSave);
//        txtClose = (TextView) findViewById(R.id.txtShowShipments);
//
//        ShowButtonAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.show_button);
//        HideButtonAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.hide_button);
//        ShowLayoutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.show_layout);
//        HideLayoutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.hide_layout);
//        OptionsButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                FloatingPressed();
//            }
//        });
//
//        SaveButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                FloatingPressed();
//                SaveData();
//            }
//        });

//        CloseButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                FloatingPressed();
//                CloseActivity();
//            }
//        });

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        int Count = dbConnections.getCount("UserSettings", " EmployID = " + String.valueOf(GlobalVar.GV().EmployID),getApplicationContext());
        if (Count > 0) {
            Cursor result = dbConnections.Fill("select * from UserSettings where EmployID = " + String.valueOf(GlobalVar.GV().EmployID),getApplicationContext());
            if (result.getCount() > 0) {
                result.moveToFirst();
                do {
                    int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    int EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
                    String IPAddress = result.getString(result.getColumnIndex("IPAddress"));
                    boolean ShowScaningCamera = Boolean.parseBoolean(result.getString(result.getColumnIndex("ShowScaningCamera")));
                    DateTime dateTime = DateTime.now().withFieldAdded(DurationFieldType.days(), -30);
                    if (dbConnections.isColumnExist("UserSettings", "LastBringMasterData",getApplicationContext()))
                        dateTime = DateTime.parse(result.getString(result.getColumnIndex("LastBringMasterData")));

                    GlobalVar.GV().currentSettings = new UserSettings(ID, EmployID, IPAddress, ShowScaningCamera, dateTime);
                }
                while (result.moveToNext());
            }
        } else {
            GlobalVar.GV().currentSettings = new UserSettings("212.93.160.150", true);
            dbConnections.InsertSettings(GlobalVar.GV().currentSettings,getApplicationContext());
            GlobalVar.GV().currentSettings.ID = dbConnections.getMaxID("UserSettings",getApplicationContext());
        }

        boolean res = GlobalVar.GV().currentSettings.ShowScaningCamera;
        txtIPAddress.setText(GlobalVar.GV().currentSettings.IPAddress);
        chShowScaningCamera.setChecked(GlobalVar.GV().currentSettings.ShowScaningCamera);
        lbLastMasterUpdate.setText((GlobalVar.GV().currentSettings.LastBringMasterData.toString()));
        if (GlobalVar.GV().EmployID != -1)
            lbLastMasterUpdate.setVisibility(View.GONE);

        dbConnections.close();
    }

    private void SaveData() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            GlobalVar.GV().currentSettings.IPAddress = txtIPAddress.getText().toString();
            GlobalVar.GV().currentSettings.ShowScaningCamera = chShowScaningCamera.isChecked();

            if (dbConnections.UpdateSettings(GlobalVar.GV().currentSettings,getWindow().getDecorView().getRootView(),getApplicationContext())) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                finish();
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
        }
    }

    private boolean IsValid() {
        boolean isValid = true;
        if (txtIPAddress.getText().toString().equals("")) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Please check the IP Address or contact with IT Department for supporting.", GlobalVar.AlertType.Error);
            isValid = false;
        }

        return isValid;
    }

//    private void FloatingPressed()
//    {
//        if (SaveButton.getVisibility() == View.VISIBLE )
//        {
//            SaveButton.setVisibility(View.GONE);
//            CloseButton.setVisibility(View.GONE);
//            txtSave.setVisibility(View.GONE);
//            txtClose.setVisibility(View.GONE);
//
//            SaveButton.startAnimation(HideLayoutAnimation);
//            CloseButton.startAnimation(HideLayoutAnimation);
//            txtSave.startAnimation(HideLayoutAnimation);
//            txtClose.startAnimation(HideLayoutAnimation);
//        }
//        else
//        {
//            SaveButton.setVisibility(View.VISIBLE);
//            CloseButton.setVisibility(View.VISIBLE);
//            txtSave.setVisibility(View.VISIBLE);
//            txtClose.setVisibility(View.VISIBLE);
//
//            SaveButton.startAnimation(ShowLayoutAnimation);
//            CloseButton.startAnimation(ShowLayoutAnimation);
//            txtSave.startAnimation(ShowLayoutAnimation);
//            txtClose.startAnimation(ShowLayoutAnimation);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settingmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:
                SaveData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        SettingActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void multipleColors(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.multiplemarker:
                if (checked)
                    edit_remove_multipleMarker(true);
                else
                    edit_remove_multipleMarker(false);
                break;
            // Perform your logic
        }
    }

    private void edit_remove_multipleMarker(boolean tf) {
        SharedPreferences sharedpreferences = getSharedPreferences("naqelSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("mc", tf);
        editor.commit();
    }
}
