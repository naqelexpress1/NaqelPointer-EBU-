package com.naqelexpress.naqelpointer.Activity.PickUp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.naqelexpress.naqelpointer.Models.DistrictDataModel;
import com.naqelexpress.naqelpointer.Models.Enum.Enum;
import com.naqelexpress.naqelpointer.Models.Request.CommonRequest;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.callback.AlertCallback;
import com.naqelexpress.naqelpointer.callback.Callback;
import com.naqelexpress.naqelpointer.utils.PickupApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;

public class PickUpFirstFragment
        extends Fragment implements AlertCallback, AdapterView.OnItemSelectedListener {
    View rootView;
    SpinnerDialog orgSpinnerDialog, destSpinnerDialog;
    EditText txtOrigin, txtDestination;
    public EditText txtWaybillNo, txtPiecesCount, txtWeight, txtRefNo, txtClientID;
    public int OriginID = 0, DestinationID = 0;
    ArrayList<Booking> bookinglist;
    int position;
    boolean flag_thread = false;
    static Spinner Loadtype;
    PickupAdapter adapter;
    public static ArrayList<HashMap<String, String>> clientdetails;
    static int al = 0;
    String division = "";
    com.toptoche.searchablespinnerlibrary.SearchableSpinner districtspinner;
    int districtID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final DBConnections dbConnections = new DBConnections(getContext(), null);
        if (rootView == null) {


            LayoutInflater lf = getActivity().getLayoutInflater();
            rootView = lf.inflate(R.layout.pickupfirstfragmentnew, container, false);

            CheckBox actualLocation = (CheckBox) rootView.findViewById(R.id.alocation);
            txtOrigin = (EditText) rootView.findViewById(R.id.txtOrigin);
            txtOrigin.setInputType(InputType.TYPE_NULL);
            OriginID = GlobalVar.GV().StationID;

            txtDestination = (EditText) rootView.findViewById(R.id.txtDestination);
            txtDestination.setInputType(InputType.TYPE_NULL);
            txtPiecesCount = (EditText) rootView.findViewById(R.id.txtPiecesCount);

            txtWaybillNo = (EditText) rootView.findViewById(R.id.txtWaybilll);
            txtClientID = (EditText) rootView.findViewById(R.id.txtClientID);
            txtPiecesCount = (EditText) rootView.findViewById(R.id.txtPiecesCount);
            txtWeight = (EditText) rootView.findViewById(R.id.txtWeight);
            txtRefNo = (EditText) rootView.findViewById(R.id.txtRefNo);

            Loadtype = (Spinner) rootView.findViewById(R.id.loadtype);
            districtspinner = (com.toptoche.searchablespinnerlibrary.SearchableSpinner) rootView.findViewById(R.id.district);
            districtspinner.setTitle("Select District");

            districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                    Toast.makeText(getContext(), parent.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                    if (parent.getSelectedItem().toString().equals("Select District"))
                        return;
                    districtID = dbConnections.getDistrictID(parent.getSelectedItem().toString(), getContext());
                    if (districtID == -1) {
                        GlobalVar.GV().alertMsgAll("Info", "Something went wrong , please select District once again",
                                getActivity(),
                                Enum.ERROR_TYPE, "PickUpFirstFragmentEBU");
                        districtspinner.setSelection(0);
                        districtID = 0;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            clientdetails = new ArrayList<>();


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
            if (division.equals("Express")) {
                LinearLayout lld = (LinearLayout) rootView.findViewById(R.id.lldistrict);
                lld.setVisibility(View.VISIBLE);
                actualLocation.setVisibility(View.GONE);
            }

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
                    initSpinner(false);
                }
            });


        }

        String class_ = (String) getArguments().get("class");
        if (class_.equals("BookingDetailAcyivity")) {
            bookinglist = (ArrayList<Booking>) getArguments().get("value");
            position = (Integer) getArguments().get("position");
            SetTextEbu();
        } else if (class_.equals("BookingDetailAcyivityforCBU")) {
            bookinglist = (ArrayList<Booking>) getArguments().get("value");
            position = (Integer) getArguments().get("position");
            SetText();
        }


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

        //FetchDistricData();
        if (dbConnections != null)
            dbConnections.close();

        txtWaybillNo.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;
                else if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onBackpressed();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_ENTER) {

                    setTxtWaybillNo(txtWaybillNo.getText().toString());
                    return true;
                }
                return false;
            }
        });

        return rootView;
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

        try {

            txtWaybillNo.setText(bookinglist.get(position).RefNo);
            txtWaybillNo.setInputType(InputType.TYPE_NULL);
            txtOrigin.setText(bookinglist.get(position).Orgin); //
            OriginID = bookinglist.get(position).OriginId;
            txtDestination.setText(bookinglist.get(position).Destination);
            txtDestination.setInputType(InputType.TYPE_NULL);
            //txtPiecesCount.setText(String.valueOf(bookinglist.get(position).PicesCount));

            DestinationID = bookinglist.get(position).DestinationId;
            txtClientID.setText(String.valueOf(bookinglist.get(position).ClientID));
            //txtWeight.setText(String.valueOf(bookinglist.get(0).Weight));
            //txtRefNo.setText(bookinglist.get(position).RefNo);
            //txtRefNo.setInputType(InputType.TYPE_NULL);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void SetTextEbu() {

        try {

            //txtWaybillNo.setText(bookinglist.get(0).RefNo);
            // txtWaybillNo.setInputType(InputType.TYPE_NULL);
            txtOrigin.setText(bookinglist.get(position).Orgin); //
            OriginID = bookinglist.get(position).OriginId;
            txtDestination.setText(bookinglist.get(position).Destination);
            txtDestination.setInputType(InputType.TYPE_NULL);
            //txtPiecesCount.setText(String.valueOf(bookinglist.get(position).PicesCount));

            DestinationID = bookinglist.get(position).DestinationId;
            txtClientID.setText(String.valueOf(bookinglist.get(position).ClientID));
            //txtWeight.setText(String.valueOf(bookinglist.get(0).Weight));
            txtRefNo.setText(bookinglist.get(position).RefNo);
            txtRefNo.setInputType(InputType.TYPE_NULL);
            txtPiecesCount.setText(String.valueOf(bookinglist.get(position).PicesCount));
            txtWeight.setText(String.valueOf(bookinglist.get(position).Weight));
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
                        setTxtWaybillNo(barcode);
                    }
                }
            }
        }
    }

    private void setTxtWaybillNo(String barcode) {

        if (barcode.length() > 8 && barcode.substring(0, 1).contains(GlobalVar.WaybillNoStartSeries)) {
            txtWaybillNo.setText(barcode.substring(0, 8));
        } else
            txtWaybillNo.setText(barcode);
        GlobalVar.GV().MakeSound(getContext(), R.raw.barcodescanned);
    }

    public ArrayList<Integer> StationList = new ArrayList<>();
    public ArrayList<String> StationNameList = new ArrayList<>();
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
        outState.putInt("districtID", districtID);
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
            districtID = savedInstanceState.getInt("districtID");
            clientdetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("clientdetails");
        }
    }

    String DomainURL = "";

    public void GetClientID(final String input) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Please wait.", "Downloading Client Details.", true);


        String isInternetAvailable = "";

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        // String URL = GlobalVar.GV().NaqelPointerAPILink + "BringLoadTypeValidateClient"; //BringLoadType
        DomainURL = GlobalVar.GV().GetDomainURL(getContext());
        String URL = DomainURL + "BringLoadTypeValidateClient"; //BringLoadType

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
//                            if(jsonObject.getInt("BlockClient") == 1)
//                            {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                builder.setTitle("Info")
//                                        .setMessage("Mentioned Client is Block,kindly contact Operational team.")
//                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int which) {
//                                                getActivity().finish();
//                                            }
//                                        }).setNegativeButton("Cancel", null).setCancelable(false);
//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.show();
//                            }
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
                //GlobalVar.GV().ShowSnackbar(rootView, error.toString(), GlobalVar.AlertType.Error);
                progressDialog.dismiss();
                if (error.toString().contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(rootView, "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        GlobalVar.GV().SwitchoverDomain(getContext(), DomainURL);

                    }

                    GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.servererror), GlobalVar.AlertType.Error);
                }

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
                GlobalVar.GV().loadbalance_Contimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
        requestQueue.getCache().remove(URL);

    }

