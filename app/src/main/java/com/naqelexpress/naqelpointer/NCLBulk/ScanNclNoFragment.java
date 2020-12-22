package com.naqelexpress.naqelpointer.NCLBulk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.FacilityStatus;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.NclNoRequest;
import com.naqelexpress.naqelpointer.JSON.Results.NclNoResult;
import com.naqelexpress.naqelpointer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ScanNclNoFragment extends Fragment {

    public static EditText txtOrgin, txtDestination;
    private INclShipmentActivity iNclShipmentActivity;

    private View rootView;
    private SpinnerDialog destSpinnerDialog , destFacilitySpinnerDialog;
    private EditText etOriginFacility , etDestFacility;
    private CheckBox checkMix;
    private Button btngenerate;

    public int OriginID = 0, DestinationID = 0 ,
               OriginFacilityID = 0 , DestinationFacilityID = 0;

    private ArrayList<String> facilityList = new ArrayList<>();
    private List<Integer> facilityIDList = new ArrayList<>();

    private final static String TAG = "ScanNclNoFragment";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            iNclShipmentActivity = (INclShipmentActivity) context;
        } catch (ClassCastException e) {
            Log.d("test" , TAG + " " + e.toString());
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (rootView == null) {
            rootView = inflater.inflate(R.layout.th_scan_nclno, container, false);


            txtOrgin = (EditText) rootView.findViewById(R.id.txtOrgin);
            txtOrgin.setInputType(InputType.TYPE_NULL);
            OriginID = GlobalVar.GV().StationID;


            // Display logged in facility
            DBConnections dbConnections = new DBConnections(getContext() , null);
            OriginFacilityID = dbConnections.getUserFacilityID(getContext() , GlobalVar.GV().EmployID);
            FacilityStatus facilityStatus = dbConnections.getFacility(getContext() , OriginFacilityID);
            etOriginFacility = rootView.findViewById(R.id.et_Orgin_facility);
            etOriginFacility.setText(facilityStatus.Code + " : " + facilityStatus.Name.toUpperCase());
            etOriginFacility.setInputType(InputType.TYPE_NULL);



            txtDestination = (EditText) rootView.findViewById(R.id.txtDestination);
            txtDestination.setInputType(InputType.TYPE_NULL);

            etDestFacility = rootView.findViewById(R.id.et_destination_facility);
            etDestFacility.setInputType(InputType.TYPE_NULL);


            checkMix = (CheckBox) rootView.findViewById(R.id.checkMix);
            btngenerate = (Button) rootView.findViewById(R.id.btngenerate);

            //if (savedInstanceState == null) {

            GetStationList();

            //}
            txtDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        destSpinnerDialog.showSpinerDialog(false);
                }
            });

            // filter based on dest
            etDestFacility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     if (txtDestination.getText().toString().trim().length() > 0) {
                         getFacilityList(DestinationID);
                         destFacilitySpinnerDialog = new SpinnerDialog(getActivity(), facilityList, "Select or Search Facility", R.style.DialogAnimations_SmileWindow);
                         destFacilitySpinnerDialog.showSpinerDialog(false);
                         bindSpinner();
                     } else {
                         GlobalVar.GV().ShowSnackbar(rootView, "Kindly select destination station first", GlobalVar.AlertType.Error);
                     }
               }

            });



            if (GlobalVar.GV().IsEnglish())
                destSpinnerDialog = new SpinnerDialog(getActivity(), StationNameList, "Select or Search Destination", R.style.DialogAnimations_SmileWindow);
            else
                destSpinnerDialog = new SpinnerDialog(getActivity(), StationFNameList, "Select or Search Destination", R.style.DialogAnimations_SmileWindow);

            destSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    if (GlobalVar.GV().IsEnglish())
                        txtDestination.setText(StationNameList.get(position));
                    else
                        txtDestination.setText(StationFNameList.get(position));
                    txtOrgin.requestFocus();
                    DestinationID = StationList.get(position);
                }
            });

            checkMix.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        if (isChecked) {
                                                            ConfirmIsMix();
                                                        }
                                                    }
                                                }
            );

            btngenerate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (DestinationID == 0 || DestinationFacilityID == 0) { //!checkMix.isChecked() &&
                        GlobalVar.GV().ShowSnackbar(rootView, "Please select destination station/facility", GlobalVar.AlertType.Error);
                        return;
                    }
                    NclNoRequest nclNoReq = new NclNoRequest();
                    nclNoReq.OrginID = OriginID;
                    nclNoReq.DestinationID = DestinationID;
                    nclNoReq.OriginStationFacilityID =  OriginFacilityID;
                    nclNoReq.DestinationStationFacilityID = DestinationFacilityID;
                    nclNoReq.IsMix = checkMix.isChecked();
                    GenerateNclNo(nclNoReq);
                }
            });

        }
        return rootView;
    }


    public ArrayList<Integer> StationList = new ArrayList<>();
    public ArrayList<String> StationNameList = new ArrayList<>();
    public ArrayList<String> StationFNameList = new ArrayList<>();

    private void ConfirmIsMix() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info");
        alertDialog.setMessage("Do you want to confirmation?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkMix.setChecked(true);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkMix.setChecked(false);
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    public void GetStationList() {
     try {
         DBConnections dbConnections = new DBConnections(getContext(), null);
         StationList.clear();
         StationNameList.clear();
         StationFNameList.clear();

         Cursor result = dbConnections.Fill("select * from Station", getContext());
         if (result.getCount() > 0) {
             result.moveToFirst();
             do {
                 int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                 String Code = result.getString(result.getColumnIndex("Code"));
                 String Name = result.getString(result.getColumnIndex("Name"));
                 String FName = result.getString(result.getColumnIndex("FName"));

                 StationList.add(ID);
                 StationNameList.add(Code + " : " + Name);
                 StationFNameList.add(FName);
             }
             while (result.moveToNext());
         }
         dbConnections.close();
         txtOrgin.setText(GlobalVar.GV().GetStationByID(GlobalVar.GV().StationID, StationNameList, StationList));
     } catch (Exception e) {
         Log.d("test" , TAG + " " + e.toString());
     }
    }


    private void getFacilityList(int destStationID) {

        try {
            DBConnections dbConnections = new DBConnections(getContext(), null);

            facilityIDList.clear();
            facilityList.clear();

            int facilityCount = dbConnections.getCount("Facility" , "Station = " + destStationID , getContext());
            Cursor result = null;

            if (facilityCount == 0 ) {
                result = dbConnections.Fill("select * from Facility", getContext());

            } else {
                result = dbConnections.Fill("select * from Facility where Station = " + destStationID, getContext());
            }

            if (result.getCount() > 0) {
                result.moveToFirst();
                do {
                    int ID = Integer.parseInt(result.getString(result.getColumnIndex("FacilityID")));
                    String Code = result.getString(result.getColumnIndex("Code"));
                    String Name = result.getString(result.getColumnIndex("Name"));
                    String facilityCodeName = Code + " : " + Name;

                    facilityIDList.add(ID);
                    facilityList.add(facilityCodeName);

                }
                while (result.moveToNext());
            }

            dbConnections.close();
            txtOrgin.setText(GlobalVar.GV().GetStationByID(GlobalVar.GV().StationID, StationNameList, StationList));
        } catch (Exception e) {
            Log.d("test" , TAG + " " + e.toString());
        }
    }

    public void GenerateNclNo(NclNoRequest nclNoRequest) {
        String jsonData = JsonSerializerDeserializer.serialize(nclNoRequest, true);
        new BringNclNo().execute(jsonData);
    }

    private class BringNclNo extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait.");
            progressDialog.setTitle("Ncl No Generating.");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().getUATUrl(getContext())+ "GenerateNclNo_v2");
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
            if (finalJson != null) {
                NclShipmentActivity nclShipmentActivity = (NclShipmentActivity) getActivity();
                nclShipmentActivity.destList.clear();
                nclShipmentActivity.secondFragment.clearList();
                NclNoResult noResult = new NclNoResult(finalJson);
                nclShipmentActivity.NclNo = noResult.NclNo;
                nclShipmentActivity.destList = noResult.DestinationList;
                nclShipmentActivity.IsMixed = checkMix.isChecked();
                iNclShipmentActivity.onNCLGenerated(noResult.NclNo , noResult.NCLDestStationID , noResult.AllowedDestStations);
                GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.ncl_GenerateNclNo) + " : " + noResult.NclNo, GlobalVar.AlertType.Info);

            } else
                GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.ncl_Notgenerate), GlobalVar.AlertType.Error);
        }
    }

    private void bindSpinner () {
        if (destFacilitySpinnerDialog != null) {
            destFacilitySpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    etDestFacility.setText(facilityList.get(position));
                    DestinationFacilityID = facilityIDList.get(position);
                }
            });

        }
    }

}
