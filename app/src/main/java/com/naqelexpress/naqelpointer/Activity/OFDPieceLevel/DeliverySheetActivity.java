package com.naqelexpress.naqelpointer.Activity.OFDPieceLevel;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.OFDPieceLevel;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDWaybill;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCloadingForD;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.OptimizedOutOfDeliveryShipmentRequest;
import com.naqelexpress.naqelpointer.JSON.Results.OptimizedOutOfDeliveryShipmentResult;
import com.naqelexpress.naqelpointer.NCLBulk.NclShipmentActivity;
import com.naqelexpress.naqelpointer.OnlineValidation.AsyncTaskCompleteListener;
import com.naqelexpress.naqelpointer.OnlineValidation.OnlineValidationAsyncTask;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Retrofit.APICall;
import com.naqelexpress.naqelpointer.Retrofit.IAPICallListener;
import com.naqelexpress.naqelpointer.service.DeliverysheetbyPiece;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DeliverySheetActivity extends AppCompatActivity implements IAPICallListener {

    DeliverySheetFirstFragment firstFragment;
    DeliverySheetSecondFragment secondFragment;
    DeliverySheetThirdFragment thirdFragment;

    DateTime TimeIn;
    TabLayout tabLayout;

    private static final String TAG =  "DeliverySheetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.deliverysheet);
        try {
            String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);
            if (division.equals("Courier")) {
                if (!isValidOnlineValidationFile()) {

                    APICall apiCall = new APICall(getApplicationContext() , DeliverySheetActivity.this , this);
                    apiCall.getOnlineValidationData(GlobalVar.DsAndInventory);

                /*OnlineValidationAsyncTask onlineValidationAsyncTask = new OnlineValidationAsyncTask(getApplicationContext() , DeliverySheetActivity.this , this);
                onlineValidationAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR , String.valueOf(GlobalVar.DsAndInventory));*/
                }
            }
        } catch (Exception e) {
            Log.d("test" , TAG + " " + e.toString());
        }




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        GlobalVar.GV().ResetTriedCount();

        TimeIn = DateTime.now();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delivery_sheet, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                    SaveData();
                else
                    GlobalVar.RedirectSettings(DeliverySheetActivity.this);
                return true;
            case R.id.mnuGetMyRouteShipments:
                GetMyRouteShipments();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void SaveData() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            boolean IsSaved = true;
            OnCloadingForD onCloadingForD = new OnCloadingForD(GlobalVar.GV().getIntegerFromString
                    (firstFragment.txtCourierID.getText().toString()),
                    thirdFragment.PieceBarCodeList.size(), thirdFragment.WaybillList.size(),
                    firstFragment.txtTruckID.getText().toString());

            if (dbConnections.InsertOnCloadingForD(onCloadingForD, getApplicationContext())) {
                int OnCloadingForDID = dbConnections.getMaxID("OnCloadingForD", getApplicationContext());
                for (int i = 0; i < thirdFragment.WaybillList.size(); i++) {
                    OnCLoadingForDWaybill onCLoadingForDWaybill = new OnCLoadingForDWaybill(thirdFragment.WaybillList.get(i),
                            OnCloadingForDID);
                    if (!dbConnections.InsertOnCLoadingForDWaybill(onCLoadingForDWaybill, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                for (int i = 0; i < thirdFragment.PieceBarCodeWaybill.size(); i++) { //thirdFragment.PieceBarCodeList

                    String split[] = thirdFragment.PieceBarCodeWaybill.get(i).split("-");

                    OFDPieceLevel onCLoadingForDDetail = new OFDPieceLevel(split[0],
                            OnCloadingForDID, split[1]);

                    if (!dbConnections.InsertOnCLoadingbyPieceLevel(onCLoadingForDDetail, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                if (IsSaved) {

                    if (!isMyServiceRunning(DeliverysheetbyPiece.class)) {
                        startService(
                                new Intent(DeliverySheetActivity.this,
                                        DeliverysheetbyPiece.class));
                    }
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                    finish();
                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
        }
        dbConnections.close();
    }

    private boolean IsValid() {
        boolean isValid = true;
        if (firstFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Courier ID", GlobalVar.AlertType.Error);
            isValid = false;
        }


        if (thirdFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the pieces list", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (firstFragment != null) {
            if (firstFragment.txtCourierID.getText().toString().equals("")) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Courier ID", GlobalVar.AlertType.Error);
                isValid = false;
            }

            if (firstFragment.txtTruckID.getText().toString().equals("")) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Truck ID", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }


        if (thirdFragment != null) {
            if (thirdFragment.PieceBarCodeList.size() <= 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the Pieces", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

        return isValid;
    }



    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.deliverysheetfirstfragment, container, false);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (firstFragment == null) {
                        firstFragment = new DeliverySheetFirstFragment();
                        return firstFragment;
                    } else
                        return firstFragment;
                case 1:
                    if (thirdFragment == null) {
                        thirdFragment = new DeliverySheetThirdFragment();
                        return thirdFragment;
                    } else {
                        return thirdFragment;
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
                    return getResources().getString(R.string.OFDEmpInfo);
                case 1:
                    return getResources().getString(R.string.DeliverySheetThirdFragment);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        DeliverySheetActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //--------------Get My Route Shipments -------------------------------
    public void GetMyRouteShipments() {
        GlobalVar.GV().optimizedOutOfDeliveryShipmentList = new ArrayList<>();
        OptimizedOutOfDeliveryShipmentRequest optimizedOutOfDeliveryShipmentRequest = new OptimizedOutOfDeliveryShipmentRequest();
        String jsonData = JsonSerializerDeserializer.serialize(optimizedOutOfDeliveryShipmentRequest, true);
        new GetMyRouteShipmentsData().execute(jsonData);
    }


    private class GetWaybillInfo extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DeliverySheetActivity.this, "Please wait.", "Downloading My Route Shipments.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "OptimizedOutOfDeliveryShipment");
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
            progressDialog.dismiss();
            progressDialog = null;

            super.onPostExecute(String.valueOf(finalJson));
            if (finalJson != null)
                new OptimizedOutOfDeliveryShipmentResult(finalJson, getWindow().getDecorView().getRootView());
        }
    }


    private class GetMyRouteShipmentsData extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DeliverySheetActivity.this, "Please wait.", "Downloading My Route Shipments.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "OptimizedOutOfDeliveryShipment");
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
            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
            if (finalJson != null)
                new OptimizedOutOfDeliveryShipmentResult(finalJson, getWindow().getDecorView().getRootView());
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");

            firstFragment = (DeliverySheetFirstFragment) getSupportFragmentManager().getFragment(savedInstanceState, "firstFragment");
            secondFragment = (DeliverySheetSecondFragment) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
            thirdFragment = (DeliverySheetThirdFragment) getSupportFragmentManager().getFragment(savedInstanceState, "thirdFragment");
            GlobalVar.GV().optimizedOutOfDeliveryShipmentList = savedInstanceState.getStringArrayList("optimizedOutOfDeliveryShipmentList");

        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putStringArrayList("optimizedOutOfDeliveryShipmentList", GlobalVar.GV().optimizedOutOfDeliveryShipmentList);
        getSupportFragmentManager().putFragment(outState, "firstFragment", firstFragment);
        if (thirdFragment != null)
            getSupportFragmentManager().putFragment(outState, "thirdFragment", thirdFragment);

        super.onSaveInstanceState(outState);
    }

   /* @Override
    public void onTaskComplete(boolean hasError, String errorMessage) {
        if (hasError)
            ErrorAlert("Failed Loading File" , "Kindly contact your supervisor \n \n " + errorMessage);
        else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "File uploaded successfully", GlobalVar.AlertType.Info);
    }*/

    @Override
    public void onCallComplete(boolean hasError, String errorMessage) {
        try {
            if (hasError)
                ErrorAlert("Failed Loading File" , "Kindly contact your supervisor \n \n " + errorMessage);
            else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "File uploaded successfully", GlobalVar.AlertType.Info);
        } catch (Exception e) {

        }
    }


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

    private void ErrorAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(DeliverySheetActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    private boolean isValidOnlineValidationFile() {
        boolean isValid;
        try {
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            isValid = dbConnections.isValidOnlineValidationFile(GlobalVar.DsAndInventory , getApplicationContext());
            if (isValid)
                return true;
        } catch (Exception ex) {
            Log.d("test" , "isValidOnlineValidationFile() e " + ex.toString());
        }
        return false;
    }

}