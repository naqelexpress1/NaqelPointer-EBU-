package com.naqelexpress.naqelpointer.Activity.PickupPieceLevel;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.Service;
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
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PickUpSecondFragment
        extends Fragment {
    View rootView;
    TextView lbTotal;
    public EditText txtBarCode;
    public ArrayList<String> PickUpBarCodeList = new ArrayList<>();
    public DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();

    private AlertDialog.Builder alertDialog;
    private EditText txtBarCodePiece;
    private int edit_position;
    private View view;
    private boolean add = false;
    private boolean fetchdata = false;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.pickupsecondfragment, container, false);
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);

            txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);
//            txtBarCode.setKeyListener(null);


            txtBarCode.setClickable(true);
            txtBarCode.setFocusable(true);
            if (!GlobalVar.GV().getDeviceName().contains("TC25") && !GlobalVar.GV().getDeviceName().contains("TC26"))
                txtBarCode.setInputType(InputType.TYPE_NULL);
            // txtBarCode.setFocusableInTouchMode(true);

//
//            txtBarCode.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                    GlobalVar.hideKeyboardFrom(rootView.getContext(), rootView.getRootView());
//                    return false;
//                }
//            });

            txtBarCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.KEYCODE_ENTER) {
                        String barcode = GlobalVar.GV().ReplaceBarcodeCharcater(txtBarCode.getText().toString());
                        if (txtBarCode != null && barcode.length() == 13
                                || barcode.length() == GlobalVar.ScanBarcodeLength) {
                            if (!fetchdata) {
                                if (barcode.startsWith("6"))
                                    new GetWaybillInfo().execute(barcode);
                                else
                                    AddNewPiece();
                            } else
                                AddNewPiece();
                        }
                        return true;
                    } else {
                        return false;
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
                        if (!fetchdata) {
                            String barcode = GlobalVar.GV().ReplaceBarcodeCharcater(txtBarCode.getText().toString());

                            if (barcode.startsWith("6"))
                                new GetWaybillInfo().execute(barcode);
                            else
                                AddNewPiece();
                        } else
                            AddNewPiece();


                        return true;
                    }
                    return false;
                }
            });

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
//                    if (txtBarCode != null && txtBarCode.getText().length() == 13 || txtBarCode.getText().length() == GlobalVar.ScanBarcodeLength) {
//                        if (!fetchdata) {
//                            if (txtBarCode.getText().toString().startsWith("6"))
//                                new GetWaybillInfo().execute(txtBarCode.getText().toString());
//                            else
//                                AddNewPiece();
//                        } else
//                            AddNewPiece();
//                    }
//                }
//            });

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
            txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.BarcodeLength)});
            initViews();
            // initDialog();
        }

        return rootView;
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(PickUpBarCodeList);
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
                        if (barcode.length() == 13 || barcode.length() == GlobalVar.ScanBarcodeLength) {
                            txtBarCode.setText(barcode);
                            AddNewPiece();
                        }
