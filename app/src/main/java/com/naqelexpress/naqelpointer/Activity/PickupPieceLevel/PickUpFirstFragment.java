package com.naqelexpress.naqelpointer.Activity.PickupPieceLevel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.naqelexpress.naqelpointer.Activity.Booking.Booking;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.BringClientData;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class PickUpFirstFragment
        extends Fragment {
    View rootView;
    SpinnerDialog orgSpinnerDialog, destSpinnerDialog;
    static EditText txtOrigin, txtDestination;
    static public EditText txtWaybillNo, txtPiecesCount, txtWeight, txtRefNo, txtClientID;
    static public int OriginID = 0, DestinationID = 0;
    ArrayList<Booking> bookinglist;
    int position;
    boolean flag_thread = false;
    static Spinner Loadtype;
    static PickupAdapter adapter;
    public static ArrayList<HashMap<String, String>> clientdetails;
    static int al = 0;
    String division = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (rootView == null) {
            rootView = inflater.inflate(R.layout.pickupwaybillinfo, container, false);

            CheckBox actualLocation = (CheckBox) rootView.findViewById(R.id.alocation);
            txtOrigin = (EditText) rootView.findViewById(R.id.txtOrigin);
            txtOrigin.setInputType(InputType.TYPE_NULL);
            OriginID = GlobalVar.GV().StationID;

            txtDestination = (EditText) rootView.findViewById(R.id.txtDestination);
            txtDestination.setInputType(InputType.TYPE_NULL);
            txtPiecesCount = (EditText) rootView.findViewById(R.id.txtPiecesCount);

            txtWaybillNo = (EditText) rootView.findViewById(R.id.txtWaybilll);
            txtWaybillNo.addTextChangedListener(textWatcher);
            txtWaybillNo.setKeyListener(null);
            txtWaybillNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanWaybillLength)});
            txtClientID = (EditText) rootView.findViewById(R.id.txtClientID);
            txtPiecesCount = (EditText) rootView.findViewById(R.id.txtPiecesCount);
            txtWeight = (EditText) rootView.findViewById(R.id.txtWeight);
            txtRefNo = (EditText) rootView.findViewById(R.id.txtRefNo);

            Loadtype = (Spinner) rootView.findViewById(R.id.loadtype);

            clientdetails = new ArrayList<>();


