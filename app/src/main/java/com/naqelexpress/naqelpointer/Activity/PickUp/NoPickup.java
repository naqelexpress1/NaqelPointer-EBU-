package com.naqelexpress.naqelpointer.Activity.PickUp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.naqelexpress.naqelpointer.Activity.Booking.Booking;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.NoPickupRequest;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NoPickup extends AppCompatActivity {

    protected boolean flag_thread = false;
    EditText txtRefNo;
    Spinner reason;
    Button btnConfirm;
    static String class_;
    static String RefNo = "";
    ArrayList<Booking> bookinglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle = getIntent().getExtras();
        class_ = bundle.getString("class");
        RefNo = bundle.getString("ref_no");

        //RefNo = "";




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_pickup);
        reason=findViewById(R.id.reason);
        txtRefNo=findViewById(R.id.txtRefNo);
        txtRefNo.setText(RefNo);
        if (class_.equals("BookingDetailAcyivity")) {


        }

        btnConfirm=findViewById(R.id.btnConfirm);
        String[] items=new String[]{"Not ready Shipment","shipper not responding","No loading facility","No any shipment","Oda schedule",};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        reason.setAdapter(adapter);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String refno=txtRefNo.getText().toString();
                //String refno=RefNo.toString();

                String reasontype = reason.getSelectedItem().toString();


                SaveData(refno,reasontype);
                UpdateNoPickup();

            }

        });

    }

    private void SaveData(String refno,String reasontype) {

        boolean IsSaved = false;
        String error = "";
        if (refno.toString().length() < 12)
            error += " Please enter the valid Pickup reference No \n";

        if (error.length() == 0) {
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            IsSaved= dbConnections.InsertNoPickupReason(refno,reasontype, GlobalVar.GV().EmployID,this.getApplicationContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(NoPickup.this);
            builder.setTitle("Info")
                    .setMessage("Do you want to Update Another Pickup again?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            txtRefNo.setText("");

                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {

                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", 0);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    }).setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        else
        {
            Toast.makeText(getApplicationContext(),error, Toast.LENGTH_SHORT).show();
            return;

        }
    }

    protected void UpdateNoPickup() {

        try {

            DBConnections db = new DBConnections(getApplicationContext(), null);

            Cursor result = db.Fill("select * from NoPickupReason where IsSync = 0 Limit 1 ", getApplicationContext());


            if (result.getCount() > 0) {

                if (result.moveToFirst()) {

                    NoPickupRequest noPickUpRequest = new NoPickupRequest();
                    noPickUpRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    noPickUpRequest.PickupRef = result.getString(result.getColumnIndex("RefNo"));
                    noPickUpRequest.Reason = result.getString(result.getColumnIndex("Reason"));
                    noPickUpRequest.Employeeid = Integer.parseInt(result.getString(result.getColumnIndex("UserID")));

                    String jsonData = JsonSerializerDeserializer.serialize(noPickUpRequest, true);
                    jsonData = jsonData.replace("Date(-", "Date(");


                    SaveNoPickup(db, jsonData, noPickUpRequest.ID);

                }


            } else {
                flag_thread = false;
                android.os.Process.killProcess(android.os.Process.myPid()); // kill service
            }
        } catch (Exception e) {
            flag_thread = false;
        }
    }
    public void SaveNoPickup(final DBConnections db, final String input, final int id) {


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String DomainURL = GlobalVar.GV().GetDomainURLforService(getApplicationContext(), "Pickup");
        String URL = DomainURL + "SumbitNoPickup";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                    if (!HasError) {
                        db.updatePickupbyID(id,getApplicationContext());
                        db.deletePickupID(id,getApplicationContext());

                        //db.deletePickupID(id, getApplicationContext());
                        // db.deletePickupDetails(id, getApplicationContext());


                        db.updateNoPickupbyID(id, getApplicationContext());


                        flag_thread = false;


                    } else
                        flag_thread = false;
                    db.close();
                    GlobalVar.GV().triedTimes_ForPickup = 0;
                } catch (JSONException e) {
                    flag_thread = false;
                    if (db != null)
                        db.close();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //ArrayList<String> value = GlobalVar.VolleyError(error);
                if (error.toString().contains("No address associated with hostname")) {

                } else {
                    GlobalVar.GV().triedTimes_ForPickup = GlobalVar.GV().triedTimes_ForPickup + 1;
                    if (GlobalVar.GV().triedTimes_ForPickup == GlobalVar.GV().triedTimesCondition) {
                        GlobalVar.GV().SwitchoverDomain_Service(getApplicationContext(), DomainURL, "Pickup");

                    }
                }
                flag_thread = false;
                db.close();
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
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
        requestQueue.getCache().remove(URL);

    }
}