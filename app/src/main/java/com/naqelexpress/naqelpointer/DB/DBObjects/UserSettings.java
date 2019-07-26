package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

/**
 * Created by sofan on 13/10/2017.
 */

public class UserSettings implements Parcelable {
    public int ID = 0;
    public int EmployID = GlobalVar.GV().EmployID;
    public String IPAddress = "";
    public boolean ShowScaningCamera = false;
    public DateTime LastBringMasterData = DateTime.now().withFieldAdded(DurationFieldType.days(), -30);

    public UserSettings() {
    }

    public UserSettings(String IPAddress, boolean showScaningCamera) {
        this.IPAddress = IPAddress;
        ShowScaningCamera = showScaningCamera;
    }

    public UserSettings(int ID, int employID, String IPAddress, boolean showScaningCamera, DateTime lastBringMasterData) {
        this.ID = ID;
        EmployID = employID;
        this.IPAddress = IPAddress;
        ShowScaningCamera = showScaningCamera;
        LastBringMasterData = lastBringMasterData;
    }

    protected UserSettings(Parcel in) {
        ID = in.readInt();
        EmployID = in.readInt();
        IPAddress = in.readString();
        ShowScaningCamera = in.readByte() != 0;
        LastBringMasterData = (DateTime) in.readSerializable();
    }

    public static final Creator<UserSettings> CREATOR = new Creator<UserSettings>() {
        @Override
        public UserSettings createFromParcel(Parcel in) {
            return new UserSettings(in);
        }

        @Override
        public UserSettings[] newArray(int size) {
            return new UserSettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeInt(EmployID);
        parcel.writeString(IPAddress);
        parcel.writeByte((byte) (ShowScaningCamera ? 1 : 0));
        parcel.writeSerializable(LastBringMasterData);
    }
}