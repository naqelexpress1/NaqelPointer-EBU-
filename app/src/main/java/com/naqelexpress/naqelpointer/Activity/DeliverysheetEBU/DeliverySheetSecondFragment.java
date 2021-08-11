package com.naqelexpress.naqelpointer.Activity.DeliverysheetEBU;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static android.app.Activity.RESULT_OK;

public class DeliverySheetSecondFragment
        extends Fragment {
    View rootView;
    private EditText txtWaybillNo, txtBarCodePiece;
    TextView lbTotal;
    public ArrayList<String> WaybillList = new ArrayList<>();
    private DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();
    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    SpinnerDialog spinnerDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        //final Intent intent;
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.deliverysheetsecondfragment, container, false);
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);

            txtWaybillNo = (EditText) rootView.findViewById(R.id.txtWaybilll);
            txtWaybillNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanWaybillLength)});

            txtWaybillNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
//                    if (txtWaybillNo != null && (txtWaybillNo.getText().toString().length() == 8 ||
//                            txtWaybillNo.getText().toString().length() == 9)
//                    )
//                        AddNewWaybill();
                    if (txtWaybillNo != null && txtWaybillNo.getText().length() >= 8)
                        // ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
                        setTxtWaybillNo();
                }
            });

            //intent = new Intent(getContext().getApplicationContext(), BarcodeScan.class);
            //intent = new Intent(getActivity().getApplicationContext(), NewBarCodeScanner.class);
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

            Button btnSelect = (Button) rootView.findViewById(R.id.btnSelect);
            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (GlobalVar.GV().optimizedOutOfDeliveryShipmentList != null && GlobalVar.GV().optimizedOutOfDeliveryShipmentList.size() > 0) {
                            if (txtWaybillNo.getText().toString().replace(" ", "").length() > 0) {

                                spinnerDialog = new SpinnerDialog(getActivity(), GlobalVar.GV().optimizedOutOfDeliveryShipmentList, "Select or Search Shipment No", R.style.DialogAnimations_SmileWindow);
                                spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                    @Override
                                    public void onClick(String item, int position) {
                                        String waybillNo = GlobalVar.GV().optimizedOutOfDeliveryShipmentList.get(position);
                                        txtWaybillNo.setText(GlobalVar.GV().optimizedOutOfDeliveryShipmentList.get(position));
                                        GlobalVar.GV().optimizedOutOfDeliveryShipmentList.remove(position);

                                    }
                                });
                            } else
                                GlobalVar.GV().ShowSnackbar(rootView, "Please enter the waybill correctly", GlobalVar.AlertType.Warning);
                        } else
                            GlobalVar.GV().ShowSnackbar(rootView, "Please enter manually", GlobalVar.AlertType.Warning);


                        spinnerDialog.showSpinerDialog(true);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            });

            initViews();
            //initDialog();
        }


        return rootView;
    }

    private void setTxtWaybillNo() {

        String barcode = txtWaybillNo.getText().toString();
        if (barcode.length() >= 8 && GlobalVar.WaybillNoStartSeries.contains(barcode.substring(0, 1))) {
            //txtBarCode.setText(barcode.substring(0, 8));
            //AddNewWaybill();//(txtWaybillNo.getText().toString().substring(0, 8));
            AddNewWaybill8and9(txtWaybillNo.getText().toString().substring(0, 8));

        } else if (barcode.length() >= GlobalVar.ScanWaybillLength) {
            //txtBarCode.setText(barcode.substring(0, GlobalVar.ScanWaybillLength));
            //ValidateWayBill(txtWaybillNo.getText().toString().substring(0, GlobalVar.ScanWaybillLength));
            AddNewWaybill8and9(txtWaybillNo.getText().toString().substring(0, GlobalVar.ScanWaybillLength));
        }

        //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));


    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(WaybillList);
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
                        if (barcode.length() > 8)
                            barcode = barcode.substring(0, 8);
                        txtWaybillNo.setText(barcode);
//                        AddNewWaybill();
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
        // initDialog();
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
                                    removeWaybillPieceBarCode(WaybillList.get(position));
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
                } else {
                    removeView();
                    edit_position = position;
                    alertDialog.setTitle(R.string.EditBarCode);
                    txtBarCodePiece.setText(WaybillList.get(position));
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
                        p.setColor(Color.RED);
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

    private void AddNewWaybill8and9(String WaybillNo) {
        // String WaybillNo = txtWaybillNo.getText().toString();
        //if (WaybillNo.length() > 8)
        //  WaybillNo = WaybillNo.substring(0, 8);

        if (WaybillNo.toString().length() >= 8) {
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
//    @SuppressLint("RestrictedApi")
//    private void initDialog() {
//        alertDialog = new AlertDialog.Builder(this.getContext());
//        view = getLayoutInflater(null).inflate(R.layout.dialog_layout, null);
//
//        alertDialog.setView(view);
//        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (add) {
//                    add = false;
//                    adapter.addItem(txtBarCodePiece.getText().toString());
//                    dialog.dismiss();
//                } else {
//                    WaybillList.set(edit_position, txtBarCodePiece.getText().toString());
//                    adapter.notifyDataSetChanged();
//                    dialog.dismiss();
//                }
//            }
//        });
//        txtBarCodePiece = (EditText) view.findViewById(R.id.txtWaybilll);
//    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtWaybillNo.setText(savedInstanceState.getString("txtWaybillNo"));
            WaybillList = savedInstanceState.getStringArrayList("WaybillList");
            lbTotal.setText(savedInstanceState.getString("lbTotal"));
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("txtWaybillNo", txtWaybillNo.getText().toString());
        outState.putStringArrayList("WaybillList", WaybillList);
        outState.putString("lbTotal", lbTotal.getText().toString());

    }

    private void removeWaybillPieceBarCode(String Waybillno) {

        HashSet<Integer> removewaybill = new HashSet<>();
        ArrayList<Integer> reversewaybill = new ArrayList<>();
        for (int i = 0; i < DeliverySheetActivity.barcode.size(); i++) {
            String waybillno = DeliverySheetActivity.barcode.get(i).get("WaybillNo");
            String piececode = DeliverySheetActivity.barcode.get(i).get("WaybillNo");
            if (Waybillno.equals(waybillno)) {
                String rmwaybill = DeliverySheetActivity.barcode.get(i).get("WaybillNo");
                DeliverySheetActivity.waybillno.remove(rmwaybill);
                DeliverySheetActivity.barcodelist.remove(piececode);
                removewaybill.add(i);
            }
        }
        reversewaybill.addAll(removewaybill);
        Collections.reverse(reversewaybill);

        for (int i = 0; i < reversewaybill.size(); i++) {
            int rmpos = reversewaybill.get(i);
            DeliverySheetActivity.barcode.remove(rmpos);
        }

    }
}