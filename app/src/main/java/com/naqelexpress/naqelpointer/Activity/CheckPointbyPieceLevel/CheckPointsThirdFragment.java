package com.naqelexpress.naqelpointer.Activity.CheckPointbyPieceLevel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.BarcodeInfoRequest;
import com.naqelexpress.naqelpointer.JSON.Results.BarcodeInfoResult;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Retrofit.Models.OnLineValidation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CheckPointsThirdFragment
        extends Fragment {
    View rootView;
    static TextView lbTotal;
    private EditText txtBarCode;
    public static ArrayList<String> BarCodeList = new ArrayList<>();
    public static DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();


    private Intent intent;
    private List<String> onHoldShipments = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.checkpointsthirdfragment, container, false);
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);

            txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);
//            txtBarCode.addTextChangedListener(new TextWatcher() {
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
//                    if (txtBarCode != null && txtBarCode.getText().length() == 13) {
//
//                        if (CheckPointsFirstFragment.CheckPointTypeDetailID == 54) {
//                            BarcodeInfoRequest barcodeInfoRequest = new BarcodeInfoRequest();
//                            barcodeInfoRequest.Barcode = Long.parseLong(txtBarCode.getText().toString());
//                            String jsonData = JsonSerializerDeserializer.serialize(barcodeInfoRequest, true);
//                            new BringBarcodeInfo().execute(jsonData);
//                        } else
//
//                            AddNewPiece();
////
//                    }
//                    //AddNewPiece();
//                }
//            });

            txtBarCode.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if (event.getAction() != KeyEvent.ACTION_DOWN)
                        return true;
                    else if (keyCode == KeyEvent.KEYCODE_BACK) {
                        onBackpressed();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (CheckPointsFirstFragment.CheckPointTypeDetailID == 54) {
                            BarcodeInfoRequest barcodeInfoRequest = new BarcodeInfoRequest();
                            barcodeInfoRequest.Barcode = Long.parseLong(txtBarCode.getText().toString());
                            String jsonData = JsonSerializerDeserializer.serialize(barcodeInfoRequest, true);
                            new BringBarcodeInfo().execute(jsonData);
                        } else {
                            String barcode = txtBarCode.getText().toString();
                            if (onHoldShipments.contains(barcode)) {
                                OnLineValidation onLineValidation = new OnLineValidation();
                                onLineValidation.setBarcode(barcode);
                                onLineValidation.setIsNoBayanNo(1);
                                showFlagsPopup(onLineValidation);
                            } else {
                                AddNewPiece();
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });

            Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
            intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!GlobalVar.GV().checkPermission(getActivity(), GlobalVar.PermissionType.Camera)) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else
                        startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                }
            });

            initViews();
            //initDialog();
        }

        return rootView;
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(BarCodeList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        initSwipe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        if (barcode.length() == 13 || barcode.length() == GlobalVar.ScanBarcodeLength)
                            txtBarCode.setText(barcode);
//                        GlobalVar.GV().MakeSound(GlobalVar.GV().context, R.raw.barcodescanned);
//                        if (txtBarCode.getText().toString().length() > 12)
//                            AddNewPiece();
                    }
                }

//                final Barcode barcode = data.getParcelableExtra("barcode");
//                txtBarCode.post(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        txtBarCode.setText(barcode.displayValue);
//
//                        if (txtBarCode.getText().toString().length() > 8)
//                            AddNewPiece();
//                    }
//                });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initViews();
        //initDialog();
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
                                    adapter.removeItem(position);
                                    lbTotal.setText(getString(R.string.lbCount) + BarCodeList.size());
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
//                else
//                {
//                    removeView();
//                    edit_position = position;
//                    alertDialog.setTitle(R.string.EditBarCode);
//                    txtBarCodePiece.setText(BarCodeList.get(position));
//                    alertDialog.show();
//                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.BLUE);
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

