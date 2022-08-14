package com.naqelexpress.naqelpointer.Activity.EBURoute;

import android.app.ProgressDialog;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.naqelexpress.naqelpointer.R.id.container;

/**
 * Created by Hasna on 11/20/18.
 */


public class DeliverySheet_OLD extends AppCompatActivity {

    public void sendData() {
        try {

            String tag = "android:switcher:" + R.id.container + ":" + 0;
            ListViewRoute f = (ListViewRoute) getSupportFragmentManager().findFragmentByTag(tag);
            f.displayReceivedData();


        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    ListViewRoute firstFragment;
    MapViewRoute secondFragment;

    ArrayList<HashMap<String, String>> deliverysheet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.delivery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        BringDeliverysheet();

//        BringMyRouteShipments(bringMyRouteShipmentsRequest, buttonclick);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (firstFragment == null) {
                        firstFragment = new ListViewRoute();
                        return firstFragment;
                    } else
                        return firstFragment;
                case 1:
                    if (secondFragment == null) {
                        secondFragment = new MapViewRoute();
                        return secondFragment;
                    } else {
                        return secondFragment;
                    }

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Listview";
                case 1:
                    return "Mapview";

            }
            return null;
        }
    }

//    private class BringMyRouteShipmentsList extends AsyncTask<String, Void, String> {
//        private ProgressDialog progressDialog;
//        String result = "";
//        StringBuffer buffer;
//        int buttonclick;
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(DeliverySheet.this, "Please wait.", "Downloading Shipments Details.", true);
//        }
//
//        @Override
//        protected String doInBackground(String... params) { //17748
//            String jsonData = params[0];
//            buttonclick = Integer.parseInt(params[1]);
//
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringMyRouteShipments");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//                result = String.valueOf(buffer);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String finalJson) {
//
//            if (finalJson != null) {
//
//                super.onPostExecute(String.valueOf(finalJson));
//
//                try {
//                    JSONObject jsonObject = new JSONObject(finalJson);
//                    //jsonObject.getJSONObject("");
//
//                    new MyRouteShipments(finalJson, String.valueOf(Latitude), String.valueOf(Longitude), getApplicationContext(),
//                            getWindow().getDecorView().getRootView());
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                adapter = new RouteListAdapter(getApplicationContext(), GlobalVar.GV().kpi, "CourierKpi");
//                mapListview.setAdapter(adapter);
//
//                //ValidateDatas();
//                btnStartTrip.setVisibility(View.GONE);
//                btnCloseTrip.setVisibility(View.GONE);
//
//            } else
//                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);
//            if (progressDialog != null && progressDialog.isShowing())
//                progressDialog.dismiss();
//
//            progressflag = 0;
//            flag_thread = false;
//        }
//    }

    public void BringDeliverysheet() {

        final ProgressDialog progressDialog = ProgressDialog.show(DeliverySheet_OLD.this, "Please wait.", "Downloading Your Route Shipments.", true);
        JSONObject js = new JSONObject();
        try {
            js.put("EmployID", GlobalVar.GV().EmployID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String input = js.toString();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = GlobalVar.GV().NaqelPointerAPILink + "BringEBURoute";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONArray deliveryshet = response.getJSONArray("DeliverySheet");
                    for (int i = 0; i < deliveryshet.length(); i++) {
                        JSONObject jsonObject = deliveryshet.getJSONObject(i);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WaybillNo", jsonObject.getString("WaybillNo"));
                        temp.put("PointerRoute", jsonObject.getString("PointerRoute"));
                        temp.put("Duration", jsonObject.getString("Duration"));
                        temp.put("PhoneNumber", jsonObject.getString("PhoneNumber"));

                        if (jsonObject.getInt("IsBookingRef") == 0) {
                            temp.put("bgcolor", "0"); //Delivery
                            temp.put("Activity", "Delivery");
                        } else if (jsonObject.getInt("IsBookingRef") == 1) {
                            temp.put("bgcolor", "1"); //Pickup
                            temp.put("Activity", "Pickup");
                        }

                        deliverysheet.add(temp);

                    }
                    if (deliveryshet.length() > 0)
                        sendData();
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //ArrayList<String> value = GlobalVar.VolleyError(error);

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
