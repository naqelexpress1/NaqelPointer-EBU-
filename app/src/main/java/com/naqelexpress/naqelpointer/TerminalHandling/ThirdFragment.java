package com.naqelexpress.naqelpointer.TerminalHandling;

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
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointBarCodeDetails;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.OnlineValidation.OnLineValidation;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ThirdFragment extends Fragment {

    View rootView;
    TextView lbTotal;
    public static EditText txtBarCode;
    public ArrayList<String> BarCodeList = new ArrayList<>();
    public ArrayList<String> Barcodes = new ArrayList<>();
    private DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();
    private Intent intent;
    private Context mContext;
    private String division;

    ArrayList<HashMap<String, String>> delrtoreq = new ArrayList<>();

    private DBConnections dbConnections = new DBConnections(getContext(), null);
    private List<OnLineValidation> onLineValidationList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.checkpointsthirdfragment, container, false);
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);


            division = GlobalVar.GV().getDivisionID(getContext(), GlobalVar.GV().EmployID);


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
                                requestfocus();
                            }
                        }
                    } else {
                        if (txtBarCode != null && txtBarCode.getText().length() == 13) {
                            if (!GlobalVar.GV().isValidBarcode(txtBarCode.getText().toString())) {
                                GlobalVar.GV().ShowSnackbar(rootView, "Wrong Barcode", GlobalVar.AlertType.Warning);
                                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
                                txtBarCode.setText("");
                                return;
                            }
                            if (IsValid()) {
                                String barcode = txtBarCode.getText().toString();
                                if (division.equals("Courier") && TerminalHandling.group.equals("Group 8")) { //Arrival - Online Validation
                                   if (isValidPieceBarcode(barcode)) {
                                        AddNewPiece();
                                    } else {
                                        showDialog(getOnLineValidationPiece(barcode));
                                    }
                                } else {
                                    AddNewPiece();
                                }

                            } else {
                                requestfocus();
                            }
                        }
                    }

                }
            });

            // this one
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


    private void requestfocus() {
        txtBarCode.setText("");
        txtBarCode.requestFocus();
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

    //this one
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

        if (txtBarCode.getText().toString().toUpperCase().matches(".*[ABCDEFGH].*")) {
            txtBarCode.requestFocus();
            txtBarCode.setText("");
            initViews();
            return;
        }

        try {
            double convert = Double.parseDouble(txtBarCode.getText().toString());
        } catch (Exception e) {
            GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
            ErrorAlert("Error",
                    "Incorrect Piece Barcode(" + txtBarCode.getText().toString() + ")"
            );
            requestfocus();
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
                    if (!TerminalHandling.isHeldout.contains(txtBarCode.getText().toString())) {
                        TerminalHandling.isHeldout.add(txtBarCode.getText().toString());
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", txtBarCode.getText().toString());
                        temp.put("Status", "44");
                        temp.put("Ref", "Request For Delivery & RTO");
                        TerminalHandling.delrtoreq.add(temp);
                        GlobalVar.GV().MakeSound(getContext(), R.raw.delivery);
                    }
                    ErrorAlert("Delivery/RTO Request", "This Waybill Number(" + txtBarCode.getText().toString() + ") is Request For Delivery & RTO ", 0);
                } else {
                    if (!TerminalHandling.isHeldout.contains(txtBarCode.getText().toString())) {
                        TerminalHandling.isHeldout.add(txtBarCode.getText().toString());
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", txtBarCode.getText().toString());
                        temp.put("Status", "44");
                        temp.put("Ref", "Request For Delivery");
                        TerminalHandling.delrtoreq.add(temp);
                        GlobalVar.GV().MakeSound(getContext(), R.raw.delivery);
                    }
                    ErrorAlert("Request For Delivery", "This Waybill Number(" + txtBarCode.getText().toString() + ") is Request For Delivery ", 0);
                }
                return;
            }

            if (!rtoreq) {
                if (TerminalHandling.isrtoReq.contains(txtBarCode.getText().toString())) {
                    if (!TerminalHandling.isHeldout.contains(txtBarCode.getText().toString())) {
                        TerminalHandling.isHeldout.add(txtBarCode.getText().toString());
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", txtBarCode.getText().toString());
                        temp.put("Status", "44");
                        temp.put("Ref", "Request For RTO");
                        TerminalHandling.delrtoreq.add(temp);
                        GlobalVar.GV().MakeSound(getContext(), R.raw.rto);
                    }
                    ErrorAlert("Request For RTO", "This Waybill Number(" + txtBarCode.getText().toString() + ") is Request For RTO ", 0);
                    return;
                }
            }
        }

        int picecodeLength = 0;
        if (FirstFragment.CheckPointTypeID == 6 || FirstFragment.CheckPointTypeID == 8 ||
                FirstFragment.CheckPointTypeID == 2) {
            picecodeLength = 10;
        } else
            picecodeLength = 13;

        if (!BarCodeList.contains(txtBarCode.getText().toString())) {
            if (txtBarCode.getText().toString().length() == picecodeLength) {
                Barcodes.add(txtBarCode.getText().toString());
                BarCodeList.add(0, txtBarCode.getText().toString());
                lbTotal.setText(getString(R.string.lbCount) + BarCodeList.size());
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                requestfocus();
                initViews();
            }

            /*if (FirstFragment.CheckPointTypeID == 6 || FirstFragment.CheckPointTypeID == 8 ||
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
            }*/
        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }

        if (Barcodes.size() == 20) {
            SaveData();
        }
        requestfocus();
    }

    private void SaveData() {

        DBConnections dbConnections = new DBConnections(getContext(), null);
        String Comments = "";
        if (FirstFragment.CheckPointTypeID == 18) {
            Comments = "Weight " + FirstFragment.txtweight.getText().toString() + " KG " + "  W * L * H" +
                    FirstFragment.txtwidth.getText().toString() + " * " +
                    FirstFragment.txtlength.getText().toString() + " * " +
                    FirstFragment.txtheight.getText().toString();
            FirstFragment.txtCheckPointTypeDDetail.setText(Comments);

        }

        //mohammed add this Integer.parseInt("")
        com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                (FirstFragment.CheckPointTypeID, String.valueOf(TerminalHandling.Latitude),
                        String.valueOf(TerminalHandling.Longitude), FirstFragment.CheckPointTypeDetailID,
                        FirstFragment.txtCheckPointTypeDDetail.getText().toString()
                        , "", Barcodes.size(), Integer.parseInt(""));

        int ID = 0;
        if (dbConnections.InsertTerminalHandling(checkPoint, getContext())) {
            ID = dbConnections.getMaxID("CheckPoint", getContext());

            if (ID > 0) {
                for (String PieceCode : Barcodes) {
                    CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(PieceCode, ID);
                    dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getContext());
                }

            }
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandling.class)) {
                getActivity().startService(
                        new Intent(getActivity(),
                                com.naqelexpress.naqelpointer.service.TerminalHandling.class));
            }
            Barcodes.clear();
        }
        dbConnections.close();


    }

    private void ErrorAlert(final String title, String message, final int clear) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (clear == 0) {
                            txtBarCode.setText("");
                            txtBarCode.requestFocus();
                        }

