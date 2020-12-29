package com.naqelexpress.naqelpointer.Activity.NCL;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.BarcodeInfoRequest;
import com.naqelexpress.naqelpointer.JSON.Results.BarcodeInfoResult;
import com.naqelexpress.naqelpointer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class ScanNclWaybillFragment extends Fragment {
    View rootView;
    public EditText txtBarcode;
    TextView lbTotal, lbNclNo;
    public ArrayList<String> WaybillList = new ArrayList<>();
    public ArrayList<Double> WaybillWeight = new ArrayList<>();
    public ArrayList<PieceDetail> PieceCodeList = new ArrayList<PieceDetail>();
    private RecyclerView recyclerView;
    public NclAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (rootView == null) {
            rootView = inflater.inflate(R.layout.scannclwaybill, container, false);


            txtBarcode = (EditText) rootView.findViewById(R.id.txtBarcode);

            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);
            lbNclNo = (TextView) rootView.findViewById(R.id.lbNclNo);

            Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!GlobalVar.GV().checkPermission(getActivity(), GlobalVar.PermissionType.Camera)) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else {
                        Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                        startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                    }
                }
            });

            txtBarcode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txtBarcode != null && txtBarcode.getText().toString().length() == 13) {
                        BarcodeInfoRequest barcodeInfoRequest = new BarcodeInfoRequest();
                        barcodeInfoRequest.Barcode = Long.parseLong(txtBarcode.getText().toString());

                        String jsonData = JsonSerializerDeserializer.serialize(barcodeInfoRequest, true);
                        new BringBarcodeInfo().execute(jsonData);

                    }

                }
            });

        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        GlobalVar.GV().MakeSound(getContext(), R.raw.barcodescanned);
                        txtBarcode.setText(barcode);
                    }
                }
            }
        }
    }

    private void AddNewWaybill(String WaybillNo) {
        if (WaybillNo.length() > 8)
            WaybillNo = WaybillNo.substring(0, 8);
        if (WaybillNo.toString().length() == 8) {
            if (!WaybillList.contains(WaybillNo.toString())) {
                WaybillList.add(0, WaybillNo.toString());
            }
        }
    }

    private void AddNewPiece(String PieceCode, String WaybillNo, double Weight) {
        GlobalVar.hideKeyboardFrom(getContext(), rootView);
        if (!IsDuplicate(PieceCode)) {
            if (PieceCode.length() == 13) {
                PieceCodeList.add(0, new PieceDetail(PieceCode, WaybillNo, Weight));
                lbTotal.setText(getString(R.string.lbCount) + PieceCodeList.size());
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                txtBarcode.setText("");
                initViews();
            }
        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarcode.setText("");
        }
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.shipmentBarCodes);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NclAdapter(PieceCodeList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        initSwipe();
    }

    private void removeWaybill(String waybillNo) {
        int index = WaybillList.indexOf(waybillNo);
        WaybillList.remove(index);
    }

    public boolean IsDuplicate(String pieceCode) {
        boolean result = false;
        for (int i = 0; i < PieceCodeList.size(); i++) {
            if (PieceCodeList.get(i).Barcode.equals(pieceCode)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)//| ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Confirm Deleting")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    String waybillNo = adapter.GetWaybillNo(position);
                                    removeWaybill(waybillNo);
                                    adapter.removeItem(position);
                                    lbTotal.setText(getString(R.string.lbCount) + PieceCodeList.size());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    initViews();
                                }
                            })
                            .setCancelable(false);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }


        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private class BringBarcodeInfo extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetBarcodeInfo");
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
            //progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
            if (finalJson != null) {
                BarcodeInfoResult barcodeInfoResult = new BarcodeInfoResult(finalJson);


                if (IsValid(barcodeInfoResult) && barcodeInfoResult.WayBillNo != 0) {
                    AddNewPiece(String.valueOf(barcodeInfoResult.BarCode), String.valueOf(barcodeInfoResult.WayBillNo), barcodeInfoResult.weight);
                    AddNewWaybill(String.valueOf(barcodeInfoResult.WayBillNo));
                } else {
                    GlobalVar.GV().ShowSnackbar(rootView, "No data with these Barcode", GlobalVar.AlertType.Error);
                    GlobalVar.GV().MakeSound(getActivity(), R.raw.wrongbarcodescan);
                    txtBarcode.setText("");
                }

            } else {
                GlobalVar.GV().ShowSnackbar(rootView, "No data with these Barcode", GlobalVar.AlertType.Error);
                GlobalVar.GV().MakeSound(getActivity(), R.raw.wrongbarcodescan);
                txtBarcode.setText("");
            }
        }
    }

    private boolean IsValid(BarcodeInfoResult barcodeInfoResult) {

        boolean Result = true;

        NclShipmentActivity nclShipmentActivity = (NclShipmentActivity) getActivity();

        if (nclShipmentActivity.NclNo == "0") {
            Result = false;
            GlobalVar.GV().ShowSnackbar(rootView, "Please Generate Ncl No", GlobalVar.AlertType.Info);
        }
        if (!nclShipmentActivity.destList.contains(barcodeInfoResult.DestId) && !nclShipmentActivity.IsMixed) {
            Result = false;
            GlobalVar.GV().ShowSnackbar(rootView, "Barcode is not belong to this destination", GlobalVar.AlertType.Info);
            GlobalVar.GV().MakeSound(getActivity(), R.raw.wrongbarcodescan);
            txtBarcode.setText("");

        }

        return Result;
    }

    public class PieceDetail {
        public String Barcode;
        public String Waybill;
        public double Weight;

        public PieceDetail(String barcode, String waybill, double weight) {
            Barcode = barcode;
            Waybill = waybill;
            Weight = weight;
        }
    }

    public void clearList() {
        initViews();
        adapter.clearAll();
        WaybillList.clear();
        lbTotal.setText(getString(R.string.lbCount) + PieceCodeList.size());
        txtBarcode.setText("");
    }

}
