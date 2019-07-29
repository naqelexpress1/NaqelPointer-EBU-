package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.naqelexpress.naqelpointer.Activity.MyRoute.MyRouteActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FindVehilceObject implements Parcelable {

    public int ID = 0;
    public String Name = "";


    public FindVehilceObject(int TruckID, String VehicleName, Context context) {
        this.ID = TruckID;
        this.Name = VehicleName;
    }

    public FindVehilceObject() {

    }


    protected FindVehilceObject(Parcel in) {
        ID = in.readInt();
        Name = in.readString();

    }

    public static final Creator<FindVehilceObject> CREATOR = new Creator<FindVehilceObject>() {
        @Override
        public FindVehilceObject createFromParcel(Parcel in) {
            return new FindVehilceObject(in);
        }

        @Override
        public FindVehilceObject[] newArray(int size) {
            return new FindVehilceObject[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(Name);
    }

}