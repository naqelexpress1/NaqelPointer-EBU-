package com.naqelexpress.naqelpointer.Activity.EBURoute;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewRoute_OLD extends Fragment {
    View rootView;

    ListviewAdapter adapter;
    ArrayList<HashMap<String, String>> deliverysheet = new ArrayList<>();

    protected void displayReceivedData(ArrayList<HashMap<String, String>> datas) {
        deliverysheet.clear();
        deliverysheet.addAll(datas);
        adapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {
                rootView = inflater.inflate(R.layout.listviewroute, container, false);

                adapter = new ListviewAdapter(deliverysheet, getContext());
                SwipeMenuListView recyclerView = (SwipeMenuListView) rootView.findViewById(R.id.listview);
                recyclerView.setAdapter(adapter);
            }

            return rootView;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
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