//            txtWaybillNo.setOnKeyListener(new View.OnKeyListener() {
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    // If the event is a key-down event on the "enter" button
//                    if (event.getAction() != KeyEvent.ACTION_DOWN)
//                        return true;
//                    else if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        onBackPressed();
//                        return true;
//                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                        if (txtWaybillNo.getText().length() == 8 ||
//                                txtWaybillNo.getText().length() == GlobalVar.ScanWaybillLength) {
//                            setTxtWaybillNo(txtWaybillNo.getText().toString());
//                        } else
//                            txtWaybillNo.setText("");
//
//                        return true;
//                    }
//                    return false;
//                }
//            });


            adapter = new PickupAdapter(clientdetails, getContext());
            //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Loadtype.setAdapter(adapter);


            actualLocation.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //is chkIos checked?
                    if (((CheckBox) v).isChecked()) {
                        al = 1;
                    } else
                        al = 0;

                }
            });

            String division = GlobalVar.getDivision(getContext());
            if (division.equals("Express"))
                actualLocation.setVisibility(View.GONE);

            if (savedInstanceState == null) {
                GetStationList();

            }
            setOriginDest();


            txtOrigin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        orgSpinnerDialog.showSpinerDialog(false);
                }
            });


            orgSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    if (GlobalVar.GV().IsEnglish())
                        txtOrigin.setText(StationNameList.get(position));
                    else
                        txtOrigin.setText(StationFNameList.get(position));
                    txtPiecesCount.requestFocus();
                    OriginID = StationList.get(position);
                }
            });

            txtDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        destSpinnerDialog.showSpinerDialog(false);
                }
            });

            destSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    if (GlobalVar.GV().IsEnglish())
                        txtDestination.setText(StationNameList.get(position));
                    else
                        txtDestination.setText(StationFNameList.get(position));
                    txtPiecesCount.requestFocus();
                    DestinationID = StationList.get(position);
                }
            });


        }
        String class_ = (String) getArguments().get("class");
        if (class_.equals("BookingDetailAcyivity")) {
            bookinglist = (ArrayList<Booking>) getArguments().get("value");
            position = (Integer) getArguments().get("position");
            SetText();
        }

        DBConnections dbConnections = new DBConnections(getContext(), null);
        Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " +
                GlobalVar.GV().EmployID, getContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            division = result.getString(result.getColumnIndex("Division"));
        }
        if (division.equals("Express")) {
            txtOrigin.setFocusable(false);
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

        final ImageView clientID = (ImageView) rootView.findViewById(R.id.searchclient);
        clientID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtClientID.getText().toString().length() > 0) {
                    BringClientData bringClientData = new BringClientData();
                    bringClientData.ClientID = Integer.parseInt(txtClientID.getText().toString());
                    String jsonData = JsonSerializerDeserializer.serialize(bringClientData, true);
                    GetClientID(jsonData);
                } else
                    GlobalVar.GV().ShowSnackbar(rootView, "Kindly enter the clientID", GlobalVar.AlertType.Error);
            }
        });

        final ImageView nonclient = (ImageView) rootView.findViewById(R.id.nonclient);
        nonclient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientdetails.clear();
                txtClientID.setText("0");
                HashMap<String, String> temp = new HashMap<>();
                temp.put("LoadTypeID", "1");
                temp.put("Name", "Express");
                clientdetails.add(temp);
                adapter.notifyDataSetChanged();
            }
        });

        ImageView close = (ImageView) rootView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtWeight.setText("0");
            }
        });

        ImageView envelop = (ImageView) rootView.findViewById(R.id.envelope);
        envelop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtWeight.setText("0.5");
            }
        });

        return rootView;
    }

    private void setTxtWaybillNo() {

        String barcode = txtWaybillNo.getText().toString();
        txtWaybillNo.removeTextChangedListener(textWatcher);
        if (barcode.length() >= 8 && GlobalVar.WaybillNoStartSeries.contains(barcode.substring(0, 1))) {
            txtWaybillNo.setText(barcode.substring(0, 8));
            //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));

        } else if (barcode.length() >= GlobalVar.ScanWaybillLength) {
            txtWaybillNo.setText(barcode.substring(0, GlobalVar.ScanWaybillLength));
            //txtBarCode.setText(barcode.substring(0, GlobalVar.ScanWaybillLength));
            //ValidateWayBill(txtBarCode.getText().toString().substring(0, GlobalVar.ScanWaybillLength));
        }
    }

    private void setTxtWaybillNo(String barcode) {
        txtWaybillNo.removeTextChangedListener(textWatcher);
        if (barcode.length() > 8 && GlobalVar.WaybillNoStartSeries.contains(barcode.substring(0, 1))) {
            txtWaybillNo.setText(barcode.substring(0, 8));
        } else
            txtWaybillNo.setText(barcode);
        GlobalVar.GV().MakeSound(getContext(), R.raw.barcodescanned);
    }

    private void setOriginDest() {

        if (GlobalVar.GV().IsEnglish())
            orgSpinnerDialog = new SpinnerDialog(getActivity(), StationNameList, "Select or Search Origin", R.style.DialogAnimations_SmileWindow);
        else
            orgSpinnerDialog = new SpinnerDialog(getActivity(), StationFNameList, "Select or Search Origin", R.style.DialogAnimations_SmileWindow);


        if (GlobalVar.GV().IsEnglish())
            destSpinnerDialog = new SpinnerDialog(getActivity(), StationNameList, "Select or Search Destination", R.style.DialogAnimations_SmileWindow);
        else
            destSpinnerDialog = new SpinnerDialog(getActivity(), StationFNameList, "Select or Search Destination", R.style.DialogAnimations_SmileWindow);

    }

    private void SetText() {

        txtOrigin.setText(bookinglist.get(position).Orgin);
        OriginID = bookinglist.get(position).OriginId;
        txtDestination.setText(bookinglist.get(position).Destination);
        txtDestination.setInputType(InputType.TYPE_NULL);
        txtPiecesCount.setText(String.valueOf(bookinglist.get(position).PicesCount));
        txtClientID.setText(String.valueOf(bookinglist.get(position).ClientID));
        txtWeight.setText(String.valueOf(bookinglist.get(position).Weight));
        txtRefNo.setText(bookinglist.get(position).RefNo);
        DestinationID = bookinglist.get(position).DestinationId;
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
                        if (barcode.length() == 8 || barcode.length() == GlobalVar.ScanWaybillLength) {

                            GlobalVar.GV().MakeSound(getContext(), R.raw.barcodescanned);
                            setTxtWaybillNo(barcode);
                            //txtWaybillNo.setText(barcode);
                        }
                    }
                }
            }
        }
    }


    static public ArrayList<Integer> StationList = new ArrayList<>();
    static public ArrayList<String> StationNameList = new ArrayList<>();
    public ArrayList<String> StationFNameList = new ArrayList<>();

    public void GetStationList() {
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
        txtOrigin.setText(GlobalVar.GV().GetStationByID(GlobalVar.GV().StationID, StationNameList, StationList));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("txtWaybillNo", txtWaybillNo.getText().toString());
        outState.putString("txtOrigin", txtOrigin.getText().toString());
        outState.putInt("OriginID", OriginID);
        outState.putString("txtDestination", txtDestination.getText().toString());
        outState.putString("txtPiecesCount", txtPiecesCount.getText().toString());
        outState.putString("txtClientID", txtClientID.getText().toString());
        outState.putString("txtWeight", txtWeight.getText().toString());
        outState.putString("txtRefNo", txtRefNo.getText().toString());
        outState.putInt("DestinationID", DestinationID);
        outState.putIntegerArrayList("StationList", StationList);
        outState.putStringArrayList("StationNameList", StationNameList);
        outState.putStringArrayList("StationFNameList", StationFNameList);
        outState.putSerializable("clientdetails", clientdetails);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

            txtWaybillNo.setText(savedInstanceState.getString("txtWaybillNo"));
            txtOrigin.setText(savedInstanceState.getString("txtOrigin"));
            OriginID = savedInstanceState.getInt("OriginID");
            txtDestination.setText(savedInstanceState.getString("txtDestination"));
            txtPiecesCount.setText(savedInstanceState.getString("txtPiecesCount"));
            txtClientID.setText(savedInstanceState.getString("txtClientID"));
            txtWeight.setText(savedInstanceState.getString("txtWeight"));
            txtRefNo.setText(savedInstanceState.getString("txtRefNo"));
            DestinationID = savedInstanceState.getInt("DestinationID");
            StationList = savedInstanceState.getIntegerArrayList("StationList");
            StationNameList = savedInstanceState.getStringArrayList("StationNameList");
            StationFNameList = savedInstanceState.getStringArrayList("StationFNameList");
            clientdetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("clientdetails");
        }
    }

    public void GetClientID(final String input) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Please wait.", "Downloading Client Details.", true);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL = GlobalVar.GV().NaqelPointerAPILink + "BringLoadType";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                    if (!HasError) {
                        JSONArray jsonArray = response.getJSONArray("Client");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, String> temp = new HashMap<>();
                            temp.put("LoadTypeID", String.valueOf(jsonObject.get("LoadTypeID")));
                            temp.put("Name", String.valueOf(jsonObject.get("Name")));
                            clientdetails.add(temp);
                        }

                        adapter.notifyDataSetChanged();
                        flag_thread = false;
                        progressDialog.dismiss();

                    } else {

                        flag_thread = false;
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    flag_thread = false;
                    progressDialog.dismiss();

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                flag_thread = false;
                GlobalVar.GV().ShowSnackbar(rootView, error.toString(), GlobalVar.AlertType.Error);
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

    public void onBackPressed() {
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

    protected TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // your logic here
            if (txtWaybillNo != null && txtWaybillNo.getText().length() >= 8)
                //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
                setTxtWaybillNo();

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // your logic here
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // your logic here
        }
    };
}