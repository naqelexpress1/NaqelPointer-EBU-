package com.naqelexpress.naqelpointer.Activity.MultiDelivery;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.utils.utilities;

import java.util.ArrayList;

public class MultiDeliverySecondFragment
        extends Fragment {
    View rootView;
    private EditText txtWaybilll, txtWaybillPiece;
    TextView lbTotal;
    public static ArrayList<String> WaybillList = new ArrayList<>();
    private com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();
    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    SpinnerDialog spinnerDialog;

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
        final Intent intent;
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.multideliverysecondfragment, container, false);
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);

            txtWaybilll = (EditText) rootView.findViewById(R.id.txtWaybilll);
//            txtWaybilll.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanWaybillLength)});
//            txtWaybilll.addTextChangedListener(textWatcher);

            txtWaybilll.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txtWaybilll != null && txtWaybilll.getText().length()  >= 9)//every making 9 bcz it was reading mentioned number count in some devices
                        AddNewWaybill();
                }
            });

//            txtWaybilll.setOnKeyListener(new View.OnKeyListener() {
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    // If the event is a key-down event on the "enter" button
//                    if (event.getAction() != KeyEvent.ACTION_DOWN)
//                        return true;
//                    else if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        onBackpressed();
//                        return true;
//                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
////                            setTxtWaybillNo(txtWaybillNo.getText().toString());
//                        if (txtWaybilll != null && txtWaybilll.getText().toString().length() >= 8)
//                            AddNewWaybill();
//                        return true;
//                    }
//                    return false;
//                }
//            });

//            txtWaybilll.addTextChangedListener(new TextWatcher() {
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
//                    if (txtWaybilll != null && txtWaybilll.getText().toString().length() >= 8)//txtWaybilll.getText().toString().length() == GlobalVar.ScanWaybillLength
//                        AddNewWaybill();
//                }
//            });

            //intent = new Intent(getContext().getApplicationContext(), BarcodeScan.class);
//            intent = new Intent(getActivity(), NewBarCodeScanner.class);
            Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
//            btnOpenCamera.setVisibility(view.GONE);
            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!GlobalVar.GV().checkPermission(getActivity(), GlobalVar.PermissionType.Camera)) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission),
                                GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else {
                        Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                        getActivity().startActivityForResult(intent, 2);
                    }
                }
            });

            initViews();
            // initDialog();
        }
        return rootView;
    }

//    private void setTxtWaybillNo() {
//
//        String barcode = txtWaybilll.getText().toString();
//        AddNewWaybill8and9(barcode);
//        // txtWaybilll.removeTextChangedListener(textWatcher);
////        utilities utilities = new utilities();
////        AddNewWaybill8and9(utilities.findwaybillno(barcode));
//
//
////        if (barcode.length() >= 8 && GlobalVar.WaybillNoStartSeries.contains(barcode.substring(0, 1))) {
////            AddNewWaybill8and9(barcode.substring(0, 8));
////
////            //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
////
////        } else if (barcode.length() >= GlobalVar.ScanWaybillLength) {
////            AddNewWaybill8and9(barcode.substring(0, GlobalVar.ScanWaybillLength));
////            //txtBarCode.setText(barcode.substring(0, GlobalVar.ScanWaybillLength));
////            //ValidateWayBill(txtBarCode.getText().toString().substring(0, GlobalVar.ScanWaybillLength));
////        }
//
//
//        //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
//
//
//    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter(WaybillList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        initSwipe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
//                        if (barcode.length() > 8)
//                            barcode = barcode.substring(0, 8);
                        txtWaybilll.setText(barcode);
                        AddNewWaybill();
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
//                else {
//                    removeView();
//                    edit_position = position;
//                    alertDialog.setTitle(R.string.EditBarCode);
//                    txtWaybillPiece.setText(WaybillList.get(position));
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


    private void AddNewWaybill8and9(String WaybillNo) {


        if (WaybillNo.toString().length() == 8 || WaybillNo.toString().length() == GlobalVar.ScanWaybillLength) {
            if (!WaybillList.contains(WaybillNo.toString())) {
                WaybillList.add(0, WaybillNo.toString());
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                lbTotal.setText(getString(R.string.lbCount) + WaybillList.size());
                txtWaybilll.setText("");
                initViews();
            } else {
                GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
                txtWaybilll.setText("");
            }
        }
//        txtWaybilll.addTextChangedListener(textWatcher);
    }

    private void AddNewWaybill() {
        String WaybillNo = txtWaybilll.getText().toString();
        utilities utilities = new utilities();
        String newBarcode = utilities.findwaybillno(WaybillNo);
        if (newBarcode.length() < 9)
            txtWaybilll.setText(newBarcode);
        AddNewWaybill8and9(newBarcode);

//        if (WaybillNo.length() > 8)
//            WaybillNo = WaybillNo.substring(0, 8);

//        if (WaybillNo.toString().length() >= 8) {
//            if (!WaybillList.contains(WaybillNo.toString())) {
//                WaybillList.add(0, WaybillNo.toString());
//                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
//                lbTotal.setText(getString(R.string.lbCount) + WaybillList.size());
//                txtWaybilll.setText("");
//                initViews();
//            } else {
//                GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
//                GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
//                txtWaybilll.setText("");
//            }
//        }
    }

    private void AddNewWaybillOld() {
        String x = txtWaybilll.getText().toString();
        if (!WaybillList.contains(txtWaybilll.getText().toString())) {
            if (txtWaybilll.getText().toString().length() == 8) {
                WaybillList.add(0, txtWaybilll.getText().toString());
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                lbTotal.setText(getString(R.string.lbCount) + WaybillList.size());
                txtWaybilll.setText("");
                initViews();
            }
        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtWaybilll.setText("");
        }
    }

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
//                    adapter.addItem(txtWaybillPiece.getText().toString());
//                    dialog.dismiss();
//                } else {
//                    WaybillList.set(edit_position, txtWaybillPiece.getText().toString());
//                    adapter.notifyDataSetChanged();
//                    dialog.dismiss();
//                }
//            }
//        });
//        txtWaybillPiece = (EditText) view.findViewById(R.id.txtWaybilll);
//    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtWaybilll", txtWaybilll.getText().toString());
        outState.putString("lbTotal", lbTotal.getText().toString());
        outState.putStringArrayList("WaybillList", WaybillList);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtWaybilll.setText(savedInstanceState.getString("txtWaybilll"));
            lbTotal.setText(savedInstanceState.getString("lbTotal"));
            WaybillList = savedInstanceState.getStringArrayList("WaybillList");
            initViews();
        }
    }

    private void onBackpressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exit Multi Delivery")
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

//    protected TextWatcher textWatcher = new TextWatcher() {
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            // your logic here
//            if (txtWaybilll != null && txtWaybilll.getText().length() >= 8)
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
}