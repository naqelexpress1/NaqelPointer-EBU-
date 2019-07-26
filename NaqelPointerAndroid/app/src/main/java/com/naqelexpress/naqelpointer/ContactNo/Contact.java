package com.naqelexpress.naqelpointer.ContactNo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

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

        items.add(new Header("Services and emergency"));
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


        items.add(new Header("Operation Staff"));
        items.add(new ContactObj("+966580233562 Praneeth", "+966580233562"));
        items.add(new Header("Operation IT Support"));
        items.add(new ContactObj("+966597307693 Anish", "+966597307693"));
        items.add(new ContactObj("+966597873780 Sheik", "+966597873780"));

        adapter.notifyDataSetChanged();
    }
}
