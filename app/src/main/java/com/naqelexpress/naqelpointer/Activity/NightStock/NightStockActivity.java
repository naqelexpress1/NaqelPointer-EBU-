package com.naqelexpress.naqelpointer.Activity.NightStock;


import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.naqelexpress.naqelpointer.R;

import java.util.HashMap;

public class NightStockActivity extends AppCompatActivity {
    String[] cellTitle;
    int cellIcon[];
    GridView gridView;
    HashMap<Integer, Integer> itemposition = new HashMap<Integer, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night_stock);
//        Toast.makeText(this, "THIS Night Stock Activity", Toast.LENGTH_SHORT).show();

//        LoadMenu();
//        gridView = (GridView) findViewById(R.id.gridnightStock);
//
//        MainPageCellAdapter adapter = new MainPageCellAdapter(NightStockActivity.this, cellIcon, cellTitle);
//        gridView.setAdapter(adapter);
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                position = itemposition.get(position);
//                switch (position) {
//                    case 0:
//                        if (GlobalVar.AskPermission_Camera(NightStockActivity.this, 6)) {
//                            Intent nightStockScan = new Intent(getApplicationContext(), NSScanShipmentActivity.class);
//                            startActivity(nightStockScan);
//
//                        }
//                        break;
//                    case 1:
//
//                           Intent Inventory = new Intent(getApplicationContext(), InventoryActivity.class);
//                           startActivity(Inventory);
//                           break;
//
//                }
//            }
//        });
    }
//    private void LoadMenu()
//    {
//        cellTitle = new String[2];
//        cellIcon = new int[2];
//        cellTitle[0] = getResources().getString(R.string.ns_scanshipment);//CBU
//        cellTitle[1] = getResources().getString(R.string.ns_inventory);//CBU
//        itemposition.put(0, 0);
//        itemposition.put(1, 1);
//        cellIcon[0] = R.drawable.deliverysheet;
//        cellIcon[1] = R.drawable.maplist;
//    }
}
