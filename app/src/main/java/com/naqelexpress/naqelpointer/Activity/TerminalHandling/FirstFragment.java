package com.naqelexpress.naqelpointer.Activity.TerminalHandling;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Ismail on 21/03/2018.
 */

public class FirstFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    View rootView;
    public EditText txtCheckPointType, txtCheckPointTypeDetail, txtCheckPointTypeDDetail, txtweight, txtheight, txtlength, txtwidth;
    public  EditText txtCheckPointType_TripID;
    SpinnerDialog checkPointTypeSpinnerDialog, checkPointTypeDetailSpinnerDialog, checkPointTypeDDetailSpinnerDialog;
    public static int CheckPointTypeID = 0;
    public int CheckPointTypeDetailID = 0, CheckPointTypeDDetailID = 0;

    public ArrayList<Integer> CheckPointTypeList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeFNameList = new ArrayList<>();

    public ArrayList<Integer> CheckPointTypeDetailList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailFNameList = new ArrayList<>();
    public ArrayList<String> isCityID = new ArrayList<>();

    public ArrayList<Integer> CheckPointTypeDDetailList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDDetailNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDDetailFNameList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.checkpointsfirstfragment, container, false);


            txtCheckPointType = (EditText) rootView.findViewById(R.id.txtCheckPointType);
            txtCheckPointTypeDetail = (EditText) rootView.findViewById(R.id.txtCheckPointTypeDetail);
            txtCheckPointTypeDDetail = (EditText) rootView.findViewById(R.id.txtCheckPointTypeDDetail);

            //mohammed
            txtCheckPointType_TripID = (EditText) rootView.findViewById(R.id.txtCheckPointType_TripID);

            txtweight = (EditText) rootView.findViewById(R.id.txtweight);
            txtwidth = (EditText) rootView.findViewById(R.id.txtwidth);
//            txtweight.setVisibility(View.VISIBLE);
            txtheight = (EditText) rootView.findViewById(R.id.txtheight);
//            txtheight.setVisibility(View.VISIBLE);
            txtlength = (EditText) rootView.findViewById(R.id.txtlength);
