package com.naqelexpress.naqelpointer.Activity.DeliverySheetPartial;

import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class SecondFrgament
        extends Fragment {
    View rootView;

    DataAdapter adapter;
    private RecyclerView recyclerView;
    private Paint p = new Paint();
    static TextView lbTotal;
    private View view;
    public static ArrayList<String> barcode = new ArrayList<>();

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
                barcode = bundle.getStringArrayList("BarCode");

            } else
                barcode.clear();
            lbTotal.setText("Count : " + String.valueOf(barcode.size()));

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
        adapter = new DataAdapter(barcode);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void removeView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
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