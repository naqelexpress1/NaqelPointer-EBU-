package com.naqelexpress.naqelpointer.Activity.PickupAsrReg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingList;
import com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingModel;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialogReason;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;
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
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class PickUpFirstFragment
        extends Fragment implements DatePickerDialog.OnDateSetListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    View rootView;
    public TextView txtdescription;
    public static TextView txtWaybillNo, lbTotal;
    ArrayList<BookingModel> bookinglist;
    int position;
    boolean flag_thread = false;
    public DataAdapter adapter;
    static int al = 0;
    EditText txtBarCode, txtCollectedPiece;
    public ArrayList<String> PickUpBarCodeList = new ArrayList<>();
    private RecyclerView recyclerView;

    double Latitude = 0;
    double Longitude = 0;
    ArrayList<String> name;
    ArrayList<Integer> IDs;
    int class_;
    ArrayList<String> waybilllist;
    private GoogleMap mMap;
    Marker now;
    SupportMapFragment mapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final DBConnections dbConnections = new DBConnections(getContext(), null);
        if (rootView == null) {


            LayoutInflater lf = getActivity().getLayoutInflater();
            rootView = lf.inflate(R.layout.spasrregular_booking, container, false);

            txtCollectedPiece = (EditText) rootView.findViewById(R.id.txtPiecesCount);
            bookinglist = (ArrayList<BookingModel>) getArguments().get("value");
            position = (Integer) getArguments().get("position");
            name = getArguments().getStringArrayList("name");
            IDs = getArguments().getIntegerArrayList("IDs");
            class_ = (Integer) getArguments().get("class");

//            mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.map);

//            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

            mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            mapFragment.getMapAsync(this);
            mapFragment.getMapAsync(this);

            if (bookinglist.get(position).getisSPL()) {
                TableLayout tl = (TableLayout) rootView.findViewById(R.id.tlv);
                tl.setVisibility(View.GONE);
            }
            if (class_ == 0)
                waybilllist = getArguments().getStringArrayList("waybilllist");

            txtWaybillNo = (TextView) rootView.findViewById(R.id.txtWaybillNo);


            txtdescription = (TextView) rootView.findViewById(R.id.txtdescription);
            txtdescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txtdescription != null && txtdescription.getText().toString().length() > 0)
                        GlobalVar.ShowDialog(getActivity(), "", txtdescription.getText().toString(), true);
                }
            });
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);

            txtBarCode = (EditText) rootView.findViewById(R.id.txtBarcode);
            txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.BarcodeLength)});
            txtBarCode.setInputType(InputType.TYPE_NULL);
            txtBarCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
//                    if (txtBarCode != null && txtBarCode.getText().length() == 13
//                            || txtBarCode.getText().length() == GlobalVar.GV().ScanBarcodeLength)
                    if (txtBarCode.getText().length() >= 13)
                        AddNewPiece();
                }
            });

            Button exception = (Button) rootView.findViewById(R.id.exception);
            exception.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    showPopup();
                    isASRAddPieceManually();
                    if (PickUpBarCodeList.size() > 0)
                        Exception();
                    else
                        GlobalVar.GV().ShowSnackbar(rootView, "Please scan atleast one Piece Barcode", GlobalVar.AlertType.Error);
                }
            });
            SetText();

            LinearLayout ll_bottom = (LinearLayout) rootView.findViewById(R.id.llbtm);
            LinearLayout ll_1 = (LinearLayout) rootView.findViewById(R.id.ll1);
            LinearLayout ll_2 = (LinearLayout) rootView.findViewById(R.id.ll2);
            if (class_ == 0) {
                ll_bottom.setVisibility(View.INVISIBLE);
                ll_1.setVisibility(View.INVISIBLE);
                ll_2.setVisibility(View.INVISIBLE);
            }

        }

        Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GlobalVar.GV().checkPermission(getActivity(), GlobalVar.PermissionType.Camera)) {
                    GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                } else {
                    Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                    getActivity().startActivityForResult(intent, 1);
                }
            }
        });


        //FetchDistricData();
        if (dbConnections != null)
            dbConnections.close();

