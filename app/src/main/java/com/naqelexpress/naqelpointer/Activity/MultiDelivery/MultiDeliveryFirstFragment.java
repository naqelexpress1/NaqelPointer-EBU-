package com.naqelexpress.naqelpointer.Activity.MultiDelivery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.R;

public class MultiDeliveryFirstFragment
        extends Fragment {
    View rootView;
    public EditText txtReceiverName;
    static int al = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.multideliveryfirstfragment, container, false);
            txtReceiverName = (EditText) rootView.findViewById(R.id.txtCheckPointType);
            CheckBox actualLocation = (CheckBox) rootView.findViewById(R.id.alocation);

            actualLocation.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //is chkIos checked?
                    if (((CheckBox) v).isChecked()) {
                        al = 1;
                    } else
                        al = 0;

                }
            });
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtReceiverName", txtReceiverName.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtReceiverName.setText(savedInstanceState.getString("txtReceiverName"));
        }
    }
}