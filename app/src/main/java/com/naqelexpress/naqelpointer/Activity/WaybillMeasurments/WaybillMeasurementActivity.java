package com.naqelexpress.naqelpointer.Activity.WaybillMeasurments;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.WaybillMeasurement;
import com.naqelexpress.naqelpointer.DB.DBObjects.WaybillMeasurementDetail;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.WayBillMeasurement;

import java.util.ArrayList;

public class WaybillMeasurementActivity extends AppCompatActivity {

    Button btnStart, btnNext, btnPlus, btnPrevious;
    EditText txtWaybillNo, txtWeight, txtTotalPieces, txtReason, txtRemaining, txtSameSize, txtLength, txtWidth, txtHeight, Ssize;
    TextView lbLengthCM, lbWidthCM, lbHeightCM, lbIndex;
    CheckBox chNoVolume;
    ConstraintLayout constraintLayout;
    ArrayList<WaybillMeasurementDetail> history;
    ArrayList<WaybillMeasurementDetail> historyhide;
    int TotalHistory = 0, CurrentIndex = 0, ReasonID = 0;
    SpinnerDialog reasonSpinnerDialog;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.waybillmeasurementactivity);

        Toast.makeText(this, "Measurement Screen", Toast.LENGTH_SHORT).show();
        btnStart = (Button) findViewById(R.id.btnStart);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPlus = (Button) findViewById(R.id.btnPlus);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);

        btnNext.setVisibility(View.INVISIBLE);
        btnPrevious.setVisibility(View.INVISIBLE);

        lbLengthCM = (TextView) findViewById(R.id.lbLengthCM);
        lbWidthCM = (TextView) findViewById(R.id.lbWidthCM);
        lbHeightCM = (TextView) findViewById(R.id.lbHeightCM);
        lbIndex = (TextView) findViewById(R.id.lbIndex);

        chNoVolume = (CheckBox) findViewById(R.id.chNoVolume);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        txtWaybillNo = (EditText) findViewById(R.id.txtWaybillNo);
//        txtWaybillNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanWaybillLength)});

        txtWeight = (EditText) findViewById(R.id.weight);
        txtTotalPieces = (EditText) findViewById(R.id.txtTotalPieces);
        txtReason = (EditText) findViewById(R.id.txtReason);
        txtRemaining = (EditText) findViewById(R.id.txtRemaining);
        txtRemaining.setEnabled(false);
        txtSameSize = (EditText) findViewById(R.id.txtSameSize);
        txtLength = (EditText) findViewById(R.id.txtLength);
        txtWidth = (EditText) findViewById(R.id.txtWidth);
        txtHeight = (EditText) findViewById(R.id.txtHeight);
        Ssize = (EditText) findViewById(R.id.txtSameSize1);

        txtReason.setVisibility(View.INVISIBLE);
        constraintLayout.setVisibility(View.GONE);
        history = new ArrayList<>();
        historyhide = new ArrayList<>();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            txtWaybillNo.setText(bundle.getString("WaybillNo"));
            txtWaybillNo.setEnabled(false);
            txtTotalPieces.setText(bundle.getString("PiecesCount"));
            txtTotalPieces.setEnabled(false);
        }

//        txtWaybillNo.addTextChangedListener(textWatcher);
      /*  txtWaybillNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
//                    if (txtWaybillNo != null && txtWaybillNo.getText().toString().length() == 8 ||
//                            txtWaybillNo.getText().toString().length() == 9)
//                        AddNewWaybill();
                if (txtWaybillNo != null && txtWaybillNo.getText().length() >= 8)
                    //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
                    setTxtWaybillNo();
            }
        });*/

        chNoVolume.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtReason.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.GONE);
                    btnStart.setVisibility(View.INVISIBLE);
                } else {
                    txtReason.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.GONE);
                    btnStart.setVisibility(View.VISIBLE);
                }
            }
        });

        txtReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (hasFocus)
                reasonSpinnerDialog.showSpinerDialog(false);
            }
        });

