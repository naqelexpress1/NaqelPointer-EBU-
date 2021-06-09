package com.naqelexpress.naqelpointer.Activity.BookingCBU;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;


public class Booking implements Parcelable {
    public int ID;
    public String RefNo = "";
    public int ClientID;
    public String ClientName = "";
    public String ClientFName = "";
    public DateTime BookingDate = DateTime.now();
    public Double PicesCount = 0.0;
    public Double Weight = 0.0;
    public String SpecialInstruction = "";
    public DateTime OfficeUpTo = DateTime.now();
    public String PickUpReqDT = "";
    public String ContactPerson = "";
    public String ContactNumber = "";
    public String Address = "";
    public String Latitude = "0.0";
    public String Longitude = "0.0";
    public int Status = 0;
    public String Orgin = "";
    public String Destination = "";
    public String LoadType = "";
    public String BillType = "";
    public int EmployeeId = 0;
    public int OriginId = 0;
    public int DestinationId = 0;
    public String GPSLocation = "";
    public int PSID;
    public int PSDID;
    public int SNo;

    public Booking() {
    }

    public Booking(int psid, int psdid, String refno, String pickUpReqDT, String contactPerson,
                   String orgin, String destination, String billType, String contactNumber, int SNo
    ) {
        PSID = psid;
        PSDID = psdid;
        RefNo = refno;
        PickUpReqDT = pickUpReqDT;
        ContactPerson = contactPerson;
        Orgin = orgin;
        Destination = destination;
        BillType = billType;
        ContactNumber = contactNumber;
        this.SNo = SNo;

    }


    public Booking(Parcel in) {
        PSID = in.readInt();
        PSDID = in.readInt();
        ID = in.readInt();
        RefNo = in.readString();
        ClientID = in.readInt();
        ClientName = in.readString();
        ClientFName = in.readString();
        PicesCount = in.readDouble();
        if (in.readByte() == 0) {
            Weight = null;
        } else {
            Weight = in.readDouble();
        }
        SpecialInstruction = in.readString();
        OfficeUpTo = (DateTime) in.readSerializable();
        PickUpReqDT = in.readString();
        ContactPerson = in.readString();
        ContactNumber = in.readString();
        Address = in.readString();
        Latitude = in.readString();
        Longitude = in.readString();
        Status = in.readInt();
        Orgin = in.readString();
        Destination = in.readString();
        LoadType = in.readString();
        BillType = in.readString();
        EmployeeId = in.readInt();
        OriginId = in.readInt();
        DestinationId = in.readInt();
        GPSLocation = in.readString();
        SNo = in.readInt();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(PSID);
        out.writeInt(PSDID);
        out.writeInt(ID);
        out.writeString(RefNo);
        out.writeInt(ClientID);
        out.writeString(ClientName);
        out.writeString(ClientFName);
        out.writeDouble(PicesCount);
        if (Weight == null) {
            out.writeByte((byte) 0);
        } else {
            out.writeByte((byte) 1);
            out.writeDouble(Weight);
        }
        out.writeString(SpecialInstruction);
        out.writeSerializable(OfficeUpTo);
        out.writeString(PickUpReqDT);
        out.writeString(ContactPerson);
        out.writeString(ContactNumber);
        out.writeString(Address);
        out.writeString(Latitude);
        out.writeString(Longitude);
        out.writeInt(Status);
        out.writeString(Orgin);
        out.writeString(Destination);
        out.writeString(LoadType);
        out.writeString(BillType);
        out.writeInt(EmployeeId);
        out.writeInt(OriginId);
        out.writeInt(DestinationId);
        out.writeString(GPSLocation);
        out.writeInt(SNo);
    }
}