//        txtWaybillNo.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (txtWaybillNo != null && (txtWaybillNo.getText().toString().length() == 8 ||
//                        txtWaybillNo.getText().toString().length() == GlobalVar.ScanWaybillLength)
//                )
//                    setTxtWaybillNo(txtWaybillNo.getText().toString());
//            }
//        });

        return rootView;
    }

    private void isASRAddPieceManually() {
        if (class_ == 1) {
            PickUpBarCodeList.clear();
            PickUpBarCodeList.add(String.valueOf(bookinglist.get(position).getWaybillNo()) + "00001");
        }
    }

    private void AddNewPiece() {
        GlobalVar.hideKeyboardFrom(getContext(), rootView);

        if (txtBarCode.getText().toString().length() < 12) {
            GlobalVar.GV().ShowSnackbar(rootView, "Please scan correct Piece Barcode", GlobalVar.AlertType.Warning);
            return;
        }

        String txtBarcode = "";
        if (class_ == 0) {
            String str = txtBarCode.getText().toString();
            txtBarcode = str.replaceAll("[^0-9]", "");

            String chr = txtBarcode.substring(0, txtBarcode.length() - 5);
            if (!waybilllist.contains(chr)) {
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
                GlobalVar.GV().ShowSnackbar(rootView, "Scanned Piece Barcode Not Matching", GlobalVar.AlertType.Warning);
                return;
            }

            DBConnections dbConnections = new DBConnections(getContext(), null);
            Cursor result = dbConnections.Fill("select * from PickUpAuto where WaybillNo= '" + chr + "'", getContext());

            if (result.getCount() > 0) {
                GlobalVar.GV().ShowSnackbar(rootView, "you picked up this item, please scan corrent one ", GlobalVar.AlertType.Error);
                return;
            }
        } else {
            String str = txtBarCode.getText().toString();
            txtBarcode = str.replaceAll("[^0-9]", "");
            //txtBarcode = txtBarCode.getText().toString();

            if (!txtBarcode.contains(PickUpFirstFragment.txtWaybillNo.getText().toString())) {
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
                GlobalVar.GV().ShowSnackbar(rootView, "Scanned Piece Barcode Not Matching", GlobalVar.AlertType.Warning);
                return;
            }
        }

        if (!PickUpBarCodeList.contains(txtBarcode)) {
            if (txtBarcode.length() == 13
                    || txtBarcode.length() == GlobalVar.GV().ScanBarcodeLength) {
                PickUpBarCodeList.add(0, txtBarcode);
                lbTotal.setText(getString(R.string.lbCount) + PickUpBarCodeList.size());
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                txtBarCode.setText("");
                initViews();
            }
        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(PickUpBarCodeList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

//    private void setOriginDest() {
//
//        if (GlobalVar.GV().IsEnglish())
//            orgSpinnerDialog = new SpinnerDialog(getActivity(), StationNameList, "Select or Search Origin", R.style.DialogAnimations_SmileWindow);
//        else
//            orgSpinnerDialog = new SpinnerDialog(getActivity(), StationFNameList, "Select or Search Origin", R.style.DialogAnimations_SmileWindow);
//
//
//        if (GlobalVar.GV().IsEnglish())
//            destSpinnerDialog = new SpinnerDialog(getActivity(), StationNameList, "Select or Search Destination", R.style.DialogAnimations_SmileWindow);
//        else
//            destSpinnerDialog = new SpinnerDialog(getActivity(), StationFNameList, "Select or Search Destination", R.style.DialogAnimations_SmileWindow);
//
//    }

    private void SetText() {

        try {

            txtWaybillNo.setText(String.valueOf(bookinglist.get(position).WaybillNo));
            txtWaybillNo.setInputType(InputType.TYPE_NULL);
            if (bookinglist.get(position).GoodDesc.equals("null") || bookinglist.get(position).GoodDesc.equals(""))
                txtdescription.setText("");
            else
                txtdescription.setText(bookinglist.get(position).GoodDesc);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
//            if (data != null) {
//                Bundle extras = data.getExtras();
//                if (extras != null) {
//                    if (extras.containsKey("barcode")) {
//                        String barcode = extras.getString("barcode");
//                        if (barcode.length() > 8)
//                            barcode = barcode.substring(0, 8);
//                        txtWaybillNo.setText(barcode);
//                        GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
//                    }
//                }
//            }
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        // if (barcode.length() == 13 || barcode.length() == GlobalVar.GV().ScanBarcodeLength)
                        if (barcode.length() >= 13)
                            txtBarCode.setText(barcode);
                    }
                }
            }
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("txtWaybillNo", txtWaybillNo.getText().toString());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

            txtWaybillNo.setText(savedInstanceState.getString("txtWaybillNo"));

        }
    }

    private void onBackpressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exit PickUp")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        getActivity().finish();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    PopupWindow popup;
    SpinnerDialog spinnerDialog;
    SpinnerDialogReason spinnerDialogforSub;
    public int ReasonID = 0, subReasonId = 0;
    EditText txtNotes;


    private void showPopup() {


        final Activity context = getActivity();
        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.popup);

        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.notdeliveredfirstfragmentcbu, viewGroup);
        final PopupWindow popup = new PopupWindow(layout, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout rl = (RelativeLayout) layout.findViewById(R.id.popup);
        rl.setBackgroundColor(Color.parseColor("#908E8E"));
        final EditText txtWaybilll = (EditText) layout.findViewById(R.id.txtWaybilll);
        txtWaybilll.setVisibility(View.GONE);
        final Button btnOpenCamera = (Button) layout.findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setVisibility(View.GONE);
        RecyclerView shipmentBarCodes = (RecyclerView) layout.findViewById(R.id.shipmentBarCodes);
        shipmentBarCodes.setVisibility(View.GONE);

        final EditText txtReason = (EditText) layout.findViewById(R.id.txtReason);
        txtReason.setInputType(InputType.TYPE_NULL);
        //New Field
        final EditText txtsubReason = (EditText) layout.findViewById(R.id.txtsubReason);
        txtsubReason.setInputType(InputType.TYPE_NULL);

        txtNotes = (EditText) layout.findViewById(R.id.txtNotes);
        Button save = (Button) layout.findViewById(R.id.save);
        save.setVisibility(View.VISIBLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), "Test", Toast.LENGTH_SHORT).show();
            }
        });

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

        popup.setFocusable(true);
        popup.update();
        popup.setOutsideTouchable(false);
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
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

    private void opencalender() {

        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, 1);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                PickUpFirstFragment.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection

        );

        dpd.setMinDate(now);
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        txtNotes.setText(String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(dayOfMonth));
    }


    //        AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailActivity.this);
