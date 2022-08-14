package com.naqelexpress.naqelpointer.ContactNo;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hasna on 10/30/18.
 */

public class Contact extends AppCompatActivity {
    private ArrayList<ListItem> items = new ArrayList<>();
    private ContactAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        recyclerView = (RecyclerView) findViewById(R.id.rvItems);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ContactAdapter(items);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new ContactAdapter(getApplicationContext(), recyclerView, new ContactAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ContactObj mobileno = (ContactObj) items.get(position);
                        GlobalVar.GV().makeCall(mobileno.getMobileno(), getWindow().getDecorView().getRootView(), Contact.this);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("select * from Contacts where StationID = " + GlobalVar.GV().StationID +
                " order by Isprimary asc", getApplicationContext());

        items.add(new Header("Operation Staff"));
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                HashMap<String, String> temp = new HashMap<>();
                String name = result.getString(result.getColumnIndex("Name"));
                String mobileno = result.getString(result.getColumnIndex("MobileNo"));
                items.add(new ContactObj(mobileno +" "+name, mobileno));

            }
            while (result.moveToNext());
        }
        result.close();
        dbConnections.close();

        items.add(new Header("IT Support"));
        items.add(new ContactObj("0593793637 Mohamed Ismail", "0593793637"));

        items.add(new Header("Services and emergency"));
        items.add(new ContactObj("9200 00560 - Najm", "9200 00560"));
        items.add(new ContactObj("933 - Saudi Electricity Customer Services", "933"));
        items.add(new ContactObj("937 - Saudi Ministry of Health Services", "937"));
        items.add(new ContactObj("939 - Saudi Water and Sewage Services (Eastern Region)", "939"));
        items.add(new ContactObj("940 - Saudi Municipal Services", "940"));
        items.add(new ContactObj("966 - Saudi Natural Disasters", "966"));
        items.add(new ContactObj("985 - Saudi General Intelligence Presidency", "985"));
        items.add(new ContactObj("989 - Saudi Public Security", "989"));
        items.add(new ContactObj("990 - Saudi Telephone Service for Security Issues", "990"));
        items.add(new ContactObj("992 - Saudi Passport", "992"));
        items.add(new ContactObj("993 - Saudi Traffic Police Force", "993"));
        items.add(new ContactObj("994 - Saudi Border Checkpoint", "994"));
        items.add(new ContactObj("995 - Saudi Anti-Narcotics", "995"));
        items.add(new ContactObj("996 - Saudi Highway Traffic Police Force", "996"));
        items.add(new ContactObj("997 - Saudi Red Crescent", "997"));
        items.add(new ContactObj("998 - Saudi Civil Defense", "998"));
        items.add(new ContactObj("999 - Saudi Police Force", "999"));
        items.add(new ContactObj("911 - Saudi Unified Emergency Number", "911"));


        adapter.notifyDataSetChanged();
    }
}