//    private void initSpinner(boolean isexitdialog) {
//
//        ArrayAdapter spinnerArrayAdapter = null;
//        DBConnections dbConnections = new DBConnections(getContext(), null);
//        if (isexitdialog) {
//            dbConnections.insertDistrictDataBulk(districtdatas, getContext());
//            spinnerArrayAdapter = new ArrayAdapter(getActivity(),
//                    android.R.layout.simple_spinner_item,
//                    utilities.DistrictModelstoList(districtdatas));
//        } else
//            spinnerArrayAdapter = new ArrayAdapter(getActivity(),
//                    android.R.layout.simple_spinner_item, dbConnections.getDistrictDatas(getContext())
//            );
//        dbConnections.close();
//        // Step 3: Tell the spinner about our adapter
//        districtspinner.setAdapter(spinnerArrayAdapter);
//        spinnerArrayAdapter.notifyDataSetChanged();
//        if (isexitdialog)
//            exitdialog();
//
//    }

    private void initSpinner(boolean isexitdialog) {

        ArrayAdapter spinnerArrayAdapter = null;
        DBConnections dbConnections = new DBConnections(getContext(), null);
        if (isexitdialog) {

            spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                    android.R.layout.simple_spinner_item,
                    dbConnections.getDistrictDatas(getContext(), DestinationID));
        } else
            spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                    android.R.layout.simple_spinner_item, dbConnections.getDistrictDatas(getContext(), DestinationID)
            );
        dbConnections.close();
        // Step 3: Tell the spinner about our adapter
        districtspinner.setAdapter(spinnerArrayAdapter);
        spinnerArrayAdapter.notifyDataSetChanged();
        if (isexitdialog)
            exitdialog();

    }

    List<DistrictDataModel> districtdatas = new ArrayList<>();
    static SweetAlertDialog alertDialog;

    public void FetchDistricData() {
        CommonRequest commonRequest = new CommonRequest();
        commonRequest.setStationID(GlobalVar.GV().StationID);
        GlobalVar.GV().alertMsgAll("Info", "Please wait to fetch District Datas.",
                getActivity(),
                Enum.PROGRESS_TYPE, "PickUpFirstFragmentEBU");
        PickupApi.fetchdistrictdata(new Callback<List<DistrictDataModel>>() {
            @Override
            public void returnResult(List<DistrictDataModel> result) {
                System.out.println();

                districtdatas.addAll(result);
                DBConnections dbConnections = new DBConnections(getContext(), null);
                dbConnections.insertDistrictDataBulk(districtdatas, getContext());
                dbConnections.close();
                exitdialog();
                //initSpinner(true);
            }

            @Override
            public void returnError(String message) {
                //mView.showError(message);
                exitdialog();
                System.out.println(message);
            }
        }, commonRequest);
    }

    //Alert Click action
    @Override
    public void returnOk(final int value, final Activity activity) {

        try {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            exitdialog();
                            //if (Enum.SUCCESS_TYPE.getValue() == value)
                            //     activity.finish();

                        }
                    });

                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void returnCancel(int cancel, SweetAlertDialog alertDialog) {
//        GlobalVar.GV().alertMsgAll("Error", "Please wait to fetch District Datas.",
//                getActivity(),
//                Enum.PROGRESS_TYPE, "PickUpFirstFragmentEBU");
        this.alertDialog = alertDialog;
    }

    private void exitdialog() {
        if (alertDialog != null) {
            alertDialog.dismissWithAnimation();
            alertDialog = null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.district:
                Toast.makeText(getContext(), adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
}