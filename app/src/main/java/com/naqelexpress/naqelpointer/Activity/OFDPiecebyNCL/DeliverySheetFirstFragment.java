package com.naqelexpress.naqelpointer.Activity.OFDPiecebyNCL;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.R;

public class DeliverySheetFirstFragment
        extends Fragment {
    View rootView;
    public EditText txtCourierID, txtTruckID;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.deliverysheetfirstfragment, container, false);
            txtCourierID = (EditText) rootView.findViewById(R.id.txtCourierID);
            txtTruckID = (EditText) rootView.findViewById(R.id.txtTruckID);
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
}