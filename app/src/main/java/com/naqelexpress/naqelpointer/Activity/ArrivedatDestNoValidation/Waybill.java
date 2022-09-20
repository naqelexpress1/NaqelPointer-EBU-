package com.naqelexpress.naqelpointer.Activity.ArrivedatDestNoValidation;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.naqelexpress.naqelpointer.utils.utilities;

import java.util.ArrayList;

public class Waybill extends Fragment {

    Button btnOpenCamera;
    View rootView;
    WaybillAdapter adapter;
    private GridView waybilgrid;
    private EditText txtBarCode;
    static ArrayList<String> validatewaybillist = new ArrayList<>();
    TextView wbScanned, bpScanned, slScanned, wbTotal, bpTotal, slTotal;
    public static TextView tripcode, tripname;

//    protected TextWatcher textWatcher = new TextWatcher() {
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            // your logic here
//            if (txtBarCode != null && txtBarCode.getText().length() >= 8)
//                //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
//                setTxtWaybillNo();
//
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.singleitem_atdest, container, false);

            wbTotal = (TextView) rootView.findViewById(R.id.wbtotal);
            tripcode = (TextView) rootView.findViewById(R.id.tripcode);
            tripname = (TextView) rootView.findViewById(R.id.tripname);
            wbScanned = (TextView) rootView.findViewById(R.id.wbscanned);

            bpTotal = (TextView) rootView.findViewById(R.id.bptotal);
            bpTotal.setText("Count : 0");
            bpScanned = (TextView) rootView.findViewById(R.id.bpscanned);


            slTotal = (TextView) rootView.findViewById(R.id.sltotal);
            slScanned = (TextView) rootView.findViewById(R.id.slscanned);


            btnOpenCamera = rootView.findViewById(R.id.btnOpenCamera);
            txtBarCode = rootView.findViewById(R.id.waybillno);
//            txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanWaybillLength)});
            waybilgrid = (GridView) rootView.findViewById(R.id.waybills);
            adapter = new WaybillAdapter(validatewaybillist, getContext(), getActivity());
            waybilgrid.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            txtBarCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txtBarCode != null && txtBarCode.getText().length()  >= 9)//every making 9 bcz it was reading mentioned number count in some devices
                        setTxtWaybillNo();
                }
            });
//            txtBarCode.setOnKeyListener(new View.OnKeyListener() {
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    // If the event is a key-down event on the "enter" button
//                    if (event.getAction() != KeyEvent.ACTION_DOWN)
//                        return true;
//                    else if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        onBackpressed();
//                        return true;
//                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                        if (txtBarCode != null && txtBarCode.getText().toString().length() >= 8)
////                            setTxtWaybillNo(txtWaybillNo.getText().toString());
//                            setTxtWaybillNo();
//                        return true;
//                    }
//                    return false;
//                }
//            });

//            txtBarCode.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if (txtBarCode != null && txtBarCode.getText().length() >= 8)
//                        // ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
//                        setTxtWaybillNo();
//                }
//            });

            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else
                    {
                        Intent newIntent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                        getActivity().startActivityForResult(newIntent, 1);
                    }

                }
            });

            ReadFromLocal();

        }

        return rootView;
    }


    private void setTxtWaybillNo() {
        String barcode = txtBarCode.getText().toString();
        utilities utilities = new utilities();
        String nbarcode = utilities.findwaybillno(barcode);
        txtBarCode.setText(nbarcode);
        ValidateWayBill(nbarcode);

    }

    private void ReadFromLocal() {
        DBConnections dbConnections = new DBConnections(getContext(), null);
        Cursor result = dbConnections.Fill("select * from AtDestWaybill where TrailerNo = '" + ArrivedatDestination.tripPlanID
                + "'", getContext());
        if (result.getCount() > 0) {
            bpTotal.setText("Count : " + String.valueOf(result.getCount()));
            result.moveToFirst();
            do {
                String waybillno = String.valueOf(result.getInt(result.getColumnIndex("WayBillNo")));
                if (!validatewaybillist.contains(waybillno))
                    validatewaybillist.add(waybillno);


            }
            while (result.moveToNext());
            adapter.notifyDataSetChanged();
        }

        result.close();
        dbConnections.close();
    }

    private void ValidateWayBill(String barcode) {
        if (!validatewaybillist.contains(barcode)) {
            validatewaybillist.add(barcode);
            adapter.notifyDataSetChanged();
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
            txtBarCode.setText("");
            SaveWaybilltoLocal(barcode);
            bpTotal.setText("Count : " + String.valueOf(validatewaybillist.size()));

        } else {
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }
    }


    private void SaveWaybilltoLocal(String waybill) {
        int wb = Integer.parseInt(waybill);
        DBConnections dbConnections = new DBConnections(getContext(), null);
        dbConnections.InsertAtDestWaybill(wb, ArrivedatDestination.tripPlanID, getContext());
        dbConnections.close();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        txtBarCode.setText(barcode);
                        setTxtWaybillNo();
                    }
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

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
            txtBarCode.setText(savedInstanceState.getString("txtBarCode"));
            ReadFromLocal();
        }
    }

    private void onBackpressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exit Arrived Dest")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        getActivity().finish();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}