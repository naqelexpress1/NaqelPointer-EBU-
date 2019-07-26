package com.naqelexpress.naqelpointer.Activity.LoadtoDest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class WayBillDetails extends Fragment // implements ResultInterface
{
    View rootView;
    private EditText txtBarCode;
    TextView waybillcount;
    static CourierAdapterNew adapter;
    private GridView waybilgrid;
    static TextView tripname;

    static ArrayList<HashMap<String, String>> waybilldetails = new ArrayList<>();
    public static ArrayList<String> validatewaybilldetails = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {

                rootView = inflater.inflate(R.layout.loadtodest, container, false);

                waybilgrid = (GridView) rootView.findViewById(R.id.waybills);
                adapter = new CourierAdapterNew(waybilldetails, getContext());
                waybilgrid.setAdapter(adapter);

                tripname = (TextView) rootView.findViewById(R.id.tripname);

                waybillcount = (TextView) rootView.findViewById(R.id.waybillcount);
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
                        if (txtBarCode != null && txtBarCode.getText().length() == 9)
                            ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
                    }
                });
                waybilldetails.clear();

            }

            return rootView;
        }
    }


    private void ValidateWayBill(String waybillno) {
        if (!validatewaybilldetails.contains(waybillno)) {

            HashMap<String, String> temp = new HashMap<>();
            temp.put("WaybillNo", waybillno);
            temp.put("bgcolor", "0");

            waybilldetails.add(temp);

            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
            txtBarCode.setText("");
            adapter.notifyDataSetChanged();
            validatewaybilldetails.add(waybillno);
            waybillcount.setText(getString(R.string.lbCount) + validatewaybilldetails.size());

        } else
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("waybilldetails", waybilldetails);
        outState.putStringArrayList("validatewaybilldetails", validatewaybilldetails);
        outState.putString("waybillcount", waybillcount.getText().toString());
        outState.putString("tripname", tripname.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            waybilldetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybilldetails");
            validatewaybilldetails = savedInstanceState.getStringArrayList("validatewaybilldetails");
            waybillcount.setText(savedInstanceState.getString("waybillcount"));
            tripname.setText(savedInstanceState.getString("tripname"));
        }
    }

}