package com.naqelexpress.naqelpointer.Activity.ArrivedDest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class OpenPallet extends Fragment {
    View rootView;
    private EditText txtBarCode;
    static PalletAdapter adapter;
    private GridView swipeMenuListView;
    private Intent intent;

    static ArrayList<HashMap<String, String>> SelectedPallet = new ArrayList<>();
    static ArrayList<HashMap<String, String>> pallets = new ArrayList<>();

    public ArrayList<String> validatepalletdetails = new ArrayList<>();

    protected void displayReceivedData(String tripid, ArrayList<HashMap<String, String>> pallets) {
        this.pallets.clear();
        this.pallets.addAll(pallets);
        adapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {
                rootView = inflater.inflate(R.layout.arivedatdestpallet, container, false);


                txtBarCode = (EditText) rootView.findViewById(R.id.palletbarcode);

                txtBarCode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (txtBarCode != null && txtBarCode.getText().length() == 7)
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


                swipeMenuListView = (GridView) rootView.findViewById(R.id.waybills);
                adapter = new PalletAdapter(pallets, getContext());
                swipeMenuListView.setAdapter(adapter);

            }

            return rootView;
        }
    }

    private void ValidateWayBill(String palletno) {
        if (!validatepalletdetails.contains(palletno)) {
            for (int i = 0; i < pallets.size(); i++) {
                if (palletno.equals(pallets.get(i).get("PalletNo"))) {
                    SelectedPallet.add(pallets.get(i));
                    pallets.get(i).put("bgcolor", "1");
                    GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
                    txtBarCode.setText("");
                    adapter.notifyDataSetChanged();
                    validatepalletdetails.add(palletno);
                    break;
                }
            }
        } else
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
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
        outState.putStringArrayList("validatepalletdetails", validatepalletdetails);
        outState.putSerializable("SelectedPallet", SelectedPallet);
        outState.putSerializable("pallets", pallets);
        outState.putString("txtBarCode", txtBarCode.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            validatepalletdetails = savedInstanceState.getStringArrayList("validatepalletdetails");
            SelectedPallet = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("SelectedPallet");
            pallets = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("Selectedwaybilldetails");
        }
    }

}