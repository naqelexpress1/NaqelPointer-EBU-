package com.naqelexpress.naqelpointer.Activity.NotDeliveredCBU;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.Activity.DeliveryOFD.DataAdapter;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialogReason;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class NotDeliveredFirstFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private View rootView;
    SpinnerDialog spinnerDialog;
    SpinnerDialogReason spinnerDialogforSub;
    EditText txtWaybillNo, txtReason, txtNotes, txtsubReason, txtpieces;
    public int ReasonID = 0, subReasonId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {

            rootView = inflater.inflate(R.layout.notdeliveredfirstfragmentcbu, container, false);


            txtWaybillNo = (EditText) rootView.findViewById(R.id.txtWaybilll);
            txtWaybillNo.setFilters(new InputFilter[] { new InputFilter.LengthFilter(GlobalVar.ScanWaybillLength) });
            txtpieces = (EditText) rootView.findViewById(R.id.pieces);
            txtReason = (EditText) rootView.findViewById(R.id.txtReason);
            txtReason.setInputType(InputType.TYPE_NULL);
            //New Field
            txtsubReason = (EditText) rootView.findViewById(R.id.txtsubReason);
            txtsubReason.setInputType(InputType.TYPE_NULL);

            txtNotes = (EditText) rootView.findViewById(R.id.txtNotes);

            txtReason.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        spinnerDialog.showSpinerDialog(false);
                    }
                }
            });

            txtsubReason.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (txtReason.getText().toString().contains("Future") ||
                                txtReason.getText().toString().contains("Customer Collection")) {

                            opencalender();
                        } else
                            spinnerDialogforSub.showSpinerDialog(false);
                    }
                }
            });

            if (savedInstanceState == null)
                GetDeliveryStatusList();

            if (GlobalVar.GV().IsArabic())
                spinnerDialog = new SpinnerDialog(getActivity(), DeliveryStatusFNameList, "Select or Search Reason", R.style.DialogAnimations_SmileWindow);
            else
                spinnerDialog = new SpinnerDialog(getActivity(), DeliveryStatusNameList, "Select or Search Reason", R.style.DialogAnimations_SmileWindow);

            spinnerDialogforSub = new SpinnerDialogReason(getActivity(), DeliveryStatussubReason, "Select or Search Sub Reason",
                    R.style.DialogAnimations_SmileWindow);


            spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {

                    if (GlobalVar.GV().IsArabic())
                        txtReason.setText(DeliveryStatusFNameList.get(position));
                    else

                        txtReason.setText(DeliveryStatusNameList.get(position));

                    txtsubReason.setText("");
                    txtNotes.setText("");
                    ReasonID = DeliveryStatusList.get(position);
                    GetDeliveryStatusReason(ReasonID);

                    if (DeliveryStatussubReason.size() == 0)
                        txtNotes.requestFocus();
                    else
                        spinnerDialogforSub.showSpinerDialog(false);


                    if (txtReason.getText().toString().contains("Future") ||
                            txtReason.getText().toString().contains("Customer Collection")) {
                        txtNotes.setKeyListener(null);
                        opencalender();
                        txtsubReason.setHint("Choose Date");
                        //spinnerDialogforSub.
                    } else
                        txtsubReason.setHint("No Delivered Sub Reason");


                }
            });


            spinnerDialogforSub.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {

                    txtsubReason.setText(DeliveryStatussubReason.get(position));

                    subReasonId = DeliveryStatussubList.get(position);

                    txtNotes.requestFocus();
                }
            });

            Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!GlobalVar.GV().checkPermission(getActivity(), GlobalVar.PermissionType.Camera)) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else {
                        Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                        startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                    }
                }
            });

            Bundle bundle = getArguments();

            if (bundle != null) {
                txtWaybillNo.setText(bundle.getString("WaybillNo"));
                txtWaybillNo.setEnabled(false);
                btnOpenCamera.setVisibility(View.GONE);
                ReadFromLocal();
            }


        }
        return rootView;
    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.shipmentBarCodes);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        DataAdapter adapter = new DataAdapter(ShipmentBarCodeList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void ReadFromLocal() {
        String wbno = txtWaybillNo.getText().toString();
        DBConnections dbConnections = new DBConnections(getContext(), null);
        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = '" + wbno + "'",
                getContext());

        if (result.getCount() > 0) {

            ReadFromLocal(result, dbConnections);
        }
    }

    public static ArrayList<String> ShipmentBarCodeList = new ArrayList<>();

    private void ReadFromLocal(Cursor result, DBConnections dbConnections) {

        ShipmentBarCodeList = new ArrayList<>();
        result.moveToFirst();
        boolean isdelivered = result.getInt(result.getColumnIndex("IsDelivered")) > 0;

        if (!isdelivered) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("ID", result.getString(result.getColumnIndex("ID")));
                jsonObject.put("WaybillNo", result.getString(result.getColumnIndex("ItemNo")));
                jsonObject.put("PiecesCount", result.getString(result.getColumnIndex("PiecesCount")));

                Cursor barcodecursor = dbConnections.Fill("select * from BarCode Where WayBillNo = '" +
                        result.getString(result.getColumnIndex("ItemNo"))
                        + "' and IsDelivered = 0", getContext());
                txtpieces.setText(String.valueOf(barcodecursor.getCount()));
                JSONArray jsonArray = new JSONArray();
                if (barcodecursor.getCount() > 0) {
                    barcodecursor.moveToFirst();
                    do {
                        ShipmentBarCodeList.add(barcodecursor.getString(barcodecursor.getColumnIndex("BarCode")));
//                        jsonArray.put(barcodecursor.getString(barcodecursor.getColumnIndex("BarCode")));
                    } while (barcodecursor.moveToNext());
                }

                jsonObject.put("BarCodeList", jsonArray);

                result.close();
                barcodecursor.close();
                dbConnections.close();


                initViews();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            GlobalVar.ShowDialog(getActivity(), "Info", "Already Delivered this Waybill", false);
        }

    }

    private void opencalender() {

        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, 1);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                NotDeliveredFirstFragment.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection

        );


        dpd.setMinDate(now);

        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    public ArrayList<String> DeliveryStatusNameList = new ArrayList<>();
    public ArrayList<String> DeliveryStatusFNameList = new ArrayList<>();
    public ArrayList<Integer> DeliveryStatusList = new ArrayList<>();
    public static ArrayList<String> DeliveryStatussubReason = new ArrayList<>();
    public ArrayList<Integer> DeliveryStatussubList = new ArrayList<>();

    public void GetDeliveryStatusList() {

        DeliveryStatusNameList.clear();
        DeliveryStatusFNameList.clear();
        DeliveryStatusList.clear();
        DeliveryStatussubReason.clear();
        DeliveryStatussubList.clear();

        DBConnections dbConnections = new DBConnections(getContext(), null);

        Cursor result = dbConnections.Fill("select * from DeliveryStatus order by SeqOrder", getContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Code = result.getString(result.getColumnIndex("Code"));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));

                DeliveryStatusList.add(ID);
                DeliveryStatusNameList.add(Name);
                DeliveryStatusFNameList.add(FName);
            }
            while (result.moveToNext());
        }

        dbConnections.close();


    }


    public void GetDeliveryStatusReason(int DSID) {

        DeliveryStatussubList.clear();
        DeliveryStatussubReason.clear();


        DBConnections dbConnections = new DBConnections(getContext(), null);

        Cursor result = dbConnections.Fill("select * from DeliveryStatusReason where DeliveyStatusID = " + DSID, getContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                DeliveryStatussubList.add(result.getInt(result.getColumnIndex("ReasonID")));
                DeliveryStatussubReason.add(result.getString(result.getColumnIndex("Name")));

            }
            while (result.moveToNext());
        }

        dbConnections.close();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        GlobalVar.GV().MakeSound(getContext(), R.raw.barcodescanned);
                        txtWaybillNo.setText(barcode);
                    }
                }
            }
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtWaybillNo", txtWaybillNo.getText().toString());
        outState.putString("txtNotes", txtNotes.getText().toString());
        outState.putString("txtReason", txtReason.getText().toString());
        outState.putStringArrayList("DeliveryStatusNameList", DeliveryStatusNameList);
        outState.putStringArrayList("DeliveryStatusFNameList", DeliveryStatusFNameList);
        outState.putIntegerArrayList("DeliveryStatusList", DeliveryStatusList);
        outState.putInt("ReasonID", ReasonID);
        outState.putInt("subReasonId", subReasonId);
        outState.putString("txtsubReason", txtsubReason.getText().toString());
        outState.putStringArrayList("DeliveryStatussubReason", DeliveryStatussubReason);
        outState.putIntegerArrayList("DeliveryStatussubList", DeliveryStatussubList);
        outState.putStringArrayList("ShipmentBarCodeList", ShipmentBarCodeList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtWaybillNo.setText(savedInstanceState.getString("txtWaybillNo"));
            txtNotes.setText(savedInstanceState.getString("txtNotes"));
            txtReason.setText(savedInstanceState.getString("txtReason"));
            DeliveryStatusNameList = savedInstanceState.getStringArrayList("DeliveryStatusNameList");
            DeliveryStatusFNameList = savedInstanceState.getStringArrayList("DeliveryStatusFNameList");
            DeliveryStatusList = savedInstanceState.getIntegerArrayList("DeliveryStatusList");
            ReasonID = savedInstanceState.getInt("ReasonID");
            subReasonId = savedInstanceState.getInt("subReasonId");
            DeliveryStatussubReason = savedInstanceState.getStringArrayList("DeliveryStatussubReason");
            DeliveryStatussubList = savedInstanceState.getIntegerArrayList("DeliveryStatussubList");
            ShipmentBarCodeList = savedInstanceState.getStringArrayList("ShipmentBarCodeList");
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        txtNotes.setText(String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(dayOfMonth));

    }
}