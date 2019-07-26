package com.naqelexpress.naqelpointer.Activity.AtOriginDelete;

import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AtOriginDeleteSecondFrgament
        extends Fragment {
    View rootView;

    public static DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();
    static TextView lbTotal;
    private View view;
    public static ArrayList<HashMap<String, String>> Selectedbarcodedetails = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.deliverythirdfragment, container, false);

            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);
            EditText txtWaybilll = (EditText) rootView.findViewById(R.id.txtWaybilll);
            txtWaybilll.setVisibility(View.GONE);
            Button camera = (Button) rootView.findViewById(R.id.btnOpenCamera);
            camera.setVisibility(View.GONE);

            view = rootView;
            Bundle bundle = getArguments();

            if (bundle != null) {
                Selectedbarcodedetails = (ArrayList<HashMap<String, String>>) bundle.getSerializable("BarCode");

            }
            lbTotal.setText("Count : " + String.valueOf(Selectedbarcodedetails.size()));

            initViews();
        }
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        initViews();
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(Selectedbarcodedetails, "BarCode");
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

                                    DeleteWaybillWithPeice(position);

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

    private void DeleteWaybillWithPeice(int position) {

        String WaybillNo = Selectedbarcodedetails.get(position).get("WaybillNo");

        for (int i = 0; i < AtOriginDeleteFirstFrgament.Selectedwaybilldetails.size(); i++) {
            if (WaybillNo.equals(AtOriginDeleteFirstFrgament.Selectedwaybilldetails.get(i).get("WaybillNo"))) {
                int ScannedPiece = Integer.parseInt(AtOriginDeleteFirstFrgament.Selectedwaybilldetails.get(i).get("ScannedPC")) - 1;
                if (ScannedPiece == 0) {
                    AtOriginDeleteFirstFrgament.Selectedwaybilldetails.remove(i);
                    AtOriginDeleteFirstFrgament.adapter.notifyDataSetChanged();
                    AtOriginDeleteFirstFrgament.lbTotal.setText(String.valueOf(AtOriginDeleteFirstFrgament.Selectedwaybilldetails.size()));


                } else
                    AtOriginDeleteFirstFrgament.Selectedwaybilldetails.get(i).put("ScannedPC", String.valueOf(ScannedPiece));

                break;
            }

        }

//        Selectedbarcodedetails.remove(position);
        adapter.removeItem(position);
        lbTotal.setText("Count : " + String.valueOf(Selectedbarcodedetails.size()));
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

            initViews();
        }
    }
}