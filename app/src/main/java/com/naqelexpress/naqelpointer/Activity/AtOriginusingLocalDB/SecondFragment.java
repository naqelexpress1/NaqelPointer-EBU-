package com.naqelexpress.naqelpointer.Activity.AtOriginusingLocalDB;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.utils.utilities;

import java.util.ArrayList;
import java.util.HashMap;

public class SecondFragment extends Fragment {
    View rootView;
    private EditText txtBarCode;
    TextView lbTotal;

    static DataAdapter adapter;
    private SwipeMenuListView swipeMenuListView;
    private Paint p = new Paint();
    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Intent intent;

    static ArrayList<HashMap<String, String>> Selectedwaybilldetails = new ArrayList<>();
    public static ArrayList<String> validatewaybilldetails = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {

                rootView = inflater.inflate(R.layout.atoriginfirstnew, container, false);
                lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);

                txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);

                txtBarCode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        if (txtBarCode != null && txtBarCode.getText().length() >= 8) {
                            //if (setTxtWaybillNo())
                            setTxtWaybillNo();
                        }
                    }
                });

                Button cmrabtn = (Button) rootView.findViewById(R.id.btnOpenCamera);
                //cmrabtn.setVisibility(View.GONE);

                cmrabtn.setOnClickListener(new View.OnClickListener() {
                    @Override
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


                swipeMenuListView = (SwipeMenuListView) rootView.findViewById(R.id.pieceslist);
                adapter = new DataAdapter(Selectedwaybilldetails, getContext());
                swipeMenuListView.setAdapter(adapter);

            }

            return rootView;
        }
    }





    private void setTxtWaybillNo() {

        String barcode = txtBarCode.getText().toString();
        utilities utilities = new utilities();
        ValidateWayBill(utilities.findwaybillno(barcode));


    }


    private void ValidateWayBill(String waybillno) {
        if (!validatewaybilldetails.contains(waybillno)) {
            for (int i = 0; i < CourierDetails.waybilldetails.size(); i++) {
                if (waybillno.equals(CourierDetails.waybilldetails.get(i).get("WaybillNo"))) {
                    Selectedwaybilldetails.add(CourierDetails.waybilldetails.get(i));
                    CourierDetails.waybilldetails.get(i).put("bgcolor", "1");
                    DBConnections dbConnections = new DBConnections(getContext(), null);
                    dbConnections.AtOriginScannedWaybill(waybillno, getView());
                    dbConnections.close();
                    GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
                    txtBarCode.setText("");
                    adapter.notifyDataSetChanged();
                    validatewaybilldetails.add(waybillno);
                    lbTotal.setText(getString(R.string.lbCount) + Selectedwaybilldetails.size());
                    break;
                }
            }
        } else
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
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
                        //AddNewPiece();
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
        outState.putStringArrayList("validatewaybilldetails", validatewaybilldetails);
        outState.putSerializable("waybilldetails", CourierDetails.waybilldetails);
        outState.putSerializable("waybillBardetails", CourierDetails.waybillBardetails);
        outState.putSerializable("Selectedwaybilldetails", Selectedwaybilldetails);
        outState.putString("txtBarCode", txtBarCode.getText().toString());
        outState.putString("waybillcount", lbTotal.getText().toString());

        //outState.putStringArrayList("ShipmentBarCodeList", ShipmentBarCodeList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            validatewaybilldetails = savedInstanceState.getStringArrayList("validatewaybilldetails");
            CourierDetails.waybilldetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybilldetails");
            CourierDetails.waybillBardetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybillBardetails");
            Selectedwaybilldetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("Selectedwaybilldetails");

        }
    }

    private void onBackpressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exit PickUp")
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