//        txtReason.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus)
//                    reasonSpinnerDialog.showSpinerDialog(false);
//            }
//        });


        if (savedInstanceState == null) {
            GetNoNeedVolumeReasonList();
            setReason();
        } else if (savedInstanceState != null) {
            NoNeedVolumeReasonNameList = savedInstanceState.getStringArrayList("NoNeedVolumeReasonNameList");
            NoNeedVolumeReasonFNameList = savedInstanceState.getStringArrayList("NoNeedVolumeReasonFNameList");
            NoNeedVolumeReasonList = savedInstanceState.getIntegerArrayList("NoNeedVolumeReasonList");
            setReason();
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidToContinue()) {
                    visibleWeightModule();
                }
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initialCalculation();
            }
        });

        reasonSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                if (GlobalVar.GV().IsEnglish())
                    txtReason.setText(NoNeedVolumeReasonNameList.get(position));
                else
                    txtReason.setText(NoNeedVolumeReasonFNameList.get(position));
                ReasonID = NoNeedVolumeReasonList.get(position);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (history.size() > CurrentIndex) {
                    txtSameSize.setText(String.valueOf(history.get(CurrentIndex).PiecesCount));
                    txtWidth.setText(String.valueOf(history.get(CurrentIndex).Width));
                    txtHeight.setText(String.valueOf(history.get(CurrentIndex).Height));
                    txtLength.setText(String.valueOf(history.get(CurrentIndex).Length));

                    lbIndex.setText("Index " + String.valueOf(CurrentIndex + 1));
                }
                if (history.size() > CurrentIndex + 1)
                    CurrentIndex = CurrentIndex + 1;
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentIndex > 0)
                    CurrentIndex = CurrentIndex - 1;

                if (CurrentIndex >= 0) {
                    txtSameSize.setText(String.valueOf(history.get(CurrentIndex).PiecesCount));
                    txtWidth.setText(String.valueOf(history.get(CurrentIndex).Width));
                    txtHeight.setText(String.valueOf(history.get(CurrentIndex).Height));
                    txtLength.setText(String.valueOf(history.get(CurrentIndex).Length));

                    lbIndex.setText("Index " + String.valueOf(CurrentIndex + 1));
                }
            }
        });
        // if (!GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID).equals("Express"))
        //     txtWaybillNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanWaybillLength)});
    }

    private void setTxtWaybillNo() {

        String barcode = txtWaybillNo.getText().toString();
//        txtWaybillNo.removeTextChangedListener(textWatcher);
        if (barcode.length() >= 8) {// && GlobalVar.WaybillNoStartSeries.contains(barcode.substring(0, 1))
//            txtWaybillNo.setText(barcode.substring(0, 8));
            txtWaybillNo.setText(barcode);

            //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));

        } else if (barcode.length() >= GlobalVar.ScanWaybillLength) {
//            txtWaybillNo.setText(barcode.substring(0, GlobalVar.ScanWaybillLength));
            txtWaybillNo.setText(barcode);
            //txtBarCode.setText(barcode.substring(0, GlobalVar.ScanWaybillLength));
            //ValidateWayBill(txtBarCode.getText().toString().substring(0, GlobalVar.ScanWaybillLength));
        }


        //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));


    }

    private void visibleWeightModule() {

        txtReason.setVisibility(View.INVISIBLE);
        chNoVolume.setVisibility(View.INVISIBLE);
        ReasonID = 0;
        constraintLayout.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.INVISIBLE);
        txtRemaining.setText(txtTotalPieces.getText());
        txtTotalPieces.setEnabled(false);
        history = new ArrayList<>();
        historyhide = new ArrayList<>();
        TotalHistory = 0;
        txtSameSize.setText(String.valueOf(history.size() + 1));
        txtSameSize.setKeyListener(null);

    }

    private boolean initialCalculation() {

        if (Integer.parseInt(txtRemaining.getText().toString()) >= GlobalVar.GV().getIntegerFromString(Ssize.getText().toString())) {
            if (Integer.parseInt(txtTotalPieces.getText().toString()) - Integer.parseInt(txtSameSize.getText().toString()) != 0)
                return validEnteredSize(true);
            else {
                double lenght = GlobalVar.GV().getDoubleFromString(txtLength.getText().toString());
                if (txtLength.getText().toString().equals("") || lenght <= 0) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to check the value of length " + txtLength.getText().toString(), GlobalVar.AlertType.Error);
                    return false;
                }

                double height = GlobalVar.GV().getDoubleFromString(txtHeight.getText().toString());
                if (txtHeight.getText().toString().equals("") || height <= 0) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to check the value of Height " + txtHeight.getText().toString(), GlobalVar.AlertType.Error);
                    return false;
                }

                double width = GlobalVar.GV().getDoubleFromString(txtWidth.getText().toString());
                if (txtWidth.getText().toString().equals("") || width <= 0) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to check the value of Width " + txtWidth.getText().toString(), GlobalVar.AlertType.Error);
                    return false;
                }


                int ssize = GlobalVar.GV().getIntegerFromString(Ssize.getText().toString());
                int r = 0;
                String re = "0";
                String ss = "0";
                if (ssize > 0) {
                    r = GlobalVar.GV().getIntegerFromString(txtRemaining.getText().toString());
                    re = String.valueOf(r - ssize);
                    ss = String.valueOf(ssize + GlobalVar.GV().getIntegerFromString(txtSameSize.getText().toString()));
                } else
                    ssize = 1;

                int sameSize = GlobalVar.GV().getIntegerFromString(txtSameSize.getText().toString());

                int asd = sameSize;
                for (int i = 0; i < ssize; i++) {
                    history.add(new WaybillMeasurementDetail(asd, width, lenght, height, 0));
                    asd = asd + 1;
                }
                historyhide.add(new WaybillMeasurementDetail(ssize, width, lenght, height, 0));
                TotalHistory = history.size();


                btnPlus.setVisibility(View.GONE);
                btnNext.setVisibility(View.VISIBLE);
                btnPrevious.setVisibility(View.VISIBLE);
                CurrentIndex++;

                txtRemaining.setText("0");
                Ssize.setText("0");

                txtSameSize.setText(String.valueOf(history.get(history.size() - 1).PiecesCount));
                txtWidth.setText(String.valueOf(history.get(history.size() - 1).Width));
                txtHeight.setText(String.valueOf(history.get(history.size() - 1).Height));
                txtLength.setText(String.valueOf(history.get(history.size() - 1).Length));

                lbIndex.setText("Index " + String.valueOf(history.size()));
                CurrentIndex = history.size();


            }
        } else {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter Samesize less" +
                    " than or equal to Remaining", GlobalVar.AlertType.Error);
            return false;
        }

        return true;
    }

    private boolean validEnteredSize(boolean clear) {

        if (txtRemaining.getText().toString().equals("0"))
            return false;

        double lenght = GlobalVar.GV().getDoubleFromString(txtLength.getText().toString());
        if (txtLength.getText().toString().equals("") || lenght <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to check the value of length " + txtLength.getText().toString(), GlobalVar.AlertType.Error);
            return false;
        }

        double height = GlobalVar.GV().getDoubleFromString(txtHeight.getText().toString());
        if (txtHeight.getText().toString().equals("") || height <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to check the value of Height " + txtHeight.getText().toString(), GlobalVar.AlertType.Error);
            return false;
        }

        double width = GlobalVar.GV().getDoubleFromString(txtWidth.getText().toString());
        if (txtWidth.getText().toString().equals("") || width <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to check the value of Width " + txtWidth.getText().toString(), GlobalVar.AlertType.Error);
            return false;
        }

        int TotalPieces = GlobalVar.GV().getIntegerFromString(txtTotalPieces.getText().toString());


        int remaining = GlobalVar.GV().getIntegerFromString(txtRemaining.getText().toString());

//        if (txtSameSize.getText().toString().equals("") ||
//                sameSize <= 0 ||
//                sameSize > remaining) {
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to check the value of Same Size " + txtSameSize.getText().toString(), GlobalVar.AlertType.Error);
//            return;
//        }
        int ssize = GlobalVar.GV().getIntegerFromString(Ssize.getText().toString());
        int r = 0;
        String re = "0";
        String ss = "0";
        if (ssize > 0) {
            r = GlobalVar.GV().getIntegerFromString(txtRemaining.getText().toString());
            re = String.valueOf(r - ssize);
            ss = String.valueOf(ssize + GlobalVar.GV().getIntegerFromString(txtSameSize.getText().toString()));
        } else
            ssize = 1;

        int sameSize = GlobalVar.GV().getIntegerFromString(txtSameSize.getText().toString());

        int asd = sameSize;
        for (int i = 0; i < ssize; i++) {
            history.add(new WaybillMeasurementDetail(asd, width, lenght, height, 0));
            asd = asd + 1;
        }
        historyhide.add(new WaybillMeasurementDetail(ssize, width, lenght, height, 0));
//        sameSize = sameSize + +Integer.parseInt(ss);
//
        TotalHistory = history.size();


        txtSameSize.setText(String.valueOf(history.size() + 1));
        txtSameSize.setKeyListener(null);

        CurrentIndex++;
        txtRemaining.setText(String.valueOf(TotalPieces - TotalHistory));

        if (txtRemaining.getText().toString().equals("0")) {
            btnPlus.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            btnPrevious.setVisibility(View.VISIBLE);
        }
        if (clear) {
            txtWidth.setText("");
            txtLength.setText("");
            txtHeight.setText("");
            Ssize.setText("");
            //txtSameSize.setText("");
        }
        if (txtRemaining.getText().toString().equals("0")) {
            txtSameSize.setText(String.valueOf(history.get(history.size() - 1).PiecesCount));
            txtWidth.setText(String.valueOf(history.get(history.size() - 1).Width));
            txtHeight.setText(String.valueOf(history.get(history.size() - 1).Height));
            txtLength.setText(String.valueOf(history.get(history.size() - 1).Length));

            lbIndex.setText("Index " + String.valueOf(history.size()));
            CurrentIndex = history.size();
        } else

            lbIndex.setText("Index " + String.valueOf(history.size() + 1));

        if (TotalPieces - TotalHistory == 0)
            return true;
        else
            return false;
    }

    private void setReason() {
        if (GlobalVar.GV().IsEnglish())
            reasonSpinnerDialog = new SpinnerDialog(WaybillMeasurementActivity.this,
                    NoNeedVolumeReasonNameList, "Select Reason", R.style.DialogAnimations_SmileWindow);
        else
            reasonSpinnerDialog = new SpinnerDialog(WaybillMeasurementActivity.this,
                    NoNeedVolumeReasonFNameList, "Select Reason", R.style.DialogAnimations_SmileWindow);


    }

    private boolean isValidToContinue() {
        boolean result = true;
        if (txtWaybillNo.getText().toString().equals("") || txtWaybillNo.getText().toString().length() < 8) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter Correct Waybill No", GlobalVar.AlertType.Error);
            result = false;
        }

        if (txtTotalPieces.getText().toString().equals("") || Integer.parseInt(txtTotalPieces.getText().toString()) <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Total Pieces", GlobalVar.AlertType.Error);
            result = false;
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.waybillmeasurmentmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuSave:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                    //SaveData();
                    IsValid();
                else
                    GlobalVar.RedirectSettings(WaybillMeasurementActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void IsValid() {


        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());

        if (txtWaybillNo.getText().toString().equals("") ||
                txtWaybillNo.getText().toString().length() < 8) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(),
                    "You have to enter the Waybill No ", GlobalVar.AlertType.Error);
            return;
        }

        if (txtTotalPieces.getText().toString().equals("") ||
                GlobalVar.GV().getIntegerFromString(txtTotalPieces.getText().toString()) <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Total Pieces", GlobalVar.AlertType.Error);
            return;
        }


        if (chNoVolume.isChecked() && ReasonID <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(),
                    "You have to enter the reason", GlobalVar.AlertType.Error);
            return;
        }
        if (!chNoVolume.isChecked()) {

            if (constraintLayout.getVisibility() == View.VISIBLE) {

                if (!txtRemaining.getText().toString().equals("0")) {
                    if (initialCalculation())
                        SaveData();
                } else
                    SaveData();

//                int remaining = GlobalVar.GV().getIntegerFromString(txtTotalPieces.getText().toString());
//                int rem = remaining - history.size();
//                if (rem > 0) {
//                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter" +
//                            " the remaining pieces " + txtRemaining.getText().toString(), GlobalVar.AlertType.Error);
//                    return;
//                }

            } else {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(),
                        "You have to press start Button", GlobalVar.AlertType.Error);
                return;
            }
        } else
            SaveData();


    }

    private void SaveData() {

        boolean IsSaved = true;

        double weight = 0.0;
        if (txtWeight.getText().toString().length() > 0)
            weight = Double.parseDouble(txtWeight.getText().toString());

        WaybillMeasurement waybillMeasurement = new WaybillMeasurement(GlobalVar.GV().getIntegerFromString(txtWaybillNo.getText().toString()),
                GlobalVar.GV().getIntegerFromString(txtTotalPieces.getText().toString()), GlobalVar.GV().getDeviceName(),
                weight, chNoVolume.isChecked(), ReasonID);

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        if (dbConnections.InsertWaybillMeasurement(waybillMeasurement, getApplicationContext())) {
            int waybillMeasurementID = dbConnections.getMaxID("WaybillMeasurement", getApplicationContext());

            for (int i = 0; i < historyhide.size(); i++) {
                WaybillMeasurementDetail waybillMeasurementDetail = new WaybillMeasurementDetail(historyhide.get(i).PiecesCount,
                        historyhide.get(i).Width, historyhide.get(i).Length, historyhide.get(i).Height, waybillMeasurementID);

                if (!dbConnections.InsertWaybillMeasurementDetail(waybillMeasurementDetail, getApplicationContext())) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                    IsSaved = false;
                    break;
                }
            }

            stopService(
                    new Intent(WaybillMeasurementActivity.this,
                            WayBillMeasurement.class));

            if (IsSaved) {
                if (!isMyServiceRunning(WayBillMeasurement.class)) {
                    startService(
                            new Intent(WaybillMeasurementActivity.this,
                                    WayBillMeasurement.class));
                }

                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", 0);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
        } else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
        dbConnections.close();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", 0);
                        setResult(Activity.RESULT_OK, returnIntent);
                        //finish();
                        WaybillMeasurementActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public ArrayList<Integer> NoNeedVolumeReasonList = new ArrayList<>();
    public ArrayList<String> NoNeedVolumeReasonNameList = new ArrayList<>();
    public ArrayList<String> NoNeedVolumeReasonFNameList = new ArrayList<>();

    public void GetNoNeedVolumeReasonList() {
        NoNeedVolumeReasonList.clear();
        NoNeedVolumeReasonNameList = new ArrayList<>();
        NoNeedVolumeReasonFNameList = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from NoNeedVolumeReason", getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));

                NoNeedVolumeReasonList.add(ID);
                NoNeedVolumeReasonNameList.add(Name);
                NoNeedVolumeReasonFNameList.add(FName);
            }
            while (result.moveToNext());
        }

        dbConnections.close();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //  if (!chNoVolume.isChecked())
        //       validEnteredSize(false);
        outState.putString("lbIndex", lbIndex.getText().toString());
        outState.putString("txtWaybillNo", txtWaybillNo.getText().toString());
        outState.putString("txtTotalPieces", txtTotalPieces.getText().toString());
        outState.putString("txtReason", txtReason.getText().toString());
        outState.putString("txtRemaining", txtRemaining.getText().toString());
        outState.putString("txtSameSize", txtSameSize.getText().toString());
        outState.putString("txtLength", txtLength.getText().toString());
        outState.putString("txtWidth", txtWidth.getText().toString());
        outState.putString("txtHeight", txtHeight.getText().toString());
        outState.putParcelableArrayList("history", history);
        outState.putInt("TotalHistory", TotalHistory);
        outState.putInt("CurrentIndex", CurrentIndex);
        outState.putInt("ReasonID", ReasonID);
        outState.putBoolean("chNoVolume", chNoVolume.isChecked());
        outState.putStringArrayList("NoNeedVolumeReasonNameList", NoNeedVolumeReasonNameList);
        outState.putStringArrayList("NoNeedVolumeReasonFNameList", NoNeedVolumeReasonFNameList);
        outState.putIntegerArrayList("NoNeedVolumeReasonList", NoNeedVolumeReasonList);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            lbIndex.setText(savedInstanceState.getString("lbIndex"));
            txtWaybillNo.setText(savedInstanceState.getString("txtWaybillNo"));
            txtTotalPieces.setText(savedInstanceState.getString("txtTotalPieces"));
            txtReason.setText(savedInstanceState.getString("txtReason"));
            txtRemaining.setText(savedInstanceState.getString("txtRemaining"));
            txtSameSize.setText(savedInstanceState.getString("txtSameSize"));
            txtLength.setText(savedInstanceState.getString("txtLength"));
            txtWidth.setText(savedInstanceState.getString("txtWidth"));
            txtHeight.setText(savedInstanceState.getString("txtHeight"));
            TotalHistory = savedInstanceState.getInt("TotalHistory");
            CurrentIndex = savedInstanceState.getInt("CurrentIndex");
            ReasonID = savedInstanceState.getInt("ReasonID");
            history = savedInstanceState.getParcelableArrayList("history");
            chNoVolume.setChecked(savedInstanceState.getBoolean("chNoVolume"));

            if (savedInstanceState.getBoolean("chNoVolume")) {
                txtReason.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.GONE);
                btnStart.setVisibility(View.INVISIBLE);
            } else {
                txtReason.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
            }
            // if (!chNoVolume.isChecked())
            //      validEnteredSize(false);

            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");

            if (isValidToContinue()) {
                txtReason.setVisibility(View.INVISIBLE);
                chNoVolume.setVisibility(View.INVISIBLE);
                ReasonID = 0;
                constraintLayout.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
                txtTotalPieces.setEnabled(false);

            }
            if (txtRemaining.getText().toString().equals("0")) {
                //btnPlus.setVisibility(View.INVISIBLE);
                btnNext.setVisibility(View.VISIBLE);
                btnPrevious.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplication()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

//    protected TextWatcher textWatcher = new TextWatcher() {
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            // your logic here
//            if (txtWaybillNo != null && txtWaybillNo.getText().length() >= 8)
//                //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
//                setTxtWaybillNo();
//
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            // your logic here
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            // your logic here
//        }
//    };
}

