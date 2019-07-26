package com.naqelexpress.naqelpointer.Activity.ShipmentPicking;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

public class ShipmentPickingSelectingDialog
        extends AppCompatActivity
{
    Button btnCallConsignee;//, btnAddShipment;
    TextView lbMoreOptions;
    ConstraintLayout ButtonsConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipmentpickingseletingdialog);

        ButtonsConstraintLayout = (ConstraintLayout) findViewById(R.id.ButtonsConstraintLayout);
        ButtonsConstraintLayout.setVisibility(View.VISIBLE);
        btnCallConsignee = (Button) findViewById(R.id.btnCallConsignee);

//        btnAddShipment = (Button) findViewById(R.id.btnAddShipment);

        lbMoreOptions = (TextView) findViewById(R.id.lbMoreOptions);
        lbMoreOptions.setVisibility(View.INVISIBLE);
    }
}