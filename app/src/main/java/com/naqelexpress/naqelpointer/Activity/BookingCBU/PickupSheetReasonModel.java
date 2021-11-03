package com.naqelexpress.naqelpointer.Activity.BookingCBU;

import android.os.Parcel;
import android.os.Parcelable;

public class PickupSheetReasonModel implements Parcelable {
    public int ID;

    public String Name = "";

    public PickupSheetReasonModel() {

    }

    protected PickupSheetReasonModel(Parcel in) {
        ID = in.readInt();
        Name = in.readString();
    }

    public static final Creator<PickupSheetReasonModel> CREATOR = new Creator<PickupSheetReasonModel>() {
        @Override
        public PickupSheetReasonModel createFromParcel(Parcel in) {
            return new PickupSheetReasonModel(in);
        }

        @Override
        public PickupSheetReasonModel[] newArray(int size) {
            return new PickupSheetReasonModel[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


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