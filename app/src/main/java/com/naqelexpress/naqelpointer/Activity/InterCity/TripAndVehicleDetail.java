package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

public class TripAndVehicleDetail  extends AppCompatActivity implements View.OnClickListener {

    TextView back, next;
    EditText tripId, vehicleTractorHead, driverEmpId, driverEmpName, driverContactNo;
    CheckBox damageOrPunctureCBY, damageOrPunctureCBN, greaseHubCupCBY, greaseHubCupCBN, spareTireCBY, spareTireCBN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_and_vehicle_details);

        findViewById();
    }

    public void findViewById(){
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        tripId = findViewById(R.id.tripId);
        vehicleTractorHead = findViewById(R.id.vehicleTractorHead);
        driverEmpId = findViewById(R.id.driverEmpId);
        driverEmpName = findViewById(R.id.driverEmpName);
        driverContactNo = findViewById(R.id.driverContactNo);
        damageOrPunctureCBY = findViewById(R.id.damageOrPunctureCBY);
        damageOrPunctureCBN = findViewById(R.id.damageOrPunctureCBN);
        greaseHubCupCBY = findViewById(R.id.greaseHubCupCBY);
        greaseHubCupCBN = findViewById(R.id.greaseHubCupCBN);
        spareTireCBY = findViewById(R.id.spareTireCBY);
        spareTireCBN = findViewById(R.id.spareTireCBN);

        back.setOnClickListener(this);
        next.setOnClickListener(this);

        damageOrPunctureCBY.setOnClickListener(this);
        damageOrPunctureCBN.setOnClickListener(this);
        greaseHubCupCBY.setOnClickListener(this);
        greaseHubCupCBN.setOnClickListener(this);
        spareTireCBY.setOnClickListener(this);
        spareTireCBN.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.damageOrPunctureCBY:
                if(damageOrPunctureCBN.isChecked()){
                    damageOrPunctureCBN.setChecked(false);
                }
                break;

            case R.id.damageOrPunctureCBN:
                if(damageOrPunctureCBY.isChecked()){
                    damageOrPunctureCBY.setChecked(false);
                }
                break;


            case R.id.greaseHubCupCBY:
                if(greaseHubCupCBN.isChecked()){
                    greaseHubCupCBN.setChecked(false);
                }
                break;

            case R.id.greaseHubCupCBN:
                if(greaseHubCupCBY.isChecked()){
                    greaseHubCupCBY.setChecked(false);
                }
                break;


            case R.id.spareTireCBY:
                if(spareTireCBN.isChecked()){
                    spareTireCBN.setChecked(false);
                }
                break;

            case R.id.spareTireCBN:
                if(spareTireCBY.isChecked()){
                    spareTireCBY.setChecked(false);
                }
                break;

            case R.id.back:
                onBackPressed();
                break;

            case R.id.next:
                Intent intent = new Intent(getApplicationContext(), TireConditionPicture.class);
                startActivity(intent);
                break;

        }
    }
}
