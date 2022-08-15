package com.naqelexpress.naqelpointer.Activity.AtOriginusingLocalDB;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ThirdFragment extends Fragment{
    View rootView;
    private EditText txtBarCode, txtBarCodePiece;
    static TextView lbTotal;
    static DataAdapterForThird adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();
    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Intent intent;
    static ArrayList<HashMap<String, String>> SelectedwaybillBardetails = new ArrayList<>();
    static ArrayList<String> ValidateBarCodeList = new ArrayList<>();
//    private ArrayList<String> ValidateBarCodeList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {

            Log.d("STARTED", "AtOriginUsingLocalDB/SecondFragment: ");
            rootView = inflater.inflate(R.layout.deliverythirdfragment, container, false);

            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);
            txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);

            txtBarCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (txtBarCode != null && txtBarCode.getText().length() >= 8) {

                        setBarcode();
                    }
                }
            });

//            txtBarCode.setOnKeyListener(new View.OnKeyListener() {
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    // If the event is a key-down event on the "enter" button
//                    if (event.getAction() != KeyEvent.ACTION_DOWN)
//                        return true;
//                    else if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        //finish();
//                        GlobalVar.onBackpressed(getActivity(), "Exit", "Are you sure want to Exit?");
//                        return true;
//                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
//
//                        setBarcode();
//                        return true;
//                    }
//                    return false;
//                }
//            });

            Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);

            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else
                    {
                        Intent newIntent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                        getActivity().startActivityForResult(newIntent, 3);
                    }

                }
            });


            initViews();
        }

        return rootView;
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapterForThird(ValidateBarCodeList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        lbTotal.setText(getString(R.string.lbCount) + ValidateBarCodeList.size());
//        initSwipe();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("SECFRAG", "AtOriginUsingLocalDB/SecondFragment: ");
        if (requestCode == 3 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        txtBarCode.setText(barcode);
                        setBarcode();
                    }
                }
            }
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("SECFRAG", "AtOriginUsingLocalDB/SecondFragment: ");
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            if (data != null) {
//                Bundle extras = data.getExtras();
//                if (extras != null) {
//                    if (extras.containsKey("barcode")) {
//                        String barcode = extras.getString("barcode");
//                        txtBarCode.setText(barcode);
//                        //AddNewPiece();
//                    }
//                }
////                final Barcode barcode = data.getParcelableExtra("barcode");
////                txtBarCode.post(new Runnable()
////                {
////                    @Override
////                    public void run()
////                    {
////                        txtBarCode.setText(barcode.displayValue);
////
////                        if (txtBarCode.getText().toString().length() > 6)
////                            AddNewPiece();
////                    }
////                });
//            }
//        }
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
////        initViews();
//    }

    private void setBarcode() {
//        ValidateWayBill(txtBarCode.getText().toString());
//        ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
        ValidateWayBill(txtBarCode.getText().toString());


    }


    private void ValidateWayBill(String barcode) {
        if (!ValidateBarCodeList.contains(barcode)) {

            int preqty = ValidateBarCodeList.size() + 1;
//            SecondFragment.Selectedwaybilldetails.get(j).put("ScannedPC", String.valueOf(preqty));

//            SelectedwaybillBardetails.add(CourierDetails.waybillBardetails.get(i));

            DBConnections dbConnections = new DBConnections(getContext(), null);
            dbConnections.AtOriginScannedPiecesCount(barcode, String.valueOf(preqty), getView());
            dbConnections.AtOriginScannedPiececode(barcode, getView());
            dbConnections.close();

            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
//            txtBarCode.setText("");

            ValidateBarCodeList.add(barcode);
            lbTotal.setText(getString(R.string.lbCount) + ValidateBarCodeList.size());
            adapter.notifyDataSetChanged();


//            for (int i = 0; i < CourierDetails.waybillBardetails.size(); i++) {
//                boolean sound = false;
//                if (barcode.equals(CourierDetails.waybillBardetails.get(i).get("BarCode"))) {
//
//                    for (int j = 0; j < SecondFragment.Selectedwaybilldetails.size(); j++) {
//
//                        if (SecondFragment.Selectedwaybilldetails.get(j).get("WaybillNo").
//                                equals(CourierDetails.waybillBardetails.get(i).get("WaybillNo"))) {
//
//
//                            int preqty = Integer.parseInt(SecondFragment.Selectedwaybilldetails.get(j).get("ScannedPC")) + 1;
//                            SecondFragment.Selectedwaybilldetails.get(j).put("ScannedPC", String.valueOf(preqty));
//
//                            SelectedwaybillBardetails.add(CourierDetails.waybillBardetails.get(i));
//
//                            DBConnections dbConnections = new DBConnections(getContext(), null);
//                            dbConnections.AtOriginScannedPiecesCount(CourierDetails.waybillBardetails.get(i).get("WaybillNo")
//                                    , String.valueOf(preqty), getView());
//                            dbConnections.AtOriginScannedPiececode(barcode, getView());
//                            dbConnections.close();
//
//                            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
//                            txtBarCode.setText("");
//                            adapter.notifyDataSetChanged();
//                            SecondFragment.adapter.notifyDataSetChanged();
//                            lbTotal.setText(getString(R.string.lbCount) + SelectedwaybillBardetails.size());
//                            ValidateBarCodeList.add(barcode);
//                            sound = true;
//                            break;
//
//                        }
//                    }
//                    if (!sound) {
//                        GlobalVar.GV().ShowSnackbar(getView(), "No WayBillNo under this BarCode", GlobalVar.AlertType.Error);
//                        GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
//                    }
//                }
//
//            }
        } else
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);

    }


    private void deletePiecealongWaybill(final int position) {


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtBarCode", txtBarCode.getText().toString());
        outState.putString("waybillcount", lbTotal.getText().toString());
        outState.putStringArrayList("ValidateBarCodeList", ValidateBarCodeList);
        outState.putSerializable("SelectedwaybillBardetails", SelectedwaybillBardetails);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtBarCode.setText(savedInstanceState.getString("txtBarCode"));
            lbTotal.setText(savedInstanceState.getString("waybillcount"));
            ValidateBarCodeList = savedInstanceState.getStringArrayList("ValidateBarCodeList");
            SelectedwaybillBardetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("SelectedwaybillBardetails");
            initViews();
        }
    }
}