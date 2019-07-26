package com.naqelexpress.naqelpointer.Activity.LoadtoDestLocalDB;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class BuildPallet extends Fragment implements IOnFocusListenable {
    View rootView;
    private EditText txtBarCode;
    TextView lbTotal, buildcount;
    static TextView palletNo;
    private DataAdapter adapter;
    private GridView recyclerView;
    private Intent intent;
    LinearLayout ll1;

    // static ArrayList<ArrayList<HashMap<String, String>>> createpallet = new ArrayList<>();
    Map<String, ArrayList<HashMap<String, String>>> map1 = new HashMap<String, ArrayList<HashMap<String, String>>>();
    static List<Map<String, ArrayList<HashMap<String, String>>>> Createpallertlist = new ArrayList<>();

//    List<Map<String,List<String>>> list = new ArrayList<Map<String,List<String>>>();//This is the final list you need
//    Map<String, List<String>> map1 = new HashMap<String, List<String>>();/

    //    private ArrayList<HashMap<String, String>> waybilldetails = new ArrayList<>();
//    public static ArrayList<HashMap<String, String>> waybillBardetails = new ArrayList<>();
    //static ArrayList<HashMap<String, String>> SelectedBarcodedetails = new ArrayList<>();
    ArrayList<HashMap<String, String>> buildpalletary = new ArrayList<>();

    public static ArrayList<String> validateBarcodeetails = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {

                rootView = inflater.inflate(R.layout.loadtodestbpallet, container, false);

                lbTotal = (TextView) rootView.findViewById(R.id.count);
                buildcount = (TextView) rootView.findViewById(R.id.buildcount);


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
                        if (txtBarCode != null && txtBarCode.getText().length() == 13)
                            ValidateWayBill(txtBarCode.getText().toString());
                        //     AddNewPiece();
                    }
                });

                ll1 = (LinearLayout) rootView.findViewById(R.id.ll1);
                palletNo = (TextView) rootView.findViewById(R.id.palletno);

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

                Button buildpallet = (Button) rootView.findViewById(R.id.buildpallet);
                buildpallet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (p != null)
                            showPopup(getActivity(), p);
                    }
                });

                buildpalletary.clear();

                recyclerView = (GridView) rootView.findViewById(R.id.barcode);
                adapter = new DataAdapter(buildpalletary, getContext());
                recyclerView.setAdapter(adapter);

            }

            return rootView;
        }
    }

    PopupWindow popup;

    private void showPopup(final Activity context, Point p) {

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.palletpopup, viewGroup);

        final EditText palletbarcode = (EditText) layout.findViewById(R.id.palletbarcode);

        palletbarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (palletbarcode != null && palletbarcode.getText().length() == 13) {
                    updatePalletNo(palletbarcode.getText().toString());
                    popup.dismiss();
                }
            }
        });
        // Creating the PopupWindow

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);

        //popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        //popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.update();
        popup.setOutsideTouchable(false);
        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
        // Getting a reference to Close button, and close the popup when clicked.
        ImageButton camera = (ImageButton) layout.findViewById(R.id.next);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                } else
                    startActivityForResult(intent, 111);

                //popup.dismiss();
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        Button button = (Button) rootView.findViewById(R.id.buildpallet);

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        button.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    private void ValidateWayBill(String barcode) {

        if (!validateBarcodeetails.contains(barcode)) {

            HashMap<String, String> temp = new HashMap<>();
            temp.put("BarCode", barcode);
            temp.put("bgcolor", "1");
            temp.put("palletNo", "0");
            temp.put("Type", "1");

            buildpalletary.add(temp);
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
            txtBarCode.setText("");
            adapter.notifyDataSetChanged();
            validateBarcodeetails.add(barcode);
            lbTotal.setText(String.valueOf(buildpalletary.size()));

        } else
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
    }

    Point p;

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
        } else if (requestCode == 111 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        ll1.setVisibility(View.VISIBLE);
                        String barcode = extras.getString("barcode");
//                        palletNo.setText(barcode);
                        popup.dismiss();
                        updatePalletNo(barcode);
                    }
                }
            }
        }
    }

    private void updatePalletNo(String palletno) {
        map1.put(palletno, buildpalletary);
        Createpallertlist.add(map1);
        buildcount.setText(String.valueOf(Createpallertlist.size()));
        buildpalletary = new ArrayList<>();
        map1 = new HashMap<String, ArrayList<HashMap<String, String>>>();
        adapter = new DataAdapter(buildpalletary, getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        lbTotal.setText("0");

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("validatewaybilldetails", validateBarcodeetails);

        outState.putSerializable("Selectedwaybilldetails", buildpalletary);
        outState.putString("txtBarCode", txtBarCode.getText().toString());
        outState.putString("waybillcount", lbTotal.getText().toString());

        //outState.putStringArrayList("ShipmentBarCodeList", ShipmentBarCodeList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            validateBarcodeetails = savedInstanceState.getStringArrayList("validatewaybilldetails");
            buildpalletary = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("Selectedwaybilldetails");

        }
    }

}