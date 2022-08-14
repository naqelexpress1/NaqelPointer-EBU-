package com.naqelexpress.naqelpointer.Activity.Waybill;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.OnUpdateListener;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.ProjectAsyncTask;
import com.naqelexpress.naqelpointer.JSON.Request.GTranslation;
import com.naqelexpress.naqelpointer.R;

/**
 * Created by sofan on 12/03/2018.
 */

public class ConsigneeAddressTranslationActivity
        extends AppCompatActivity {
    SpinnerDialog spinnerDialog;
    TextView txtTargetLanguage;
    private Bundle bundle;
    private String languageCode = "en";
    private TextView txtConsigneeName;
    private TextView txtAddress;
    private TextView txtSecondAddress;
    private TextView txtNear;
    private TextView txtMobileNo;
    private TextView txtPhoneNo;
    private TextView txtTranslationResult;
    private String AddressText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.consigneeaddresstranslation);
        bundle = getIntent().getExtras();


        txtConsigneeName = (TextView) findViewById(R.id.txtConsigneeName);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtSecondAddress = (TextView) findViewById(R.id.txtSecondAddress);
        txtNear = (TextView) findViewById(R.id.txtNear);
        txtMobileNo = (TextView) findViewById(R.id.txtMobileNo);
        txtPhoneNo = (TextView) findViewById(R.id.txtPhoneNo);
        txtTranslationResult = (TextView) findViewById(R.id.txtTranslationResult);

        txtTargetLanguage = (TextView) findViewById(R.id.txtTargetLanguage);
        txtTargetLanguage.setInputType(InputType.TYPE_NULL);
        txtTargetLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog(false);
            }
        });

        if (savedInstanceState != null)
            setSavedInstance(savedInstanceState);


        if (bundle != null) {
            int position = bundle.getInt("position");

            MyRouteShipments myRouteShipments = GlobalVar.GV().myRouteShipmentList.get(position);
            if (GlobalVar.GV().IsEnglish())
                txtConsigneeName.setText(getResources().getString(R.string.txtConsigneeName) + myRouteShipments.ConsigneeName);
            else
                txtConsigneeName.setText(getResources().getString(R.string.txtConsigneeName) + myRouteShipments.ConsigneeName);
            txtAddress.setText(getResources().getString(R.string.txtAddress) + myRouteShipments.ConsigneeFirstAddress);

            txtSecondAddress.setText(getResources().getString(R.string.txtSecondAddress) + myRouteShipments.ConsigneeSecondAddress);
            txtNear.setText(getResources().getString(R.string.txtNear) + myRouteShipments.ConsigneeNear);
            txtMobileNo.setText(getResources().getString(R.string.txtMobileNo) + myRouteShipments.ConsigneeMobile);
            txtPhoneNo.setText(getResources().getString(R.string.txtPhoneNo) + myRouteShipments.ConsigneePhoneNumber);

            AddressText = txtAddress.getText().toString();
            //if(txtSecondAddress.getText().toString())
            // AddressText += " " + txtSecondAddress.getText().toString();
            // AddressText += " " + txtNear.getText().toString();

        }


        Button btnTranslate = (Button) findViewById(R.id.btnTranslate);
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GTranslation gTranslation = new GTranslation(AddressText, languageCode);
                String jsonData = JsonSerializerDeserializer.serialize(gTranslation, true);
                ProjectAsyncTask task = new ProjectAsyncTask("getGoogleTranslation", "Post", jsonData);
                task.setUpdateListener(new OnUpdateListener() {
                    ProgressDialog progressDialog;

                    public void onPostExecuteUpdate(String obj) {
                        txtTranslationResult.setText(obj);
                        progressDialog.dismiss();
                    }

                    public void onPreExecuteUpdate() {
                        progressDialog = ProgressDialog.show(ConsigneeAddressTranslationActivity.this, "Please wait.", "Translating your request."
                                , true);
                        ;
                    }
                });
                task.execute();
            }
        });

        spinnerDialog = new SpinnerDialog(ConsigneeAddressTranslationActivity.this,
                GlobalVar.GV().LanguageNameList, "Select Language", R.style.DialogAnimations_SmileWindow);
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                txtTargetLanguage.setText(GlobalVar.GV().LanguageNameList.get(position));
                languageCode = GlobalVar.GV().LanguageList.get(position).Code;
            }
        });

        //btnTranslate
    }

    private void setSavedInstance(Bundle savedInstanceState) {
        GlobalVar.GV().myRouteShipmentList = savedInstanceState.getParcelableArrayList("kpi");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("kpi", GlobalVar.GV().myRouteShipmentList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}