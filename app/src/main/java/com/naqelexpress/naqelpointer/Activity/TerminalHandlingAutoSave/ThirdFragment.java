package com.naqelexpress.naqelpointer.Activity.TerminalHandlingAutoSave;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointBarCodeDetails;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ThirdFragment
        extends Fragment {
    View rootView;
    TextView lbTotal;
    public static EditText txtBarCode;
    public ArrayList<String> BarCodeList = new ArrayList<>();
    private DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();

    //    private AlertDialog.Builder alertDialog;
//    private EditText txtBarCodePiece;
//    private int edit_position;
//    private View view;
//    private boolean add = false;
    private Intent intent;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.checkpointsthirdfragment, container, false);
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

                    if (FirstFragment.CheckPointTypeID == 6 || FirstFragment.CheckPointTypeID == 8 ||
                            FirstFragment.CheckPointTypeID == 2) {
                        if (txtBarCode != null && txtBarCode.getText().toString().length() == 10) {
                            if (IsValid()) {
                                AddNewPiece();
                            } else {
                                txtBarCode.setText("");
                            }
                        }
                    } else {
                        if (txtBarCode != null && txtBarCode.getText().length() == 13) {
                            if (IsValid()) {
                                AddNewPiece();
                            } else {
                                txtBarCode.setText("");
                            }
                        }
                    }

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
                        if (barcode.length() == 13)
                            txtBarCode.setText(barcode);
//                        GlobalVar.GV().MakeSound(GlobalVar.GV().context, R.raw.barcodescanned);
//                        if (txtBarCode.getText().toString().length() > 12)
//                            AddNewPiece();
                    }
                }

