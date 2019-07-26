package com.naqelexpress.naqelpointer.Activity.AtOriginusingLocalDB;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class SecondFragment extends Fragment {
    View rootView;
    private EditText txtBarCode, txtBarCodePiece;
    static TextView lbTotal;
    static DataAdapterForThird adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();
    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Intent intent;
    static ArrayList<HashMap<String, String>> SelectedwaybillBardetails = new ArrayList<>();
    static ArrayList<String> ValidateBarCodeList = new ArrayList<>();
//    private ArrayList<String> ValidateBarCodeList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.deliverythirdfragment, container, false);
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);

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
                }
            });


            // SelectedwaybillBardetails.clear();
            // ValidateBarCodeList.clear();
            lbTotal.setText(getString(R.string.lbCount) + SelectedwaybillBardetails.size());
            initViews();
//            initDialog();
        }

        return rootView;
    }

    private void ValidateWayBill(String barcode) {
        if (!ValidateBarCodeList.contains(barcode)) {
            for (int i = 0; i < CourierDetails.waybillBardetails.size(); i++) {
                boolean sound = false;
                if (barcode.equals(CourierDetails.waybillBardetails.get(i).get("BarCode"))) {

                    for (int j = 0; j < FirstFragment.Selectedwaybilldetails.size(); j++) {

                        if (FirstFragment.Selectedwaybilldetails.get(j).get("WaybillNo").
                                equals(CourierDetails.waybillBardetails.get(i).get("WaybillNo"))) {


                            int preqty = Integer.parseInt(FirstFragment.Selectedwaybilldetails.get(j).get("ScannedPC")) + 1;
                            FirstFragment.Selectedwaybilldetails.get(j).put("ScannedPC", String.valueOf(preqty));

                            SelectedwaybillBardetails.add(CourierDetails.waybillBardetails.get(i));

                            DBConnections dbConnections = new DBConnections(getContext(), null);
                            dbConnections.AtOriginScannedPiecesCount(CourierDetails.waybillBardetails.get(i).get("WaybillNo")
                                    , String.valueOf(preqty), getView());
                            dbConnections.AtOriginScannedPiececode(barcode, getView());
                            dbConnections.close();

                            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
                            txtBarCode.setText("");
                            adapter.notifyDataSetChanged();
                            FirstFragment.adapter.notifyDataSetChanged();
                            lbTotal.setText(getString(R.string.lbCount) + SelectedwaybillBardetails.size());
                            ValidateBarCodeList.add(barcode);
                            sound = true;
                            break;

                        }
                    }
                    if (!sound) {
                        GlobalVar.GV().ShowSnackbar(getView(), "No WayBillNo under this BarCode", GlobalVar.AlertType.Error);
                        GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
                    }
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
        adapter = new DataAdapterForThird(SelectedwaybillBardetails);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //initSwipe();
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
//                final Barcode barcode = data.getParcelableExtra("barcode");
//                txtBarCode.post(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        txtBarCode.setText(barcode.displayValue);
//
//                        if (txtBarCode.getText().toString().length() > 6)
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
                            .setMessage("Are you sure you want to delete this piece?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
//                                    deletePiecealongWaybill(position);

//                                    ValidateBarCodeList.remove(position);
//                                    adapter.removeItem(position);
//                                    lbTotal.setText(getString(R.string.lbCount) + SelectedwaybillBardetails.size());

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

    private void deletePiecealongWaybill(final int position) {

//        String Barcode = ValidateBarCodeList.get(position);
//        String WayBillFromPiece = "";
//        int PieceCountFromPiece = 0;
//
//        int CourierDetailsWaybillLocation = 0;
//
//        for (int i = 0; i < CourierDetails.waybillBardetails.size(); i++) {
//            if (CourierDetails.waybillBardetails.get(i).get("BarCode").equals(Barcode)) {
//                WayBillFromPiece = CourierDetails.waybillBardetails.get(i).get("WaybillNo");
//
//                break;
//            }
//        }
//
//        int ScannedPieceCount = 0;
//        for (int i = 0; i < SelectedwaybillBardetails.size(); i++) {
//            if (SelectedwaybillBardetails.get(i).get("WaybillNo").equals(WayBillFromPiece)) {
//                ScannedPieceCount = ScannedPieceCount + 1;
//            }
//        }
//
//        for (int i = 0; i < CourierDetails.waybilldetails.size(); i++) {
//            if (CourierDetails.waybilldetails.get(i).get("WaybillNo").equals(WayBillFromPiece)) {
//                PieceCountFromPiece = Integer.parseInt(CourierDetails.waybillBardetails.get(i).get("PieceCount"));
//
//                if (ScannedPieceCount == 1) {
//                    CourierDetails.waybilldetails.get(i).put("isdelete", "1");
//                    if (SelectedwaybillBardetails.contains(WayBillFromPiece))
//                        SelectedwaybillBardetails.remove(WayBillFromPiece);
//                }
//
//                break;
//            }
//        }
//
//
//        ValidateBarCodeList.remove(position);
//        adapter.removeItem(position);
//        lbTotal.setText(getString(R.string.lbCount) + SelectedwaybillBardetails.size());


    }
//    private void AddNewPiece() {
//        if (!DeliveryBarCodeList.contains(txtBarCode.getText().toString())) {
//            if (ShipmentBarCodeList.contains(txtBarCode.getText().toString())) {
//                DeliveryBarCodeList.add(0, txtBarCode.getText().toString());
//                waybillcount.setText(getString(R.string.lbCount) + DeliveryBarCodeList.size());
//                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
//                txtBarCode.setText("");
//                initViews();
//            } else {
//                GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
//                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
//                builder.setMessage("This piece is not belong to this waybill, Are you sure you want to add it?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int which) {
//                                DeliveryBarCodeList.add(0, txtBarCode.getText().toString());
//                                waybillcount.setText(getString(R.string.lbCount) + DeliveryBarCodeList.size());
//                                initViews();
//                                txtBarCode.setText("");
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int which) {
//                                waybillcount.setText(getString(R.string.lbCount) + DeliveryBarCodeList.size());
//                                txtBarCode.setText("");
//                            }
//                        })
//                        .setCancelable(false);
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
//        } else {
//            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
//            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
//            txtBarCode.setText("");
//        }
//    }

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
////                    adapter.addItem(txtBarCodePiece.getText().toString());
//                    dialog.dismiss();
//                } else {
////                    DeliveryBarCodeList.set(edit_position, txtBarCodePiece.getText().toString());
//                    adapter.notifyDataSetChanged();
//                    dialog.dismiss();
//                }
//
//            }
//        });
//        txtBarCodePiece = (EditText) view.findViewById(R.id.txtWaybilll);
//    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtBarCode", txtBarCode.getText().toString());
        outState.putString("waybillcount", lbTotal.getText().toString());
        outState.putStringArrayList("ValidateBarCodeList", ValidateBarCodeList);
        outState.putSerializable("SelectedwaybillBardetails", SelectedwaybillBardetails);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtBarCode.setText(savedInstanceState.getString("txtBarCode"));
            lbTotal.setText(savedInstanceState.getString("waybillcount"));
            ValidateBarCodeList = savedInstanceState.getStringArrayList("ValidateBarCodeList");
            SelectedwaybillBardetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("SelectedwaybillBardetails");
            initViews();
        }
    }
}