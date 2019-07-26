package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sofan on 17/03/2018.
 */

public class WaybillMeasurementDetail implements Parcelable {
    public int ID = 0;
    public int PiecesCount = 0;
    public double Width = 0;
    public double Length = 0;
    public double Height = 0;
    public boolean IsSync = false;
    public int WaybillMeasurementID = 0;

    public WaybillMeasurementDetail(int piecesCount, double width, double length, double height, int waybillMeasurementID) {
        PiecesCount = piecesCount;
        Width = width;
        Length = length;
        Height = height;
        WaybillMeasurementID = waybillMeasurementID;
    }

    protected WaybillMeasurementDetail(Parcel in) {
        ID = in.readInt();
        PiecesCount = in.readInt();
        Width = in.readDouble();
        Length = in.readDouble();
        Height = in.readDouble();
        IsSync = in.readByte() != 0;
        WaybillMeasurementID = in.readInt();
    }

    public static final Creator<WaybillMeasurementDetail> CREATOR = new Creator<WaybillMeasurementDetail>() {
        @Override
        public WaybillMeasurementDetail createFromParcel(Parcel in) {
            return new WaybillMeasurementDetail(in);
        }

        @Override
        public WaybillMeasurementDetail[] newArray(int size) {
            return new WaybillMeasurementDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeInt(PiecesCount);
        parcel.writeDouble(Width);
        parcel.writeDouble(Length);
        parcel.writeDouble(Height);
        parcel.writeByte((byte) (IsSync ? 1 : 0));
        parcel.writeInt(WaybillMeasurementID);
    }
}