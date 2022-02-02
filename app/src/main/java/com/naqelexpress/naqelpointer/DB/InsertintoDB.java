package com.naqelexpress.naqelpointer.DB;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.TLAllocationAreaModels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class InsertintoDB {

    private static final String DBName = "NaqelPointerDB.db";

    public void insertFacilityAllowedStationBulk(JSONArray facilityallowedstation, Activity activity) {

        String sql = "insert into FacilityAllowedStation (FacilityID,AllowedStationID) values (?, ?);";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(activity.getDatabasePath(DBName).getPath(),
                null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        try {
            db.execSQL("delete from FacilityAllowedStation");
            db.beginTransaction();
            SQLiteStatement stmt = db.compileStatement(sql);

            for (int i = 0; i < facilityallowedstation.length(); i++) {
                try {

                    JSONObject jsonObject = facilityallowedstation.getJSONObject(i);
                    stmt.bindString(1, jsonObject.getString("FacilityID"));
                    stmt.bindString(2, jsonObject.getString("AllowedStationID"));
                    stmt.executeInsert();
                    stmt.clearBindings();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        } catch (Exception ex) {
            Log.d("test", "ex " + ex.toString());
        }

        if (db != null)
            db.close();
    }

    public void insertTLAllcocationAreaBulk(List<TLAllocationAreaModels> list, Context context) {
        String sql = "insert into TLAreaAllocation (AreaName, DominateArea, TeamName , WaybillNo , BarCode , DummyCourierID , DestStaionID," +
                "sysDate ) values (?, ?, ?, ? , ? , ?, ? , ?);";

        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null,
                SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        db.execSQL("delete from TLAreaAllocation");
        //db.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(sql);

        for (TLAllocationAreaModels models : list) {
            stmt.bindString(1, models.getAreaName());
            stmt.bindString(2, models.getDominateArea());
            stmt.bindString(3, models.getTeamName()); //GlobalVar.GV().getCurrentDateTime()
            stmt.bindString(4, models.getWaybillNo());
            stmt.bindString(5, models.getBarCode());
            stmt.bindString(6, models.getDummyCourierID());
            stmt.bindString(7, String.valueOf(models.getDestStaionID()));
            stmt.bindString(8, GlobalVar.getCurrentDateTime());
            long entryID = stmt.executeInsert();
            stmt.clearBindings();

        }

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
    }
}