//            txtlength.setVisibility(View.VISIBLE);

            txtCheckPointType.setInputType(InputType.TYPE_NULL);
            txtCheckPointTypeDetail.setInputType(InputType.TYPE_NULL);
            txtCheckPointTypeDDetail.setInputType(InputType.TYPE_NULL);

            //mohammed
            txtCheckPointType_TripID.setInputType(InputType.TYPE_NULL);

            txtCheckPointTypeDetail.setVisibility(View.INVISIBLE);
            txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);

            //mohammed
           txtCheckPointType_TripID.setVisibility(View.VISIBLE);

            txtCheckPointType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPointTypeSpinnerDialog.showSpinerDialog(false);
                }
            });

            txtCheckPointTypeDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPointTypeDetailSpinnerDialog.showSpinerDialog(false);
                }
            });

            txtCheckPointTypeDDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    checkPointTypeDDetailSpinnerDialog.showSpinerDialog(false);
                    if (CheckPointTypeID != 20) {
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


            FacilityStatus();

            checkPointTypeSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {

                    if (GlobalVar.GV().IsEnglish())
                        txtCheckPointType.setText(CheckPointTypeNameList.get(position));
                    else
                        txtCheckPointType.setText(CheckPointTypeFNameList.get(position));

                    CheckPointTypeID = CheckPointTypeList.get(position);
                    clearAll();

//                    if (CheckPointTypeID != 3)
                    GetCheckPointTypeDetailList();
//                    else


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
                    if (CheckPointTypeID == 18) {
                        txtweight.setVisibility(View.VISIBLE);
                        txtwidth.setVisibility(View.VISIBLE);
                        txtheight.setVisibility(View.VISIBLE);
                        txtlength.setVisibility(View.VISIBLE);
                    } else {
                        txtwidth.setVisibility(View.GONE);
                        txtweight.setVisibility(View.GONE);
                        txtheight.setVisibility(View.GONE);
                        txtlength.setVisibility(View.GONE);
                    }

                    if (CheckPointTypeID == 20) {
                        txtCheckPointTypeDetail.setVisibility(View.GONE);
                        txtCheckPointTypeDDetail.setVisibility(View.GONE);
                        txtCheckPointTypeDDetail.setText("");
                        txtCheckPointTypeDDetail.setVisibility(View.VISIBLE);
                        txtCheckPointTypeDDetail.requestFocus();
                        txtCheckPointTypeDDetail.setInputType(InputType.TYPE_CLASS_TEXT);
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

                    txtCheckPointTypeDetail.setText(CheckPointTypeDetailNameList.get(position));

                    CheckPointTypeDetailID = CheckPointTypeDetailList.get(position);


//                    GetCheckPointTypeDDetailList();
                    if (CheckPointTypeID != 20 && CheckPointTypeID != 6 && CheckPointTypeID != 7 && CheckPointTypeID != 8) {

//
                        txtCheckPointTypeDDetail.setText("");
                        CheckPointTypeDDetailID = 0;

//                    if (CheckPointTypeDDetailList.size() > 0) {
//                        txtCheckPointTypeDDetail.setVisibility(View.VISIBLE);
//                    } else {
//                        txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);
//                        CheckPointTypeDDetailID = 0;
//                    }

                        boolean isdate = false;
                        if (CheckPointTypeDetailFNameList.get(position).equals("1")) {
                            isdate = true;
                            CheckPointTypeDDetailID = 1;
                            opencalender();
                            txtCheckPointTypeDDetail.setVisibility(View.VISIBLE);
                        } else {
                            CheckPointTypeDDetailID = 0;
                            txtCheckPointTypeDDetail.setText("");
                            txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);
                        }


                        if (!isdate) {
                            if (isCityID.get(position).equals("1")) {
                                if (CheckPointTypeID != 3)
                                    operationcity();
                                else
                                    outletname();

                                CheckPointTypeDDetailID = 1;
                                txtCheckPointTypeDDetail.setVisibility(View.VISIBLE);
                            } else {
                                CheckPointTypeDDetailID = 0;
                                txtCheckPointTypeDDetail.setText("");
                                txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);

                            }

                        }
                    } else {

                        if (CheckPointTypeID != 6 && CheckPointTypeID != 7 && CheckPointTypeID != 8) {
                            txtCheckPointTypeDDetail.setText("");
                            txtCheckPointTypeDDetail.setVisibility(View.VISIBLE);
                            txtCheckPointTypeDDetail.requestFocus();
                            txtCheckPointTypeDDetail.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
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
                    // CheckPointTypeDDetailID = CheckPointTypeDDetailList.get(position);
                }
            });
        }

        return rootView;
    }

    private void clearAll() {
        CheckPointTypeDetailList.clear();// = new ArrayList<>();
        CheckPointTypeDetailNameList.clear();// = new ArrayList<>();
        CheckPointTypeDetailFNameList.clear();// = new ArrayList<>();
        isCityID.clear();
        CheckPointTypeDDetailList.clear();
        CheckPointTypeDDetailNameList.clear();
        CheckPointTypeDDetailFNameList.clear();

    }

    public void FacilityStatus() {

        CheckPointTypeDDetailList.clear();
        CheckPointTypeDDetailNameList.clear();
        CheckPointTypeDDetailFNameList.clear();

//
//        DBConnections dbConnections = new DBConnections(getContext(), null);
//
//        Cursor cursor = dbConnections.Fill("select * from UserME where EmployID = " + GlobalVar.GV().EmployID, getContext());
//        int usertype = 0;
//        if (cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            usertype = cursor.getInt(cursor.getColumnIndex("UserTypeID"));
//        }
//        Cursor result = dbConnections.Fill("select * from Facility where FacilityTypeID = " + usertype
//                + " and Station = " + GlobalVar.GV().StationID, getContext());

        DBConnections dbConnections = new DBConnections(getContext(), null);
        Cursor result = dbConnections.Fill("select * from Facility", getContext());

        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                HashMap<String, String> temp = new HashMap<>();

                CheckPointTypeDDetailList.add(result.getInt(result.getColumnIndex("FacilityID")));
                CheckPointTypeDDetailNameList.add(result.getString(result.getColumnIndex("Name")));
                CheckPointTypeDDetailFNameList.add(result.getString(result.getColumnIndex("Name")));

            }
            while (result.moveToNext());
        }


        dbConnections.close();

    }

    private void outletname() {
        CheckPointTypeDDetailList.clear();
        CheckPointTypeDDetailNameList.clear();
        CheckPointTypeDDetailFNameList.clear();

//        CheckPointTypeDDetailList.add(1);
        CheckPointTypeDDetailNameList.addAll(TerminalHandling.city);
        CheckPointTypeDDetailFNameList.addAll(TerminalHandling.city);


    }

    private void operationcity() {
        CheckPointTypeDDetailList.clear();
        CheckPointTypeDDetailNameList.clear();
        CheckPointTypeDDetailFNameList.clear();

//        CheckPointTypeDDetailList.add(1);
        CheckPointTypeDDetailNameList.addAll(TerminalHandling.operationalcity);
        CheckPointTypeDDetailFNameList.addAll(TerminalHandling.operationalcity);


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


        for (int i = 0; i < TerminalHandling.status.size(); i++) {
            int ID = Integer.parseInt(TerminalHandling.status.get(i).get("ID"));
            String Name = TerminalHandling.status.get(i).get("Name");
            String FName = TerminalHandling.status.get(i).get("Name");

            CheckPointTypeList.add(ID);
            CheckPointTypeNameList.add(Name);
            CheckPointTypeFNameList.add(FName);
        }
    }

    private void opencalender() {

        Calendar now = Calendar.getInstance();
        // now.add(Calendar.DAY_OF_MONTH, 1);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                FirstFragment.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection

        );

        dpd.setMinDate(now);

