package com.naqelexpress.naqelpointer.Activity.DataActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.ShowData;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class ShowDataActivity
        extends AppCompatActivity {
    private EditText txtDataType;
    private Button btnCheck;
    public ArrayList<ShowData> DataList = new ArrayList<>();
    com.naqelexpress.naqelpointer.Classes.SpinnerDialog SpinnerDialog;
    private SwipeMenuListView mapListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showdata);

        btnCheck = (Button) findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowData();
            }
        });

        txtDataType = (EditText) findViewById(R.id.txtDataType);
        txtDataType.setInputType(InputType.TYPE_NULL);
        txtDataType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    SpinnerDialog.showSpinerDialog(false);
            }
        });

        SpinnerDialog = new SpinnerDialog(ShowDataActivity.this, GlobalVar.GV().StationNameList, "Select or Search Dat aType", R.style.DialogAnimations_SmileWindow);
    }

    private void ShowData() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        DataList = new ArrayList<>();
        Cursor result = dbConnections.Fill("select * from OnDelivery", getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                ShowData showData = new ShowData();
                showData.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                showData.Date = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
                showData.ItemNo = result.getString(result.getColumnIndex("WaybillNo"));
                showData.IsSync = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsSync")));
                DataList.add(showData);
            }
            while (result.moveToNext());
        }
        dbConnections.close();
    }
}
