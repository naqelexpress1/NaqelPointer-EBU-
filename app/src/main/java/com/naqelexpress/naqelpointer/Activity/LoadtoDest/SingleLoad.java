package com.naqelexpress.naqelpointer.Activity.LoadtoDest;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleLoad extends Fragment {
    View rootView;
    private EditText txtBarCode, txtBarCodePiece;
    TextView lbTotal, buildcount;
    static DataAdapterForThird adapter;
    private GridView recyclerView;
    private Paint p = new Paint();
    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Intent intent;
    //  static ArrayList<HashMap<String, String>> SelectedwaybillBardetails = new ArrayList<>();
    static ArrayList<String> ValidateBarCodeList = new ArrayList<>();
    Map<String, ArrayList<HashMap<String, String>>> map1 = new HashMap<>();
    ArrayList<HashMap<String, String>> buildpalletary = new ArrayList<>();
    ArrayList<HashMap<String, String>> showbuildpalletary = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.loadtodestbpallet, container, false);

            lbTotal = (TextView) rootView.findViewById(R.id.count);
            buildcount = (TextView) rootView.findViewById(R.id.buildcount);

            txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);
            Button builedpallet = (Button) rootView.findViewById(R.id.buildpallet);
            builedpallet.setVisibility(View.GONE);
            txtBarCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txtBarCode != null && txtBarCode.getText().length() == 13)
                        ValidateWayBill(txtBarCode.getText().toString());
                }
            });

            Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
            intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else
                        startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                }
            });

            buildpalletary.clear();
            showbuildpalletary.clear();
            recyclerView = (GridView) rootView.findViewById(R.id.barcode);
            adapter = new DataAdapterForThird(showbuildpalletary, getContext());
            recyclerView.setAdapter(adapter);

        }

        return rootView;
    }

    private void ValidateWayBill(String barcode) {

        if (!BuildPallet.validateBarcodeetails.contains(barcode)) {

            HashMap<String, String> temp = new HashMap<>();
            temp.put("BarCode", barcode);
            temp.put("bgcolor", "1");
            temp.put("palletNo", "0");
            temp.put("Type", "0");

            buildpalletary.add(temp);
            showbuildpalletary.add(temp);
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
            txtBarCode.setText("");
            adapter.notifyDataSetChanged();
            BuildPallet.validateBarcodeetails.add(barcode);
            ValidateBarCodeList.add(barcode);
            buildcount.setText(String.valueOf(showbuildpalletary.size()));
            lbTotal.setText(String.valueOf(showbuildpalletary.size()));
            updatePalletNo(barcode);

        } else
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);

    }

    private void updatePalletNo(String palletno) {

        map1.put(palletno, buildpalletary);
        BuildPallet.Createpallertlist.add(map1);
        buildpalletary = new ArrayList<>();
        map1 = new HashMap<>();
        adapter = new DataAdapterForThird(showbuildpalletary, getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        txtBarCode.setText(barcode);
                        //AddNewPiece();
                    }
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtBarCode", txtBarCode.getText().toString());
        outState.putString("waybillcount", lbTotal.getText().toString());
        //outState.putStringArrayList("ValidateBarCodeList", BuildPallet.validateBarcodeetails);
        outState.putStringArrayList("thisValidateBarCodeList", ValidateBarCodeList);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtBarCode.setText(savedInstanceState.getString("txtBarCode"));
            lbTotal.setText(savedInstanceState.getString("waybillcount"));
            //BuildPallet.validateBarcodeetails = savedInstanceState.getStringArrayList("ValidateBarCodeList");
            ValidateBarCodeList = savedInstanceState.getStringArrayList("thisValidateBarCodeList");
        }
    }
}