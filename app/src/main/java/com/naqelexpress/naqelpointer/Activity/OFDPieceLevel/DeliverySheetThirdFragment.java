package com.naqelexpress.naqelpointer.Activity.OFDPieceLevel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class DeliverySheetThirdFragment
        extends Fragment {
    View rootView;
    TextView lbTotal;
    private EditText txtBarCode, txtBarCodePiece;
    public ArrayList<String> PieceBarCodeList = new ArrayList<>();
    public ArrayList<String> PieceBarCodeWaybill = new ArrayList<>();
    private DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();

    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    public ArrayList<String> pieceDenied = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Intent intent;
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.deliverysheetthirdfragment, container, false);

            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);

            txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);

            txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanBarcodeLength)});

            DBConnections dbConnections = new DBConnections(getContext(), null);
            if (GlobalVar.ValidateAutomacticDate(getContext())) {
                dbConnections.DeleteFacilityLoggedIn(getContext());
                dbConnections.DeleteExsistingLogin(getContext());
                dbConnections.DeleteAllSyncData(getContext());
                dbConnections.deleteDenied(getContext());
            }

            if (!GlobalVar.GV().getDeviceName().contains("TC25") && !GlobalVar.GV().getDeviceName().contains("TC26"))
                txtBarCode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (txtBarCode != null && (txtBarCode.getText().length() == 13 || txtBarCode.getText().length() == GlobalVar.ScanBarcodeLength)) {
                            if (!GlobalVar.GV().isValidBarcode(txtBarCode.getText().toString())) {
                                GlobalVar.GV().ShowSnackbar(rootView, "Wrong Barcode", GlobalVar.AlertType.Warning);
                                GlobalVar.GV().MakeSound(getActivity(), R.raw.wrongbarcodescan);
                                txtBarCode.setText("");
                                return;
                            }
                            if (!pieceDenied.contains(txtBarCode.getText().toString()))
                                new GetWaybillInfo().execute(txtBarCode.getText().toString());
                            else {
                                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
                                ShowAlertMessage("ON HOLD WAYBILL CONTACT YOU SUPERVISOR");
                            }
                        }
                    }
                });

            txtBarCode.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if (event.getAction() != KeyEvent.ACTION_DOWN)
                        return true;
                    else if (keyCode == KeyEvent.KEYCODE_BACK) {
                        onBackPressed();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (txtBarCode != null && (txtBarCode.getText().length() == 13
                                || txtBarCode.getText().length() == GlobalVar.ScanBarcodeLength)) {
                            if (!GlobalVar.GV().isValidBarcode(txtBarCode.getText().toString())) {
                                GlobalVar.GV().ShowSnackbar(rootView, "Wrong Barcode", GlobalVar.AlertType.Warning);
                                GlobalVar.GV().MakeSound(getActivity(), R.raw.wrongbarcodescan);
                                txtBarCode.setText("");
                                return true;
                            }
                            if (!pieceDenied.contains(txtBarCode.getText().toString()))
                                new GetWaybillInfo().execute(txtBarCode.getText().toString());
                            else {
                                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
                                ShowAlertMessage("ON HOLD WAYBILL CONTACT YOU SUPERVISOR");
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });
            intent = new Intent(this.getContext(), NewBarCodeScanner.class);

            Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
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


            if (!GlobalVar.GV().isFortesting) {
                if (GlobalVar.GV().istxtBoxEnabled(getContext())) {
                    btnOpenCamera.setVisibility(View.GONE);

                    if (!GlobalVar.GV().getDeviceName().contains("TC25") && !GlobalVar.GV().getDeviceName().contains("TC26")) {
                        txtBarCode.setKeyListener(null);
                        txtBarCode.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!GlobalVar.GV().checkPermission(getActivity(), GlobalVar.PermissionType.Camera)) {
                                    GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                                    GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                                } else
                                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                            }
                        });
                    } else {

                        GlobalVar.GV().disableSoftInputFromAppearing(txtBarCode);
                    }
                }
            }
            initViews();
            //initDialog();
        }

        ReadFromLocal();
        return rootView;
    }


    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(PieceBarCodeWaybill);
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
                        txtBarCode.setText(barcode);
