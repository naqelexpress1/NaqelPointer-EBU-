package com.naqelexpress.naqelpointer.Activity.NotDelivered;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.utils.SharedHelper;
import com.naqelexpress.naqelpointer.utils.utilities;

import java.util.ArrayList;

public class NotDeliveredFirstFragment extends Fragment {

    private View rootView;
    SpinnerDialog spinnerDialog;
    EditText txtWaybillNo, txtReason, txtNotes;
    public int ReasonID = 0;

//    protected TextWatcher textWatcher = new TextWatcher() {
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            // your logic here
//            if (txtWaybillNo != null && txtWaybillNo.getText().length() >= 8)
//                //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
//                setTxtWaybillNo();
//
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            // your logic here
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            // your logic here
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {

            rootView = inflater.inflate(R.layout.notdeliveredfirstfragment, container, false);


            txtWaybillNo = (EditText) rootView.findViewById(R.id.txtWaybilll);
//            txtWaybillNo.addTextChangedListener(textWatcher);

            txtWaybillNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txtWaybillNo != null && txtWaybillNo.getText().length() >= 9)//every making 9 bcz it was reading mentioned number count in some devices
                        SetText();
                }
            });

//            txtWaybillNo.setOnKeyListener(new View.OnKeyListener() {
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    // If the event is a key-down event on the "enter" button
//                    if (event.getAction() != KeyEvent.ACTION_DOWN)
//                        return true;
//                    else if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        onBackpressed();
//                        return true;
//                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
////                            setTxtWaybillNo(txtWaybillNo.getText().toString());
//                        if (txtWaybillNo != null && txtWaybillNo.getText().toString().length() >= 8)
//                            SetText();
//                        return true;
//                    }
//                    return false;
//                }
//            });

//            txtWaybillNo.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
////                    if (txtBarCode != null && txtBarCode.getText().length() == 13)
////                        AddNewPiece();
//                    if (txtWaybillNo != null && txtWaybillNo.getText().length() >= 8){}
////                        setTxtWaybillNo();
//                }
//            });

//            txtWaybillNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanWaybillLength)});
            txtReason = (EditText) rootView.findViewById(R.id.txtReason);
            txtReason.setInputType(InputType.TYPE_NULL);
            txtNotes = (EditText) rootView.findViewById(R.id.txtNotes);

            txtReason.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        spinnerDialog.showSpinerDialog(false);
                }
            });

            if (savedInstanceState == null)
                GetDeliveryStatusList();

            if (GlobalVar.GV().IsArabic())
                spinnerDialog = new SpinnerDialog(getActivity(), DeliveryStatusFNameList, "Select or Search Reason", R.style.DialogAnimations_SmileWindow);
            else
                spinnerDialog = new SpinnerDialog(getActivity(), DeliveryStatusNameList, "Select or Search Reason", R.style.DialogAnimations_SmileWindow);


            spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {

                    if (GlobalVar.GV().IsArabic())
                        txtReason.setText(DeliveryStatusFNameList.get(position));
                    else

                        txtReason.setText(DeliveryStatusNameList.get(position));
                    ReasonID = DeliveryStatusList.get(position);
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
                        getActivity().startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                    }
                }
            });

            Bundle bundle = getArguments();

            if (bundle != null) {
                txtWaybillNo.setText(bundle.getString("WaybillNo"));
                txtWaybillNo.setEnabled(false);
                btnOpenCamera.setVisibility(View.GONE);
            }


        }
        return rootView;
    }

