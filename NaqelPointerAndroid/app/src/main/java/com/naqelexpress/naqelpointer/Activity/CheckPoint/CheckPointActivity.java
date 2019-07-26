package com.naqelexpress.naqelpointer.Activity.CheckPoint;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPoint;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointWaybillDetails;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class CheckPointActivity
        extends AppCompatActivity {
    private EditText txtBarCode, txtCheckPointType;
    private int CheckPointTypeID;
    TextView lbTotal;
    public ArrayList<String> CheckPointWaybillDetailsList = new ArrayList<>();
    private DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();

    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    SpinnerDialog checkPointTypeSpinnerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.checkpoint);

        lbTotal = (TextView) findViewById(R.id.lbTotal);
        txtCheckPointType = (EditText) findViewById(R.id.txtCheckPointType);

        txtBarCode = (EditText) findViewById(R.id.txtWaybilll);
        txtBarCode.setInputType(InputType.TYPE_NULL);
        txtBarCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO
//                if (txtBarCode != null && txtBarCode.getText().length() == 8)
//                    AddNewWaybill();
            }
        });

        txtBarCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    checkPointTypeSpinnerDialog.showSpinerDialog(false);
            }
        });

        if (GlobalVar.GV().IsEnglish())
            checkPointTypeSpinnerDialog = new SpinnerDialog(CheckPointActivity.this, GlobalVar.GV().StationNameList, "Select or Search Check Point", R.style.DialogAnimations_SmileWindow);
        else
            checkPointTypeSpinnerDialog = new SpinnerDialog(CheckPointActivity.this, GlobalVar.GV().StationFNameList, "Select or Search Check Point", R.style.DialogAnimations_SmileWindow);
        checkPointTypeSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                if (GlobalVar.GV().IsEnglish())
                    txtCheckPointType.setText(GlobalVar.GV().CheckPointTypeNameList.get(position));
                else
                    txtCheckPointType.setText(GlobalVar.GV().CheckPointTypeFNameList.get(position));
                txtBarCode.requestFocus();
                CheckPointTypeID = GlobalVar.GV().StationList.get(position).ID;
            }
        });

        Button btnOpenCamera = (Button) findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GlobalVar.GV().checkPermission(CheckPointActivity.this, GlobalVar.PermissionType.Camera)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(CheckPointActivity.this, GlobalVar.PermissionType.Camera);
                } else {
                    Intent intent = new Intent(getApplicationContext(), NewBarCodeScanner.class);
                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                }
            }
        });

        initViews();
        initDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checkpointmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:
                SaveData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    double Latitude = 0, Longitude = 0;

    private void SaveData() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            boolean IsSaved = true;
            CheckPoint checkPoint = new CheckPoint(CheckPointTypeID, String.valueOf(Latitude), String.valueOf(Longitude), 0, 0);
            if (dbConnections.InsertOnCheckPoint(checkPoint,getApplicationContext())) {
                int CheckPointID = dbConnections.getMaxID("CheckPoint",getApplicationContext());
                for (int i = 0; i < CheckPointWaybillDetailsList.size(); i++) {
                    CheckPointWaybillDetails checkPointWaybillDetails = new CheckPointWaybillDetails(CheckPointWaybillDetailsList.get(i), CheckPointID);
                    if (!dbConnections.InsertOnCheckPointWaybillDetail(checkPointWaybillDetails,getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                if (IsSaved) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                    finish();
                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
        }
    }

    private boolean IsValid() {
        boolean isValid = true;


        return isValid;
    }

    @Override
    public void onStart() {
        super.onStart();
        initViews();
        initDialog();
    }

    private void initDialog() {
        alertDialog = new AlertDialog.Builder(CheckPointActivity.this);
        view = getWindow().getDecorView().getRootView();

        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (add) {
                    add = false;
                    adapter.addItem(txtBarCode.getText().toString());
                    dialog.dismiss();
                } else {
                    CheckPointWaybillDetailsList.set(edit_position, txtBarCode.getText().toString());
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        txtBarCode = (EditText) view.findViewById(R.id.txtWaybilll);
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(CheckPointWaybillDetailsList);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle("Confirm Deleting")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    adapter.removeItem(position);
                                    lbTotal.setText(getString(R.string.lbCount) + CheckPointWaybillDetailsList.size());
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
                    txtBarCode.setText(CheckPointWaybillDetailsList.get(position));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        if (barcode.length() == 183)
                            txtBarCode.setText(barcode);
//                        if (txtBarCode.getText().toString().length() > 8)
//                            AddNewPiece();
                    }
                }
//                final Barcode barcode = data.getParcelableExtra("barcode");
//                //final MediaPlayer barcodeSound = MediaPlayer.create(getContext().getApplicationContext(),R.raw.barcodescanned);
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
    //TODO Error
//    private void AddNewWaybill()
//    {
//        String WaybillNo = txtBarCode.getText().toString();
//        if (WaybillNo.length() > 8)
//            WaybillNo = WaybillNo.substring(0,8);
//        if (WaybillNo.toString().length() == 8)
//        {
//            if (!txtBarCode.contains(WaybillNo.toString()))
//            {
//                txtBarCode.add(0, WaybillNo.toString());
//                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
//                lbTotal.setText(getString(R.string.lbCount) + WaybillList.size());
//                txtBarCode.setText("");
//                initViews();
//            }
//            else
//            {
//                GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
//                GlobalVar.GV().MakeSound(this.getContext(),R.raw.wrongbarcodescan);
//                txtBarCode.setText("");
//            }
//        }
//    }

//    private void AddNewWaybill()
//    {
//        if (!CheckPointWaybillDetailsList.contains(txtBarCode.getText().toString()))
//        {
//            if (txtBarCode.getText().toString().length() == 8)
//            {
//                CheckPointWaybillDetailsList.add(0, txtBarCode.getText().toString());
//                lbTotal.setText(getString(R.string.lbCount) + CheckPointWaybillDetailsList.size());
//                GlobalVar.GV().MakeSound(GlobalVar.GV().context, R.raw.barcodescanned);
//                txtBarCode.setText("");
//                initViews();
//            }
//        }
//        else
//        {
//            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
//            GlobalVar.GV().MakeSound(GlobalVar.GV().context,R.raw.wrongbarcodescan);
//            txtBarCode.setText("");
//        }
//    }
}