package com.naqelexpress.naqelpointer.Activity.Login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hasna on 10/14/18.
 */

public class UpdatePasswordModule extends AppCompatActivity {

    LinearLayout ll;
    EditText otp;
    int verificationcode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.resetpwd);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ll = (LinearLayout) findViewById(R.id.ll);
        TextView textview = (TextView) findViewById(R.id.textView);
        textview.setText("******* update password *******");
        final EditText empid = (EditText) findViewById(R.id.employid);
        empid.setText(String.valueOf(GlobalVar.GV().EmployID));
        empid.setKeyListener(null);
        final EditText currentpwd = (EditText) findViewById(R.id.crntpwd);
        final EditText newpwd = (EditText) findViewById(R.id.newpwd);
        final EditText cnfpwd = (EditText) findViewById(R.id.cnfpwd);
        otp = (EditText) findViewById(R.id.otp);

        Button update = (Button) findViewById(R.id.updatepwd);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                com.naqelexpress.naqelpointer.DB.DBObjects.UpdatePassword up = new com.naqelexpress.naqelpointer.DB.DBObjects.UpdatePassword();

                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " + GlobalVar.GV().EmployID
                        , getApplicationContext());
                String curpwd = "";
                if (result != null && result.getCount() > 0) {

                    result.moveToFirst();
                    curpwd = result.getString(result.getColumnIndex("Password"));
                }
                result.close();
                dbConnections.close();
                if (currentpwd.getText().toString().replace(" ", "").length() > 0) {
                    if (curpwd.equals(currentpwd.getText().toString())) {
                        if (newpwd.getText().toString().replace(" ", "").length() > 0) {

                            if (cnfpwd.getText().toString().replace(" ", "").length() > 0) {
                                if (newpwd.getText().toString().equals(cnfpwd.getText().toString())) {


                                    up.EmployID = Integer.parseInt(empid.getText().toString());
                                    up.password = newpwd.getText().toString();
                                    up.CurrentPassword = currentpwd.getText().toString();

                                    up.Otp = 0;

                                    String jsonData = JsonSerializerDeserializer.serialize(up, true);
                                    UpdatePwd(jsonData);

                                } else
                                    Toast.makeText(getApplicationContext(), "Entered Passwords doesn't Match.", Toast.LENGTH_LONG).show();
                            } else
                                Toast.makeText(getApplicationContext(), "Enter Confirm Password", Toast.LENGTH_LONG).show();


                        } else
                            Toast.makeText(getApplicationContext(), "Enter New Password", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Current Password does not match with previos password", Toast.LENGTH_LONG).show();

                } else
                    Toast.makeText(getApplicationContext(), "Enter Current Password", Toast.LENGTH_LONG).show();


            }
        });


    }


    public void UpdatePwd(final String input) {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait.");
        progressDialog.setTitle("your request is being process");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = GlobalVar.GV().NaqelPointerAPILink + "UpdatePassowrdMainMenu";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    String message = response.getString("ErrorMessage");
//                    boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
//                    if (!HasError) {
//
//                    }
                    alertDialog(message);
                    progressDialog.dismiss();
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return input == null ? null : input.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", input, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }

        };
        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
        requestQueue.getCache().remove(URL);

    }


    private void alertDialog(final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(UpdatePasswordModule.this);
        builder.setTitle("Info")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (message.contains("Verification Code sent Sucessfully")) {
                            verificationcode = 1;
                            ll.setVisibility(View.VISIBLE);
                            otp.setText("");
                        } else if (message.contains("Your password is sucessfully updated")) {
                            updatepasword();
                        } else if (message.contains("OTP entered is expired.")) {
                            verificationcode = 0;
                            otp.setText("0");
                            ll.setVisibility(View.GONE);
                        }

                    }
                })

                .setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updatepasword() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        int id = dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ", getApplicationContext());
        UserMeLogin userMeLogin = new UserMeLogin(id);
        dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
        dbConnections.deleteUserME(GlobalVar.GV().EmployID);

        ActivityCompat.finishAffinity(UpdatePasswordModule.this);
        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        startActivity(intent);


    }
}
