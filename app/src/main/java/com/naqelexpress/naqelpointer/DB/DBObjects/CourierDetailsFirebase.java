package com.naqelexpress.naqelpointer.DB.DBObjects;

public class CourierDetailsFirebase {

    public String EmpID;
    public String LatLng;
    public String EmpName;
    public String WaybillNo;
    public String MobileNo;
    public String ConsLocation;
    public String ID;
    public String NextWaybillNo;
    public float Speed;
    public String isnotify;
    public String BillingType;
    public String CollectedAmount;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public CourierDetailsFirebase() {
    }

    public CourierDetailsFirebase(String EmpID, String LatLng, String EmpName, String WaybillNo, String MobileNo, String ConsLocation, String NextWaybillNo, float Speed, String isnotify,
                                  String BillingType, String CollectedAmount) {
        this.EmpID = EmpID;
        this.LatLng = LatLng;
        this.EmpName = EmpName;
        this.WaybillNo = WaybillNo;
        this.MobileNo = MobileNo;
        this.ConsLocation = ConsLocation;
        this.NextWaybillNo = NextWaybillNo;
        this.Speed = Speed;
        this.isnotify = isnotify;
        this.BillingType = BillingType;
        this.CollectedAmount = CollectedAmount;
    }


}