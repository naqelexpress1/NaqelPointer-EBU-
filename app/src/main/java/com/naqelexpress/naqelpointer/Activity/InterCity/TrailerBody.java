package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.naqelexpress.naqelpointer.Activity.Constants.Constant;
import com.naqelexpress.naqelpointer.R;

public class TrailerBody extends AppCompatActivity implements View.OnClickListener{

    TextView back, next;
    CheckBox checkRearDoorBoltsCBY, checkRearDoorBoltsCBN, checkRearDoorLocksCBY, checkRearDoorLocksCBN,
            checkSlidingSupportCBY, checkSlidingSupportCBN, checkNumberPlateCBY, checkNumberPlateCBN,
            checkAirLeakCBY, checkAirLeakCBN, checkAirSuspensionCBY, checkAirSuspensionCBN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_boy);

        findViewById();



    }

    public void findViewById(){
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);

        checkRearDoorBoltsCBY = findViewById(R.id.checkRearDoorBoltsCBY);
        checkRearDoorBoltsCBN = findViewById(R.id.checkRearDoorBoltsCBN);
        checkRearDoorLocksCBY = findViewById(R.id.checkRearDoorLocksCBY);
        checkRearDoorLocksCBN = findViewById(R.id.checkRearDoorLocksCBN);
        checkSlidingSupportCBY = findViewById(R.id.checkSlidingSupportCBY);
        checkSlidingSupportCBN = findViewById(R.id.checkSlidingSupportCBN);
        checkNumberPlateCBY = findViewById(R.id.checkNumberPlateCBY);
        checkNumberPlateCBN = findViewById(R.id.checkNumberPlateCBN);
        checkAirLeakCBY = findViewById(R.id.checkAirLeakCBY);
        checkAirLeakCBN = findViewById(R.id.checkAirLeakCBN);
        checkAirSuspensionCBY = findViewById(R.id.checkAirSuspensionCBY);
        checkAirSuspensionCBN = findViewById(R.id.checkAirSuspensionCBN);

        back.setOnClickListener(this);
        next.setOnClickListener(this);

        checkRearDoorBoltsCBY.setOnClickListener(this);
        checkRearDoorBoltsCBN.setOnClickListener(this);
        checkRearDoorLocksCBY.setOnClickListener(this);
        checkRearDoorLocksCBN.setOnClickListener(this);
        checkSlidingSupportCBY.setOnClickListener(this);
        checkSlidingSupportCBN.setOnClickListener(this);
        checkNumberPlateCBY.setOnClickListener(this);
        checkNumberPlateCBN.setOnClickListener(this);
        checkAirLeakCBY.setOnClickListener(this);
        checkAirLeakCBN.setOnClickListener(this);
        checkAirSuspensionCBY.setOnClickListener(this);
        checkAirSuspensionCBN.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.checkRearDoorBoltsCBY:
                Constant.interCityModel.setCheckRearDoorBolts(1);
                if(checkRearDoorBoltsCBN.isChecked()){
                    checkRearDoorBoltsCBN.setChecked(false);
                }
                break;

            case R.id.checkRearDoorBoltsCBN:
                Constant.interCityModel.setCheckRearDoorBolts(0);
                if(checkRearDoorBoltsCBY.isChecked()){
                    checkRearDoorBoltsCBY.setChecked(false);
                }
                break;

            case R.id.checkRearDoorLocksCBY:
                Constant.interCityModel.setCheckRearDoorLocks(1);
                if(checkRearDoorLocksCBN.isChecked()){
                    checkRearDoorLocksCBN.setChecked(false);
                }
                break;

            case R.id.checkRearDoorLocksCBN:
                Constant.interCityModel.setCheckRearDoorLocks(0);
                if(checkRearDoorLocksCBY.isChecked()){
                    checkRearDoorLocksCBY.setChecked(false);
                }
                break;


            case R.id.checkSlidingSupportCBY:
                Constant.interCityModel.setCheckSlidingSupportPostAndItsLocking(1);
                if(checkSlidingSupportCBN.isChecked()){
                    checkSlidingSupportCBN.setChecked(false);
                }
                break;

            case R.id.checkSlidingSupportCBN:
                Constant.interCityModel.setCheckSlidingSupportPostAndItsLocking(0);
                if(checkSlidingSupportCBY.isChecked()){
                    checkSlidingSupportCBY.setChecked(false);
                }
                break;

            case R.id.checkNumberPlateCBY:
                Constant.interCityModel.setCheckNumberPlateAndHolder(1);
                if(checkNumberPlateCBN.isChecked()){
                    checkNumberPlateCBN.setChecked(false);
                }
                break;

            case R.id.checkNumberPlateCBN:
                Constant.interCityModel.setCheckNumberPlateAndHolder(0);
                if(checkNumberPlateCBY.isChecked()){
                    checkNumberPlateCBY.setChecked(false);
                }
                break;


            case R.id.checkAirLeakCBY:
                Constant.interCityModel.setCheckAirLeak(1);
                if(checkAirLeakCBN.isChecked()){
                    checkAirLeakCBN.setChecked(false);
                }
                break;

            case R.id.checkAirLeakCBN:
                Constant.interCityModel.setCheckAirLeak(0);
                if(checkAirLeakCBY.isChecked()){
                    checkAirLeakCBY.setChecked(false);
                }
                break;

            case R.id.checkAirSuspensionCBY:
                Constant.interCityModel.setCheckAirSuspensionCondition(1);
                if(checkAirSuspensionCBN.isChecked()){
                    checkAirSuspensionCBN.setChecked(false);
                }
                break;

            case R.id.checkAirSuspensionCBN:
                Constant.interCityModel.setCheckAirSuspensionCondition(0);
                if(checkAirSuspensionCBY.isChecked()){
                    checkAirSuspensionCBY.setChecked(false);
                }
                break;

            case R.id.back:
                onBackPressed();
                break;

            case R.id.next:
                Intent intent = new Intent(getApplicationContext(), TrailerBodyRemarks.class);
                startActivity(intent);
                break;

        }
    }
}
