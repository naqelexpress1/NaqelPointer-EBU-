package com.naqelexpress.naqelpointer.Activity.ArrivedDest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
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

public class SingleItem extends Fragment {


    EditText palletbarcode;
    View rootView;
    SingleItemAdapter adapter;
    static ArrayList<HashMap<String, String>> SelectedSingleLoad = new ArrayList<>();
    ArrayList<HashMap<String, String>> SingleLoad = new ArrayList<>();
    ArrayList<String> ValidateBarCodeList = new ArrayList<>();
    private GridView waybilgrid;

    protected void displayReceivedData(String tripid, ArrayList<HashMap<String, String>> singleload) {
        SingleLoad.clear();
        SingleLoad.addAll(singleload);
        adapter.notifyDataSetChanged();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {
                rootView = inflater.inflate(R.layout.arivedatdestpallet, container, false);

                waybilgrid = (GridView) rootView.findViewById(R.id.waybills);
                adapter = new SingleItemAdapter(SingleLoad, getContext());
                waybilgrid.setAdapter(adapter);

                palletbarcode = (EditText) rootView.findViewById(R.id.palletbarcode);

                palletbarcode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (palletbarcode != null && palletbarcode.getText().length() == 7)
                            ValidatePallet(palletbarcode.getText().toString());
                    }
                });


                Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
                final Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
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

            }


            return rootView;
        }
    }


    private void ValidatePallet(String barcode) {
        if (!ValidateBarCodeList.contains(barcode)) {
            for (int i = 0; i < SingleLoad.size(); i++) {
                if (barcode.equals(SingleLoad.get(i).get("PalletNo"))) {
                    SingleLoad.get(i).put("bgcolor", "1");
                    SelectedSingleLoad.add(SingleLoad.get(i));
                    GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
                    palletbarcode.setText("");
                    adapter.notifyDataSetChanged();
                    ValidateBarCodeList.add(barcode);
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
                        palletbarcode.setText(barcode);
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("SelectedSingleLoad", SingleLoad);
        outState.putStringArrayList("ValidateBarCodeList", ValidateBarCodeList);
        outState.putString("palletbarcode", palletbarcode.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            SingleLoad = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("SelectedSingleLoad");
            ValidateBarCodeList = savedInstanceState.getStringArrayList("ValidateBarCodeList");
        }
    }

}