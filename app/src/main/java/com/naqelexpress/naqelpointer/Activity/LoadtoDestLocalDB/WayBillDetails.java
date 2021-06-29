package com.naqelexpress.naqelpointer.Activity.LoadtoDestLocalDB;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class WayBillDetails extends Fragment // implements ResultInterface
{
    View rootView;
    private EditText txtBarCode;
    static TextView waybillcount;
    static CourierAdapterNew adapter;
    private GridView waybilgrid;
    static TextView tripname, tripcode;

    //static ArrayList<HashMap<String, String>> waybilldetails = new ArrayList<>();
    public static ArrayList<String> validatewaybilldetails = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {

                rootView = inflater.inflate(R.layout.loadtodest, container, false);

                waybilgrid = (GridView) rootView.findViewById(R.id.waybills);
                adapter = new CourierAdapterNew(validatewaybilldetails, getContext(), getActivity());
                waybilgrid.setAdapter(adapter);

                tripname = (TextView) rootView.findViewById(R.id.tripname);
                tripcode = (TextView) rootView.findViewById(R.id.tripcode);

                waybillcount = (TextView) rootView.findViewById(R.id.waybillcount);
                txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);

                txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanWaybillLength)});

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
                            //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
                            setTxtWaybillNo();

                    }
                });
                // waybilldetails.clear();

            }
            ReadFromLocal();
            return rootView;
        }
    }

    private void setTxtWaybillNo() {

        String barcode = txtBarCode.getText().toString();
        if (barcode.length() >= 8 && GlobalVar.WaybillNoStartSeries.contains(barcode.substring(0, 1))) {
            //txtBarCode.setText(barcode.substring(0, 8));
            ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));

        } else if (barcode.length() >= GlobalVar.ScanWaybillLength) {
            //txtBarCode.setText(barcode.substring(0, GlobalVar.ScanWaybillLength));
            ValidateWayBill(txtBarCode.getText().toString().substring(0, GlobalVar.ScanWaybillLength));
        }

        //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));


    }

    private void ValidateWayBill(String waybillno) {
        if (!validatewaybilldetails.contains(waybillno)) {

            DBConnections dbConnections = new DBConnections(getContext(), null);
            dbConnections.InsertLoadtoDestWaybill(waybillno, LoadtoDestination.triplanID, getContext());
            dbConnections.close();

            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
            txtBarCode.setText("");
            adapter.notifyDataSetChanged();
            validatewaybilldetails.add(waybillno);
            waybillcount.setText(getString(R.string.lbCount) + validatewaybilldetails.size());

        } else {
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
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
        outState.putString("waybillcount", waybillcount.getText().toString());
        outState.putString("tripname", tripname.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

            validatewaybilldetails = savedInstanceState.getStringArrayList("validatewaybilldetails");
            waybillcount.setText(savedInstanceState.getString("waybillcount"));
            tripname.setText(savedInstanceState.getString("tripname"));
        }
    }

    private void ReadFromLocal() {
        validatewaybilldetails.clear();
        DBConnections dbConnections = new DBConnections(getContext(), null);
        Cursor result = dbConnections.Fill("select * from LoadtoDestLastWayBill where TrailerNo = '" + LoadtoDestination.triplanID
                + "'", getContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                String waybillno = String.valueOf(result.getString(result.getColumnIndex("WaybillNo")));
                if (!validatewaybilldetails.contains(waybillno))
                    validatewaybilldetails.add(waybillno);


            }
            while (result.moveToNext());
            adapter.notifyDataSetChanged();
            waybillcount.setText("Count : " + String.valueOf(result.getCount()));
        }

        result.close();
        dbConnections.close();
    }
}