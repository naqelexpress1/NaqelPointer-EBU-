package com.naqelexpress.naqelpointer.MiscodeSearch;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.TerminalHandling.FirstFragment;

import java.util.ArrayList;
import java.util.List;

public class Miscode extends AppCompatActivity implements MiscodeAdapter.ContactsAdapterListener {
    private static final String TAG = Miscode.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<MiscodeModel> contactList;
    private MiscodeAdapter mAdapter;
    private SearchView searchView;

    // url to fetch contacts json


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.miscode);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        double ratio = ((float) (width))/300.0;
        int height = (int)(ratio*50);


        recyclerView = findViewById(R.id.recycler_view);
        contactList = new ArrayList<>();
        mAdapter = new MiscodeAdapter(this, contactList, this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);


        FacilityStatus();
    }


    public void FacilityStatus() {


        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//        Cursor result = dbConnections.Fill("select * from Facility", getApplicationContext());
//
//        contactList.clear();
//
//
//        // refreshing recycler view
//
//
//        if (result.getCount() > 0) {
//            result.moveToFirst();
//            do {
//                MiscodeModel items = new MiscodeModel();
//
//                HashMap<String, String> temp = new HashMap<>();
//
//                items.setFacilityID(result.getInt(result.getColumnIndex("FacilityID")));
//                items.setCountrycode(result.getString(result.getColumnIndex("CountryCode")));
//                items.setCityname(result.getString(result.getColumnIndex("Name")));
//                items.setCitycode(result.getString(result.getColumnIndex("Code")));
//                contactList.add(items);
//            }
//            while (result.moveToNext());
//        }

        Cursor result = dbConnections.Fill("select * from CityLists", getApplicationContext());

        contactList.clear();


        // refreshing recycler view


        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                MiscodeModel items = new MiscodeModel();



                items.setFacilityID(result.getInt(result.getColumnIndex("StationID")));
                items.setCountrycode(result.getString(result.getColumnIndex("CountryCode")));
                items.setCityname(result.getString(result.getColumnIndex("CityName")));
                items.setCitycode(result.getString(result.getColumnIndex("CityCode")));
                contactList.add(items);
            }
            while (result.moveToNext());
        }


        if (contactList.size() > 0)
            mAdapter.notifyDataSetChanged();

        dbConnections.close();

    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.miscode_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onContactSelected(MiscodeModel contact) {
        // Toast.makeText(getApplicationContext(), "Selected: " + contact.getCountryCode() + ", " + contact.getCityName(), Toast.LENGTH_LONG).show();
        FirstFragment.CheckPointTypeDDetailID = contact.FacilityID;
        FirstFragment.txtCheckPointTypeDDetail.setText(contact.getCityName() + "(" + contact.getCountryCode() + ")");
        finish();
    }
}