//    private void removeView()
//    {
//        if(view.getParent()!=null)
//        {
//            ((ViewGroup) view.getParent()).removeView(view);
//        }
//    }

    private void AddNewPiece() {

        if (!GlobalVar.GV().isValidBarcodeCons(txtBarCode.getText().toString())) {
            GlobalVar.GV().ShowSnackbar(rootView, "Wrong Barcode", GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
            return;
        }

        if (!GlobalVar.ValidateAutomacticDate(getContext())) {
            GlobalVar.RedirectSettings(getActivity());
            return;
        }
        if (BarCodeList.size() == 50) {
            ErrorAlert("Kindly save Scanned Data and Scan again...", 1);
            return;
        }

        if (!BarCodeList.contains(txtBarCode.getText().toString())) {
            //if (txtBarCode.getText().toString().length() == 13) {
            BarCodeList.add(0, txtBarCode.getText().toString());
            lbTotal.setText(getString(R.string.lbCount) + BarCodeList.size());
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
            txtBarCode.setText("");
            initViews();
            //}
        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }
    }

    private void ErrorAlert(String message, final int clear) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (clear == 0)
                            txtBarCode.setText("");
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("lbTotal", lbTotal.getText().toString());
        outState.putString("txtBarCode", txtBarCode.getText().toString());
        outState.putStringArrayList("BarCodeList", BarCodeList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            lbTotal.setText(savedInstanceState.getString("lbTotal"));
            txtBarCode.setText(savedInstanceState.getString("txtBarCode"));
            BarCodeList = savedInstanceState.getStringArrayList("BarCodeList");
        }
    }


//    private void initDialog()
//    {
//        alertDialog = new AlertDialog.Builder(this.getContext());
//        view = getLayoutInflater(null).inflate(R.layout.dialog_layout,null);
//
//        alertDialog.setView(view);
//        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if(add)
//                {
//                    add =false;
//                    adapter.addItem(txtBarCodePiece.getText().toString());
//                    dialog.dismiss();
//                }
//                else
//                {
//                    BarCodeList.set(edit_position,txtBarCodePiece.getText().toString());
//                    adapter.notifyDataSetChanged();
//                    dialog.dismiss();
//                }
//            }
//        });
//        txtBarCodePiece = (EditText)view.findViewById(R.id.txtWaybilll);
//    }

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
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILinkForHighValueAlarm + "GetBarcodeInfo");
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

                AddNewPiece();

                if (barcodeInfoResult.DecalaredValue >= 1000) {
                    try {
                        GlobalVar.GV().MakeSound(getContext(), R.raw.rto);
                    } catch (Exception e) {

                    }

                }
            }
        }
    }

    private void onBackpressed() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Exit Custom Screen")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        getActivity().finish();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /*** Riyam ***/
    public void setOnHoldShipments(List<String> onHoldShipments) {
        try {
            this.onHoldShipments = onHoldShipments;
        } catch (Exception ex) {
            Log.d("test", "test " + ex.toString());
        }
    }

    public void showFlagsPopup(final OnLineValidation onLineValidation) {
        try {
            if (onLineValidation != null) {

                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);

                final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getContext());
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                final TextView tvBarcode = dialogView.findViewById(R.id.tv_barcode);
                tvBarcode.setText("Piece #" + onLineValidation.getBarcode());
                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
                btnConfirm.setVisibility(View.VISIBLE);
                btnConfirm.setText("OK & Quit");

                if (onLineValidation.getIsNoBayanNo() == 1) {

                    LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_no_bayan);
                    llStopShipment.setVisibility(View.VISIBLE);

                    TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_no_bayan_header);
                    tvStopShipmentHeader.setText("Bayan Number");

                    TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_no_bayan_body);
                    tvStopShipmentBody.setText("The Shipment has no Bayan number.Kindly contact GateWay team");

                }

                final android.app.AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To avoid leaked window
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();

                            //Don't record if it's high value with no Bayan
                            if (onLineValidation.getIsNoBayanNo() == 1) {
                                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
                                AddNewPiece();
                                //GlobalVar.GV().ShowSnackbar(rootView, "Shipment has no Bayan number.Scan won't be recorded", GlobalVar.AlertType.Warning);
                                txtBarCode.getText().clear();
                            }

                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.d("test", "test " + e.toString());
        }
    }

    /*** Riyam - END ***/
}