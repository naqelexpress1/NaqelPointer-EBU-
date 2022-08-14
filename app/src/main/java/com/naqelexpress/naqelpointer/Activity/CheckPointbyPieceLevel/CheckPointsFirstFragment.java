package com.naqelexpress.naqelpointer.Activity.CheckPointbyPieceLevel;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserME;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sofan on 21/03/2018.
 */

public class CheckPointsFirstFragment
        extends Fragment {
    View rootView;
    public EditText txtCheckPointType, txtCheckPointTypeDDetail;
    public static EditText txtCheckPointTypeDetail;
    SpinnerDialog checkPointTypeSpinnerDialog, checkPointTypeDetailSpinnerDialog, checkPointTypeDDetailSpinnerDialog;
    private LinearLayout llTripID;
    public int CheckPointTypeID = 0, CheckPointTypeDDetailID = 0;
    public static int CheckPointTypeDetailID = 0;
    public ArrayList<Integer> CheckPointTypeList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeFNameList = new ArrayList<>();

    public ArrayList<Integer> CheckPointTypeDetailList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailFNameList = new ArrayList<>();

    public ArrayList<Integer> CheckPointTypeDDetailList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDDetailNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDDetailFNameList = new ArrayList<>();

    public ArrayList<String> onHoldShipments = new ArrayList<>();
    private ICheckPoint iCheckPoint;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            iCheckPoint = (ICheckPoint) context;
        } catch (ClassCastException e) {
            Log.d("test" , "test"  + e.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.checkpointsfirstfragment, container, false);
            txtCheckPointType = (EditText) rootView.findViewById(R.id.txtCheckPointType);
            txtCheckPointTypeDetail = (EditText) rootView.findViewById(R.id.txtCheckPointTypeDetail);
            txtCheckPointTypeDDetail = (EditText) rootView.findViewById(R.id.txtCheckPointTypeDDetail);

            llTripID = rootView.findViewById(R.id.tripID_Linear);
            txtCheckPointType.setInputType(InputType.TYPE_NULL);
            txtCheckPointTypeDetail.setInputType(InputType.TYPE_NULL);
            txtCheckPointTypeDDetail.setInputType(InputType.TYPE_NULL);

            txtCheckPointTypeDetail.setVisibility(View.INVISIBLE);
            txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);

            txtCheckPointType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPointTypeSpinnerDialog.showSpinerDialog(false);
                }
            });

            CheckPointTypeDetailID = 0;
            txtCheckPointTypeDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckPointTypeID != 9 && CheckPointTypeID != 19) {
                        txtCheckPointTypeDetail.setInputType(InputType.TYPE_NULL);
                        checkPointTypeDetailSpinnerDialog.showSpinerDialog(false);
                    } else {
                        if (CheckPointTypeID == 9) {
                            txtCheckPointTypeDetail.requestFocus();
                            txtCheckPointTypeDetail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
                            txtCheckPointTypeDetail.setInputType(InputType.TYPE_CLASS_NUMBER);
                        } else if (CheckPointTypeID == 9) {
                            txtCheckPointTypeDetail.requestFocus();
                            txtCheckPointTypeDetail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                            txtCheckPointTypeDetail.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                    }
                }
            });

            txtCheckPointTypeDetail.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {

                    if (CheckPointTypeID == 9) {
                        if (s.length() == 3) {
                            txtCheckPointTypeDetail.setText(s.toString() + "-");
                            txtCheckPointTypeDetail.setSelection(txtCheckPointTypeDetail.getText().length());
                        } else if (s.length() == 8) {
                            txtCheckPointTypeDetail.setText(s.toString() + " ");
//                            txtCheckPointTypeDetail.setLineSpacing(0,1.5f);
                            txtCheckPointTypeDetail.setSelection(txtCheckPointTypeDetail.getText().length());
                        }

                    }

                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                }
            });

            txtCheckPointTypeDDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (CheckPointTypeID != 7) {
                        txtCheckPointTypeDDetail.setInputType(InputType.TYPE_NULL);
                        checkPointTypeDDetailSpinnerDialog.showSpinerDialog(false);
                    } else {
                        txtCheckPointTypeDDetail.requestFocus();
                        txtCheckPointTypeDDetail.setInputType(InputType.TYPE_CLASS_TEXT);
                    }


                }
            });


            if (savedInstanceState == null)
                new GetCheckPointData().execute("");

            if (savedInstanceState != null)
                setSpinnerDatas(savedInstanceState);


            if (GlobalVar.GV().IsEnglish())
                checkPointTypeSpinnerDialog = new SpinnerDialog(getActivity(), CheckPointTypeNameList,
                        "Select Check Point Type", R.style.DialogAnimations_SmileWindow);
            else
                checkPointTypeSpinnerDialog = new SpinnerDialog(getActivity(), CheckPointTypeFNameList,
                        "Select Check Point Type", R.style.DialogAnimations_SmileWindow);


            checkPointTypeSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {

                    if (GlobalVar.GV().IsEnglish())
                        txtCheckPointType.setText(CheckPointTypeNameList.get(position));
                    else
                        txtCheckPointType.setText(CheckPointTypeFNameList.get(position));

                    CheckPointTypeID = CheckPointTypeList.get(position);
                    txtCheckPointTypeDetail.setText("");
                    txtCheckPointTypeDDetail.setText("");

                    txtCheckPointTypeDetail.setVisibility(View.GONE);
                    txtCheckPointTypeDetail.setInputType(InputType.TYPE_NULL);

                    txtCheckPointTypeDDetail.setInputType(InputType.TYPE_NULL);
                    txtCheckPointTypeDDetail.setVisibility(View.GONE);

                    CheckPointTypeDetailID = 0;
                    CheckPointTypeDDetailID = 0;

                    if (CheckPointTypeID != 9 && CheckPointTypeID != 19) {
                        GetCheckPointTypeDetailList();

                        txtCheckPointTypeDetail.setText("");
                        txtCheckPointTypeDDetail.setText("");

                        CheckPointTypeDetailID = 0;
                        CheckPointTypeDDetailID = 0;

                        if (CheckPointTypeDetailList.size() > 0) {
                            txtCheckPointTypeDetail.setVisibility(View.VISIBLE);
                        } else {
                            txtCheckPointTypeDetail.setVisibility(View.INVISIBLE);
                            txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);
                            CheckPointTypeDetailID = 0;
                            CheckPointTypeDDetailID = 0;
                        }
                    } else {
                        txtCheckPointTypeDetail.setText("");
                        txtCheckPointTypeDetail.setVisibility(View.VISIBLE);
                        txtCheckPointTypeDetail.requestFocus();
                        txtCheckPointTypeDetail.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                    //Riyam - If check point is Arrival get HV shipment to hold it if Bayan not available
                    if (CheckPointTypeID == 1) {
                        getOnHoldShipments();
                    }
                }
            });

            if (GlobalVar.GV().IsEnglish())
                checkPointTypeDetailSpinnerDialog = new SpinnerDialog(getActivity(), CheckPointTypeDetailNameList,
                        "Select Reason", R.style.DialogAnimations_SmileWindow);
            else
                checkPointTypeDetailSpinnerDialog = new SpinnerDialog(getActivity(), CheckPointTypeDetailFNameList,
                        "Select Reason", R.style.DialogAnimations_SmileWindow);

            checkPointTypeDetailSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    if (GlobalVar.GV().IsEnglish())
                        txtCheckPointTypeDetail.setText(CheckPointTypeDetailNameList.get(position));
                    else
                        txtCheckPointTypeDetail.setText(CheckPointTypeDetailFNameList.get(position));
                    CheckPointTypeDetailID = CheckPointTypeDetailList.get(position);

                    if (CheckPointTypeID != 7) {
                        GetCheckPointTypeDDetailList();

                        txtCheckPointTypeDDetail.setText("");
                        CheckPointTypeDDetailID = 0;


                        if (CheckPointTypeDDetailList.size() > 0) {
                            txtCheckPointTypeDDetail.setVisibility(View.VISIBLE);
                        } else {
                            txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);
                            CheckPointTypeDDetailID = 0;
                        }
                    } else {
                        txtCheckPointTypeDDetail.setText("");
                        txtCheckPointTypeDDetail.setVisibility(View.VISIBLE);
                        txtCheckPointTypeDDetail.requestFocus();
                        txtCheckPointTypeDDetail.setInputType(InputType.TYPE_CLASS_TEXT);
                    }


                }
            });

            if (GlobalVar.GV().IsEnglish())
                checkPointTypeDDetailSpinnerDialog = new SpinnerDialog(getActivity(), CheckPointTypeDDetailNameList,
                        "Select Reason", R.style.DialogAnimations_SmileWindow);
            else
                checkPointTypeDDetailSpinnerDialog = new SpinnerDialog(getActivity(),
                        CheckPointTypeDDetailFNameList, "Select Reason", R.style.DialogAnimations_SmileWindow);

            checkPointTypeDDetailSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    if (GlobalVar.GV().IsEnglish())
                        txtCheckPointTypeDDetail.setText(CheckPointTypeDDetailNameList.get(position));
                    else
                        txtCheckPointTypeDDetail.setText(CheckPointTypeDDetailFNameList.get(position));
                    CheckPointTypeDDetailID = CheckPointTypeDDetailList.get(position);
                }
            });

            //Riyam - Hide trip ID for GTW
             try {
                 if (GlobalVar.getDivisionID(getContext() , GlobalVar.GV().EmployID).equals("IRS")) {
                     llTripID.setVisibility(View.GONE);
                 }
             } catch (Exception e) {}

        }

        return rootView;
    }

    private void setSpinnerDatas(Bundle savedInstanceState) {
        CheckPointTypeID = savedInstanceState.getInt("CheckPointTypeID");
        CheckPointTypeList = savedInstanceState.getIntegerArrayList("CheckPointTypeList");
        CheckPointTypeNameList = savedInstanceState.getStringArrayList("CheckPointTypeNameList");
        CheckPointTypeFNameList = savedInstanceState.getStringArrayList("CheckPointTypeFNameList");

        CheckPointTypeDetailID = savedInstanceState.getInt("CheckPointTypeDetailID");
        CheckPointTypeDetailList = savedInstanceState.getIntegerArrayList("CheckPointTypeDetailList");
        CheckPointTypeDetailNameList = savedInstanceState.getStringArrayList("CheckPointTypeDetailNameList");
        CheckPointTypeDetailFNameList = savedInstanceState.getStringArrayList("CheckPointTypeDetailFNameList");


        CheckPointTypeDDetailID = savedInstanceState.getInt("CheckPointTypeDDetailID");
        CheckPointTypeDDetailList = savedInstanceState.getIntegerArrayList("CheckPointTypeDDetailList");
        CheckPointTypeDDetailNameList = savedInstanceState.getStringArrayList("CheckPointTypeDDetailNameList");
        CheckPointTypeDDetailFNameList = savedInstanceState.getStringArrayList("CheckPointTypeDDetailFNameList");
    }

    private class GetCheckPointData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        //  ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Please wait.", "Downloading Checkpoints Data"
                    , true);
        }

        @Override
        protected String doInBackground(String... params) {
            GetCheckPointTypeList();
            // GetCheckPointTypeDDetailList();
            // GetCheckPointTypeDetailList();

            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            if (finalJson != null) {

            }

            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }


    public void GetCheckPointTypeList() {
        CheckPointTypeList.clear();// = new ArrayList<>();
        CheckPointTypeNameList.clear();// = new ArrayList<>();
        CheckPointTypeFNameList.clear();// = new ArrayList<>();


        //For High Value Alarm

        DBConnections dbConnections = new DBConnections(getContext(), null);

        Cursor result = dbConnections.Fill("select * from CheckPointType", getContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));


                CheckPointTypeList.add(ID);
                CheckPointTypeNameList.add(Name);
                CheckPointTypeFNameList.add(FName);
            }
            while (result.moveToNext());

        }
        dbConnections.close();

    }


    public void GetCheckPointTypeDDetailList() {
        CheckPointTypeDDetailList.clear();// = new ArrayList<>();
        CheckPointTypeDDetailNameList.clear();// = new ArrayList<>();
        CheckPointTypeDDetailFNameList.clear();// = new ArrayList<>();

        String selectCommand = "select * from CheckPointTypeDDetail";
        if (CheckPointTypeDetailID > 0)
            selectCommand += " where CheckPointTypeDetailID=" + CheckPointTypeDetailID;

        DBConnections dbConnections = new DBConnections(getContext(), null);

        Cursor result = dbConnections.Fill(selectCommand, getContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));

                CheckPointTypeDDetailList.add(ID);
                CheckPointTypeDDetailNameList.add(Name);
                CheckPointTypeDDetailFNameList.add(FName);
            }
            while (result.moveToNext());

        }
        dbConnections.close();


    }


    public void GetCheckPointTypeDetailList() {
        CheckPointTypeDetailList.clear();// = new ArrayList<>();
        CheckPointTypeDetailNameList.clear();// = new ArrayList<>();
        CheckPointTypeDetailFNameList.clear();// = new ArrayList<>();

        String selectCommand = "select * from CheckPointTypeDetail";
        if (CheckPointTypeID > 0)
            selectCommand += " where CheckPointTypeID=" + CheckPointTypeID;

        DBConnections dbConnections = new DBConnections(getContext(), null);

        Cursor result = dbConnections.Fill(selectCommand, getContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));
                int CheckPointTypeID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeID")));

                CheckPointTypeDetailList.add(ID);
                CheckPointTypeDetailNameList.add(Name);
                CheckPointTypeDetailFNameList.add(FName);
            }
            while (result.moveToNext());

        }

        dbConnections.close();

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtCheckPointType", txtCheckPointType.getText().toString());
        outState.putString("txtCheckPointTypeDetail", txtCheckPointTypeDetail.getText().toString());
        outState.putString("txtCheckPointTypeDDetail", txtCheckPointTypeDDetail.getText().toString());
        outState.putInt("CheckPointTypeID", CheckPointTypeID);
        outState.putIntegerArrayList("CheckPointTypeList", CheckPointTypeList);
        outState.putStringArrayList("CheckPointTypeNameList", CheckPointTypeNameList);
        outState.putStringArrayList("CheckPointTypeFNameList", CheckPointTypeFNameList);

        outState.putInt("CheckPointTypeDetailID", CheckPointTypeDetailID);
        outState.putIntegerArrayList("CheckPointTypeDetailList", CheckPointTypeDetailList);
        outState.putStringArrayList("CheckPointTypeDetailNameList", CheckPointTypeDetailNameList);
        outState.putStringArrayList("CheckPointTypeDetailFNameList", CheckPointTypeDetailFNameList);

        outState.putInt("CheckPointTypeDDetailID", CheckPointTypeDDetailID);
        outState.putIntegerArrayList("CheckPointTypeDDetailList", CheckPointTypeDDetailList);
        outState.putStringArrayList("CheckPointTypeDDetailNameList", CheckPointTypeDDetailNameList);
        outState.putStringArrayList("CheckPointTypeDDetailFNameList", CheckPointTypeDDetailFNameList);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtCheckPointType.setText(savedInstanceState.getString("txtCheckPointType"));
            txtCheckPointTypeDetail.setText(savedInstanceState.getString("txtCheckPointTypeDetail"));
            txtCheckPointTypeDDetail.setText(savedInstanceState.getString("txtCheckPointTypeDDetail"));


            if (CheckPointTypeDetailList.size() > 0) {
                txtCheckPointTypeDetail.setVisibility(View.VISIBLE);
            } else {
                txtCheckPointTypeDetail.setVisibility(View.INVISIBLE);
                txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);
                CheckPointTypeDetailID = 0;
                CheckPointTypeDDetailID = 0;
            }

            if (CheckPointTypeDDetailList.size() > 0) {
                txtCheckPointTypeDDetail.setVisibility(View.VISIBLE);
            } else {
                txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);
                CheckPointTypeDDetailID = 0;
            }


        }
    }

    /*** Riyam - Getting on Hold shipments ***/
    private void getOnHoldShipments  () {
        if (onHoldShipments == null || onHoldShipments.size() == 0) {
            UserME nclNoReq = new UserME();
            nclNoReq.EmployID = GlobalVar.GV().EmployID;
            String jsonData = JsonSerializerDeserializer.serialize(nclNoReq, true);
            new OnHoldWaybills().execute(jsonData);
        }
    }

    private class OnHoldWaybills extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait.");
            progressDialog.setTitle("Collecting OnHold Waybills.");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetonHoldShipments");
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

                JSONObject jsonObject = null;
                try {
                    GlobalVar.GV().ShowSnackbar(rootView, "On hold shipments uploaded", GlobalVar.AlertType.Info);
                    jsonObject = new JSONObject(finalJson);
                    fetchOnHoldShipments(jsonObject.getJSONArray("HoldWaybills"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else
                GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.ncl_Notgenerate), GlobalVar.AlertType.Error);
        }
    }
    private void fetchOnHoldShipments(JSONArray waybills) {

        for (int i = 0; i < waybills.length(); i++) {
            try {
                JSONObject jsonObject1 = waybills.getJSONObject(i);
                onHoldShipments.add(jsonObject1.getString("BarCode"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        iCheckPoint.onHoldShipments(onHoldShipments);
    }
    /*** Riyam - End  ***/
}