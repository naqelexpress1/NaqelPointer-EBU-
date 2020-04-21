package com.naqelexpress.naqelpointer.Activity.CustomerRating;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.CustomerRating;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CustomerRatings extends AppCompatActivity {

    ArrayList<String> waybills;
    String Rating;
    EditText remarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ratingbar);

        Bundle bundle = getIntent().getExtras();

        remarks = (EditText) findViewById(R.id.remarks);
        waybills = bundle.getStringArrayList("waybill");

        // get the rating bar result
        addListenerOnButton();

        // listen to the rating bar changes
        addListenerOnRatingBar();
    }

    public void addListenerOnButton() {

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        Button button = (Button) findViewById(R.id.buttonGetRatingBarResult);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                float result = ratingBar.getRating();
                try {
                    JSONObject header = new JSONObject();
                    JSONArray jsonArray = new JSONArray();

                    header.put("DeliveredBy", GlobalVar.GV().EmployID);
                    header.put("Date", DateTime.now());
                    header.put("Rating", Rating);
                    header.put("Remark", remarks.getText().toString());

                    int partialDeliver = 0;
                    for (int i = 0; i < waybills.size(); i++) {
                        JSONObject jsonObject = new JSONObject();

                        if (partialDeliver == 50) {
                            header.put("Ratings", jsonArray);
                            jsonArray = new JSONArray();
                            insertRating(header.toString());
                            partialDeliver = 0;
                        }
                        jsonObject.put("WaybillNo", waybills.get(i));
                        jsonArray.put(jsonObject);
                        partialDeliver++;

                    }

                    header.put("Ratings", jsonArray);
                    insertRating(header.toString());


                    if (!isMyServiceRunning(CustomerRating.class)) {
                        startService(
                                new Intent(CustomerRatings.this, CustomerRating.class));
                    }
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", "done");
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Toast.makeText(CustomerRatings.this, "You have rated : " + String.valueOf(result), Toast.LENGTH_LONG).show();
            }

        });
    }

    // instead of getting the rating bar manually each time when the user press the button
    // we can get the result automagically adding a change listener on it.

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplication()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void insertRating(String json) {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.InsertCustomerRating(json, getApplicationContext());
        dbConnections.close();
    }

    public void addListenerOnRatingBar() {

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        final TextView ratingBarResult = (TextView) findViewById(R.id.ratingBarResult);

        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                // update our result
                Rating = String.valueOf(rating);
                ratingBarResult.setText("Result : " + String.valueOf(rating));

            }
        });
    }


}