//                        if (txtBarCode.getText().toString().length() > 8)
//                            AddNewPiece();
                    }
                }


            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initViews();

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
                                    lbTotal.setText(getString(R.string.lbCount) + PieceBarCodeList.size());
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
                } else {
                    removeView();
                    edit_position = position;
                    alertDialog.setTitle(R.string.EditBarCode);
                    txtBarCodePiece.setText(PieceBarCodeList.get(position));
                    alertDialog.show();
                }
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

    private void removeView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private void AddNewPiece(String WaybillNo) {
        if (!PieceBarCodeList.contains(txtBarCode.getText().toString())) {
            if (txtBarCode.getText().toString().length() == 13 || txtBarCode.getText().toString().length() == GlobalVar.ScanBarcodeLength) {
                PieceBarCodeWaybill.add(0, txtBarCode.getText().toString() + "-" + WaybillNo);
                PieceBarCodeList.add(0, txtBarCode.getText().toString());
                lbTotal.setText(getString(R.string.lbCount) + PieceBarCodeList.size());
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                txtBarCode.setText("");
                txtBarCode.requestFocus();
                initViews();
            }
        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtBarCode.setText(savedInstanceState.getString("txtBarCode"));
            PieceBarCodeList = savedInstanceState.getStringArrayList("PieceBarCodeList");
            lbTotal.setText(savedInstanceState.getString("lbTotal"));
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("txtBarCode", txtBarCode.getText().toString());
        outState.putStringArrayList("PieceBarCodeList", PieceBarCodeList);
        outState.putString("lbTotal", lbTotal.getText().toString());

    }

    private class GetWaybillInfo extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;
        String DomainURL = "";
        String isInternetAvailable = "";

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), "", "Please wait.", true);
            //f (GlobalVar.GV().GetDeviceVersion())
            DomainURL = GlobalVar.GV().GetDomainURL(getContext());
//            else
//                DomainURL = GlobalVar.GV().NaqelPointerAPILink;
        }

        @Override
        protected String doInBackground(String... params) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("BarCode", params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(DomainURL + "BringWaybillInfobyPiece");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                httpURLConnection.connect();

                dos = httpURLConnection.getOutputStream();
                httpURLConnection.getOutputStream();
                dos.write(jsonObject.toString().getBytes());

                ist = httpURLConnection.getInputStream();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return String.valueOf(buffer);
            } catch (Exception e) {
                isInternetAvailable = e.toString();
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

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            progressDialog = null;

            super.onPostExecute(String.valueOf(finalJson));
            if (finalJson != null) {
                try {
                    JSONObject jsonObject = new JSONObject(finalJson);
                    if (jsonObject.getBoolean("HasError") == false) {
                        AddNewWaybill(String.valueOf(jsonObject.getInt("WaybillNo")));
                        AddNewPiece(String.valueOf(jsonObject.getInt("WaybillNo")));
                    } else {

                        ShowAlertMessage(jsonObject.getString("ErrorMessage"));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(rootView, "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        GlobalVar.GV().SwitchoverDomain(getContext(), DomainURL);

                    }

                    GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.servererror), GlobalVar.AlertType.Error);
                }
            }
        }
    }

    public ArrayList<String> WaybillList = new ArrayList<>();

    private void AddNewWaybill(String WaybillNo) {

        //--Below one for 8 Digits old method
        // if (WaybillNo.length() > 8)
        //     WaybillNo = WaybillNo.substring(0, 8);

//        if (WaybillNo.toString().length() == 8) {
//            if (!WaybillList.contains(WaybillNo.toString())) {
//                WaybillList.add(0, WaybillNo.toString());
//            }
//        }
        // }
        //_____________________
        if (!WaybillList.contains(WaybillNo))
            WaybillList.add(0, WaybillNo);


    }

    private void ShowAlertMessage(String Message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(Message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        txtBarCode.setText("");
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void ReadFromLocal() {


        pieceDenied.clear();
        DBConnections dbConnections = new DBConnections(getContext(), null);
        Cursor result = dbConnections.Fill("select * from DeniedWaybills", getContext());
        try {
            if (result.getCount() > 0) {
                result.moveToFirst();

                do {

                    pieceDenied.add(result.getString(result.getColumnIndex("BarCode")));

                } while (result.moveToNext());
            } else {

            }

            result.close();

            result.close();
            dbConnections.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exit")
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