//                        GlobalVar.GV().MakeSound(GlobalVar.GV().context, R.raw.barcodescanned);
//                        if (txtBarCode.getText().toString().length() > 12)
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
        // initDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        // isAppOnTop();
    }

    @Override
    public void onResume() {
        super.onResume();
        //isAppOnTop();
    }

    private boolean isAppOnTop() {
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks;
        tasks = am.getRunningTasks(1);
        ActivityManager.RunningTaskInfo running = tasks.get(0);
        if (running.topActivity.getPackageName().equals("com.naqelexpress.naqelpointer")) {
            return true;
        } else {
            return false;
        }
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
                                    char startwith = PickUpBarCodeList.get(position).charAt(0);
                                    adapter.removeItem(position);
                                    lbTotal.setText(getString(R.string.lbCount) + PickUpBarCodeList.size());
                                    if (PickUpBarCodeList.size() == 0) {
                                        if (startwith == '6') {
                                            getActivity().finish();
                                        }
                                    }
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
                    txtBarCodePiece.setText(PickUpBarCodeList.get(position));
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

    boolean prvCharAt = false;

    private void AddNewPiece() {

        String barcode = GlobalVar.GV().ReplaceBarcodeCharcater(txtBarCode.getText().toString());


        if (barcode.length() == 13 || barcode.length() == GlobalVar.ScanBarcodeLength) {


            txtBarCode.setFocusable(true);
            if (!GlobalVar.GV().isValidBarcode(barcode)) {
                GlobalVar.GV().ShowSnackbar(rootView, "Wrong Barcode", GlobalVar.AlertType.Warning);
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
                txtBarCode.setText("");

                return;
            }

            GlobalVar.hideKeyboardFrom(getContext(), rootView);
            String preBarcode = "";

            prvCharAt = true;

            for (int i = 0; i < PickUpBarCodeList.size(); i++) {


                if (barcode.charAt(0) == '6') {
                    if (PickUpBarCodeList.get(i).charAt(0) != '6') {
                        ShowAlertMessage("your piece is mismatch with previos one. " + PickUpBarCodeList.get(i) + " " + preBarcode);
//                    allowtosave = false;
                        return;
                    }
                }
//            if (PickUpBarCodeList.get(i).charAt(0) != txtBarCode.getText().toString().charAt(0)) {
//                ShowAlertMessage("your piece is mismatch with previos one. " + PickUpBarCodeList.get(i) + " " + preBarcode);
//                allowtosave = false;
//                return;
//
//            }
            }

            if (fetchdata)
                if (!PieceLists.contains(barcode)) {
                    ShowAlertMessage("your piece is not belongs with this Waybill(" + Waybill + ")");
                    return;
                }
            if (!PickUpBarCodeList.contains(barcode)) {
                if (barcode.length() == 13 || barcode.length() == GlobalVar.ScanBarcodeLength) {
                    PickUpBarCodeList.add(0, barcode);
                    lbTotal.setText(getString(R.string.lbCount) + PickUpBarCodeList.size());
                    GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                    txtBarCode.setText("");
                    initViews();
                }
            } else {
                GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
                txtBarCode.setText("");
            }
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtBarCode", txtBarCode.getText().toString());
        outState.putString("lbTotal", lbTotal.getText().toString());
        outState.putStringArrayList("PickUpBarCodeList", PickUpBarCodeList);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtBarCode.setText(savedInstanceState.getString("txtBarCode"));
            lbTotal.setText(savedInstanceState.getString("lbTotal"));
            PickUpBarCodeList = savedInstanceState.getStringArrayList("PickUpBarCodeList");
            initViews();
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
//                    PickUpBarCodeList.set(edit_position,txtBarCodePiece.getText().toString());
//                    adapter.notifyDataSetChanged();
//                    dialog.dismiss();
//                }
//            }
//        });
//        txtBarCodePiece = (EditText)view.findViewById(R.id.txtWaybilll);
//    }


    private void ShowAlertMessage(String Message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name));
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

    public ArrayList<String> PieceLists = new ArrayList<>();
    String Waybill = "";

    private class GetWaybillInfo extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), "", "Please wait.", true);
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
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringWaybillInfobyPiecePickup");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
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
                if (!prvCharAt) {
                    try {

                        JSONObject jsonObject = new JSONObject(finalJson);
                        if (jsonObject.getBoolean("HasError") == false) {
                            fetchdata = true;
                            Waybill = String.valueOf(jsonObject.getInt("WayBillNo"));
//                        PickUpFirstFragment waybillinfo = new PickUpFirstFragment();
                            PickUpFirstFragment.txtWaybillNo.setText(String.valueOf(jsonObject.getInt("WayBillNo")));
                            PickUpFirstFragment.txtClientID.setText(String.valueOf(jsonObject.getInt("ClientID")));
                            PickUpFirstFragment.txtPiecesCount.setText(String.valueOf(jsonObject.getInt("PieceCount")));
                            PickUpFirstFragment.txtWeight.setText(String.valueOf(jsonObject.getInt("Weight")));
                            if (jsonObject.getString("RefNo") != null && !jsonObject.getString("RefNo").equals("null"))
                                PickUpFirstFragment.txtRefNo.setText(String.valueOf(jsonObject.getString("RefNo")));

                            HashMap<String, String> temp = new HashMap<>();
                            temp.put("LoadTypeID", String.valueOf(jsonObject.get("LoadTypeID")));
                            temp.put("Name", jsonObject.getString("LoadType"));

                            PickUpFirstFragment.clientdetails.add(temp);
                            PickUpFirstFragment.adapter.notifyDataSetChanged();

                            JSONArray jsonArray = jsonObject.getJSONArray("BarCodes");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo = jsonArray.getJSONObject(i);
                                PieceLists.add(jo.getString("BarCode"));
                            }

                            if (PickUpFirstFragment.StationList.size() > 0) {
                                int regionStationID = PickUpFirstFragment.StationList.indexOf(jsonObject.getInt("Origin"));
                                int DestStationID = PickUpFirstFragment.StationList.indexOf(jsonObject.getInt("Destination"));
                                PickUpFirstFragment.txtOrigin.setText(PickUpFirstFragment.StationNameList.get(regionStationID));
                                PickUpFirstFragment.txtDestination.setText(PickUpFirstFragment.StationNameList.get(DestStationID));
                                PickUpFirstFragment.OriginID = jsonObject.getInt("Origin");
                                PickUpFirstFragment.DestinationID = jsonObject.getInt("Destination");
                            }

                            AddNewPiece();
                        } else {

                            ShowAlertMessage(jsonObject.getString("ErrorMessage"));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    AddNewPiece();
            }
        }
    }


    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exit PickUp")
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