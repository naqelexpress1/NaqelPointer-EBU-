package com.naqelexpress.naqelpointer.Activity.NightStock;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.utils.utilities;

import java.util.ArrayList;


public class ScanShipmentFragment extends Fragment {
    View rootView;
    public EditText txtWaybillNo, txtbinlocation;
    TextView lbTotal;
    public ArrayList<String> WaybillList = new ArrayList<>();
    private RecyclerView recyclerView;
    public DataAdapter adapter;

//    protected TextWatcher textWatcher = new TextWatcher() {
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            // your logic here
//            if (txtWaybillNo != null && txtWaybillNo.getText().length() >= 8)
//                //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
//                setTxtWaybillNo();
//
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            // your logic here
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            // your logic here
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_scan_shipment, container, false);


            txtWaybillNo = (EditText) rootView.findViewById(R.id.txtWaybilll);
//            txtWaybillNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanWaybillLength)});

            txtbinlocation = (EditText) rootView.findViewById(R.id.binlocation);

            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);

            Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
            btnOpenCamera.setVisibility(View.GONE);
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

            txtWaybillNo.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if (event.getAction() != KeyEvent.ACTION_DOWN)
                        return true;
                    else if (keyCode == KeyEvent.KEYCODE_BACK) {
                        onBackpressed();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                            setTxtWaybillNo(txtWaybillNo.getText().toString());
                        if (txtWaybillNo != null && txtWaybillNo.getText().toString().length() >= 8)
                            setTxtWaybillNo();
                        return true;
                    }
                    return false;
                }
            });

//            txtWaybillNo.addTextChangedListener(new TextWatcher() {
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
////                    if (txtWaybillNo != null && txtWaybillNo.getText().toString().length() == 8 ||
////                            txtWaybillNo.getText().toString().length() == 9)
////                        AddNewWaybill();
//                    if (txtWaybillNo != null && txtWaybillNo.getText().length() >= 8)
//                        //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
//                        setTxtWaybillNo();
//                }
//            });

        }
        return rootView;
    }

    private void setTxtWaybillNo() {

        String barcode = txtWaybillNo.getText().toString();
        utilities utilities = new utilities();
        String nbarcode = utilities.findwaybillno(barcode);
        txtWaybillNo.setText(nbarcode);
        AddNewWaybill8and9(nbarcode);

//        // txtWaybilll.removeTextChangedListener(textWatcher);
//        if (barcode.length() >= 8 && GlobalVar.WaybillNoStartSeries.contains(barcode.substring(0, 1))) {
////            AddNewWaybill8and9(barcode.substring(0, 8));
//
//            //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
//
//        } else if (barcode.length() >= 8) {
////            AddNewWaybill8and9(barcode.substring(0, GlobalVar.ScanWaybillLength));
//            AddNewWaybill8and9(barcode);
//            //txtBarCode.setText(barcode.substring(0, GlobalVar.ScanWaybillLength));
//            //ValidateWayBill(txtBarCode.getText().toString().substring(0, GlobalVar.ScanWaybillLength));
//        }


        //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));


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
                        txtWaybillNo.setText(barcode);
                    }
                }
            }
        }
    }

    private void AddNewWaybill8and9(String WaybillNo) {
//        String WaybillNo = txtWaybillNo.getText().toString();
//        if (WaybillNo.length() > 8)
//            WaybillNo = WaybillNo.substring(0, 8);
        if (WaybillNo.toString().length() >=8) {
            if (!WaybillList.contains(WaybillNo.toString())) {
                WaybillList.add(0, WaybillNo.toString());
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                lbTotal.setText(getString(R.string.lbCount) + WaybillList.size());
                txtWaybillNo.setText("");
                initViews();
            } else {
                GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
                txtWaybillNo.setText("");
            }
        }
    }

    private void AddNewWaybill() {
        String WaybillNo = txtWaybillNo.getText().toString();
        if (WaybillNo.length() > 8)
            WaybillNo = WaybillNo.substring(0, 8);
        if (WaybillNo.toString().length() == 8) {
            if (!WaybillList.contains(WaybillNo.toString())) {
                WaybillList.add(0, WaybillNo.toString());
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                lbTotal.setText(getString(R.string.lbCount) + WaybillList.size());
                txtWaybillNo.setText("");
                initViews();
            } else {
                GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
                txtWaybillNo.setText("");
            }
        }
    }


    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.shipmentBarCodes);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(WaybillList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        initSwipe();
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
                                    lbTotal.setText(getString(R.string.lbCount) + WaybillList.size());
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

    private void onBackpressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exit Night Stock")
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
