package com.naqelexpress.naqelpointer.Activity.Rating;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.MainPage.MainPageActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.Enum.Enum;
import com.naqelexpress.naqelpointer.Models.RatingModel;
import com.naqelexpress.naqelpointer.Models.Request.CommonRequest;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.callback.AlertCallback;
import com.naqelexpress.naqelpointer.callback.Callback;
import com.naqelexpress.naqelpointer.utils.RatingApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class CourierRating extends AppCompatActivity implements AlertCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ratingcbu);

        // Bundle bundle = getIntent().getExtras();

        // EditText remarks = (EditText) findViewById(R.id.remarks);
        // remarks.setVisibility(View.GONE);
        //  Button btn = (Button) findViewById(R.id.buttonGetRatingBarResult);
        //   btn.setVisibility(View.GONE);

        FetchRating();
        // redirectMainPage();

    }

    private void RedirecttoMainPage() {
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(5000);

                    redirectMainPage();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    finish();
                    //redirectMainPage();
                }
            }
        };
        myThread.start();
    }

    private void redirectMainPage() {
        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
        startActivity(intent);
        finish();
    }


    private void FetchRating() {
        final DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        int EMPID = GlobalVar.GetEmployID(getApplicationContext());
        HashMap<String, Object> empdetails = GlobalVar.GetEmployID_Name(getApplicationContext());
        TextView txtname = (TextView) findViewById(R.id.txtname);
        txtname.setText("Mr." + empdetails.get("EmployName").toString());

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(cal.getTime());

        TextView txtmonth = (TextView) findViewById(R.id.txtyourrating);
        txtmonth.setText("Your Rating For This Month  \n (" + month_name + " , " + String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) + ")");

        GlobalVar.GV().alertMsgAll("Info", "Please wait " + String.valueOf(EMPID), CourierRating.this,
                Enum.PROGRESS_TYPE, "CourierRating");

        CommonRequest commonRequest = new CommonRequest();

        commonRequest.setEmployID(EMPID);
        commonRequest.setPassword(DBConnections.getUserPassword(getApplicationContext(), EMPID));

        RatingApi.fetchRating(new Callback<RatingModel>() {
            @Override
            public void returnResult(RatingModel result) {
                System.out.println();
                // listen to the rating bar changes
                dbConnections.InsertCourierRating(result, getApplicationContext());
                addListenerOnRatingBar(result.getRating());
                RedirecttoMainPage();

                exitdialog();
            }

            @Override
            public void returnError(String message) {
                //RedirecttoMainPage();
                exitdialog();
                GlobalVar.GV().alertMsgAll("Info", message, CourierRating.this,
                        Enum.NORMAL_TYPE, "CourierRating");

                System.out.println(message);

            }
        }, commonRequest);
    }


    public void addListenerOnRatingBar(float ratingtxt) {

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);


        //ratingBar.setStepSize(ratingtxt);
        ratingBar.setNumStars(3);
        ratingBar.setRating(ratingtxt);
        final TextView ratingBarResult = (TextView) findViewById(R.id.ratingBarResult);


        ratingBarResult.setText(String.valueOf(ratingtxt));

//        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//
//                // update our result
//               // Rating = String.valueOf(rating);
//                ratingBarResult.setText("Result : " + String.valueOf(rating));
//
//            }
//        });
    }


    @Override
    public void returnOk(int ok, Activity activity) {

    }

    @Override
    public void returnCancel(int cancel, SweetAlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }

    static SweetAlertDialog alertDialog;

    private void exitdialog() {
        if (alertDialog != null) {
            alertDialog.dismissWithAnimation();
            alertDialog = null;
        }
    }
}