//        builder.setTitle("Pickup Exception");
//
//        final EditText notes = new EditText(BookingDetailActivity.this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        notes.setLayoutParams(lp);
//
//
//        CharSequence[] cs = name.toArray(new CharSequence[name.size()]);
//        //String[] animals = {"horse", "cow", "camel", "sheep", "goat"};
//        builder.setItems(cs, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(), notes.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.setView(notes);
//        dialog.show();
    private void Exception() {

        requestLocation();

        android.app.AlertDialog.Builder builderSingle = new android.app.AlertDialog.Builder(getActivity());
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Pickup Exception");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item);

        arrayAdapter.addAll(name);


        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                final int exceptionID = IDs.get(which);
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setTitle(strName);

                final EditText notes = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                notes.setLayoutParams(lp);


                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String Notes = "";
                        if (notes.getText().toString().length() > 0)
                            Notes = notes.getText().toString();
                        JSONObject jsonObject = new JSONObject();
                        try {


                            jsonObject.put("EmployID", GlobalVar.GV().EmployID);


                            jsonObject.put("Latitude", String.valueOf(Latitude));
                            jsonObject.put("Longitude", String.valueOf(Longitude));
                            jsonObject.put("Notes", Notes);
                            jsonObject.put("PickupExceptionID", exceptionID);
                            jsonObject.put("PSDID", bookinglist.get(position).getPickupsheetDetailID());
                            jsonObject.put("PSID", bookinglist.get(position).getPickupSheetID());
                            jsonObject.put("StationID", GlobalVar.GV().StationID);
                            jsonObject.put("TimeIn", DateTime.now());
                            jsonObject.put("UserID", GlobalVar.GV().UserID);
                            jsonObject.put("WaybillNo", bookinglist.get(position).getWaybillNo());


                            String jsonData = jsonObject.toString();

                            new SavePickupException().execute(jsonData);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                android.app.AlertDialog dialog1 = builder.create();
                dialog1.setView(notes);
                dialog1.show();
            }
        });
        builderSingle.show();
    }

    private void requestLocation() {
        Location location = GlobalVar.getLastKnownLocation(getContext());
        if (location != null) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();

            LatLng latLng = new LatLng(Latitude, Longitude);
            GlobalVar.GV().currentLocation = latLng;


        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            GlobalVar.GV().ChangeMapSettings(mMap, getActivity(), rootView);
            requestLocation();
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.currentlocation);
            now = mMap.addMarker(new MarkerOptions().position(GlobalVar.GV().currentLocation)
                    .icon(icon)
                    .title(getString(R.string.MyLocation)));

            mMap.getUiSettings().setMapToolbarEnabled(true);

            ShowShipmentMarker();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void ShowShipmentMarker() {
        if (bookinglist.get(position).getLat().length() > 3 && bookinglist.get(position).getLng().length() > 3
        ) {
            LatLng latLng = new LatLng(GlobalVar.GV().getDoubleFromString(bookinglist.get(position).getLat()),
                    GlobalVar.GV().getDoubleFromString(bookinglist.get(position).getLng()));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.deliverymarker);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(icon)
                    .title(String.valueOf(bookinglist.get(position).getWaybillNo())));
            mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) getActivity());
        }
//        else
//            mapFragment.getView().setVisibility(View.GONE);
    }

    private class SavePickupException extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setTitle("Loading");
            pd.setMessage("Your request is being process,kindly please wait ");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];


            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "InsertPickupException"); //LoadtoDestination
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
            if (finalJson != null) {
                try {
                    JSONObject jsonObject = new JSONObject(finalJson);
                    crreateAlert(jsonObject.getString("ErrorMessage"));
                    if (!jsonObject.getBoolean("HasError")) {
                        DBConnections dbConnections = new DBConnections(getContext(), null);

                        dbConnections.UpdatepickupsheetdetailsID(bookinglist.get(position).getWaybillNo(), 1);
                        bookinglist.get(position).setIsPickedup(1);
                        dbConnections.close();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onPostExecute(String.valueOf(finalJson));

            }

            pd.dismiss();
        }
    }


    private void crreateAlert(String msg) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle(msg);


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BookingList.isFinish = true;
                getActivity().finish();
            }
        });

        android.app.AlertDialog dialog1 = builder.create();
        dialog1.show();
    }

}