package com.naqelexpress.naqelpointer.Activity.LoadtoDest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.HashMap;

/**
 * Created by Hasna on 11/7/18.
 */

public class TripDetails extends AppCompatActivity {

    static EditText seal1, seal2, seal3, seal4, seal5, seal6, bagseal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tripplandetails);
        HashMap<String, String> tripDetails = new HashMap<>();
        Bundle bundle = getIntent().getExtras();
        tripDetails = (HashMap<String, String>) bundle.getSerializable("tripDetails");

        TextView driver1 = (TextView) findViewById(R.id.driver1);
        TextView driver2 = (TextView) findViewById(R.id.driver2);
        TextView trackheadid = (TextView) findViewById(R.id.trackheadid);
        TextView trailerid = (TextView) findViewById(R.id.trailerid);

        seal1 = (EditText) findViewById(R.id.seal1);
        seal2 = (EditText) findViewById(R.id.seal2);
        seal3 = (EditText) findViewById(R.id.seal3);
        seal4 = (EditText) findViewById(R.id.seal4);
        seal5 = (EditText) findViewById(R.id.seal5);
        seal6 = (EditText) findViewById(R.id.seal6);
        bagseal = (EditText) findViewById(R.id.bagseal);
        TextView etd = (TextView) findViewById(R.id.etd);
        Button trilerclose = (Button) findViewById(R.id.trilerclose);
        trilerclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seal1.getText().toString().length() > 0)
                    if (seal2.getText().toString().length() > 0)
                        if (seal3.getText().toString().length() > 0)
                            if (seal4.getText().toString().length() > 0)
                                if (seal5.getText().toString().length() > 0)
                                    if (seal6.getText().toString().length() > 0) {
                                        finish();
                                    } else
                                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter Seal6", GlobalVar.AlertType.Error);
                                else
                                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter Seal5", GlobalVar.AlertType.Error);
                            else
                                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter Seal4", GlobalVar.AlertType.Error);
                        else
                            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter Seal3", GlobalVar.AlertType.Error);
                    else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter Seal2", GlobalVar.AlertType.Error);
                else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter Seal1", GlobalVar.AlertType.Error);
            }
        });

        driver1.setText(tripDetails.get("Driver1"));
        driver2.setText(tripDetails.get("Driver2"));
        trackheadid.setText(tripDetails.get("TruckID"));
        trailerid.setText(tripDetails.get("TrailerID"));
        etd.setText(tripDetails.get("ETD"));


    }
}
