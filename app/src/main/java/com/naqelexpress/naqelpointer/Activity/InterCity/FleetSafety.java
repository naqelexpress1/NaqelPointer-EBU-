package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

public class FleetSafety extends AppCompatActivity implements View.OnClickListener{

    TextView back, next;
    CheckBox fireExtinguisherAvailabilityCBY, fireExtinguisherAvailabilityCBN,
            fireExtinguisherValidityCBY, fireExtinguisherValidityCBN;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleet_safety);

        findViewById();


    }

    public void findViewById(){
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);

        fireExtinguisherAvailabilityCBY = findViewById(R.id.fireExtinguisherAvailabilityCBY);
        fireExtinguisherAvailabilityCBN = findViewById(R.id.fireExtinguisherAvailabilityCBN);
        fireExtinguisherValidityCBY = findViewById(R.id.fireExtinguisherValidityCBY);
        fireExtinguisherValidityCBN = findViewById(R.id.fireExtinguisherValidityCBN);
        submit = findViewById(R.id.submit);

        back.setOnClickListener(this);
        next.setOnClickListener(this);
        fireExtinguisherAvailabilityCBY.setOnClickListener(this);
        fireExtinguisherAvailabilityCBN.setOnClickListener(this);
        fireExtinguisherValidityCBY.setOnClickListener(this);
        fireExtinguisherValidityCBN.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.fireExtinguisherAvailabilityCBY:
                if(fireExtinguisherAvailabilityCBN.isChecked()){
                    fireExtinguisherAvailabilityCBN.setChecked(false);
                }
                break;

            case R.id.fireExtinguisherAvailabilityCBN:
                if(fireExtinguisherAvailabilityCBY.isChecked()){
                    fireExtinguisherAvailabilityCBY.setChecked(false);
                }
                break;

            case R.id.fireExtinguisherValidityCBY:
                if(fireExtinguisherValidityCBN.isChecked()){
                    fireExtinguisherValidityCBN.setChecked(false);
                }
                break;

            case R.id.fireExtinguisherValidityCBN:
                if(fireExtinguisherValidityCBY.isChecked()){
                    fireExtinguisherValidityCBY.setChecked(false);
                }
                break;

            case R.id.submit:

                break;



            case R.id.back:
                onBackPressed();
                break;

            case R.id.next:
                break;

        }
    }

}
