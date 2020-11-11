package com.naqelexpress.naqelpointer.Activity.OFDPieceLevel;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.OnlineValidation.OnLineValidation;
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
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class DeliverySheetThirdFragment extends Fragment {

    View rootView;
    TextView lbTotal;
    private EditText txtBarCode, txtBarCodePiece;
    private DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();

    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false , isPiecesAvailable;
    private Context mContext;

    public ArrayList<String> PieceBarCodeList = new ArrayList<>();
    public ArrayList<String> PieceBarCodeWaybill = new ArrayList<>();
    public ArrayList<String> pieceDenied = new ArrayList<>();

    private DBConnections dbConnections = new DBConnections(getContext(), null);
    private List<OnLineValidation> onLineValidationList = new ArrayList<>();


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

            DBConnections dbConnections = new DBConnections(getContext(), null);
            if (GlobalVar.ValidateAutomacticDate(getContext())) {
                dbConnections.DeleteFacilityLoggedIn(getContext());
                dbConnections.DeleteExsistingLogin(getContext());
                dbConnections.DeleteAllSyncData(getContext());
                dbConnections.deleteDenied(getContext());
            }

            txtBarCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txtBarCode != null && txtBarCode.getText().length() == 13) {
                        if (!pieceDenied.contains(txtBarCode.getText().toString()))
                            new GetWaybillInfo().execute(txtBarCode.getText().toString());
                        else {
                            GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
                            ShowAlertMessage("ON HOLD WAYBILL CONTACT YOU SUPERVISOR");
                        }
                    }
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

                    if (!GlobalVar.GV().getDeviceName().contains("TC25")) {
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
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
                                    //todo riyam added. Once piece is deleted and scanned again
                                    // It says already exists.
                                    PieceBarCodeList.remove(position);
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
            if (txtBarCode.getText().toString().length() == 13) {
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
                         String barcode = txtBarCode.getText().toString();
                         //TODO Riyam TH App Only
                        if (isValidPieceBarcode(barcode)) {
                            AddNewWaybill(String.valueOf(jsonObject.getInt("WaybillNo")));
                            AddNewPiece(String.valueOf(jsonObject.getInt("WaybillNo")));
                        } else {
                            showDialog(getOnLineValidationPiece(barcode) , String.valueOf(jsonObject.getInt("WaybillNo")));
                        }

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

        if (WaybillNo.length() > 8)
            WaybillNo = WaybillNo.substring(0, 8);

        if (WaybillNo.toString().length() == 8) {
            if (!WaybillList.contains(WaybillNo.toString())) {
                WaybillList.add(0, WaybillNo.toString());
            }
        }
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


    private boolean isValidPieceBarcode(String pieceBarcode) {
        boolean isValid = true;
        try {
            OnLineValidation onLineValidationLocal = dbConnections.getPieceInformationByBarcode(pieceBarcode, getContext());
            OnLineValidation onLineValidation = new OnLineValidation();

            if (onLineValidationLocal != null) {

                if (onLineValidationLocal.getDestID() != GlobalVar.GV().StationID) {
                    onLineValidation.setIsWrongDest(1);
                    onLineValidation.setDestID(onLineValidationLocal.getDestID());
                    isValid = false;
                }

                if (onLineValidationLocal.getIsMultiPiece() == 1) {
                    onLineValidation.setIsMultiPiece(1);
                    isValid = false;
                }

                if (onLineValidationLocal.getIsStopShipment() == 1) {
                    onLineValidation.setIsStopShipment(1);
                    isValid = false;
                }

                if (onLineValidationLocal.getIsRTORequest() == 1) {
                    onLineValidation.setIsRTORequest(1);
                    isValid = false;
                }

                if (onLineValidationLocal.getIsRelabel() == 1) {
                    onLineValidation.setIsRelabel(1);
                    isValid = false;
                }

                if (!isValid) {
                    onLineValidation.setPieceBarcode(pieceBarcode);
                    onLineValidationList.add(onLineValidation);
                }
                return isValid;
            }

        } catch (Exception e) {
            Log.d("test" , "isValidPieceBarcode " + e.toString());
        }
        return isValid;
    }

    public void showDialog(final OnLineValidation pieceDetails , final String waybill) {
        final View dialogView;
        try {
            if (pieceDetails != null) {
                final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getContext());
                LayoutInflater inflater = this.getLayoutInflater();
                dialogView = inflater.inflate(R.layout.test, null);
                dialogBuilder.setView(dialogView);

                TextView tvBarcode = dialogView.findViewById(R.id.tv_barcode);
                tvBarcode.setText("Piece #" + pieceDetails.getPieceBarcode());


                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
                btnConfirm.setVisibility(View.VISIBLE);
                btnConfirm.setText("OK");


                if (pieceDetails.getIsWrongDest() == 1) {

                    String stationName = "";
                    try {
                        Station station = dbConnections.getStationByID(pieceDetails.getDestID() , mContext);
                        if (station != null)
                            stationName = station.Name;
                        else
                            Log.d("test" , "Station is null");
                    } catch (Exception e) { Log.d("test" , "showDialog " + e.toString());}


                    LinearLayout llWrongDest = dialogView.findViewById(R.id.ll_wrong_dest);
                    llWrongDest.setVisibility(View.VISIBLE);

                    TextView tvWrongDestHeader = dialogView.findViewById(R.id.tv_wrong_dest_header);
                    tvWrongDestHeader.setText("Wrong Destination");

                    TextView tvWrongDestBody = dialogView.findViewById(R.id.tv_wrong_dest_body);
                    tvWrongDestBody.setText("Shipment destination station : " + stationName);

                }

                if (pieceDetails.getIsMultiPiece() == 1) {

                    LinearLayout llMultiPiece = dialogView.findViewById(R.id.ll_is_multi_piece);
                    llMultiPiece.setVisibility(View.VISIBLE);

                    TextView tvMultiPieceHeader = dialogView.findViewById(R.id.tv_multiPiece_header);
                    tvMultiPieceHeader.setText("Multi Piece");

                    TextView tvMultiPieceBody = dialogView.findViewById(R.id.tv_multiPiece_body);
                    tvMultiPieceBody.setText("Please confirm all pcs are available.");

                    RadioGroup rgMultiPiece = dialogView.findViewById(R.id.rg_multi_piece);
                    rgMultiPiece.setVisibility(View.VISIBLE);
                    radioGroupCheckListener(dialogView);
                }

                if (pieceDetails.getIsStopShipment() == 1) {

                    LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_stop_shipment);
                    llStopShipment.setVisibility(View.VISIBLE);

                    TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_stop_shipment_header);
                    tvStopShipmentHeader.setText("Stop Shipment");

                    TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_stop_shipment_body);
                    tvStopShipmentBody.setText("Stop shipment.Please Hold.");

                }

                if (pieceDetails.getIsRTORequest() == 1) {

                    LinearLayout llRto = dialogView.findViewById(R.id.ll_is_rto);
                    llRto.setVisibility(View.VISIBLE);

                    TextView tvRTOHeader = dialogView.findViewById(R.id.tv_rto_header);
                    tvRTOHeader.setText("RTO Request");

                    TextView tvRTOBody = dialogView.findViewById(R.id.tv_rto_body);
                    tvRTOBody.setText("RTO Request.");
                }

                if (pieceDetails.getIsDeliveryRequest() == 1) {

                    LinearLayout llDeliveryReq = dialogView.findViewById(R.id.ll_is_delivery_req);
                    llDeliveryReq.setVisibility(View.VISIBLE);

                    TextView tvDeliveryRequestHeader = dialogView.findViewById(R.id.tv_delivery_req_header);
                    tvDeliveryRequestHeader.setText("Delivery Request");

                    TextView tvDeliveryRequestBody = dialogView.findViewById(R.id.tv_delivery_req_body);
                    tvDeliveryRequestBody.setText("Delivery Request.");
                }

              /*  if (pieceDetails.getIsRelabel() == 1) {
                    LinearLayout llIsRelabel = dialogView.findViewById(R.id.ll_is_relabel);
                    llIsRelabel.setVisibility(View.VISIBLE);
                }*/

                final android.app.AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To avoid leaked window
                        if (alertDialog != null && alertDialog.isShowing()) {
                            if (pieceDetails.getIsMultiPiece() == 1) {
                                if (isRadioButtonSelected(dialogView)) {
                                    if (isPiecesAvailable){
                                        Log.d("test" , "pcs available");
                                        pieceDetails.setIsPiecesAvailable(isPiecesAvailable? 1 : 0);
                                        if (pieceDetails.getIsPiecesAvailable() != 0) { // Record shouldn't be captured if not all pieces are available
                                            AddNewWaybill(pieceDetails.getPieceBarcode());
                                            AddNewPiece(waybill);
                                            alertDialog.dismiss();
                                        }
                                    } else {
                                        Log.d("test" , "pcs not available");
                                        Toast.makeText(getContext() ,"Pieces not available. Scan won't be recorded" , Toast.LENGTH_LONG ).show();
                                        txtBarCode.getText().clear();
                                        alertDialog.dismiss();
                                     }
                                 } else {
                                     if (getContext() != null)
                                        Toast.makeText(getContext() ,"Kindly confirm all pieces are available." , Toast.LENGTH_LONG ).show();
                                }
                            } else {
                                AddNewWaybill(pieceDetails.getPieceBarcode());
                                AddNewPiece(waybill);
                                alertDialog.dismiss();
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.d("test" , "showDialog " + e.toString());
        }
    }

    private OnLineValidation getOnLineValidationPiece (String barcode) {
        try {
            for (OnLineValidation pieceDetail : onLineValidationList) {
                if (pieceDetail.getPieceBarcode().equals(barcode))
                    return pieceDetail;
            }

        } catch (Exception e) {
            Log.d("test" , "getOnLineValidationPiece " + e.toString());
        }
        return null;
    }

    private boolean isRadioButtonSelected (View v) {
        RadioGroup g = v.findViewById(R.id.rg_multi_piece);
        return  g.getCheckedRadioButtonId() != -1 ;
    }

    private void setIsPiecesAvailable (boolean isPcsAvailable) {
        isPiecesAvailable = isPcsAvailable;
    }

    private void radioGroupCheckListener(View v) {
        RadioGroup multiPiecesRadioGroup = v.findViewById(R.id.rg_multi_piece);
        multiPiecesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_yes:
                        setIsPiecesAvailable(true);
                        break;
                    case R.id.rb_no:
                        setIsPiecesAvailable(false);
                        break;

                }
            }
        });
    }
}
