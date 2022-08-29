package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.naqelexpress.naqelpointer.Activity.Constants.Constant;
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
                Constant.interCityModel.setLandingLegFunctional(1);
                if(landingLelFunctionalCBN.isChecked()){
                    landingLelFunctionalCBN.setChecked(false);
                }
                break;

            case R.id.landingLelFunctionalCBN:
                Constant.interCityModel.setLandingLegFunctional(0);
                if(landingLelFunctionalCBY.isChecked()){
                    landingLelFunctionalCBY.setChecked(false);
                }
                break;

            case R.id.landingLegsShoesCBY:
                Constant.interCityModel.setLandingLegShoes(1);
                if(landingLegsShoesCBN.isChecked()){
                    landingLegsShoesCBN.setChecked(false);
                }
                break;

            case R.id.landingLegsShoesCBN:
                Constant.interCityModel.setLandingLegShoes(0);
                if(landingLegsShoesCBY.isChecked()){
                    landingLegsShoesCBY.setChecked(false);
                }
                break;

            case R.id.checkLightsConditionCBY:
                Constant.interCityModel.setCheckLightConditions(1);
                if(checkLightsConditionCBN.isChecked()){
                    checkLightsConditionCBN.setChecked(false);
                }
                break;

            case R.id.checkLightsConditionCBN:
                Constant.interCityModel.setCheckLightConditions(0);
                if(checkLightsConditionCBY.isChecked()){
                    checkLightsConditionCBY.setChecked(false);
                }
                break;



            case R.id.back:
                onBackPressed();
                break;

            case R.id.next:
                if(trailerBoyRemarks.getText().toString().equals("") || trailerBoyRemarks.getText().toString().equals(null)){
                    Constant.alert("Alert", "All Fields Required", TrailerBodyRemarks.this);
                }else{
                    Constant.interCityModel.setTrailerBodyRemarks(trailerBoyRemarks.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), AttachPictures.class);
                    startActivity(intent);
                }

                break;

        }
    }
}
