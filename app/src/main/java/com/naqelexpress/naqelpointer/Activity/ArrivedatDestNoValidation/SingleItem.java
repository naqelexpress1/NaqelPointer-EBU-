package com.naqelexpress.naqelpointer.Activity.ArrivedatDestNoValidation;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;


public class SingleItem extends Fragment {

    Button btnOpenCamera;
    EditText txtBarCode;
    View rootView;
    public SingleItemAdapter adapter;
    static ArrayList<String> ValidateBarCodeList = new ArrayList<>();
    private GridView waybilgrid;
    TextView bpTotal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {
                rootView = inflater.inflate(R.layout.arivedatdestpallet, container, false);

                waybilgrid = (GridView) rootView.findViewById(R.id.waybills);
                bpTotal = (TextView) rootView.findViewById(R.id.bptotal);
                bpTotal.setText("Count : 0");
                adapter = new SingleItemAdapter(ValidateBarCodeList, getContext(), getActivity());
                waybilgrid.setAdapter(adapter);

                btnOpenCamera = rootView.findViewById(R.id.btnOpenCamera);
                txtBarCode = (EditText) rootView.findViewById(R.id.palletbarcode);
//                txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanBarcodeLength)});

                txtBarCode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (txtBarCode != null && txtBarCode.getText().length() >= 13)//every making 13 bcz it was reading mentioned number count in some devices
//                            ValidatePallet(txtBarCode.getText().toString());
                            setBarcode();
                    }
                });

//                txtBarCode.setOnKeyListener(new View.OnKeyListener() {
//                    public boolean onKey(View v, int keyCode, KeyEvent event) {
//                        // If the event is a key-down event on the "enter" button
//                        if (event.getAction() != KeyEvent.ACTION_DOWN)
//                            return true;
//                        else if (keyCode == KeyEvent.KEYCODE_BACK) {
//                            //finish();
//                            GlobalVar.onBackpressed(getActivity(), "Exit", "Are you sure want to Exit?");
//                            return true;
//                        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
//
//                            if (txtBarCode != null && txtBarCode.getText().length() >= 13)
////                            ValidatePallet(txtBarCode.getText().toString());
//                                setBarcode();
//                            return true;
//                        }
//                        return false;
//                    }
//                });

                btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                            GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                        } else
                        {
                            Intent newIntent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                            getActivity().startActivityForResult(newIntent, 2);
                        }

                    }
                });

            }

            ReadFromLocal();
            return rootView;
        }
    }


    private void setBarcode() {

        String barcode = txtBarCode.getText().toString();
//        utilities utilities = new utilities();
//        ValidatePallet(utilities.findwaybillno(barcode));
        ValidatePallet(barcode);


    }



    private void ValidatePallet(String barcode) {
        if (!ValidateBarCodeList.contains(barcode)) {
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
            txtBarCode.setText("");
            adapter.notifyDataSetChanged();
            ValidateBarCodeList.add(barcode);
            SaveBarcodetoLocal(barcode);
            bpTotal.setText("Count : " + String.valueOf(ValidateBarCodeList.size()));

        } else {
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }
    }

    private void SaveBarcodetoLocal(String barcode) {
        DBConnections dbConnections = new DBConnections(getContext(), null);
        dbConnections.InsertAtDestPieces(barcode, ArrivedatDestination.tripPlanID, getContext());
        dbConnections.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        txtBarCode.setText(barcode);
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtBarCode", txtBarCode.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ReadFromLocal();
        }
    }

    private void ReadFromLocal() {
        DBConnections dbConnections = new DBConnections(getContext(), null);
        // dbConnections.deleteArrivedatDestPieces(getContext());
        Cursor result = dbConnections.Fill("select * from AtDestPiece where TrailerNo = '" + ArrivedatDestination.tripPlanID
                + "'", getContext());
        if (result.getCount() > 0) {
            bpTotal.setText("Count : " + String.valueOf(result.getCount()));
            result.moveToFirst();
            do {
                String waybillno = String.valueOf(result.getString(result.getColumnIndex("BarCode")));
                if (!ValidateBarCodeList.contains(waybillno))
                    ValidateBarCodeList.add(waybillno);


            }
            while (result.moveToNext());
            adapter.notifyDataSetChanged();
        }

        result.close();
        dbConnections.close();
    }

}