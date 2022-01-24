package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class UpdateData {
    private final String DBName = "NaqelPointerDB.db";

    public boolean updateWaybillDestID_offset(Context context, String waybillNo, int newDestID) {

        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

        ContentValues contentValues = new ContentValues();
        contentValues.put("WaybillDestID", newDestID);

        try {
            String args[] = {waybillNo};
            db.update("OnLineValidationOffset", contentValues, "WaybillNo=?", args);
        } catch (Exception e) {
            db.close();
            return false;
        }
        if (db != null)
            db.close();
        return true;
    }
}
