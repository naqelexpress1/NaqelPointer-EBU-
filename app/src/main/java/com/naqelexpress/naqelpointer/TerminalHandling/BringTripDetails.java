package com.naqelexpress.naqelpointer.TerminalHandling;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Error.ErrorReporter;

/**
 * Created by Hasna on 8/7/18.
 */

public class BringTripDetails extends Activity implements TripDetailsAdapter.ItemClickListener {
    TripDetailsAdapter adapter;
    private RecyclerView recyclerView;
    static ArrayList<HashMap<String, String>> tripDetails;
    private Paint p = new Paint();
    int function = 0; // 0 LoadToTrip - 1 TruckArrival

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());
        setContentView(R.layout.notdeliveredsecondfragement);


        TextView lbTotal = (TextView) findViewById(R.id.lbTotal);
        //lbTotal.setText("-- Trip Plan Details --");
        lbTotal.setVisibility(View.GONE);

        EditText txtWaybilll = (EditText) findViewById(R.id.txtWaybilll);
        txtWaybilll.setVisibility(View.GONE);
        Button camera = (Button) findViewById(R.id.btnOpenCamera);
        camera.setVisibility(View.GONE);

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        tripDetails = dbConnections.GetTripDetails(getApplicationContext());


        initViews();
        initSwipe();

        Intent intent = getIntent();
        function = intent.getIntExtra("Function", 0);
        try {
            JSONObject jsonObject = new JSONObject();
//            if (function == 0)
            jsonObject.put("StationID", GlobalVar.GV().StationID);
//            else
//            jsonObject.put("StationID", 502);
            jsonObject.put("Function", function);
            new BringTripData().execute(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TripDetailsAdapter(tripDetails);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setClickListener(this);
        initSwipe();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)//| ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BringTripDetails.this);
                    builder.setTitle("Confirm Deleting")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {

                                    int id = Integer.parseInt(tripDetails.get(position).get("ID"));
                                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                                    dbConnections.deleteTripDDetails(id, getApplicationContext());
                                    adapter.removeItem(position);


                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    initViews();
                                }
                            })
                            .setCancelable(false);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    removeView();

                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.BLUE);
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removeView() {
//        if (view.getParent() != null) {
//            ((ViewGroup) view.getParent()).removeView(view);
//            ((ViewGroup) view.getParent()).removeView(view);
//        }
    }


    ProgressDialog progressDialog;

    public void SaveOnTripPlan(final DBConnections db, final String input, final int id) {


        if (progressDialog == null)
            progressDialog = ProgressDialog.show(BringTripDetails.this, "Please wait.", "your request is processing", true);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = GlobalVar.GV().NaqelPointerAPILink + "SendtoTriplanDDetails";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    boolean IsSync = Boolean.parseBoolean(response.getString("IsSync"));
                    boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                    if (IsSync && !HasError) {
                        db.deleteTripDDetails(id, getApplicationContext());

                    }
                    if (progressDialog.isShowing() && progressDialog != null)
                        progressDialog.dismiss();
                    db.close();
                } catch (JSONException e) {
                    if (progressDialog.isShowing() && progressDialog != null)
                        progressDialog.dismiss();
                    if (db != null)
                        db.close();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ShowAlertMessage(error.toString(), 0);
                if (progressDialog.isShowing() && progressDialog != null)
                    progressDialog.dismiss();
                progressDialog.dismiss();
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

    private void ShowAlertMessage(String message, final int close) {
        AlertDialog alertDialog = new AlertDialog.Builder(BringTripDetails.this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        if (close == 1)
                            finish();
                    }
                });
        alertDialog.show();
    }


    @Override
    public void onItemClick(View view, int position) {

        if (function == 0) {
            Intent intent = new Intent(BringTripDetails.this, TripDetails.class);
            intent.putExtra("tripdata", tripDetails.get(position));
            intent.putExtra("position", position);
            startActivity(intent);
        } else if (function == 1) {
            if (tripDetails.get(position).get("isReceived").equals("1")) {
                Intent intent = new Intent(BringTripDetails.this, TripArrviedatDestbyNCL.class);
                intent.putExtra("tripdata", tripDetails.get(position));

                startActivity(intent);
            } else {
                ShowAlertMessage("Kindly Received the Truck in Infotrack", 1);
            }
        } else {
            //Intent intent = new Intent(BringTripDetails.this, UndoNcl.class);
            Intent intent = new Intent(BringTripDetails.this, UndoNclbyScan.class);
            intent.putExtra("tripdata", tripDetails.get(position));
            startActivity(intent);
        }

    }

    private class BringTripData extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(BringTripDetails.this,
                        "Please wait.", "Bringing Trip Details...", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringTripDetails");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                dos = httpURLConnection.getOutputStream();
                httpURLConnection.getOutputStream();
                dos.write(jsonData.getBytes());

                ist = httpURLConnection.getInputStream();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return String.valueOf(buffer);
            } catch (Exception ignored) {
            } finally {
                try {
                    if (ist != null)
                        ist.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (dos != null)
                        dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            return null;
//


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {

                        fetchData(jsonObject);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                LoadDivisionError();
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    private void fetchData(JSONObject jsonObject) {

        tripDetails.clear();


        try {

            JSONArray status = jsonObject.getJSONArray("TripDetails");

            if (status.length() > 0) {
                for (int i = 0; i < status.length(); i++) {
                    JSONObject jsonObject1 = status.getJSONObject(i);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("ID", jsonObject1.getString("LinehaulPlanID"));
                    temp.put("TripID", jsonObject1.getString("TripID"));
                    temp.put("TripCode", jsonObject1.getString("TripCode"));
                    temp.put("VehicleType", jsonObject1.getString("VehicleType"));
                    temp.put("OriginID", jsonObject1.getString("OriginID"));
                    temp.put("DestinationID", jsonObject1.getString("DestinationID"));
                    temp.put("Vendor", jsonObject1.getString("Vendor"));
                    //String spilt[] = jsonObject1.getString("ETD").split("T");
                    if (function == 1) {
                        temp.put("ETA", jsonObject1.getString("ETA"));
                        temp.put("function", String.valueOf(function));
                    } else {
                        temp.put("function", String.valueOf(function));
                        String date[] = jsonObject1.getString("Date").split("T");

                        temp.put("ETA", date[0] + " " + jsonObject1.getString("ETD"));
                    }
                    temp.put("Origin", jsonObject1.getString("Origin"));
                    temp.put("Destination", jsonObject1.getString("Destination"));
                    temp.put("DestinationsID", jsonObject1.getString("DestinationsID"));
//                    String AdHoc = "0";
//                    if (jsonObject1.getBoolean("AdHoc"))
//                        AdHoc = "1";0
                    temp.put("AdHoc", String.valueOf(jsonObject1.getInt("AdHoc")));
                    temp.put("isReceived", String.valueOf(jsonObject1.getInt("isReceived")));
                    tripDetails.add(temp);

                }

                adapter.notifyDataSetChanged();
            } else
                ShowAlertMessage("No Trips Belongs your Station, Your Station is " + String.valueOf(GlobalVar.GV().StationID), 0);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(BringTripDetails.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage("Kindly Check your Internet Connection,please try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("StationID", GlobalVar.GV().StationID);
                            jsonObject.put("Function", 0);
                            new BringTripData().execute(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
