package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.naqelexpress.naqelpointer.utils.FinalClass;

public class UpdateData {
    FinalClass finalClass = new FinalClass();

    public boolean updateWaybillDestID_offset(Context context, String waybillNo, int newDestID) {

        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(finalClass.DBName).getPath(),
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

    public void UpdateAddtoScope(String IDs, Context context) {
        try {

            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(finalClass.DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("update AddtoScope set IsSync = 1 where ID in (" + IDs + ")");

        } catch (SQLiteException ignored) {

        }
    }
}
