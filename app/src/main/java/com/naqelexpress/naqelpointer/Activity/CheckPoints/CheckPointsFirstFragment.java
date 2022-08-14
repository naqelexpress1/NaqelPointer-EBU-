package com.naqelexpress.naqelpointer.Activity.CheckPoints;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;

/**
 * Created by sofan on 21/03/2018.
 */

public class CheckPointsFirstFragment
        extends Fragment {
    View rootView;
    public EditText txtCheckPointType, txtCheckPointTypeDetail, txtCheckPointTypeDDetail, txtCheckPointType_TripID;
    SpinnerDialog checkPointTypeSpinnerDialog, checkPointTypeDetailSpinnerDialog, checkPointTypeDDetailSpinnerDialog;
    public int CheckPointTypeID = 0, CheckPointTypeDetailID = 0, CheckPointTypeDDetailID = 0;

    public ArrayList<Integer> CheckPointTypeList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeFNameList = new ArrayList<>();

    public ArrayList<Integer> CheckPointTypeDetailList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailFNameList = new ArrayList<>();

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

            // mohammed
            txtCheckPointType_TripID = (EditText) rootView.findViewById(R.id.txtCheckPointType_TripID);

            txtCheckPointType.setInputType(InputType.TYPE_NULL);
            txtCheckPointTypeDetail.setInputType(InputType.TYPE_NULL);
            txtCheckPointTypeDDetail.setInputType(InputType.TYPE_NULL);
            //mohammed
            txtCheckPointType_TripID.setInputType(InputType.TYPE_NULL);

            txtCheckPointTypeDetail.setVisibility(View.INVISIBLE);
            txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);

            //mohammed
            txtCheckPointType_TripID.setVisibility(View.INVISIBLE);

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
                    checkPointTypeDDetailSpinnerDialog.showSpinerDialog(false);
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

                    GetCheckPointTypeDDetailList();

                    txtCheckPointTypeDDetail.setText("");
                    CheckPointTypeDDetailID = 0;


                    if (CheckPointTypeDDetailList.size() > 0) {
                        txtCheckPointTypeDDetail.setVisibility(View.VISIBLE);
                    } else {
                        txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);
                        CheckPointTypeDDetailID = 0;
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

        // mohammed
        outState.putString("txtCheckPointType_TripID", txtCheckPointType.getText().toString());

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
}