//    private void setTxtWaybillNo() {
//
//        String barcode = txtWaybillNo.getText().toString();
////        txtWaybillNo.removeTextChangedListener(textWatcher);
//        if (barcode.length() >= 8) {// && GlobalVar.WaybillNoStartSeries.contains(barcode.substring(0, 1))
////            txtWaybillNo.setText(barcode.substring(0, 8));
//            txtWaybillNo.setText(barcode);
//            //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
//
//        } else if (barcode.length() >= GlobalVar.ScanWaybillLength) {
////            txtWaybillNo.setText(barcode.substring(0, GlobalVar.ScanWaybillLength));
//            txtWaybillNo.setText(barcode);
//            //txtBarCode.setText(barcode.substring(0, GlobalVar.ScanWaybillLength));
//            //ValidateWayBill(txtBarCode.getText().toString().substring(0, GlobalVar.ScanWaybillLength));
//        }
//
//
//        ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
//
//
//    }

    public ArrayList<String> DeliveryStatusNameList = new ArrayList<>();
    public ArrayList<String> DeliveryStatusFNameList = new ArrayList<>();
    public ArrayList<Integer> DeliveryStatusList = new ArrayList<>();

    public void GetDeliveryStatusList() {

        DeliveryStatusNameList.clear();
        DeliveryStatusFNameList.clear();
        DeliveryStatusList.clear();

        DBConnections dbConnections = new DBConnections(getContext(), null);

        Cursor result = dbConnections.Fill("select * from DeliveryStatus", getContext());
        int RoleMEID = SharedHelper.getKeyInteger(getActivity(), "RoleMEID");
        int empID = SharedHelper.getKeyInteger(getActivity(), "EmployID");
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                boolean toAdd = false;
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Code = result.getString(result.getColumnIndex("Code"));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));
                if (empID == 17824 || empID == 17734 || empID == 20211 || empID == 20923) {
                    toAdd = true;
                } else {
                    if (Name.equals("Accident - Collision")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Accident - Theft")) {//
                        if (RoleMEID == 28 || RoleMEID == 30 || RoleMEID == 31) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Accident - Breakdown")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Attempt - Not Available")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Customer facility challenges")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Attempt - Change Location")) {//Customer - Change Location
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Incomplete Information - address")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Claims - Damage")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Refused Delivery - Unsealed")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Not received")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Claims - Lost")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Customer - No response")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Delivered")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Incomplete Information - address")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Refused Delivery - Cash")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Incomplete Information - DCode")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("No Attempt - Charges Approval")) {//Charges Approval
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("No attempt -  Weekend Off")) {// Weekend Off
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Attempt - Wrong Destination")) {//Wrong Destination
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Weather")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Legal Holiday- National Day")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Access restricted")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("ODA - Schedule")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("On Hold - Credit Control")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("On Hold - Customer Collection")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Refused Delivery - Missing Content")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Refused Delivery - Wrong Content")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Refused Delivery - Damage Content")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Refused Delivery - Open Request")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Refused Delivery-Customer dont need the shipment")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Return to Origin")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.contains("Trucknban")) {//No Attempt - Late Trip Trucknban
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.contains("Late Trip")) {//No Attempt - Late Trip Trucknban
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Attached Document Incomplete")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Mall Timing Restricted")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("UCS")) {//
                        if (RoleMEID == 28 || RoleMEID == 30 || RoleMEID == 31) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Conginee Location Sealed by The Baladia")) {//
                        if (RoleMEID == 29 || RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Que Delivery")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Forward-Pharma")) {//
                        toAdd = true;
                    } else if (Name.equals("Customer Request")) {//
                        if (RoleMEID == 27 || RoleMEID == 28 || RoleMEID == 30) {
                            toAdd = true;
                        }
                    } else if (Name.equals("Redirection request")) {//
                        if (RoleMEID == 30) {
                            toAdd = true;
                        }
                    }
                    else {
                        toAdd = true;
                    }
                }

                if (toAdd) {
                    DeliveryStatusList.add(ID);
                    DeliveryStatusNameList.add(Name);
                    DeliveryStatusFNameList.add(FName);
                }
            }
            while (result.moveToNext());

            Log.d("LIST: ",DeliveryStatusNameList.toString());
        }
        dbConnections.close();


    }

    private void SetText() {
        String WaybillNo = txtWaybillNo.getText().toString();
        utilities utilities = new utilities();
        String newBarcode = utilities.findwaybillno(WaybillNo);
        if (newBarcode.length() < 9)
            txtWaybillNo.setText(newBarcode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        txtWaybillNo.setText(barcode);
                        SetText();
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


        }
    }

    private void onBackpressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exit Not Delivered")
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

//    protected TextWatcher textWatcher = new TextWatcher() {
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            // your logic here
//            if (txtWaybillNo != null && txtWaybillNo.getText().length() >= 8)
//                //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
//                setTxtWaybillNo();
//
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            // your logic here
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            // your logic here
//        }
//    };
}