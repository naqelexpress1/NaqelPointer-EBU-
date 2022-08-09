package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

public class TireConditionPicture extends AppCompatActivity implements View.OnClickListener {

    TextView back, next;
    Button uploadFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tire_condition_pictures);


        findViewById();

    }

    public void findViewById(){
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);

        uploadFile = findViewById(R.id.uploadFile);

        back.setOnClickListener(this);
        next.setOnClickListener(this);
        uploadFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uploadFile:

                break;

            case R.id.back:
                onBackPressed();
                break;

            case R.id.next:
                Intent intent = new Intent(getApplicationContext(), SafetyCurtainsAndCargo.class);
                startActivity(intent);
                break;

        }
    }
}
