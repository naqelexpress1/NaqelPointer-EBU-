package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

public class TrailerBodyRemarks extends AppCompatActivity implements View.OnClickListener{

    TextView back, next;
    EditText trailerBoyRemarks;
    CheckBox landingLelFunctionalCBY, landingLelFunctionalCBN, landingLegsShoesCBY, landingLegsShoesCBN,
            checkLightsConditionCBY, checkLightsConditionCBN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_boy_remarks);

        findViewById();


    }

    public void findViewById(){
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);

        trailerBoyRemarks = findViewById(R.id.trailerBoyRemarks);
        landingLelFunctionalCBY = findViewById(R.id.landingLelFunctionalCBY);
        landingLelFunctionalCBN = findViewById(R.id.landingLelFunctionalCBN);
        landingLegsShoesCBY = findViewById(R.id.landingLegsShoesCBY);
        landingLegsShoesCBN = findViewById(R.id.landingLegsShoesCBN);
        checkLightsConditionCBY = findViewById(R.id.checkLightsConditionCBY);
        checkLightsConditionCBN = findViewById(R.id.checkLightsConditionCBN);

        back.setOnClickListener(this);
        next.setOnClickListener(this);
        landingLelFunctionalCBY.setOnClickListener(this);
        landingLelFunctionalCBN.setOnClickListener(this);
        landingLegsShoesCBY.setOnClickListener(this);
        landingLegsShoesCBN.setOnClickListener(this);
        checkLightsConditionCBY.setOnClickListener(this);
        checkLightsConditionCBN.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.landingLelFunctionalCBY:
                if(landingLelFunctionalCBN.isChecked()){
                    landingLelFunctionalCBN.setChecked(false);
                }
                break;

            case R.id.landingLelFunctionalCBN:
                if(landingLelFunctionalCBY.isChecked()){
                    landingLelFunctionalCBY.setChecked(false);
                }
                break;

            case R.id.landingLegsShoesCBY:
                if(landingLegsShoesCBN.isChecked()){
                    landingLegsShoesCBN.setChecked(false);
                }
                break;

            case R.id.landingLegsShoesCBN:
                if(landingLegsShoesCBY.isChecked()){
                    landingLegsShoesCBY.setChecked(false);
                }
                break;

            case R.id.checkLightsConditionCBY:
                if(checkLightsConditionCBN.isChecked()){
                    checkLightsConditionCBN.setChecked(false);
                }
                break;

            case R.id.checkLightsConditionCBN:
                if(checkLightsConditionCBY.isChecked()){
                    checkLightsConditionCBY.setChecked(false);
                }
                break;



            case R.id.back:
                onBackPressed();
                break;

            case R.id.next:
                Intent intent = new Intent(getApplicationContext(), AttachPictures.class);
                startActivity(intent);
                break;

        }
    }
}
