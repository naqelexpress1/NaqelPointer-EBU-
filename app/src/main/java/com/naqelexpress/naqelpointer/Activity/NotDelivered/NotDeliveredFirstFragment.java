package com.naqelexpress.naqelpointer.Activity.NotDelivered;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
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

            txtWaybillNo.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if (event.getAction() != KeyEvent.ACTION_DOWN)
                        return true;
                    else if (keyCode == KeyEvent.KEYCODE_BACK) {
                        onBackpressed();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                            setTxtWaybillNo(txtWaybillNo.getText().toString());
                        if (txtWaybillNo != null && txtWaybillNo.getText().toString().length() >= 8)
                            SetText();
                        return true;
                    }
                    return false;
                }
            });

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

    private void SetText() {
        String WaybillNo = txtWaybillNo.getText().toString();
        utilities utilities = new utilities();
        String nbarcode = utilities.findwaybillno(WaybillNo);
        txtWaybillNo.setText(nbarcode);

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