//        Calendar now = Calendar.getInstance();
//        DatePickerDialog dpd = DatePickerDialog.newInstance(
//                FirstFragment.this,
//                now.get(Calendar.YEAR), // Initial year selection
//                now.get(Calendar.MONTH), // Initial month selection
//                now.get(Calendar.DAY_OF_MONTH + 1) // Inital day selection
//
//        );

        dpd.setMinDate(now);

        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
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
        isCityID.clear();


        for (int i = 0; i < TerminalHandling.reason.size(); i++) {
            if (CheckPointTypeID == Integer.parseInt(TerminalHandling.reason.get(i).get("StatusID"))) {
                int ID = Integer.parseInt(TerminalHandling.reason.get(i).get("ID"));
                String Name = TerminalHandling.reason.get(i).get("Name");
                String FName = TerminalHandling.reason.get(i).get("IsDaterequired");
                String isCity = TerminalHandling.reason.get(i).get("IsCityrequired");

                CheckPointTypeDetailList.add(ID);
                CheckPointTypeDetailNameList.add(Name);
                CheckPointTypeDetailFNameList.add(FName);
                isCityID.add(isCity);

            }
        }

        if (FirstFragment.CheckPointTypeID == 6 || FirstFragment.CheckPointTypeID == 8 ||
                FirstFragment.CheckPointTypeID == 2) {
            ThirdFragment.txtBarCode.setHint("NCL Number");
        } else
            ThirdFragment.txtBarCode.setHint("Piece Number");

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtCheckPointType", txtCheckPointType.getText().toString());
        outState.putString("txtCheckPointTypeDetail", txtCheckPointTypeDetail.getText().toString());
        outState.putString("txtCheckPointTypeDDetail", txtCheckPointTypeDDetail.getText().toString());

        //mohammed
        outState.putString("txtCheckPointType_TripID", txtCheckPointType_TripID.getText().toString());

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

            //mohammed
            txtCheckPointType_TripID.setText(savedInstanceState.getString("txtCheckPointType_TripID"));


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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        txtCheckPointTypeDDetail.setText(String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(dayOfMonth));

    }
}