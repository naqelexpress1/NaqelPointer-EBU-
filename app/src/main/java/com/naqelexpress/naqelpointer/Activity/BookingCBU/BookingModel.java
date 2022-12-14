package com.naqelexpress.naqelpointer.Activity.BookingCBU;

import android.os.Parcel;
import android.os.Parcelable;

public class BookingModel implements Parcelable {
    public int sNo;
    public int PickupSheetID;
    public int FromStationID;
    public int ToStationID;
    public String OrgCode = "";
    public String DestCode = "";
    public int WaybillNo;
    public String Code = "";
    public String ConsigneeName = "";
    public String Remark = "";
    public int PickupsheetDetailID;
    public String Lat = "0.0";
    public String Lng = "0.0";
    public String Date = "";
    public String PhoneNo = "";
    public int isPickedup;
    public String ClientName = "";
    public int ClientID;
    public int EmployID;
    public String GoodDesc = "";
    public String RefNo = "";
    public String MobileNo = "";

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public BookingModel() {
    }

    public String getGoodDesc() {
        return GoodDesc;
    }

    public void setGoodDesc(String goodDesc) {
        GoodDesc = goodDesc;
    }

    public String getRefNo() {
        return RefNo;
    }

    public void setRefNo(String refNo) {
        RefNo = refNo;
    }

    protected BookingModel(Parcel in) {
        sNo = in.readInt();
        PickupSheetID = in.readInt();
        FromStationID = in.readInt();
        ToStationID = in.readInt();
        OrgCode = in.readString();
        DestCode = in.readString();
        WaybillNo = in.readInt();
        Code = in.readString();
        ConsigneeName = in.readString();
        Remark = in.readString();
        PickupsheetDetailID = in.readInt();
        Lat = in.readString();
        Lng = in.readString();
        Date = in.readString();
        PhoneNo = in.readString();
        isPickedup = in.readInt();
        ClientName = in.readString();
        ClientID = in.readInt();
        EmployID = in.readInt();
        RefNo = in.readString();
        GoodDesc = in.readString();
        MobileNo = in.readString();
    }

    public static final Creator<BookingModel> CREATOR = new Creator<BookingModel>() {
        @Override
        public BookingModel createFromParcel(Parcel in) {
            return new BookingModel(in);
        }

        @Override
        public BookingModel[] newArray(int size) {
            return new BookingModel[size];
        }
    };

    public int getEmployID() {
        return EmployID;
    }

    public void setEmployID(int employID) {
        EmployID = employID;
    }

    public int getPickupSheetID() {
        return PickupSheetID;
    }

    public void setPickupSheetID(int pickupSheetID) {
        PickupSheetID = pickupSheetID;
    }

    public int getFromStationID() {
        return FromStationID;
    }

    public void setFromStationID(int fromStationID) {
        FromStationID = fromStationID;
    }

    public int getToStationID() {
        return ToStationID;
    }

    public void setToStationID(int toStationID) {
        ToStationID = toStationID;
    }

    public String getOrgCode() {
        return OrgCode;
    }

    public void setOrgCode(String orgCode) {
        OrgCode = orgCode;
    }

    public String getDestCode() {
        return DestCode;
    }

    public void setDestCode(String destCode) {
        DestCode = destCode;
    }

    public int getWaybillNo() {
        return WaybillNo;
    }

    public void setWaybillNo(int waybillNo) {
        WaybillNo = waybillNo;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getConsigneeName() {
        return ConsigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        ConsigneeName = consigneeName;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public int getPickupsheetDetailID() {
        return PickupsheetDetailID;
    }

    public void setPickupsheetDetailID(int pickupsheetDetailID) {
        PickupsheetDetailID = pickupsheetDetailID;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public int getIsPickedup() {
        return isPickedup;
    }

    public void setIsPickedup(int isPickedup) {
        this.isPickedup = isPickedup;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    public int getClientID() {
        return ClientID;
    }

    public void setClientID(int clientID) {
        ClientID = clientID;
    }

    public int getsNo() {
        return sNo;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(sNo);
        parcel.writeInt(PickupSheetID);
        parcel.writeInt(FromStationID);
        parcel.writeInt(ToStationID);
        parcel.writeString(OrgCode);
        parcel.writeString(DestCode);
        parcel.writeInt(WaybillNo);
        parcel.writeString(Code);
        parcel.writeString(ConsigneeName);
        parcel.writeString(Remark);
        parcel.writeInt(PickupsheetDetailID);
        parcel.writeString(Lat);
        parcel.writeString(Lng);
        parcel.writeString(Date);
        parcel.writeString(PhoneNo);
        parcel.writeInt(isPickedup);
        parcel.writeString(ClientName);
        parcel.writeInt(ClientID);
        parcel.writeInt(EmployID);
        parcel.writeString(RefNo);
        parcel.writeString(GoodDesc);
        parcel.writeString(MobileNo);
    }
}