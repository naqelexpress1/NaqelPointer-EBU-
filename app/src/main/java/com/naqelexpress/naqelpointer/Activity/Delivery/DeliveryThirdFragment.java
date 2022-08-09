package com.naqelexpress.naqelpointer.Activity.Delivery;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class DeliveryThirdFragment extends Fragment {
    View rootView;
    private EditText txtBarCode, txtBarCodePiece;
    TextView lbTotal;
    //    public static  ArrayList<String> ShipmentBarCodeList = new ArrayList<>();
    public ArrayList<String> DeliveryBarCodeList = new ArrayList<>();
    private DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();
    private AlertDialog.Builder alertDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Intent intent;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.deliverythirdfragment, container, false);

            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);
            txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);
//            txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanBarcodeLength)});

//            txtBarCode.setOnKeyListener(new View.OnKeyListener() {
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    // If the event is a key-down event on the "enter" button
//                    if (event.getAction() != KeyEvent.ACTION_DOWN)
//                        return true;
//                    else if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        //finish();
//                        GlobalVar.onBackpressed(getActivity(), "Exit", "Are you sure want to Exit?");
//                        return true;
//                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
//
//                        if (txtBarCode != null && txtBarCode.getText().length() >= 13)
//                            AddNewPiece8and9();
//                        return true;
//                    }
//                    return false;
//                }
//            });

            txtBarCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
//                    if (txtBarCode != null && txtBarCode.getText().length() == 13)
//                        AddNewPiece();
                    if (txtBarCode != null && txtBarCode.getText().length() >= 8)
                        AddNewPiece8and9();
                }
            });

            Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
//            btnOpenCamera.setVisibility(View.GONE);

            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else{
                        Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                        getActivity().startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                    }

                }
            });

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
        adapter = new DataAdapter(DeliveryBarCodeList);
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
                    }
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initViews();
        initDialog();
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
                                    lbTotal.setText(getString(R.string.lbCount) + DeliveryBarCodeList.size());
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
                    alertDialog.setTitle("Edit BarCode");
                    txtBarCodePiece.setText(DeliveryBarCodeList.get(position));
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

    private void AddNewPiece8and9() {
        if (!DeliveryBarCodeList.contains(txtBarCode.getText().toString())) {
            DeliveryBarCodeList.add(0, txtBarCode.getText().toString());
            lbTotal.setText(getString(R.string.lbCount) + DeliveryBarCodeList.size());
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
            txtBarCode.setText("");
            initViews();


        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }
    }

    private void AddNewPiece() {
        if (!DeliveryBarCodeList.contains(txtBarCode.getText().toString())) {
            DeliveryBarCodeList.add(0, txtBarCode.getText().toString());
            lbTotal.setText(getString(R.string.lbCount) + DeliveryBarCodeList.size());
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
            txtBarCode.setText("");
            initViews();


//            if (DeliveryFirstFragment.ShipmentBarCodeList.contains(txtBarCode.getText().toString())) {
//                DeliveryBarCodeList.add(0, txtBarCode.getText().toString());
//                lbTotal.setText(getString(R.string.lbCount) + DeliveryBarCodeList.size());
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
//                                lbTotal.setText(getString(R.string.lbCount) + DeliveryBarCodeList.size());
//                                initViews();
//                                txtBarCode.setText("");
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int which) {
//                                lbTotal.setText(getString(R.string.lbCount) + DeliveryBarCodeList.size());
//                                txtBarCode.setText("");
//                            }
//                        })
//                        .setCancelable(false);
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }
    }

    @SuppressLint("RestrictedApi")
    private void initDialog() {
        alertDialog = new AlertDialog.Builder(this.getContext());
        view = getLayoutInflater(null).inflate(R.layout.dialog_layout, null);

        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (add) {
                    add = false;
                    adapter.addItem(txtBarCodePiece.getText().toString());
                    dialog.dismiss();
                } else {
                    DeliveryBarCodeList.set(edit_position, txtBarCodePiece.getText().toString());
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }

            }
        });
        txtBarCodePiece = (EditText) view.findViewById(R.id.txtWaybilll);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("DeliveryBarCodeList", DeliveryBarCodeList);
//        outState.putStringArrayList("ShipmentBarCodeList", DeliveryFirstFragment.ShipmentBarCodeList);
        outState.putString("lbTotal", lbTotal.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            DeliveryBarCodeList = savedInstanceState.getStringArrayList("DeliveryBarCodeList");
//            DeliveryFirstFragment.ShipmentBarCodeList = savedInstanceState.getStringArrayList("ShipmentBarCodeList");
            lbTotal.setText(savedInstanceState.getString("lbTotal"));
            initViews();
            //  initDialog();
        }
    }
}