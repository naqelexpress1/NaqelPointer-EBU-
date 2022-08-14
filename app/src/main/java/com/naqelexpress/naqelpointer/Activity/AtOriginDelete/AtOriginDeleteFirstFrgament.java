package com.naqelexpress.naqelpointer.Activity.AtOriginDelete;

import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AtOriginDeleteFirstFrgament extends Fragment {

    private View rootView;
    public static ArrayList<HashMap<String, String>> Selectedwaybilldetails = new ArrayList<>();


    public static DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();

    private View view;
    static TextView lbTotal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {

            rootView = inflater.inflate(R.layout.deliverythirdfragment, container, false);

            view = rootView;

            EditText txtWaybilll = (EditText) rootView.findViewById(R.id.txtWaybilll);
            txtWaybilll.setVisibility(View.GONE);
            Button camera = (Button) rootView.findViewById(R.id.btnOpenCamera);
            camera.setVisibility(View.GONE);
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);

            Bundle bundle = getArguments();

            if (bundle != null) {
                Selectedwaybilldetails = (ArrayList<HashMap<String, String>>) bundle.getSerializable("Waybills");

            }
            lbTotal.setText("Count : " + String.valueOf(Selectedwaybilldetails.size()));
            initViews();
        }
        return rootView;
    }


    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(Selectedwaybilldetails, "WaybillNo");
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
//                                    adapter.removeItem(position);
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
        String WayBillNo = Selectedwaybilldetails.get(position).get("WaybillNo");
        ArrayList<HashMap<String, String>> Selectedbarcodedetails = new ArrayList<>();

        for (int i = 0; i < AtOriginDeleteSecondFrgament.Selectedbarcodedetails.size(); i++) {
            if (!AtOriginDeleteSecondFrgament.Selectedbarcodedetails.get(i).get("WaybillNo").equals(WayBillNo)) {

                Selectedbarcodedetails.add(AtOriginDeleteSecondFrgament.Selectedbarcodedetails.get(i));
            }
        }
        AtOriginDeleteSecondFrgament.Selectedbarcodedetails.clear();
        AtOriginDeleteSecondFrgament.Selectedbarcodedetails.addAll(Selectedbarcodedetails);
        AtOriginDeleteSecondFrgament.adapter.notifyDataSetChanged();
//        Selectedwaybilldetails.remove(position);
        adapter.removeItem(position);
        AtOriginDeleteSecondFrgament.lbTotal.setText("Count : " + String.valueOf(AtOriginDeleteSecondFrgament.Selectedbarcodedetails.size()));
        lbTotal.setText("Count : " + String.valueOf(Selectedwaybilldetails.size()));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
        }
    }
}