package com.naqelexpress.naqelpointer.Activity.OFDPieceLevel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.naqelexpress.naqelpointer.DB.SelectData;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class DeliverySheetFirstFragment
        extends Fragment {
    View rootView;
    public EditText txtCourierID, txtTruckID;
    ArrayList<String> tlareaList = new ArrayList<>();
    String tlAreaName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.deliverysheetfirstfragment, container, false);
            txtCourierID = (EditText) rootView.findViewById(R.id.txtCourierID);
            txtTruckID = (EditText) rootView.findViewById(R.id.txtTruckID);

//            Spinner spinner = (Spinner) rootView.findViewById(R.id.tlareaallocation);
//            if (getResources().getBoolean(R.bool.isYandextest)) {
//                spinner.setVisibility(View.VISIBLE);
//                setSpinnerList();
//                setSpinnerAdapter(spinner);
//            }

        }


        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtCourierID.setText(savedInstanceState.getString("txtCourierID"));
            txtTruckID.setText(savedInstanceState.getString("txtTruckID"));
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString("txtCourierID", txtCourierID.getText().toString());
        outState.putString("txtTruckID", txtTruckID.getText().toString());

        super.onSaveInstanceState(outState);
    }

    private void setSpinnerList() {
        tlareaList.clear();
        SelectData selectData = new SelectData();
        tlareaList.addAll(selectData.FetchTLAllocationArea(getContext()));
    }

    private void setSpinnerAdapter(Spinner spinner) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, tlareaList);

        spinner.setBackgroundResource(android.R.drawable.spinner_dropdown_background);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false);
        // You can create an anonymous listener to handle the event when is selected an spinner item
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                // String value = adapter.getItem(position);
                tlAreaName = adapter.getItem(position);

                // DeliverySheetFirstFragment deliverySheetFirstFragment = new DeliverySheetFirstFragment();
                SelectData selectData = new SelectData();
                if (tlAreaName.length() > 0 && !tlAreaName.equals("Select Area"))
                    selectData.FetchTLAllocationPiecesbyArea(getContext(), tlAreaName);


                DeliverySheetThirdFragment deliverySheetThirdFragment = new DeliverySheetThirdFragment();
                deliverySheetThirdFragment.initViews();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
    }

}