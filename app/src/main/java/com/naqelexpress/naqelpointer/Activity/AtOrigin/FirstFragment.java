package com.naqelexpress.naqelpointer.Activity.AtOrigin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.AlertDialog;import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static android.app.Activity.RESULT_OK;

public class FirstFragment
        extends Fragment
        implements TextWatcher {
    View rootView;
    private EditText txtBarCode;
    TextView lbTotal;

    private DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();
    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Intent intent;

    private ArrayList<HashMap<String, String>> waybilldetails = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> waybillBardetails = new ArrayList<>();
    static ArrayList<HashMap<String, String>> Selectedwaybilldetails = new ArrayList<>();

    public ArrayList<String> validatewaybilldetails = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {
                rootView = inflater.inflate(R.layout.atoriginfirstfragment, container, false);
                lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);

                txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);
                final EditText employeid = (EditText) rootView.findViewById(R.id.empid);

                txtBarCode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (txtBarCode != null && txtBarCode.getText().length() == 9)
                            ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
                        //     AddNewPiece();
                    }
                });

                Button pickdata = (Button) rootView.findViewById(R.id.pickup);
                pickdata.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (employeid.getText().toString().replaceAll(" ", "").length() > 0) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("EmployID", employeid.getText().toString());

                                String jsonData = jsonObject.toString();

                                new GetPickUpData().execute(jsonData);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
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

                Selectedwaybilldetails.clear();
                waybillBardetails.clear();
                waybilldetails.clear();
                initViews();
                //initDialog();


            }

            return rootView;
        }
    }

    private void ValidateWayBill(String waybillno) {
        if (!validatewaybilldetails.contains(waybillno)) {
            for (int i = 0; i < waybilldetails.size(); i++) {
                if (waybillno.equals(waybilldetails.get(i).get("WaybillNo"))) {
                    Selectedwaybilldetails.add(waybilldetails.get(i));
                    GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
                    txtBarCode.setText("");
                    adapter.notifyDataSetChanged();
                    validatewaybilldetails.add(waybillno);
                    lbTotal.setText(getString(R.string.lbCount) + Selectedwaybilldetails.size());
                    break;
                }
            }
        } else
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(Selectedwaybilldetails);
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
                        //AddNewPiece();
                    }
                }
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
                    //GlobalVar.GV().ShowSnackbar(getActivity().getWindow().getDecorView().getRootView(), "Please Scan BarCode Correctly", GlobalVar.AlertType.Error);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Confirm Deleting")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    removeBarCode(position);
                                    adapter.removeItem(position);
                                    lbTotal.setText(getString(R.string.lbCount) + Selectedwaybilldetails.size());
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
//                else {
//                    removeView();
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

    private void removeBarCode(int position) {


        String waybill = FirstFragment.Selectedwaybilldetails.get(position).get("WaybillNo");
        HashSet<Integer> set = new HashSet<>();

        for (int j = 0; j < SecondFragment.SelectedwaybillBardetails.size(); j++) {
            if (waybill.
                    equals(SecondFragment.SelectedwaybillBardetails.get(j).get("WaybillNo"))) {
                set.add(j);
                // SingleItem.SelectedwaybillBardetails.remove(j);

            }
        }

        Set<Integer> sorted = new TreeSet<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });
        sorted.addAll(set);


        for (int j = 0; j < set.size(); j++) {


            SecondFragment.SelectedwaybillBardetails.remove(set.iterator());


        }

        SecondFragment.adapter.notifyDataSetChanged();


    }


//    private void removeView() {
//        if (view.getParent() != null) {
//            ((ViewGroup) view.getParent()).removeView(view);
//        }
//    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private class GetPickUpData extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;
        String isInternetAvailable = "";
        String DomainURL = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            //progressDialog.setMax(100);
            progressDialog.setMessage("Please wait.");
            progressDialog.setTitle("Downloading PickUp Data");
            progressDialog.show();
            ;
            DomainURL = GlobalVar.GV().GetDomainURL(getContext());
            //progressDialog = ProgressDialog.show(getActivity().getApplicationContext(), "Please wait.", "Downloading PickUp Data.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                DomainURL = GlobalVar.GV().GetDomainURL(getContext());
                URL url = new URL(DomainURL + "GetArrivedAtOriginData");
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
            if (finalJson != null) {
                waybillBardetails.clear();
                waybilldetails.clear();
                Selectedwaybilldetails.clear();
                SecondFragment.SelectedwaybillBardetails.clear();

                try {
                    JSONObject job = new JSONObject(finalJson);
                    JSONArray jsonArray = job.getJSONArray("Pickup");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject = jsonArray.getJSONObject(i);
                        HashMap<String, String> tempwaybill = new HashMap<>();
                        tempwaybill.put("WaybillNo", jsonObject.getString("WaybillNo"));
                        tempwaybill.put("PieceCount", String.valueOf(jsonObject.getInt("PieceCount")));
                        tempwaybill.put("EmployID", String.valueOf(jsonObject.getInt("EmployID")));
                        tempwaybill.put("UserID", String.valueOf(jsonObject.getInt("UserID")));
                        tempwaybill.put("IsSync", "false");
                        tempwaybill.put("StationID", "StationID");
                        waybilldetails.add(tempwaybill);
                    }
                    JSONArray jsonArray1 = job.getJSONArray("PickupBarCode");

                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jsonObject = jsonArray1.getJSONObject(i);
                        HashMap<String, String> tempwaybill = new HashMap<>();
                        tempwaybill.put("WaybillNo", jsonObject.getString("WaybillNo"));
                        tempwaybill.put("BarCode", jsonObject.getString("BarCode"));
                        tempwaybill.put("IsSync", "false");

                        waybillBardetails.add(tempwaybill);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                // GlobalVar.GV().ShowSnackbar(rootView, "No data with Current Employee ID", GlobalVar.AlertType.Error);
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
            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("validatewaybilldetails", validatewaybilldetails);
        outState.putSerializable("waybilldetails", waybilldetails);
        outState.putSerializable("waybillBardetails", waybillBardetails);
        outState.putSerializable("Selectedwaybilldetails", Selectedwaybilldetails);
        outState.putString("txtBarCode", txtBarCode.getText().toString());
        outState.putString("lbTotal", lbTotal.getText().toString());

        //outState.putStringArrayList("ShipmentBarCodeList", ShipmentBarCodeList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            validatewaybilldetails = savedInstanceState.getStringArrayList("validatewaybilldetails");
            waybilldetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybilldetails");
            waybillBardetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybillBardetails");
            Selectedwaybilldetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("Selectedwaybilldetails");

        }
    }

}