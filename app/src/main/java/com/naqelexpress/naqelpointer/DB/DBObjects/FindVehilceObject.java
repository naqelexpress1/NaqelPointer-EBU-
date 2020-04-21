package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

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