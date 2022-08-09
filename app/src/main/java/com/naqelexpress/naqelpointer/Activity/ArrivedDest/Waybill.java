package com.naqelexpress.naqelpointer.Activity.ArrivedDest;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Waybill extends Fragment // implements ResultInterface
{


    View rootView;
    static WaybillAdapter adapter;
    private GridView waybilgrid;
    private EditText txtBarCode;
    ArrayList<HashMap<String, String>> waybilldetails = new ArrayList<>();
    static ArrayList<HashMap<String, String>> Selectedwaybill = new ArrayList<>();

    //    static ArrayList<HashMap<String, String>> waybillBardetails = new ArrayList<>();
    ArrayList<String> validatewaybillist = new ArrayList<>();
    TextView wbScanned, bpScanned, slScanned, wbTotal, bpTotal, slTotal;

    protected void displayReceivedData(String tripid, HashMap<String, String> headers, ArrayList<HashMap<String, String>> waybills) {
        waybilldetails.clear();
        wbTotal.setText(headers.get("WaybillCount"));
        bpTotal.setText(headers.get("PalletCount"));
        slTotal.setText(headers.get("SingleCount"));

        waybilldetails.addAll(waybills);
        adapter.notifyDataSetChanged();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.singleitem_atdest, container, false);

            wbTotal = (TextView) rootView.findViewById(R.id.wbtotal);
            wbScanned = (TextView) rootView.findViewById(R.id.wbscanned);

            bpTotal = (TextView) rootView.findViewById(R.id.bptotal);
            bpScanned = (TextView) rootView.findViewById(R.id.bpscanned);


            slTotal = (TextView) rootView.findViewById(R.id.sltotal);
            slScanned = (TextView) rootView.findViewById(R.id.slscanned);


            txtBarCode = (EditText) rootView.findViewById(R.id.waybillno);

            waybilgrid = (GridView) rootView.findViewById(R.id.waybills);
            adapter = new WaybillAdapter(waybilldetails, getContext());
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
                    if (txtBarCode != null && txtBarCode.getText().length() >= 8)
                        ValidateWayBill(txtBarCode.getText().toString());
                }
            });

            Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else{
                        Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                        getActivity().startActivityForResult(intent, 1);
                    }
                }
            });
        }

        return rootView;
    }

    private void ValidateWayBill(String barcode) {
        if (!validatewaybillist.contains(barcode)) {
            for (int i = 0; i < waybilldetails.size(); i++) {
                boolean sound = false;
                if (barcode.equals(waybilldetails.get(i).get("WaybillNo"))) {
                    validatewaybillist.add(barcode);
                    waybilldetails.get(i).put("bgcolor", "1");
                    Selectedwaybill.add(waybilldetails.get(i));
                    adapter.notifyDataSetChanged();
                    break;
                }

            }
        } else
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);

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
        outState.putString("txtBarCode", txtBarCode.getText().toString());
        outState.putStringArrayList("ValidateBarCodeList", validatewaybillist);
        outState.putSerializable("waybilldetails", waybilldetails);
        outState.putSerializable("Selectedwaybill", Selectedwaybill);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtBarCode.setText(savedInstanceState.getString("txtBarCode"));
            validatewaybillist = savedInstanceState.getStringArrayList("ValidateBarCodeList");
            waybilldetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybilldetails");
            Selectedwaybill = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("Selectedwaybill");
        }
    }
}