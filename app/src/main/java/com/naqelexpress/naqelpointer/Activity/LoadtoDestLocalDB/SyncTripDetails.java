package com.naqelexpress.naqelpointer.Activity.LoadtoDestLocalDB;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hasna on 8/7/18.
 */

public class SyncTripDetails extends Activity implements TripDetailsAdapter.ItemClickListener {
    TripDetailsAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<HashMap<String, String>> tripDetails;
    private Paint p = new Paint();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notdeliveredsecondfragement);


        TextView lbTotal = (TextView) findViewById(R.id.lbTotal);
        lbTotal.setText("-- Trip Plan Details --");
//        lbTotal.setVisibility(View.GONE);
        EditText txtWaybilll = (EditText) findViewById(R.id.txtWaybilll);
        txtWaybilll.setVisibility(View.GONE);
        Button camera = (Button) findViewById(R.id.btnOpenCamera);
        camera.setVisibility(View.GONE);

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        tripDetails = dbConnections.GetTripDetails(getApplicationContext());


        initViews();
        initSwipe();
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
        //initSwipe();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(SyncTripDetails.this);
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
                    // removeView();

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


    protected void updatefile(int ID) {

        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);

            Cursor result = db.Fill("select * from TripPlanDDetails Where ID = " + ID, getApplicationContext());


            if (result.getCount() > 0) {

                if (result.moveToFirst()) {
                    int id = result.getInt(result
                            .getColumnIndex("ID"));
                    int tripid = result.getInt(result.getColumnIndex("TripPlanNo"));
                    int isSync = result.getInt(result.getColumnIndex("IsSync"));
                    if (isSync == 0) {
                        JSONObject jo = new JSONObject();
                        try {
                            jo.put("TripID", tripid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //String json = result.getString(result.getColumnIndex("Json"));
                        // json = json.replace("Date(-", "Date(");
                        Istripvalid(jo.toString(), tripid);
                    } else
                        ShowAlertMessage("Please wait already you requsted to Vehicle Manifest for this Trip (" + tripid + ")");
                }


            }
        } catch (Exception e) {
        }
    }

    ProgressDialog progressDialog;


    public void Istripvalid(final String input, final int tripid) {


        if (progressDialog == null)
            progressDialog = ProgressDialog.show(SyncTripDetails.this, "Please wait.",
                    "validating your trip", true);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = GlobalVar.GV().NaqelPointerAPILink + "IsTripIDValid";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                DBConnections db = new DBConnections(getApplicationContext(), null);

                try {
                    boolean IsSync = Boolean.parseBoolean(response.getString("IsSync"));
                    boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                    if (!HasError) {
                        db.UpdateTripDetails(tripid, getWindow().getDecorView().getRootView());

                        stopService(
                                new Intent(SyncTripDetails.this,
                                        com.naqelexpress.naqelpointer.service.VehicleManifest.class));
                        startService(
                                new Intent(SyncTripDetails.this,
                                        com.naqelexpress.naqelpointer.service.VehicleManifest.class));
                        finish();
                    } else
                        ShowAlertMessage(response.getString("ErrorMessage"));
                    if (progressDialog.isShowing() && progressDialog != null)
                        progressDialog.dismiss();

                    progressDialog = null;
                    db.close();
                } catch (JSONException e) {
                    if (progressDialog.isShowing() && progressDialog != null)
                        progressDialog.dismiss();
                    progressDialog = null;
                    if (db != null)
                        db.close();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ShowAlertMessage(error.toString());
                if (progressDialog.isShowing() && progressDialog != null)
                    progressDialog.dismiss();
                progressDialog.dismiss();
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

    public void SaveOnTripPlan(final DBConnections db, final String input, final int id) {


        if (progressDialog == null)
            progressDialog = ProgressDialog.show(SyncTripDetails.this, "Please wait.", "your request is processing", true);

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
                        finish();
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
                ShowAlertMessage(error.toString());
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

    private void ShowAlertMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(SyncTripDetails.this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    @Override
    public void onItemClick(View view, int position) {
        int id = Integer.parseInt(tripDetails.get(position).get("ID"));
        updatefile(id);

    }
}
