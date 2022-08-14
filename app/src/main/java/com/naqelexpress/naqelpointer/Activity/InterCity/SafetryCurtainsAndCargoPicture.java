package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

public class SafetryCurtainsAndCargoPicture extends AppCompatActivity implements View.OnClickListener{

    TextView back, next;
    Button uploadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_curtains_and_cargo_pictures);

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
                Intent intent = new Intent(getApplicationContext(), TrailerBody.class);
                startActivity(intent);
                break;

        }
    }
}
