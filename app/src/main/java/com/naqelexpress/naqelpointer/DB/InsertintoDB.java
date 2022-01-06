package com.naqelexpress.naqelpointer.DB;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
}
