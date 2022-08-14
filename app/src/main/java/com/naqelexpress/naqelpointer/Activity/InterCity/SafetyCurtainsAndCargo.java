package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

public class SafetyCurtainsAndCargo extends AppCompatActivity implements View.OnClickListener{

    TextView back, next;
    CheckBox singleDeckerCBY, singleDeckerCBN, doubleDeckerCBY, doubleDeckerCBN, checkCurtainLockCBY, checkCurtainLockCBN,
            checkCurtainBeltCBY, checkCurtainBeltCBN, cargoBeltAppliedCBY, cargoBeltAppliedCBN, checkCurtainRollersCBY,
            checkCurtainRollersCBN, checkCurtainCleanlinessCBY, checkCurtainCleanlinessCBN, curtainBeltLockCBY, curtainBeltLockCBN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_curtains_and_cargo);


        findViewById();


    }

    public void findViewById(){
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);

        singleDeckerCBY = findViewById(R.id.singleDeckerCBY);
        singleDeckerCBN = findViewById(R.id.singleDeckerCBN);
        doubleDeckerCBY = findViewById(R.id.doubleDeckerCBY);
        doubleDeckerCBN = findViewById(R.id.doubleDeckerCBN);
        checkCurtainLockCBY = findViewById(R.id.checkCurtainLockCBY);
        checkCurtainLockCBN = findViewById(R.id.checkCurtainLockCBN);
        checkCurtainBeltCBY = findViewById(R.id.checkCurtainBeltCBY);
        checkCurtainBeltCBN = findViewById(R.id.checkCurtainBeltCBN);
        cargoBeltAppliedCBY = findViewById(R.id.cargoBeltAppliedCBY);
        cargoBeltAppliedCBN = findViewById(R.id.cargoBeltAppliedCBN);
        checkCurtainRollersCBY = findViewById(R.id.checkCurtainRollersCBY);
        checkCurtainRollersCBN = findViewById(R.id.checkCurtainRollersCBN);
        checkCurtainCleanlinessCBY = findViewById(R.id.checkCurtainCleanlinessCBY);
        checkCurtainCleanlinessCBN = findViewById(R.id.checkCurtainCleanlinessCBN);
        curtainBeltLockCBY = findViewById(R.id.curtainBeltLockCBY);
        curtainBeltLockCBN = findViewById(R.id.curtainBeltLockCBN);

        back.setOnClickListener(this);
        next.setOnClickListener(this);
        singleDeckerCBY.setOnClickListener(this);
        singleDeckerCBN.setOnClickListener(this);
        doubleDeckerCBY.setOnClickListener(this);
        doubleDeckerCBN.setOnClickListener(this);
        checkCurtainLockCBY.setOnClickListener(this);
        checkCurtainLockCBN.setOnClickListener(this);
        checkCurtainBeltCBY.setOnClickListener(this);
        checkCurtainBeltCBN.setOnClickListener(this);
        cargoBeltAppliedCBY.setOnClickListener(this);
        cargoBeltAppliedCBN.setOnClickListener(this);
        checkCurtainRollersCBY.setOnClickListener(this);
        checkCurtainRollersCBN.setOnClickListener(this);
        checkCurtainCleanlinessCBY.setOnClickListener(this);
        checkCurtainCleanlinessCBN.setOnClickListener(this);
        curtainBeltLockCBY.setOnClickListener(this);
        curtainBeltLockCBN.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.singleDeckerCBY:
                if(singleDeckerCBN.isChecked()){
                    singleDeckerCBN.setChecked(false);
                }
                break;

            case R.id.singleDeckerCBN:
                if(singleDeckerCBY.isChecked()){
                    singleDeckerCBY.setChecked(false);
                }
                break;

            case R.id.doubleDeckerCBY:
                if(doubleDeckerCBN.isChecked()){
                    doubleDeckerCBN.setChecked(false);
                }
                break;

            case R.id.doubleDeckerCBN:
                if(doubleDeckerCBY.isChecked()){
                    doubleDeckerCBY.setChecked(false);
                }
                break;

            case R.id.checkCurtainLockCBY:
                if(checkCurtainLockCBN.isChecked()){
                    checkCurtainLockCBN.setChecked(false);
                }
                break;

            case R.id.checkCurtainLockCBN:
                if(checkCurtainLockCBY.isChecked()){
                    checkCurtainLockCBY.setChecked(false);
                }
                break;

            case R.id.checkCurtainBeltCBY:
                if(checkCurtainBeltCBN.isChecked()){
                    checkCurtainBeltCBN.setChecked(false);
                }
                break;

            case R.id.checkCurtainBeltCBN:
                if(checkCurtainBeltCBY.isChecked()){
                    checkCurtainBeltCBY.setChecked(false);
                }
                break;


            case R.id.cargoBeltAppliedCBY:
                if(cargoBeltAppliedCBN.isChecked()){
                    cargoBeltAppliedCBN.setChecked(false);
                }
                break;

            case R.id.cargoBeltAppliedCBN:
                if(cargoBeltAppliedCBY.isChecked()){
                    cargoBeltAppliedCBY.setChecked(false);
                }
                break;

            case R.id.checkCurtainRollersCBY:
                if(checkCurtainRollersCBN.isChecked()){
                    checkCurtainRollersCBN.setChecked(false);
                }
                break;

            case R.id.checkCurtainRollersCBN:
                if(checkCurtainRollersCBY.isChecked()){
                    checkCurtainRollersCBY.setChecked(false);
                }
                break;


            case R.id.checkCurtainCleanlinessCBY:
                if(checkCurtainCleanlinessCBN.isChecked()){
                    checkCurtainCleanlinessCBN.setChecked(false);
                }
                break;

            case R.id.checkCurtainCleanlinessCBN:
                if(checkCurtainCleanlinessCBY.isChecked()){
                    checkCurtainCleanlinessCBY.setChecked(false);
                }
                break;

            case R.id.curtainBeltLockCBY:
                if(curtainBeltLockCBN.isChecked()){
                    curtainBeltLockCBN.setChecked(false);
                }
                break;

            case R.id.curtainBeltLockCBN:
                if(curtainBeltLockCBY.isChecked()){
                    curtainBeltLockCBY.setChecked(false);
                }
                break;
            case R.id.back:
                onBackPressed();
                break;

            case R.id.next:
                Intent intent = new Intent(getApplicationContext(), SafetryCurtainsAndCargoPicture.class);
                startActivity(intent);
                break;

        }
    }
}
