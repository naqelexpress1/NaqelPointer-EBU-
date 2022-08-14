package com.naqelexpress.naqelpointer.Activity.EBURoute;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import android.support.v4.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toolbar;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.naqelexpress.naqelpointer.R.id.container;

/**
 * Created by Hasna on 11/20/18.
 */


public class DeliverySheet extends AppCompatActivity implements ListViewRoute.sendMapData {

    public void sendData() {
        try {

            String tag = "android:switcher:" + R.id.container + ":" + 0;
            ListViewRoute f = (ListViewRoute) getSupportFragmentManager().findFragmentByTag(tag);
            f.displayReceivedData();


        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void sendmapData() {
        try {

            String tag = "android:switcher:" + R.id.container + ":" + 1;
            MapViewRoute f = (MapViewRoute) getSupportFragmentManager().findFragmentByTag(tag);
            f.displayReceivedData();


        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    ListViewRoute firstFragment;
    MapViewRoute secondFragment;
    static int call = 0;

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
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.deleteDeliverysheetExceptByToday(getWindow().getDecorView().getRootView(), getApplicationContext());
        new DeleteContact().execute("");
//        dbConnections.deleteBarCodeExceptByToday(getWindow().getDecorView().getRootView());
//
        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where DDate = '" +
                GlobalVar.getDate() + "' and EmpID = " + GlobalVar.GV().EmployID, getApplicationContext());

        call = 0;
        if (result.getCount() == 0)
            new BringMyRouteShipmentsList().execute("");
        else
            call = 1;


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
                    }
                    return firstFragment;
                case 1:
                    if (secondFragment == null) {
                        secondFragment = new MapViewRoute();

                    }

                    return secondFragment;


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

    private class DeleteContact extends AsyncTask<String, String, String> {


        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {

            try {
                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                boolean loop = false;
                loop = GlobalVar.deleteContactRawID(dbConnections.ContactDetails(getApplicationContext()), getApplicationContext(), 0);
                int time = 1000;
                while (!loop)
                    Thread.sleep(time);


            } catch (InterruptedException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            finish();
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DeliverySheet.this,
                    "Info",
                    "Your Request is being process,kindly please wait");
        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    private class BringMyRouteShipmentsList extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DeliverySheet.this, "Please wait.", "Downloading Shipments Details.", true);
        }

        @Override
        protected String doInBackground(String... params) { //17748
            JSONObject jo = new JSONObject();
            try {
                jo.put("EmployID", GlobalVar.GV().EmployID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String jsonData = jo.toString();

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringMyRouteShipments");
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
            } catch (Exception e) {
                e.printStackTrace();
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
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {

            if (finalJson != null) { // 17099

                super.onPostExecute(String.valueOf(finalJson));

                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {

                        Location location = GlobalVar.getLastKnownLocation(getApplicationContext());

                        if (location != null) {
                            new MyRouteShipments(finalJson, String.valueOf(location.getLatitude()),
                                    String.valueOf(location.getLongitude()), getApplicationContext(),
                                    getWindow().getDecorView().getRootView());
                        } else
                            new MyRouteShipments(finalJson, "0", "0", getApplicationContext(),
                                    getWindow().getDecorView().getRootView());
                    }

                    sendData();
                    sendmapData();


                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