//                final Barcode barcode = tripdata.getParcelableExtra("barcode");
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


    private void AddNewPiece() {

        try {
            double convert = Double.parseDouble(txtBarCode.getText().toString());
        } catch (Exception e) {
            GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
            ErrorAlert("Error",
                    "Incorrect Piece Barcode(" + txtBarCode.getText().toString() + ")"
            );
            txtBarCode.setText("");
            return;
        }

//        if (BarCodeList.size() == 50) {
//            ErrorAlert("Info", "Kindly save Scanned Data and Scan again...", 1, "");
//            return;
//        }

        if (TerminalHandling.group.equals("Group 1")) {
            boolean rtoreq = false;

            if (TerminalHandling.isdeliveryReq.contains(txtBarCode.getText().toString())) {
                if (TerminalHandling.isrtoReq.contains(txtBarCode.getText().toString())) {
                    rtoreq = true;
                    ErrorAlert("Delivery/RTO Request", "This Waybill Number(" + txtBarCode.getText().toString() + ") is Request For Delivery & RTO ", 0, txtBarCode.getText().toString());
                    if (!TerminalHandling.isHeldout.contains(txtBarCode.getText().toString())) {
                        TerminalHandling.isHeldout.add(txtBarCode.getText().toString());
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", txtBarCode.getText().toString());
                        temp.put("Status", "44");
                        temp.put("Ref", "Request For Delivery & RTO");
                        TerminalHandling.delrtoreq.add(temp);
                        GlobalVar.GV().MakeSound(getContext(), R.raw.delivery);
                    }

                } else {
                    ErrorAlert("Request For Delivery", "This Waybill Number(" + txtBarCode.getText().toString() + ") is Request For Delivery ", 0, txtBarCode.getText().toString());
                    if (!TerminalHandling.isHeldout.contains(txtBarCode.getText().toString())) {
                        TerminalHandling.isHeldout.add(txtBarCode.getText().toString());
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", txtBarCode.getText().toString());
                        temp.put("Status", "44");
                        temp.put("Ref", "Request For Delivery");
                        TerminalHandling.delrtoreq.add(temp);
                        GlobalVar.GV().MakeSound(getContext(), R.raw.delivery);
                    }
                }

                return;
            }

            if (!rtoreq) {
                if (TerminalHandling.isrtoReq.contains(txtBarCode.getText().toString())) {
                    ErrorAlert("Request For RTO", "This Waybill Number(" + txtBarCode.getText().toString() + ") is Request For RTO ", 0, txtBarCode.getText().toString());
                    if (!TerminalHandling.isHeldout.contains(txtBarCode.getText().toString())) {
                        TerminalHandling.isHeldout.add(txtBarCode.getText().toString());
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", txtBarCode.getText().toString());
                        temp.put("Status", "44");
                        temp.put("Ref", "Request For RTO");
                        TerminalHandling.delrtoreq.add(temp);
                        GlobalVar.GV().MakeSound(getContext(), R.raw.rto);
                    }
                    return;
                }
            }
        }

        if (!BarCodeList.contains(txtBarCode.getText().toString())) {

            if (FirstFragment.CheckPointTypeID == 6 || FirstFragment.CheckPointTypeID == 8 ||
                    FirstFragment.CheckPointTypeID == 2) {
                if (txtBarCode.getText().toString().length() == 10) {
                    SaveData(txtBarCode.getText().toString());
                    BarCodeList.add(0, txtBarCode.getText().toString());
                    lbTotal.setText(getString(R.string.lbCount) + BarCodeList.size());
                    GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                    txtBarCode.setText("");

                    initViews();
                }
            } else {
                if (txtBarCode.getText().toString().length() == 13) {
                    SaveData(txtBarCode.getText().toString());
                    BarCodeList.add(0, txtBarCode.getText().toString());
                    lbTotal.setText(getString(R.string.lbCount) + BarCodeList.size());
                    GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                    txtBarCode.setText("");
                    initViews();
                }
            }
        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }
    }

    private void ErrorAlert(final String title, String message, final int clear, final String piececode) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (clear == 0)
                            txtBarCode.setText("");
                        if (piececode.length() > 0)
                            SaveData(piececode, title);
                    }
                });

        alertDialog.show();
    }

    private void ErrorAlert(final String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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

    private void SaveData(String piece) {

        DBConnections dbConnections = new DBConnections(getContext(), null);
        String Comments = "";
        if (FirstFragment.CheckPointTypeID == 18) {
            Comments = "Weight " + FirstFragment.txtweight.getText().toString() + " KG " + "  W * L * H" +
                    FirstFragment.txtwidth.getText().toString() + " * " +
                    FirstFragment.txtlength.getText().toString() + " * " +
                    FirstFragment.txtheight.getText().toString();
            FirstFragment.txtCheckPointTypeDDetail.setText(Comments);

        }

        com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                (FirstFragment.CheckPointTypeID, String.valueOf(TerminalHandling.Latitude),
                        String.valueOf(TerminalHandling.Longitude), FirstFragment.CheckPointTypeDetailID,
                        FirstFragment.txtCheckPointTypeDDetail.getText().toString()
                        , "" , 0);

        int ID = 0;
        if (dbConnections.InsertTerminalHandling(checkPoint, getContext())) {
            ID = dbConnections.getMaxID("CheckPoint", getContext());

            if (ID > 0) {
                CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(piece, ID);
                dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getContext());
            }
            if (!isMyServiceRunning(TerminalHandling.class)) {
                getActivity().startService(
                        new Intent(getActivity(),
                                com.naqelexpress.naqelpointer.service.TerminalHandling.class));
            }
        }
        dbConnections.close();


    }

    private void SaveData(String PieceCode, String req) {

        DBConnections dbConnections = new DBConnections(getContext(), null);

        com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                (FirstFragment.CheckPointTypeID, String.valueOf(TerminalHandling.Latitude),
                        String.valueOf(TerminalHandling.Longitude), 44, req
                        , "" , 0);

        if (dbConnections.InsertTerminalHandling(checkPoint, getContext())) {
            int ID = dbConnections.getMaxID("CheckPoint", getContext());


            CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(PieceCode, ID);
            dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getContext());


            if (isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandling.class))
                getActivity().startService(
                        new Intent(getActivity(),
                                com.naqelexpress.naqelpointer.service.TerminalHandling.class));
        }

        dbConnections.close();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean IsValid() {
        boolean isValid = true;
        if (FirstFragment.CheckPointTypeID <= 0) {
            GlobalVar.GV().ShowSnackbar(rootView, "You have to select the Check Point Type",
                    GlobalVar.AlertType.Error);
            return false;
        } else if (FirstFragment.txtCheckPointTypeDetail.getVisibility() == View.VISIBLE &&
                FirstFragment.CheckPointTypeDetailID == 0) {
            GlobalVar.GV().ShowSnackbar(rootView, "You have to select the reason",
                    GlobalVar.AlertType.Error);
            return false;
        } else if (FirstFragment.txtCheckPointTypeDDetail.getVisibility() == View.VISIBLE) {
            if (FirstFragment.CheckPointTypeDDetailID == 1 && FirstFragment.txtCheckPointTypeDDetail.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(rootView, "You have to select the Date",
                        GlobalVar.AlertType.Error);
                return false;
            } else if (FirstFragment.CheckPointTypeID == 20 && FirstFragment.txtCheckPointTypeDDetail.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(rootView, "You have enter the Bin Location",
                        GlobalVar.AlertType.Error);
                return false;
            }

        }

        if (FirstFragment.CheckPointTypeID == 18) {
            if (FirstFragment.txtweight.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(rootView, "You have to enter weight",
                        GlobalVar.AlertType.Error);
                return false;
            }

            if (FirstFragment.txtheight.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(rootView, "You have to enter height",
                        GlobalVar.AlertType.Error);
                return false;
            }

            if (FirstFragment.txtlength.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(rootView, "You have to enter length",
                        GlobalVar.AlertType.Error);
                return false;
            }
            if (FirstFragment.txtwidth.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(rootView, "You have to enter length",
                        GlobalVar.AlertType.Error);
                return false;
            }

        }

        return isValid;
    }
}