//                        if (piececode.length() > 0)
//                            SaveData(piececode, title);

                        if (Barcodes.size() == 20) {
                            SaveData();
                        }
                    }
                });

        alertDialog.show();
    }

    /*private void SaveData(String piece) {

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
                        , "");

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


    }*/


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

   /* private void SaveData(String piece) {

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
                        , "");

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


    }*/

   //mohammed add this Integer.parseInt("")
    private void SaveData(String PieceCode, String req) {

        DBConnections dbConnections = new DBConnections(getContext(), null);

        com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                (FirstFragment.CheckPointTypeID, String.valueOf(TerminalHandling.Latitude),
                        String.valueOf(TerminalHandling.Longitude), 44, req
                        , "" , 0, Integer.parseInt(""));

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

    private boolean isValidPieceBarcode(String pieceBarcode) {
        boolean isValid = true;
        try {
            OnLineValidation onLineValidationLocal = dbConnections.getPieceInformationByBarcode(pieceBarcode, getContext());
            OnLineValidation onLineValidation = new OnLineValidation();

           /* if (onLineValidationLocal == null) {
                onLineValidation.setNotInFile(true);
                isValid = false;
            }  else {*/

             /*   int isManifested = onLineValidationLocal.getIsManifested();

                if (isManifested == 0) {
                    Log.d("test" , "Not manifested");
                    onLineValidation.setIsManifested(0);
                    isValid = false;
                }


                if (isManifested == 0 && onLineValidationLocal.getCustomerWaybillDestID() != GlobalVar.GV().StationID) {
                    onLineValidation.setIsWrongDest(1);
                    onLineValidation.setCustomerWaybillDestID(onLineValidationLocal.getCustomerWaybillDestID());
                    isValid = false;
                } */

                if (onLineValidationLocal != null) {
                    if ( onLineValidationLocal.getWaybillDestID() != GlobalVar.GV().StationID) {
                        onLineValidation.setIsWrongDest(1);
                        onLineValidation.setWaybillDestID(onLineValidationLocal.getWaybillDestID());
                        isValid = false;
                    }

                    if (onLineValidationLocal.getIsMultiPiece() == 1) {
                        onLineValidation.setIsMultiPiece(1);
                        isValid = false;
                    }

                    if (onLineValidationLocal.getIsStopped() == 1) {
                        onLineValidation.setIsStopped(1);
                        isValid = false;
                    }

                    if (onLineValidationLocal.getIsRelabel() == 1) {
                        onLineValidation.setIsRelabel(1);
                        isValid = false;
                    }
                }

           // }

            if (!isValid) {
                onLineValidation.setBarcode(pieceBarcode);
                onLineValidationList.add(onLineValidation);
            }

        } catch (Exception e) {
            Log.d("test" , "isValidPieceBarcode " + e.toString());
        }
        return isValid;
    }


    public void showDialog(final OnLineValidation pieceDetails) {
        DBConnections dbConnections = new DBConnections(mContext , null);
        try {
            if (pieceDetails != null) {
                final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(mContext);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);


                TextView tvBarcode = dialogView.findViewById(R.id.tv_barcode);
                tvBarcode.setText("Piece #" + pieceDetails.getBarcode());

               //Uncomment once script is changed.
                /*if (pieceDetails.isNotInFile()) {

                    LinearLayout llWrongDest = dialogView.findViewById(R.id.ll_not_manifested);
                    llWrongDest.setVisibility(View.VISIBLE);

                    TextView tvWrongDestHeader = dialogView.findViewById(R.id.tv_not_manifested_header);
                    tvWrongDestHeader.setText("Manifest");

                    TextView tvWrongDestBody = dialogView.findViewById(R.id.tv_not_manifested_body);
                    tvWrongDestBody.setText("Shipment is not manifested yet. ");
                }*/


                if (pieceDetails.getIsWrongDest() == 1) {
                    String stationName = "";
                    try {
                        Station station = null;

                     /*   if (pieceDetails.getIsManifested() == 0)
                            station = dbConnections.getStationByID(pieceDetails.getCustomerWaybillDestID() , mContext);
                        else
                            station = dbConnections.getStationByID(pieceDetails.getWaybillDestID() , mContext);*/

                        station = dbConnections.getStationByID(pieceDetails.getWaybillDestID() , mContext);

                        if (station != null)
                            stationName = station.Name;
                        else
                            Log.d("test" , "Station is null");
                    } catch (Exception e) {}

                    LinearLayout llWrongDest = dialogView.findViewById(R.id.ll_wrong_dest);
                    llWrongDest.setVisibility(View.VISIBLE);

                    TextView tvWrongDestHeader = dialogView.findViewById(R.id.tv_wrong_dest_header);
                    tvWrongDestHeader.setText("Wrong Destination");

                    TextView tvWrongDestBody = dialogView.findViewById(R.id.tv_wrong_dest_body);
                    tvWrongDestBody.setText("Shipment destination : " + stationName + "."
                           );
                }

                if (pieceDetails.getIsMultiPiece() == 1) {

                    LinearLayout llMultiPiece = dialogView.findViewById(R.id.ll_is_multi_piece);
                    llMultiPiece.setVisibility(View.VISIBLE);

                    TextView tvMultiPieceHeader = dialogView.findViewById(R.id.tv_multiPiece_header);
                    tvMultiPieceHeader.setText("Multi Piece");

                    TextView tvMultiPieceBody = dialogView.findViewById(R.id.tv_multiPiece_body);
                    tvMultiPieceBody.setText("Please check pieces.");
                }

                if (pieceDetails.getIsStopped() == 1) {

                    LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_stop_shipment);
                    llStopShipment.setVisibility(View.VISIBLE);

                    TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_stop_shipment_header);
                    tvStopShipmentHeader.setText("Stop Shipment");

                    TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_stop_shipment_body);
                    tvStopShipmentBody.setText("Stop shipment.Please Hold.");
                }

                /*if (pieceDetails.getIsRelabel() == 1) {
                    LinearLayout llRelabel = dialogView.findViewById(R.id.ll_is_relabel);
                    llRelabel.setVisibility(View.VISIBLE);
                } */

                final android.app.AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
                btnConfirm.setVisibility(View.VISIBLE);
                btnConfirm.setText("OK");

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To avoid leaked window
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                                AddNewPiece();
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
                if (pieceDetail.getBarcode().equals(barcode))
                    return pieceDetail;
            }

        } catch (Exception e) {
            Log.d("test" , "getOnLineValidationPiece " + e.toString());
        }
        return null;
    }



}