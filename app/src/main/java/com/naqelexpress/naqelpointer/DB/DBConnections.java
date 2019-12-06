package com.naqelexpress.naqelpointer.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBObjects.Booking;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPoint;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointBarCodeDetails;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointType;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointTypeDDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointTypeDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointWaybillDetails;
import com.naqelexpress.naqelpointer.DB.DBObjects.CourierDailyRoute;
import com.naqelexpress.naqelpointer.DB.DBObjects.DeliveryStatus;
import com.naqelexpress.naqelpointer.DB.DBObjects.MultiDelivery;
import com.naqelexpress.naqelpointer.DB.DBObjects.MultiDeliveryDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.MultiDeliveryWaybillDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipmentsNew;
import com.naqelexpress.naqelpointer.DB.DBObjects.Ncl;
import com.naqelexpress.naqelpointer.DB.DBObjects.NclDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.NclWaybillDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.NightStock;
import com.naqelexpress.naqelpointer.DB.DBObjects.NightStockDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.NightStockWaybillDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.NoNeedVolumeReason;
import com.naqelexpress.naqelpointer.DB.DBObjects.NotDelivered;
import com.naqelexpress.naqelpointer.DB.DBObjects.NotDeliveredDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.OFDPieceLevel;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDWaybill;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCloadingForD;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnDelivery;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnDeliveryDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.PickUp;
import com.naqelexpress.naqelpointer.DB.DBObjects.PickUpDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserME;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserSettings;
import com.naqelexpress.naqelpointer.DB.DBObjects.WaybillMeasurement;
import com.naqelexpress.naqelpointer.DB.DBObjects.WaybillMeasurementDetail;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class DBConnections
        extends SQLiteOpenHelper {
    private static final int Version = 91; // Change the concept of deliver and not deliver
    private static final String DBName = "NaqelPointerDB.db";
    //    public Context context;
    public View rootView;

    //Added by ismail
    public static final String TABLENAME = "signature";
    public static final String COLUMNID = "id";
    public static final String COLUMNNAME_FILE = "filename";
    public static final String COLUMNNAME_EMPID = "empid";
    public static final String COLUMNNAME_FLAG = "flag";
    private String ddate;

    public DBConnections(Context context, View view) {
        super(context, DBName, null, Version);
        //this.context = context;
        this.rootView = view;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS \"UserME\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"EmployID\" INTEGER NOT NULL" +
                " , \"Password\" TEXT NOT NULL, \"StationID\" INTEGER NOT NULL , \"RoleMEID\" INTEGER, \"StatusID\" INTEGER NOT NULL," +
                " \"MachineID\" TEXT,  \"EmployName\" TEXT, \"EmployFName\" TEXT, \"MobileNo\" TEXT, \"StationCode\" TEXT, " +
                "\"StationName\" TEXT, \"StationFName\" TEXT,\"Division\" TEXT DEFAULT 0 ," +
                "\"UserTypeID\"  INTEGER NOT NULL,\"Date\"  TEXT ,\"Menu\"  INTEGER  DEFAULT 0 ,  \"TruckID\" INTEGER DEFAULT 0," +
                " UpdateMenu Integer Default 0 , DisableEnabletxtBox Integer Default 1 ," +
                "CountryID Interger , CountryCode TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"UserLogs\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"UserID\" INTEGER NOT NULL , \"SuperVisorID\" INTEGER NOT NULL, \"IsSync\" BOOL NOT NULL , \"LogTypeID\" INTEGER NOT NULL , \"CTime\" DATETIME NOT NULL , \"MachineID\" TEXT , \"Remarks\" TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"UserMeLogin\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE ," +
                " \"EmployID\" INTEGER NOT NULL , \"StateID\" INTEGER NOT NULL , \"Date\" DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP," +
                " \"HHDName\" TEXT check(typeof(\"HHDName\") = 'text') , \"Version\" TEXT NOT NULL  check(typeof(\"Version\") = 'text') , \"IsSync\" BOOL NOT NULL , \"TruckID\" INTEGER , \"LogoutDate\" DATETIME , \"LogedOut\" BOOL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"OnDelivery\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                "\"WaybillNo\" INTEGER NOT NULL , \"ReceiverName\" TEXT (100) NOT NULL , \"PiecesCount\" INTEGER NOT NULL ," +
                " \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" INTEGER NOT NULL , \"EmployID\" INTEGER NOT NULL , " +
                "\"StationID\" INTEGER NOT NULL , \"IsPartial\" BOOL NOT NULL  DEFAULT 0, \"Latitude\" TEXT, \"Longitude\" TEXT ," +
                " \"TotalReceivedAmount\" DOUBLE NOT NULL , \"CashAmount\" DOUBLE NOT NULL DEFAULT 0, \"POSAmount\" DOUBLE NOT NULL DEFAULT 0 ," +
                " \"IsSync\" BOOL NOT NULL,\"AL\" INTEGER DEFAULT 0 , Barcode Text)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"OnDeliveryDetail\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , " +
                "\"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"DeliveryID\" INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Station\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Code\" TEXT, \"Name\" TEXT NOT NULL , \"FName\" TEXT, \"CountryID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"DeliveryStatus\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , " +
                "\"Code\" TEXT, \"Name\" TEXT NOT NULL , \"FName\" TEXT , SeqOrder INTEGER )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"NotDelivered\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , " +
                "\"WaybillNo\" TEXT NOT NULL ," +
                " \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" DATETIME NOT NULL , \"UserID\" INTEGER NOT NULL ," +
                " \"IsSync\" BOOL NOT NULL , \"StationID\" INTEGER NOT NULL , \"PiecesCount\" INTEGER NOT NULL ," +
                " \"DeliveryStatusID\" INTEGER NOT NULL ,\"DeliveryStatusReasonID\" INTEGER NOT NULL, \"Notes\" TEXT, " +
                "\"Latitude\" TEXT, \"Longitude\" TEXT , Barcode Text )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"NotDeliveredDetail\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE ," +
                " \"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"NotDeliveredID\" INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUp\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"WaybillNo\" TEXT NOT NULL ," +
                " \"ClientID\" INTEGER, \"FromStationID\" INTEGER NOT NULL , \"ToStationID\" INTEGER NOT NULL , " +
                "\"PieceCount\" INTEGER NOT NULL , \"Weight\" DOUBLE, \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" DATETIME NOT NULL , " +
                "\"IsSync\" BOOL NOT NULL , \"UserID\" INTEGER NOT NULL , \"StationID\" INTEGER NOT NULL , \"RefNo\" TEXT, \"Latitude\"" +
                " TEXT, \"CurrentVersion\" TEXT NOT NULL, \"Longitude\" TEXT ,\"LoadTypeID\" INTEGER NOT NULL,\"AL\" INTEGER DEFAULT 0," +
                " \"TruckID\" Integer Default 0)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUpDetail\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"PickUpID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"UserSettings\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE  , \"EmployID\" INTEGER NOT NULL , \"ShowScaningCamera\" BOOL NOT NULL , \"IPAddress\" TEXT NOT NULL , \"LastBringMasterData\" DATETIME)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CourierDailyRoute\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , \"EmployID\" INTEGER NOT NULL , \"StartingTime\" DATETIME NOT NULL , \"StartLatitude\" TEXT, \"StartLongitude\" TEXT, " +
                "\"EndTime\" DATETIME  , \"EndLatitude\" TEXT, \"EndLongitude\" TEXT, \"DeliverySheetID\" INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"OnCloadingForD\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , \"CourierID\" INTEGER NOT NULL , \"UserID\" INTEGER NOT NULL , \"IsSync\" BOOL NOT NULL , \"CTime\" DATETIME NOT NULL , \"PieceCount\" INTEGER NOT NULL , \"TruckID\" TEXT, \"WaybillCount\" INTEGER NOT NULL , \"StationID\" INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"OnCLoadingForDDetail\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
                "\"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"OnCLoadingForDID\" INTEGER NOT NULL ,\"WaybillNo\" TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"OnCLoadingForDWaybill\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL ," +
                "\"WaybillNo\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"OnCLoadingID\" INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"MyRouteShipments\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
                "\"OrderNo\" INTEGER NOT NULL , \"ItemNo\" TEXT NOT NULL , \"TypeID\" INTEGER NOT NULL , \"BillingType\" TEXT, " +
                "\"CODAmount\" DOUBLE NOT NULL , \"DeliverySheetID\" INTEGER, \"Date\" DATETIME NOT NULL , \"ExpectedTime\" DATETIME," +
                " \"Latitude\" TEXT NOT NULL , \"Longitude\" TEXT NOT NULL , \"ClientID\" INTEGER NOT NULL , " +
                "\"ClientName\" TEXT NOT NULL , \"ClientFName\" TEXT NOT NULL , \"ClientAddressPhoneNumber\" TEXT NOT NULL ," +
                " \"ClientAddressFirstAddress\" TEXT NOT NULL , \"ClientAddressSecondAddress\" TEXT NOT NULL ," +
                " \"ClientContactName\" TEXT NOT NULL , \"ClientContactFName\" TEXT NOT NULL , \"ClientContactPhoneNumber\" TEXT ," +
                " \"ClientContactMobileNo\" TEXT NOT NULL , \"ConsigneeName\" TEXT NOT NULL , \"ConsigneeFName\" TEXT NOT NULL ," +
                " \"ConsigneePhoneNumber\" TEXT NOT NULL , \"ConsigneeFirstAddress\" TEXT NOT NULL , " +
                "\"ConsigneeSecondAddress\" TEXT NOT NULL , \"ConsigneeNear\" TEXT NOT NULL , \"ConsigneeMobile\" TEXT NOT NULL , " +
                "\"Origin\"  TEXT NOT NULL , \"Destination\" TEXT NOT NULL , \"PODNeeded\" BOOL NOT NULL , " +
                "\"PODDetail\" TEXT NOT NULL , \"PODTypeCode\" TEXT NOT NULL , \"PODTypeName\" TEXT NOT NULL , \"IsDelivered\" BOOL , " +
                "\"NotDelivered\" BOOL , \"CourierDailyRouteID\" INTEGER , \"OptimzeSerialNo\" INTEGER , \"HasComplaint\" BOOL, " +
                "\"HasDeliveryRequest\" BOOL ,\"DDate\" TEXT NOT NULL,\"EmpID\" INTEGER NOT NULL,\"Weight\" TEXT NOT NULL," +
                "\"PiecesCount\" TEXT NOT NULL, \"Sign\" INTEGER Default 0 ,\"SeqNo\" INTEGER Default 0 ," +
                "\"OnDeliveryDate\" DATETIME ,\"POS\" INTEGER Default 0 ,\"Notification\" INTEGER Default 0 ," +
                "\"Refused\" BOOL , \"PartialDelivered\" BOOL  , \"UpdateDeliverScan\" BOOL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPoint\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , " +
                "\"EmployID\" INTEGER NOT NULL , \"Date\" DATETIME NOT NULL , \"CheckPointTypeID\" INTEGER NOT NULL , " +
                "\"CheckPointTypeDetailID\" INTEGER  , \"CheckPointTypeDDetailID\" INTEGER  , \"Latitude\" TEXT, " +
                "\"Longitude\" TEXT, \"IsSync\" BOOL NOT NULL ,\"Comments\" TEXT , \"Ref\" TEXT,\"Count\" INTEGER Default 0)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointWaybillDetails\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"WaybillNo\" TEXT NOT NULL , \"CheckPointID\" INTEGER NOT NULL , \"IsSync\" BOOL NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointBarCodeDetails\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"BarCode\" TEXT NOT NULL , \"CheckPointID\" INTEGER NOT NULL , \"IsSync\" BOOL NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointType\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Name\" TEXT NOT NULL , \"FName\" TEXT )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointTypeDetail\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Name\" TEXT NOT NULL , \"FName\" TEXT , \"CheckPointTypeID\" INTEGER NOT NULL  )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointTypeDDetail\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Name\" TEXT NOT NULL , \"FName\" TEXT , \"CheckPointTypeDetailID\" INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"MultiDelivery\"  ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, " +
                "\"ReceiverName\" TEXT NOT NULL, \"PiecesCount\" INTEGER NOT NULL, \"TimeIn\" DATETIME NOT NULL , " +
                "\"TimeOut\" DATETIME NOT NULL , \"UserID\" INTEGER NOT NULL, \"IsSync\" BOOL NOT NULL, " +
                "\"StationID\" INTEGER NOT NULL, \"WaybillsCount\" INTEGER NOT NULL, \"Latitude\" TEXT, \"Longitude\" TEXT," +
                " \"ReceivedAmt\" DOUBLE, \"ReceiptNo\" TEXT, \"StopPointsID\" INTEGER,\"AL\" INTEGER DEFAULT 0 )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"MultiDeliveryDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"BarCode\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"MultiDeliveryID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"MultiDeliveryWaybillDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"WaybillNo\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"MultiDeliveryID\" INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"WaybillMeasurement\"  ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, " +
                "\"WaybillNo\" INTEGER NOT NULL, \"TotalPieces\" INTEGER NOT NULL, \"EmployID\" INTEGER NOT NULL , " +
                "\"StationID\" INTEGER NOT NULL , \"CTime\" DATETIME NOT NULL, \"IsSync\" BOOL NOT NULL, \"HHD\" TEXT," +
                " \"Weight\" DOUBLE NOT NULL, \"NoNeedVolume\" BOOL NOT NULL , \"NoNeedVolumeReasonID\" INTEGER , " +
                "\"UserID\" INTEGER )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"WaybillMeasurementDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"PiecesCount\" INTEGER NOT NULL, \"Width\" DOUBLE NOT NULL, \"Length\" DOUBLE NOT NULL, \"Height\" DOUBLE NOT NULL, \"IsSync\" BOOL NOT NULL, \"WaybillMeasurementID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"NoNeedVolumeReason\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Name\" TEXT NOT NULL , \"FName\" TEXT )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"Booking\" (\"ID\" INTEGER PRIMARY KEY NOT NULL  UNIQUE  ,\"RefNo\" TEXT  NOT NULL ,\"ClientID\" INTEGER NOT NULL ,\"ClientName\" TEXT NOT NULL,\"ClientFName\" TEXT  NOT NULL, \"BookingDate\" DATETIME NOT NULL,\"PiecesCount\" INTEGER NOT NULL , \"Weight\" DOUBLE NOT NULL ,\"SpecialInstruction\" TEXT  NOT NULL,\"OfficeUpTo\" DATETIME NOT NULL ,\"PickUpReqDT\" DATETIME NOT NULL, \"ContactPerson\" TEXT  NOT NULL,\"ContactNumber\" TEXT  NOT NULL,\"Address\" TEXT  NOT NULL, \"EmployID\" INTEGER NOT NULL ,  \"Latitude\" TEXT, \"Longitude\" TEXT ,\"BillType\" TEXT , \"LoadType\" TEXT ,\"Orgin\" TEXT ,\"Destination\" TEXT ,\"Status\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CallingHistory\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , \"EmployID\" INTEGER NOT NULL , \"MobileNo\" TEXT , \"CallingTime\" DATETIME, \"CallType\" TEXT , \"WaybillNo\" TEXT NOT NULL ,  \"IsSync\" BOOL NOT NULL )");

        //Added by ismail
        db.execSQL("create table if not exists " + TABLENAME + "(" + COLUMNID
                + " integer primary key," + COLUMNNAME_FILE + " text,"
                + COLUMNNAME_EMPID + " text," + COLUMNNAME_FLAG
                + " integer not null default 1)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"MobileNo\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                "\"Name\" INTEGER NOT NULL , \"MobileNo\" TEXT, \"RawID\" INTEGER  )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"LoadtoDestination\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Productivity\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"OFD\"  INTEGER NOT NULL DEFAULT 0 , \"Attempted\"  INTEGER NOT NULL DEFAULT 0," +
                "\"Delivered\"  INTEGER NOT NULL DEFAULT 0, \"Exceptions\"  INTEGER NOT NULL DEFAULT 0, \"Date\" DATETIME NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"CallLog\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Type\"  text NOT NULL , \"MNO\"  text NOT NULL," +
                "\"Duration\"  text NOT NULL, \"WayBillNo\"  text , \"Date\" DATETIME NOT NULL, \"CallStartTime\" DATETIME NOT NULL," +
                "\"CallEndTime\" DATETIME NOT NULL , \"Number\"  text, \"EmpID\"  INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"AppVersion\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Version\"  INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Complaint\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"TotalComp\"  INTEGER NOT NULL DEFAULT 0 , \"Attempted\"  INTEGER NOT NULL DEFAULT 0," +
                "\"Delivered\"  INTEGER NOT NULL DEFAULT 0, \"Exceptions\"  INTEGER NOT NULL DEFAULT 0, \"Date\" DATETIME NOT NULL ," +
                "\"Request\"  INTEGER NOT NULL DEFAULT 0)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"AtOrigin\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Palletize\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"TripPlanDetails\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"TripPlanDDetails\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL,\"TripPlanNo\" Integer , \"IsSync\" Integer )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestination\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"BarCode\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"BarCode\"  TEXT NOT NULL, \"WayBillNo\"  TEXT NOT NULL,\"Date\" TEXT NOT NULL , \"IsDelivered\" INTEGER Default 0 , \"WayBillID\" INTEGER Default 0 )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Discrepancy\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"DeviceToken\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CurrentLocation\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"ActualLocation\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Radius200\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Lat\"  TEXT NOT NULL, \"Long\"  TEXT NOT NULL ,\"Timein\"  DATETIME NOT NULL,\"Timeout\"  DATETIME NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"SendConsigneeNotification\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Data\"  TEXT NOT NULL, \"WayBillNo\"  TEXT NOT NULL ,\"Timein\"  DATETIME NOT NULL,\"Timeout\"  DATETIME NOT NULL )");


        db.execSQL("CREATE TABLE IF NOT EXISTS \"LastLogin\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"EmpID\"  INTEGER NOT NULL ,\"AndroidID\"  TEXT NOT NULL ,  \"UserTypeID\"  INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"NightStock\"  ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"PiecesCount\" INTEGER NOT NULL, " +
                "\"CTime\" DATETIME NOT NULL  ,\"UserID\" INTEGER NOT NULL, \"IsSync\" BOOL NOT NULL, \"StationID\" INTEGER NOT NULL, " +
                "\"WaybillsCount\" INTEGER NOT NULL,\"IDs\" NOT NULL DEFAULT 0,\"BIN\" TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"NightStockDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"BarCode\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"NightStockID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"NightStockWaybillDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE," +
                " \"WaybillNo\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"NightStockID\" INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"EmployInfo\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"EmpID\"  INTEGER NOT NULL ,\"EmpName\"  TEXT NOT NULL ,  \"IqamaNumber\"  TEXT NOT NULL," +
                " \"MobileNo\"  TEXT NOT NULL , \"StationID\"  INTEGER NOT NULL," +
                "  \"ImageName\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"FacilityLoggedIn\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"IsDate\"  TEXT NOT NULL , \"EmpID\"  INTEGER NOT NULL)");


        db.execSQL("CREATE TABLE IF NOT EXISTS \"Facility\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                "\"FacilityID\" Integer NOT NULL , \"Code\" Text NOT NULL , \"Name\" Text NOT NULL , \"Station\" Integer Not Null , " +
                "\"FacilityTypeID\" Integer Not Null, \"FacilityTypeName\" TEXT )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"DeliveryStatusReason\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , " +
                "\"ReasonID\" Integer NOT NULL ,\"Code\" TEXT, \"Name\" TEXT NOT NULL , \"FName\" TEXT, " +
                "\"DeliveyStatusID\" Integer NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Ncl\"( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE," +
                " \"NclNo\" TEXT  NOT NULL,\"UserID\" INTEGER NOT NULL, \"Date\" DATETIME NOT NULL  ,\"PieceCount\" INTEGER NOT NULL," +
                " \"WaybillCount\" INTEGER NOT NULL,\"IsSync\" BOOL NOT NULL,JsonData Text )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"NclDetail\"( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"BarCode\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"NclID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"NclWaybillDetail\"( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"WaybillNo\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"NclID\" INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestinationImages\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Contacts\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                "\"Name\" Text , \"StationID\" Integer , \"MobileNo\" Text , \"Isprimary\" Integer )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestWaybill\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                "\"WayBillNo\" Integer , \"Date\" DATETIME NOT NULL ,  \"TrailerNo\" Text )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestPiece\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                "\"BarCode\" TEXT , \"Date\" DATETIME NOT NULL , \"TrailerNo\" Text )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"AtOriginLastWaybill\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                "\"WaybillNo\" TEXT , \"PieceCount\" TEXT ,\"EmployID\" TEXT,\"UserID\" TEXT ," +
                "\"IsSync\" TEXT , \"StationID\" Text,\"ScannedPC\" Text,\"bgcolor\" Text " +
                ", \"isdelete\" Text, \"Date\" DATETIME NOT NULL  )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"AtOriginLastPieces\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                "\"WaybillNo\" TEXT , \"BarCode\" TEXT ,\"IsSync\" TEXT ,\"bgcolor\" Text " +
                ", \"isdelete\" Text, \"Date\" DATETIME NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"LoadtoDestLastWayBill\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                "\"WaybillNo\" TEXT  , \"Date\" DATETIME NOT NULL ,  \"TrailerNo\" Text )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"LoadtoDestLastPiece\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                "\"BarCode\" TEXT  , \"Date\" DATETIME NOT NULL ,  \"TrailerNo\" Text )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"CustomerRating\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                "\"Json\" TEXT  NOT NULL  )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"InCabCheckList\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"IsDate\"  TEXT NOT NULL , \"EmpID\"  INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"TerminalHandling\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                "\"Json\" TEXT  NOT NULL , \"Count\" Integer DEFAULT 0)");


        db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUpAuto\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , \"WaybillNo\" TEXT NOT NULL ," +
                " \"ClientID\" INTEGER, \"FromStationID\" INTEGER NOT NULL , \"ToStationID\" INTEGER NOT NULL , " +
                "\"PieceCount\" INTEGER NOT NULL , \"Weight\" DOUBLE, \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" DATETIME NOT NULL , " +
                "\"IsSync\" BOOL NOT NULL , \"UserID\" INTEGER NOT NULL , \"StationID\" INTEGER NOT NULL , \"RefNo\" TEXT, \"Latitude\"" +
                " TEXT, \"CurrentVersion\" TEXT NOT NULL, \"Longitude\" TEXT ,\"LoadTypeID\" INTEGER NOT NULL,\"AL\" INTEGER DEFAULT 0," +
                " \"TruckID\" Integer Default 0 , JsonData Text Not Null)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUpDetailAuto\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , " +
                "\"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"PickUpID\" INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUpTemp\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"WaybillNo\" TEXT NOT NULL ," +
                " \"ClientID\" INTEGER, \"FromStationID\" INTEGER NOT NULL , \"ToStationID\" INTEGER NOT NULL , " +
                "\"PieceCount\" INTEGER NOT NULL , \"Weight\" DOUBLE, \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" DATETIME NOT NULL , " +
                "\"IsSync\" BOOL NOT NULL , \"UserID\" INTEGER NOT NULL , \"StationID\" INTEGER NOT NULL , \"RefNo\" TEXT, \"Latitude\"" +
                " TEXT, \"CurrentVersion\" TEXT NOT NULL, \"Longitude\" TEXT ,\"LoadTypeID\" INTEGER NOT NULL,\"AL\" INTEGER DEFAULT 0 ," +
                " \"TruckID\" Integer Default 0 , JsonData Text Not Null)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUpDetailTemp\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"PickUpID\" INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Truck\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE , \"Name\" TEXT NOT NULL , \"FName\" TEXT," +
                " \"TruckID\" Interger Not Null,\"IsDate\"  TEXT NOT NULL  )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"UpdateMenu\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE , " +
                " \"MenuChanges\" Integer Default 0 )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"DeliverReq\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"WaybillNo\"  TEXt NOT NULL ,BarCode  TEXT NOT NULL , InsertedDate TEXT NOT NULL , ValidDate TEXT NOT NULL  )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"RtoReq\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"WaybillNo\"  TEXT NOT NULL ,BarCode  TEXT NOT NULL , InsertedDate TEXT NOT NULL , ValidDate TEXT NOT NULL )");

    }

    public int getVersion() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.getVersion();
    }

    private SQLiteDatabase mDefaultWritableDatabase = null;

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if (oldVersion < 2)
//        {
//            db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPoint\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"EmployID\" INTEGER NOT NULL , \"Date\" DATETIME NOT NULL , \"CheckPointTypeID\" INTEGER NOT NULL , \"Latitude\" TEXT, \"Longitude\" TEXT, \"IsSync\" BOOL NOT NULL )");
//            db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointWaybillDetails\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"WaybillNo\" TEXT NOT NULL , \"CheckPointID\" INTEGER NOT NULL , \"IsSync\" BOOL NOT NULL )");
//            db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointBarCodeDetails\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"BarCode\" TEXT NOT NULL , \"CheckPointID\" INTEGER NOT NULL , \"IsSync\" BOOL NOT NULL )");
//            db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointType\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Name\" TEXT NOT NULL , \"FName\" TEXT )");
//            db.execSQL("CREATE TABLE IF NOT EXISTS \"MultiDelivery\"  ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"ReceiverName\" TEXT NOT NULL, \"PiecesCount\" INTEGER NOT NULL, \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" DATETIME NOT NULL , \"UserID\" INTEGER NOT NULL, \"IsSync\" BOOL NOT NULL, \"StationID\" INTEGER NOT NULL, \"WaybillsCount\" INTEGER NOT NULL, \"Latitude\" TEXT, \"Longitude\" TEXT, \"ReceivedAmt\" DOUBLE, \"ReceiptNo\" TEXT, \"StopPointsID\" INTEGER )");
//            db.execSQL("CREATE TABLE IF NOT EXISTS \"MultiDeliveryDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"BarCode\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"MultiDeliveryID\" INTEGER NOT NULL )");
//            db.execSQL("CREATE TABLE IF NOT EXISTS \"MultiDeliveryWaybillDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"WaybillNo\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"MultiDeliveryID\" INTEGER NOT NULL )");
//            db.execSQL("CREATE TABLE IF NOT EXISTS \"WaybillMeasurement\"  ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"WaybillNo\" INTEGER NOT NULL, \"TotalPieces\" INTEGER NOT NULL, \"EmployID\" INTEGER NOT NULL , \"StationID\" INTEGER NOT NULL , \"CTime\" DATETIME NOT NULL, \"IsSync\" BOOL NOT NULL, \"HHD\" TEXT, \"Weight\" DOUBLE NOT NULL, \"NoNeedVolume\" BOOL NOT NULL , \"NoNeedVolumeReasonID\" INTEGER )");
//            db.execSQL("CREATE TABLE IF NOT EXISTS \"WaybillMeasurementDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"PiecesCount\" INTEGER NOT NULL, \"Width\" DOUBLE NOT NULL, \"Length\" DOUBLE NOT NULL, \"Height\" DOUBLE NOT NULL, \"IsSync\" BOOL NOT NULL, \"WaybillMeasurementID\" INTEGER NOT NULL )");
//            db.execSQL("CREATE TABLE IF NOT EXISTS \"NoNeedVolumeReason\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Name\" TEXT NOT NULL , \"FName\" TEXT )");
//        }

        if (oldVersion < newVersion) {

            db.execSQL("delete from UserMELogin");
            db.execSQL("delete from UserME");
            //db.execSQL("delete from NotDelivered");
            //db.execSQL("delete from NotDeliveredDetail");
            //db.execSQL("delete from OnDelivery");
            //db.execSQL("delete from OnDeliveryDetail");
            db.execSQL("delete from DeliveryStatus");

            //Added by ismail
            this.mDefaultWritableDatabase = db;
            db.execSQL("create table if not exists " + TABLENAME + "(" + COLUMNID
                    + " integer primary key," + COLUMNNAME_FILE + " text,"
                    + COLUMNNAME_EMPID + " text," + COLUMNNAME_FLAG
                    + " integer not null default 1)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"MobileNo\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"Name\" INTEGER NOT NULL , \"MobileNo\" TEXT, \"RawID\" INTEGER)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"AtOrigin\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"Productivity\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"OFD\"  INTEGER NOT NULL DEFAULT 0 , \"Attempted\"  INTEGER NOT NULL DEFAULT 0," +
                    "\"Delivered\"  INTEGER NOT NULL DEFAULT 0, \"Exceptions\"  INTEGER NOT NULL DEFAULT 0, \"Date\" DATETIME NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"AppVersion\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Version\"  INTEGER NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"CallLog\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Type\"  text NOT NULL , \"MNO\"  text NOT NULL," +
                    "\"Duration\"  text NOT NULL, \"WayBillNo\"  text , \"Date\" DATETIME NOT NULL, \"CallStartTime\" DATETIME NOT NULL," +
                    "\"CallEndTime\" DATETIME NOT NULL , \"Number\"  text, \"EmpID\"  INTEGER DEFAULT 0)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"Complaint\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"TotalComp\"  INTEGER NOT NULL DEFAULT 0 , \"Attempted\"  INTEGER NOT NULL DEFAULT 0," +
                    "\"Delivered\"  INTEGER NOT NULL DEFAULT 0, \"Exceptions\"  INTEGER NOT NULL DEFAULT 0, \"Date\" DATETIME NOT NULL," +
                    "\"Request\"  INTEGER NOT NULL DEFAULT 0 )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"Palletize\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"TripPlanDetails\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"TripPlanDDetails\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL ,\"TripPlanNo\" Integer ,\"IsSync\" Integer )");


            db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestination\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

//            db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestination\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
//                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"BarCode\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"BarCode\"  TEXT NOT NULL, \"WayBillNo\"  TEXT NOT NULL,\"Date\" TEXT NOT NULL ,\"IsDelivered\" INTEGER Default 0,\"WayBillID\" INTEGER Default 0 )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"LoadtoDestination\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"Discrepancy\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"DeviceToken\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"CurrentLocation\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"ActualLocation\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"Radius200\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Lat\"  TEXT NOT NULL, \"Long\"  TEXT NOT NULL ,\"Timein\"  DATETIME NOT NULL,\"Timeout\"  DATETIME NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"LastLogin\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"EmpID\"  INTEGER NOT NULL ,\"AndroidID\"  TEXT NOT NULL ,  \"UserTypeID\"  INTEGER NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"NightStock\"  ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, " +
                    "\"PiecesCount\" INTEGER NOT NULL, \"CTime\" DATETIME NOT NULL  ,\"UserID\" INTEGER NOT NULL, " +
                    "\"IsSync\" BOOL NOT NULL, \"StationID\" INTEGER NOT NULL, \"WaybillsCount\" INTEGER NOT NULL," +
                    "\"IDs\" NOT NULL DEFAULT 0,\"BIN\" TEXT )");
            db.execSQL("CREATE TABLE IF NOT EXISTS \"NightStockDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"BarCode\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"NightStockID\" INTEGER NOT NULL )");
            db.execSQL("CREATE TABLE IF NOT EXISTS \"NightStockWaybillDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"WaybillNo\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"NightStockID\" INTEGER NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"SendConsigneeNotification\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Data\"  TEXT NOT NULL, \"WayBillNo\"  TEXT NOT NULL ,\"Timein\"  DATETIME NOT NULL,\"Timeout\"  DATETIME NOT NULL )");


            db.execSQL("CREATE TABLE IF NOT EXISTS \"EmployInfo\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"EmpID\"  INTEGER NOT NULL ,\"EmpName\"  TEXT NOT NULL ,  \"IqamaNumber\"  TEXT NOT NULL," +
                    " \"MobileNo\"  TEXT NOT NULL , \"StationID\"  INTEGER NOT NULL," +
                    "  \"ImageName\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"FacilityLoggedIn\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"IsDate\"  TEXT NOT NULL , \"EmpID\"  INTEGER NOT NULL)");


            db.execSQL("CREATE TABLE IF NOT EXISTS \"Facility\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"FacilityID\" Integer NOT NULL , \"Code\" Text NOT NULL , \"Name\" Text NOT NULL , \"Station\" Integer Not Null , " +
                    "\"FacilityTypeID\" Integer Not Null, \"FacilityTypeName\" TEXT )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"DeliveryStatusReason\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , " +
                    "\"ReasonID\" Integer NOT NULL ,\"Code\" TEXT, \"Name\" TEXT NOT NULL , \"FName\" TEXT, " +
                    "\"DeliveyStatusID\" Integer NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"Ncl\"( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, " +
                    "\"NclNo\" TEXT  NOT NULL,\"UserID\" INTEGER NOT NULL, \"Date\" DATETIME NOT NULL  ,\"PieceCount\" " +
                    "INTEGER NOT NULL, \"WaybillCount\" INTEGER NOT NULL,\"IsSync\" BOOL NOT NULL,JsonData Text )");
            db.execSQL("CREATE TABLE IF NOT EXISTS \"NclDetail\"( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"BarCode\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"NclID\" INTEGER NOT NULL )");
            db.execSQL("CREATE TABLE IF NOT EXISTS \"NclWaybillDetail\"( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"WaybillNo\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"NclID\" INTEGER NOT NULL )");
            //,"SeqNo" INTEGER Default 0

            db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestinationImages\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"Contacts\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"Name\" Text , \"StationID\" Integer , \"MobileNo\" Text , \"Isprimary\" Integer )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestWaybill\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"WayBillNo\" Integer , \"Date\" DATETIME NOT NULL ,  \"TrailerNo\" Text )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestPiece\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"BarCode\" TEXT , \"Date\" DATETIME NOT NULL , \"TrailerNo\" Text )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"AtOriginLastWaybill\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"WaybillNo\" TEXT , \"PieceCount\" TEXT ,\"EmployID\" TEXT,\"UserID\" TEXT ," +
                    "\"IsSync\" TEXT , \"StationID\" Text,\"ScannedPC\" Text,\"bgcolor\" Text " +
                    ", \"isdelete\" Text, \"Date\" DATETIME NOT NULL  )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"AtOriginLastPieces\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"WaybillNo\" TEXT , \"BarCode\" TEXT ,\"IsSync\" TEXT ,\"bgcolor\" Text " +
                    ", \"isdelete\" Text, \"Date\" DATETIME NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"LoadtoDestLastWayBill\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"WaybillNo\" TEXT  , \"Date\" DATETIME NOT NULL ,  \"TrailerNo\" Text )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"LoadtoDestLastPiece\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"BarCode\" TEXT  , \"Date\" DATETIME NOT NULL ,  \"TrailerNo\" Text )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"CustomerRating\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"Json\" TEXT  NOT NULL  )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"InCabCheckList\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"IsDate\"  TEXT NOT NULL , \"EmpID\"  INTEGER NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"TerminalHandling\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"Json\" TEXT  NOT NULL  , \"Count\" Integer DEFAULT 0 )");

            //Error Pickup

            db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUpAuto\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , \"WaybillNo\" TEXT NOT NULL ," +
                    " \"ClientID\" INTEGER, \"FromStationID\" INTEGER NOT NULL , \"ToStationID\" INTEGER NOT NULL , " +
                    "\"PieceCount\" INTEGER NOT NULL , \"Weight\" DOUBLE, \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" DATETIME NOT NULL , " +
                    "\"IsSync\" BOOL NOT NULL , \"UserID\" INTEGER NOT NULL , \"StationID\" INTEGER NOT NULL , \"RefNo\" TEXT, \"Latitude\"" +
                    " TEXT, \"CurrentVersion\" TEXT NOT NULL, \"Longitude\" TEXT ,\"LoadTypeID\" INTEGER NOT NULL,\"AL\" INTEGER DEFAULT 0," +
                    " \"TruckID\" Integer Default 0)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUpDetailAuto\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"PickUpID\" INTEGER NOT NULL )");


            db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUpTemp\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"WaybillNo\" TEXT NOT NULL ," +
                    " \"ClientID\" INTEGER, \"FromStationID\" INTEGER NOT NULL , \"ToStationID\" INTEGER NOT NULL , " +
                    "\"PieceCount\" INTEGER NOT NULL , \"Weight\" DOUBLE, \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" DATETIME NOT NULL , " +
                    "\"IsSync\" BOOL NOT NULL , \"UserID\" INTEGER NOT NULL , \"StationID\" INTEGER NOT NULL , \"RefNo\" TEXT, \"Latitude\"" +
                    " TEXT, \"CurrentVersion\" TEXT NOT NULL, \"Longitude\" TEXT ,\"LoadTypeID\" INTEGER NOT NULL,\"AL\" INTEGER DEFAULT 0," +
                    "\"TruckID\" Integer Default 0)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUpDetailTemp\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"PickUpID\" INTEGER NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"Truck\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , \"Name\" TEXT NOT NULL , \"FName\" TEXT, " +
                    " \"TruckID\" Interger Not Null , \"IsDate\"  TEXT NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"UpdateMenu\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE , " +
                    " \"MenuChanges\" Integer Default 0 )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"DeliverReq\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"WaybillNo\"  TEXT NOT NULL ,BarCode  TEXT NOT NULL , InsertedDate TEXT NOT NULL , ValidDate TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"RtoReq\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"WaybillNo\"  TEXT NOT NULL ,BarCode  TEXT NOT NULL , InsertedDate TEXT NOT NULL , ValidDate TEXT NOT NULL )");

            if (!isColumnExist("CallLog", "EmpID"))
                db.execSQL("ALTER TABLE CallLog ADD COLUMN EmpID INTEGER DEFAULT 0");
            if (!isColumnExist("PickUp", "LoadTypeID"))
                db.execSQL("ALTER TABLE PickUp ADD COLUMN LoadTypeID INTEGER DEFAULT 0");
            if (!isColumnExist("PickUp", "AL"))
                db.execSQL("ALTER TABLE PickUp ADD COLUMN AL INTEGER DEFAULT 0");
            if (!isColumnExist("OnDelivery", "AL"))
                db.execSQL("ALTER TABLE OnDelivery ADD COLUMN AL INTEGER DEFAULT 0");
            if (!isColumnExist("MultiDelivery", "AL"))
                db.execSQL("ALTER TABLE MultiDelivery ADD COLUMN AL INTEGER DEFAULT 0");
            if (!isColumnExist("MyRouteShipments", "DDate"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN DDate TEXT");
            if (!isColumnExist("MyRouteShipments", "EmpID"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN EmpID INTEGER DEFAULT 0");
            if (!isColumnExist("MyRouteShipments", "Weight"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN Weight TEXT");
            if (!isColumnExist("MyRouteShipments", "PiecesCount"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN PiecesCount TEXT");
            if (!isColumnExist("MyRouteShipments", "Sign"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN Sign INTEGER DEFAULT 0");
            if (!isColumnExist("MyRouteShipments", "SeqNo"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN SeqNo INTEGER DEFAULT 0");
            if (!isColumnExist("MyRouteShipments", "OnDeliveryDate"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN OnDeliveryDate DATETIME ");
            if (!isColumnExist("MyRouteShipments", "PartialDelivered"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN PartialDelivered BOOL ");
            if (!isColumnExist("UserME", "Division"))
                db.execSQL("ALTER TABLE UserME ADD COLUMN Division TEXT DEFAULT 0 ");
            if (!isColumnExist("UserME", "UserTypeID"))
                db.execSQL("ALTER TABLE UserME ADD COLUMN UserTypeID INTEGER DEFAULT 0 ");
            if (!isColumnExist("UserME", "Date"))
                db.execSQL("ALTER TABLE UserME ADD COLUMN Date TEXT");
            if (!isColumnExist("MobileNo", "RawID"))
                db.execSQL("ALTER TABLE MobileNo ADD COLUMN RawID INTEGER ");
            if (!isColumnExist("LastLogin", "AndroidID"))
                db.execSQL("ALTER TABLE LastLogin ADD COLUMN AndroidID TEXT ");
            if (!isColumnExist("LastLogin", "UserTypeID"))
                db.execSQL("ALTER TABLE LastLogin ADD COLUMN UserTypeID INTEGER ");
            if (!isColumnExist("Complaint", "Request"))
                db.execSQL("ALTER TABLE Complaint ADD COLUMN Request INTEGER");
            if (!isColumnExist("MyRouteShipments", "POS"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN POS INTEGER ");

            if (!isColumnExist("MyRouteShipments", "Notification"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN Notification INTEGER ");
            if (!isColumnExist("MyRouteShipments", "Refused"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN Refused BOOL ");

            if (!isColumnExist("WaybillMeasurement", "UserID"))
                db.execSQL("ALTER TABLE WaybillMeasurement ADD COLUMN UserID INTEGER ");


            //New Checkpoints
            if (!isColumnExist("NotDelivered", "DeliveryStatusReasonID"))
                db.execSQL("ALTER TABLE NotDelivered ADD COLUMN DeliveryStatusReasonID INTEGER ");

            if (!isColumnExist("OnCLoadingForDDetail", "WaybillNo"))
                db.execSQL("ALTER TABLE OnCLoadingForDDetail ADD COLUMN WaybillNo TEXT ");
            if (!isColumnExist("CheckPoint", "Comments"))
                db.execSQL("ALTER TABLE CheckPoint ADD COLUMN Comments TEXT");
            if (!isColumnExist("CheckPoint", "Ref"))
                db.execSQL("ALTER TABLE CheckPoint ADD COLUMN Ref TEXT ");
            if (!isColumnExist("BarCode", "IsDelivered"))
                db.execSQL("ALTER TABLE BarCode ADD COLUMN IsDelivered INTEGER ");
            if (!isColumnExist("BarCode", "WayBillID"))
                db.execSQL("ALTER TABLE BarCode ADD COLUMN WayBillID INTEGER ");

            if (!isColumnExist("TripPlanDDetails", "TripPlanNo"))
                db.execSQL("ALTER TABLE TripPlanDDetails ADD COLUMN TripPlanNo INTEGER ");
            if (!isColumnExist("TripPlanDDetails", "IsSync"))
                db.execSQL("ALTER TABLE TripPlanDDetails ADD COLUMN IsSync INTEGER ");

            if (!isColumnExist("UserME", "Menu"))
                db.execSQL("ALTER TABLE UserME ADD COLUMN Menu Integer DEFAULT 0 ");

            if (!isColumnExist("TerminalHandling", "Count"))
                db.execSQL("ALTER TABLE TerminalHandling ADD COLUMN Count Integer DEFAULT 0  ");

            if (!isColumnExist("PickUp", "TruckID"))
                db.execSQL("ALTER TABLE PickUp ADD COLUMN TruckID INTEGER DEFAULT 0");

            if (!isColumnExist("PickUpAuto", "TruckID"))
                db.execSQL("ALTER TABLE PickUpAuto ADD COLUMN TruckID INTEGER DEFAULT 0");

            if (!isColumnExist("PickUpTemp", "TruckID"))
                db.execSQL("ALTER TABLE PickUpTemp ADD COLUMN TruckID INTEGER DEFAULT 0");

            if (!isColumnExist("UserME", "TruckID"))
                db.execSQL("ALTER TABLE UserME ADD COLUMN TruckID INTEGER DEFAULT 0");
            if (!isColumnExist("CheckPoint", "Count"))
                db.execSQL("ALTER TABLE CheckPoint ADD COLUMN Count INTEGER Default 0");
            if (!isColumnExist("Ncl", "JsonData"))
                db.execSQL("ALTER TABLE Ncl ADD COLUMN JsonData TEXT");

            if (!isColumnExist("UserME", "UpdateMenu"))
                db.execSQL("ALTER TABLE UserME ADD COLUMN UpdateMenu Integer DEFAULT 0 ");
            if (!isColumnExist("UserME", "DisableEnabletxtBox"))
                db.execSQL("ALTER TABLE UserME ADD COLUMN DisableEnabletxtBox  Integer DEFAULT 1 ");

            if (!isColumnExist("PickUpAuto", "JsonData"))
                db.execSQL("ALTER TABLE PickUpAuto ADD COLUMN JsonData TEXT");

            if (!isColumnExist("PickUpTemp", "JsonData"))
                db.execSQL("ALTER TABLE PickUpTemp ADD COLUMN JsonData TEXT");

            if (!isColumnExist("LoadtoDestination", "TimeIn"))
                db.execSQL("ALTER TABLE LoadtoDestination ADD COLUMN TimeIn DATETIME");
            if (!isColumnExist("NotDelivered", "Barcode"))
                db.execSQL("ALTER TABLE NotDelivered ADD COLUMN Barcode Text");
            if (!isColumnExist("OnDelivery", "Barcode"))
                db.execSQL("ALTER TABLE OnDelivery ADD COLUMN Barcode Text");

            if (!isColumnExist("MyRouteShipments", "UpdateDeliverScan"))
                db.execSQL("ALTER TABLE MyRouteShipments ADD COLUMN UpdateDeliverScan BOOL ");

            if (!isColumnExist("DeliveryStatus", "SeqOrder"))
                db.execSQL("ALTER TABLE DeliveryStatus ADD COLUMN SeqOrder Integer ");

            if (!isColumnExist("UserME", "CountryID"))
                db.execSQL("ALTER TABLE UserME ADD COLUMN CountryID Integer DEFAULT 0 ");
            if (!isColumnExist("UserME", "CountryCode"))
                db.execSQL("ALTER TABLE UserME ADD COLUMN CountryCode Text ");
        }


    }


    public Cursor getStationID(int EmployID, Context context) {
        // int StationID = 0;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT CountryID , CountryCode FROM UserME Where EmployID = " + EmployID + "  ORDER BY ID DESC LIMIT 1";
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            cursor = db.rawQuery(selectQuery, null);


//            if (cursor.getCount() > 0) {
//                cursor.moveToFirst();
//                StationID = cursor.getInt(cursor.getColumnIndex("StationID"));
//            }
            // cursor.close();
        } catch (SQLiteException e) {

        }
        return cursor;
    }

    public boolean EmployeInfo(int EmpID, String EmpName, String IqamaNumber, String MobileNo, int StationID, String ImageName, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            final String maxid = "SELECT *  FROM  EmployInfo where EmpID = " + EmpID;
            Cursor cur = db.rawQuery(maxid, null);
            if (cur.getCount() == 0) {

                ContentValues contentValues = new ContentValues();
                contentValues.put("EmpID", EmpID);
                contentValues.put("EmpName", EmpName);
                contentValues.put("IqamaNumber", IqamaNumber);
                contentValues.put("MobileNo", MobileNo);
                contentValues.put("StationID", StationID);
                contentValues.put("ImageName", ImageName);

                result = db.insert("EmployInfo", null, contentValues);
            }
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }


    public String getDBPath(Context context) {
        String path = "";
        File dbFile = context.getDatabasePath(DBName);
        if (dbFile.exists())
            path = dbFile.toString();
        return path;
    }

    public Cursor Fill(String Query, Context context) {
        try {

            SQLiteDatabase db = getReadableDatabase();
            return db.rawQuery(Query, null);
        } catch (SQLiteException e) {
            System.out.println(e);

        }
        return null;
    }

    public boolean isColumnExist(String tableName, String fieldName) {
        boolean isExist = false;
        try {
            final SQLiteDatabase db;
            if (mDefaultWritableDatabase != null) {
                db = mDefaultWritableDatabase;
            } else {
                db = super.getReadableDatabase();
            }


            Cursor res = null;
            try {
                res = db.rawQuery("Select * from " + tableName + " limit 1", null);
                int colIndex = res.getColumnIndex(fieldName);
                if (colIndex != -1)
                    isExist = true;

            } catch (Exception ignored) {
            } finally {
                try {
                    if (res != null)
                        res.close();

                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isExist;
    }


    public boolean isColumnExist(String tableName, String fieldName, Context context) {
        boolean isExist = false;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            Cursor res = null;
            try {
                res = db.rawQuery("Select * from " + tableName + " limit 1", null);
                int colIndex = res.getColumnIndex(fieldName);
                if (colIndex != -1)
                    isExist = true;
            } catch (Exception ignored) {
            } finally {
                try {
                    if (res != null)
                        res.close();
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isExist;
    }

    public int getMaxID(String tableName, Context context) {
        int ID = 0;
        try {
            String selectQuery = "SELECT * FROM " + tableName + " ORDER BY ID DESC LIMIT 1";
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                ID = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID")));
            }
            cursor.close();
        } catch (SQLiteException e) {

        }
        return ID;
    }

    public int getCount(String tableName, String WhereCondition, Context context) {
        int ID = 0;
        try {
            String selectQuery = "SELECT Count(*) as Count FROM " + tableName;
            if (!WhereCondition.equals(""))
                selectQuery += " where " + WhereCondition;

            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst())
                ID = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Count")));
            cursor.close();
        } catch (SQLiteException e) {

        }
        return ID;
    }

    //---------------------------------User Table-------------------------------
    public void deleteUserME(UserME instance, Context context, View view) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ;
            try {
                String args[] = {String.valueOf(instance.ID)};
                db.delete("UserME", "ID=?", args);
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            }
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean InsertUserME(UserME instance, Context context) {

        Long result = null;
        try {

            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", instance.ID);
            contentValues.put("EmployID", instance.EmployID);
            contentValues.put("Password", instance.Password);
            contentValues.put("StationID", instance.StationID);
            contentValues.put("RoleMEID", instance.RoleMEID);
            contentValues.put("StatusID", instance.StatusID);

            contentValues.put("EmployName", instance.EmployName);
            contentValues.put("EmployFName", instance.EmployFName);
            contentValues.put("MobileNo", instance.MobileNo);
            contentValues.put("StationCode", instance.StationCode);
            contentValues.put("StationName", instance.StationName);
            contentValues.put("StationFName", instance.StationFName);
            contentValues.put("Division", instance.Division);
            contentValues.put("UserTypeID", instance.UsertypeID);
            contentValues.put("Menu", instance.Menu);
            contentValues.put("Date", GlobalVar.getDate());
            contentValues.put("TruckID", instance.TruckID);
            contentValues.put("DisableEnabletxtBox", instance.DisableEnabletxtBox);
            contentValues.put("CountryID", instance.CountryID);
            contentValues.put("CountryCode", instance.CountryCode);
            result = db.insert("UserME", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;

    }

    //--------------------------------------User Me Login Logs Table--------------------------
    public boolean InsertUserMeLogin(UserMeLogin instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();
            contentValues.put("EmployID", instance.EmployID);
            contentValues.put("HHDName", instance.HHDName);
            contentValues.put("Version", instance.Version);
            contentValues.put("TruckID", instance.TruckID);
            contentValues.put("StateID", instance.StateID);
            contentValues.put("IsSync", instance.IsSync);
//            contentValues.put("Date", instance.Date.toString());
            contentValues.put("Date", GlobalVar.getDate());

            result = db.insert("UserMeLogin", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean FacilityLoggedIn(Context context, int EmpID) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();
            contentValues.put("IsDate", GlobalVar.getDate());
            contentValues.put("EmpID", EmpID);

            result = db.insert("FacilityLoggedIn", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean isFacilityLoggedIn(Context context, int EmpID) {
        try {
            Cursor empid = Fill("select Distinct EmpID from FacilityLoggedIn Where IsDate = '" + GlobalVar.getDate() + "'" +
                    " and EmpID = " + EmpID, context);


            if (empid != null && empid.getCount() > 0) {
                empid.close();
                return true;
            } else {
                empid.close();
                return false;
            }

        } catch (SQLiteException e) {

        }

        return false;
    }

    public boolean DeleteFacilityLoggedIn(Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            String args[] = {GlobalVar.getDate()};
            db.delete("FacilityLoggedIn", "IsDate!=?", args);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }


    public boolean DeleteExsistingLogin(Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            String args[] = {GlobalVar.getDate()};
            db.delete("UserMELogin", "Date!=?", args);
            db.delete("UserME", "Date!=?", args);

            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }


    public boolean UpdateUserDivision(String division, View view, int UpdateMenu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Put the filed which you want to update.
        contentValues.put("Division", division);
        contentValues.put("UpdateMenu", UpdateMenu);
        try {
            String args[] = {String.valueOf(GlobalVar.GV().EmployID)};
            db.update("UserME", contentValues, "EmployID=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            return false;
        }
        db.close();
        return true;
    }

    public boolean UpdateMenu(String division, View view, int UpdateMenu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("MenuChanges", UpdateMenu);
        try {
            db.delete("UpdateMenu", null, null);
            // db.update("UpdateMenu", contentValues, null, null);
            db.insert("UpdateMenu", null, contentValues);
            db.close();
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            return false;
        }
        db.close();
        return true;
    }


    public boolean UpdateUserMeLogin(UserMeLogin instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //Put the filed which you want to update.
        contentValues.put("IsSync", true);
        try {
            String args[] = {String.valueOf(instance.ID)};
            db.update("UserMELogin", contentValues, "ID=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
            return false;
        }
        db.close();
        return true;
    }

    public boolean UpdateUserMeLogout(UserMeLogin instance, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            //Put the filed which you want to update.
            contentValues.put("LogoutDate", DateTime.now().toString());
            contentValues.put("LogedOut", Boolean.TRUE.toString());

            try {
                String args[] = {String.valueOf(instance.ID)};
                //db.update("UserMELogin", contentValues, "ID=?", args);
                // db.delete("UserMELogin", "ID=?", args);
                db.execSQL("delete from UserMELogin");
                db.execSQL("delete from UserME"); // added new

            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
                return false;
            }
            db.close();
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    public void deleteUserME(int EmployId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String args[] = {String.valueOf(EmployId)};
            db.delete("UserME", "EmployID=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
        }
    }

    //--------------------------------------User Logs Table--------------------------
//    public boolean InsertUserLogs(UserLogs instance)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("UserID",instance.UserID);
//        contentValues.put("LogTypeID",instance.LogTypeID);
//        contentValues.put("MachineID",instance.MachineID);
//        contentValues.put("CTime", instance.CTime.toString());
//
//        long result = db.insert("UserLogs",null,contentValues);
//        if (result == -1)
//            return false;
//        else
//        {
//            //GlobalVar.GV().ShowMessage(context,"Row Inserted");
//            return true;
//        }
//    }

//    public boolean UpdateUserLogs(UserLogs instance)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        //Put the filed which you want to update.
//        contentValues.put("CTime",instance.CTime.toString());
//        try
//        {
//            String args[] = {instance.ID.toString()};
//            db.update("UserLogs", contentValues, "ID=?", args);
//        }
//        catch (Exception e)
//        {
//            GlobalVar.GV().ShowMessage(context,e.getMessage());
//    return false;
//        }
//        return true;
//    }

    //---------------------------------On Delivery Table-------------------------------
    public boolean InsertOnDelivery(OnDelivery instance, Context context, int al) {

        long result = 0;

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", instance.WaybillNo);
            contentValues.put("ReceiverName", instance.ReceiverName);
            contentValues.put("PiecesCount", instance.PiecesCount);
            contentValues.put("TimeIn", instance.TimeIn.toString());
            contentValues.put("TimeOut", instance.TimeOut.toString());
            contentValues.put("EmployID", instance.EmployID);
            contentValues.put("StationID", instance.StationID);
            contentValues.put("IsPartial", instance.IsPartial);
            contentValues.put("Latitude", instance.Latitude);
            contentValues.put("Longitude", instance.Longitude);
            contentValues.put("TotalReceivedAmount", instance.TotalReceivedAmount);
            contentValues.put("CashAmount", instance.CashAmount);
            contentValues.put("POSAmount", instance.POSAmount);
            contentValues.put("IsSync", instance.IsSync);
            contentValues.put("AL", al);
            contentValues.put("Barcode", instance.Barcode);


            result = db.insert("OnDelivery", null, contentValues);
            if (result != -1)
                update_DeliveryScan(context, String.valueOf(instance.WaybillNo));

            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void update_DeliveryScan(Context context, String Waybill) {
        String args[] = {String.valueOf(Waybill)};
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        try {

            Cursor mnocursor = Fill("select * from MyRouteShipments where ItemNo='" + Waybill + "'", context);

            if (mnocursor.getCount() > 0) {
                ContentValues contentValues = new ContentValues();

                contentValues.put("UpdateDeliverScan", true);

                try {

                    db.update("MyRouteShipments", contentValues, "ItemNo=?", args);

                } catch (Exception e) {


                }
            }
        } catch (SQLiteException e) {

        }
        db.close();

    }

    public boolean InsertOnDeliveryDetail(OnDeliveryDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", instance.BarCode);
            contentValues.put("DeliveryID", instance.DeliveryID);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("OnDeliveryDetail", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean UpdateOnDelivery(OnDelivery instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //Put the filed which you want to update.
        contentValues.put("IsSync", true);
        try {
            String args[] = {String.valueOf(instance.ID)};
            db.update("OnDelivery", contentValues, "ID=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
            return false;
        }
        db.close();
        return true;
    }

    //---------------------------------PickUp Table-------------------------------
    public boolean InsertPickUp(PickUp instance, Context context, int lid, int al, String JsonData) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", instance.WaybillNo);
            contentValues.put("ClientID", instance.ClientID);
            contentValues.put("FromStationID", instance.FromStationID);
            contentValues.put("ToStationID", instance.ToStationID);
            contentValues.put("PieceCount", instance.PieceCount);
            contentValues.put("Weight", instance.Weight);
            contentValues.put("TimeIn", instance.TimeIn.toString());
            contentValues.put("TimeOut", instance.TimeOut.toString());
            contentValues.put("IsSync", instance.IsSync);
            contentValues.put("UserID", instance.UserID);
            contentValues.put("StationID", instance.StationID);
            contentValues.put("RefNo", instance.RefNo);
            contentValues.put("Latitude", instance.Latitude);
            contentValues.put("Longitude", instance.Longitude);
            contentValues.put("CurrentVersion", instance.CurrentVersion);
            contentValues.put("LoadTypeID", lid);
            contentValues.put("AL", al);
            contentValues.put("TruckID", GetTruck(context));
            contentValues.put("JsonData", JsonData);

//            result = db.insert("PickUp", null, contentValues);
            result = db.insert("PickUpAuto", null, contentValues);
            db.insert("PickUpTemp", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertPickUpDetail(PickUpDetail instance, Context context) {
        long result = 0;

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS
                    | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", instance.BarCode);
            contentValues.put("PickUpID", instance.PickUpID);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("PickUpDetailAuto", null, contentValues);
            db.insert("PickUpDetailTemp", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean UpdatePickUp(PickUp instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //Put the filed which you want to update.
        contentValues.put("IsSync", true);
        try {
            String args[] = {String.valueOf(instance.ID)};
            db.update("PickUp", contentValues, "ID=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
            return false;
        }
        db.close();
        return true;
    }

    //---------------------------------Station Table-------------------------------
    public boolean InsertStation(Station instance, Context context) {
        long result = 0;

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", instance.ID);
            contentValues.put("Code", instance.Code);
            contentValues.put("Name", instance.Name);
            contentValues.put("FName", instance.FName);
            contentValues.put("CountryID", instance.CountryID);
            result = db.insert("Station", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void deleteStation(Station instance) {
        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();

        try {
            String args[] = {String.valueOf(instance.ID)};
            db.delete("Station", "ID=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
        }
        db.close();
    }

    public void deleteStation(int ID, Context context, View view) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            try {
                String args[] = {String.valueOf(ID)};
                db.delete("Station", "ID=?", args);
                db.close();
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            }
        } catch (SQLiteException e) {

        }

    }

    public void deleteAllStation(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from Station");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    //---------------------------------Not Delivery Table-------------------------------
    public boolean InsertNotDelivered(NotDelivered intstance, Context context) {

        long result = 0;
        try {

            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
//            contentValues.put("ID", maxID + 1);
            contentValues.put("WaybillNo", intstance.WaybillNo);
            contentValues.put("TimeIn", String.valueOf(intstance.TimeIn));
            contentValues.put("TimeOut", String.valueOf(intstance.TimeOut));
            contentValues.put("UserID", intstance.UserID);
            contentValues.put("IsSync", intstance.IsSync);
            contentValues.put("StationID", intstance.StationID);
            contentValues.put("PiecesCount", intstance.PiecesCount);
            contentValues.put("DeliveryStatusID", intstance.DeliveryStatusID);
            contentValues.put("DeliveryStatusReasonID", intstance.DeliveryStatusReasonID);
            contentValues.put("Notes", intstance.Notes);
            contentValues.put("Latitude", intstance.Latitude);
            contentValues.put("Longitude", intstance.Longitude);
            contentValues.put("Barcode", intstance.Barcode);

            result = db.insertOrThrow("NotDelivered", null, contentValues);
            db.close();

        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }

    public boolean UpdateNotDelivered(NotDelivered instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //Put the filed which you want to update.
        contentValues.put("IsSync", true);
        try {
            String args[] = {String.valueOf(instance.ID)};
            db.update("NotDelivered", contentValues, "ID=?", args);

            for (int i = 0; i < instance.NotDeliveredDetails.size(); i++) {
                db.update("NotDeliveredDetail", contentValues, "ID=?", args);
            }
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
            return false;
        }
        db.close();
        return true;
    }

    public boolean InsertNotDeliveredDetail(NotDeliveredDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", instance.BarCode);
            contentValues.put("NotDeliveredID", instance.NotDeliveredID);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("NotDeliveredDetail", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    //---------------------------------Delivery Status Table-------------------------------
    public boolean InsertDeliveryStatus(DeliveryStatus instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", instance.ID);
            contentValues.put("Code", instance.Code);
            contentValues.put("Name", instance.Name);
            contentValues.put("FName", instance.FName);
            contentValues.put("SeqOrder", instance.SeqOrder);
            result = db.insert("DeliveryStatus", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void deleteDeliveryStatus(int ID, View view, Context context) {

        try {
            try {
                SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
                String args[] = {String.valueOf(ID)};
                db.delete("DeliveryStatus", "ID=?", args);
                db.close();
            } catch (SQLiteException e) {

            }
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);

        }

    }

    public void deleteAllDeliveryStatus(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from DeliveryStatus");
            db.close();
        } catch (SQLiteException e) {

        }
    }


    //---------------------------------No Need Volume Reason Table-------------------------------
    public boolean InsertVolumeReason(NoNeedVolumeReason instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", instance.ID);
            contentValues.put("Name", instance.Name);
            contentValues.put("FName", instance.FName);
            result = db.insert("NoNeedVolumeReason", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void deleteVolumeReason(int ID, Context context, View view) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            try {
                String args[] = {String.valueOf(ID)};
                db.delete("NoNeedVolumeReason", "ID=?", args);
                db.close();
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            }
        } catch (SQLiteException e) {

        }

    }

    public void deleteAllVolumeReason(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from NoNeedVolumeReason");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    //---------------------------------Check Point Table-------------------------------
    public boolean InsertOnCheckPoint(CheckPoint instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("EmployID", instance.EmployID);
            contentValues.put("Date", String.valueOf(instance.Date));
            contentValues.put("CheckPointTypeID", instance.CheckPointTypeID);
            contentValues.put("Latitude", instance.Latitude);
            contentValues.put("Longitude", instance.Longitude);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("CheckPoint", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertOnCheckPointWaybillDetail(CheckPointWaybillDetails instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", instance.WaybillNo);
            contentValues.put("CheckPointID", instance.CheckPointID);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("CheckPointWaybillDetail", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    //---------------------------------Check Point Type Table-------------------------------
    public boolean InsertCheckPointType(CheckPointType instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", instance.ID);
            contentValues.put("Name", instance.Name);
            contentValues.put("FName", instance.FName);
            result = db.insert("CheckPointType", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void deleteCheckPointType(CheckPointType instance) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String args[] = {String.valueOf(instance.ID)};
            db.delete("CheckPointType", "ID=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
        }
        db.close();
    }

    public void deleteCheckPointType(int ID, View view, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            try {
                String args[] = {String.valueOf(ID)};
                db.delete("CheckPointType", "ID=?", args);
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            }
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteAllCheckpoint(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from CheckPointType");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    //---------------------------------Check Point Type Detail Table-------------------------------
    public boolean InsertCheckPointTypeDetail(CheckPointTypeDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", instance.ID);
            contentValues.put("Name", instance.Name);
            contentValues.put("FName", instance.FName);
            contentValues.put("CheckPointTypeID", instance.CheckPointTypeID);
            result = db.insert("CheckPointTypeDetail", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void deleteCheckPointTypeDetail(CheckPointTypeDetail instance) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String args[] = {String.valueOf(instance.ID)};
            db.delete("CheckPointTypeDetail", "ID=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
        }
        db.close();
    }

    public void deleteCheckPointTypeDetail(int ID, View view, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            try {
                String args[] = {String.valueOf(ID)};
                db.delete("CheckPointTypeDetail", "ID=?", args);
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            }
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteAllCheckPointTypeDetail(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from CheckPointTypeDetail");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    //---------------------------------Check Point Type DDetail Table-------------------------------
    public boolean InsertCheckPointTypeDDetail(CheckPointTypeDDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", instance.ID);
            contentValues.put("Name", instance.Name);
            contentValues.put("FName", instance.FName);
            contentValues.put("CheckPointTypeDetailID", instance.CheckPointTypeDetailID);
            result = db.insert("CheckPointTypeDDetail", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void deleteCheckPointTypeDDetail(CheckPointTypeDDetail instance) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String args[] = {String.valueOf(instance.ID)};
            db.delete("CheckPointTypeDDetail", "ID=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
        }
        db.close();
    }

    public void deleteCheckPointTypeDDetail(int ID, View view, Context context) {

        try {

            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            try {
                String args[] = {String.valueOf(ID)};
                db.delete("CheckPointTypeDDetail", "ID=?", args);
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            }
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteAllCheckPointTypeDDetail(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from CheckPointTypeDDetail");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    //---------------------------------Settings Table-------------------------------
    public boolean InsertSettings(UserSettings instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("EmployID", instance.EmployID);
            contentValues.put("ShowScaningCamera", String.valueOf(instance.ShowScaningCamera));
            contentValues.put("IPAddress", instance.IPAddress);
            if (isColumnExist("UserSettings", "LastBringMasterData", context))
                contentValues.put("LastBringMasterData", instance.LastBringMasterData.toString());

            result = db.insert("UserSettings", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean UpdateSettings(UserSettings instance, View view, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            //Put the filed which you want to update.
            contentValues.put("IPAddress", instance.IPAddress);
            if (instance.ShowScaningCamera)
                contentValues.put("ShowScaningCamera", "true");
            else
                contentValues.put("ShowScaningCamera", "false");
            if (isColumnExist("UserSettings", "LastBringMasterData", context))
                contentValues.put("LastBringMasterData", DateTime.now().toString());
            try {
                String args[] = {String.valueOf(instance.ID)};
                db.update("UserSettings", contentValues, "ID=?", args);
                GlobalVar.GV().ShowSnackbar(view, "Saved Successfully 1", GlobalVar.AlertType.Info);
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
                return false;
            }
            db.close();

        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    public boolean UpdateSettingsLastBringMasterData(UserSettings instance, View view, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            contentValues.put("LastBringMasterData", DateTime.now().toString());
            try {
                String args[] = {String.valueOf(instance.ID)};
                db.update("UserSettings", contentValues, "ID=?", args);
                GlobalVar.GV().ShowSnackbar(view, "Saved Successfully 2", GlobalVar.AlertType.Info);
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
                return false;
            }
            db.close();
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    //--------------------------------------Courier Daily Route Table--------------------------
    public boolean InsertCourierDailyRoute(CourierDailyRoute instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("EmployID", instance.EmployID);
            contentValues.put("StartLatitude", instance.StartLatitude);
            contentValues.put("StartLongitude", instance.StartLongitude);
            contentValues.put("StartingTime", instance.StartingTime.toString());

            result = db.insert("CourierDailyRoute", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void clearAllCourierDailyRoute(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            // db.delete("CourierDailyRoute", null, null);
            db.delete("MyRouteShipments", null, null);
            db.delete("BarCode", null, null);
            db.delete("Productivity", null, null);
            db.delete("Complaint", null, null);
//            GlobalVar.deleteContactRawID(ContactDetails(context), context);
            db.close();
        } catch (SQLiteException e) {

        }

    }


    public boolean CloseCurrentCourierDailyRoute(View View, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            //Put the filed which you want to update.
            contentValues.put("EndTime", String.valueOf(DateTime.now()));
            contentValues.put("EndLatitude", GlobalVar.GV().currentLocation.latitude);
            contentValues.put("EndLongitude", GlobalVar.GV().currentLocation.longitude);

            try {
                String args[] = {String.valueOf(GlobalVar.GV().CourierDailyRouteID)};
                db.update("CourierDailyRoute", contentValues, "ID=?", args);
                GlobalVar.GV().CourierDailyRouteID = 0;
                GlobalVar.GV().ShowSnackbar(View, "Saved Successfully 3", GlobalVar.AlertType.Info);
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(View, e.getMessage(), GlobalVar.AlertType.Error);
                return false;
            }
            db.close();
        } catch (SQLiteException e) {
            return false;

        }
        return true;
    }

    //---------------------------------On C Loading For Delivery Table-------------------------------
    public boolean InsertOnCloadingForD(OnCloadingForD instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("CourierID", instance.CourierID);
            contentValues.put("UserID", instance.UserID);
            contentValues.put("IsSync", instance.IsSync);
            contentValues.put("CTime", instance.CTime.toString());
            contentValues.put("PieceCount", instance.PieceCount);
            contentValues.put("TruckID", instance.TruckID);
            contentValues.put("WaybillCount", instance.WaybillCount);
            contentValues.put("StationID", instance.StationID);

            result = db.insert("OnCloadingForD", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertOnCLoadingForDDetail(OnCLoadingForDDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", instance.BarCode);
            contentValues.put("OnCLoadingForDID", instance.OnCLoadingForDID);
            contentValues.put("WaybillNo", instance.WaybillNo);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("OnCLoadingForDDetail", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertOnCLoadingbyPieceLevel(OFDPieceLevel instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", instance.BarCode);
            contentValues.put("OnCLoadingForDID", instance.OnCLoadingForDID);
            contentValues.put("WaybillNo", instance.WaybillNo);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("OnCLoadingForDDetail", null, contentValues);

            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertOnCLoadingForDWaybill(OnCLoadingForDWaybill instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", instance.WaybillNo);
            contentValues.put("OnCLoadingID", instance.OnCLoadingID);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("OnCLoadingForDWaybill", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean UpdateOnCloadingForD(OnCloadingForD instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("IsSync", true);
        try {
            String args[] = {String.valueOf(instance.ID)};
            db.update("OnCloadingForD", contentValues, "ID=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
        }
        db.close();
        return true;
    }

    //---------------------------------Multi Delivery Table-------------------------------
    public boolean InsertMultiDelivery(MultiDelivery instance, Context context, int al) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("ReceiverName", instance.ReceiverName);
            contentValues.put("PiecesCount", instance.PiecesCount);
            contentValues.put("TimeIn", instance.TimeIn.toString());
            contentValues.put("TimeOut", instance.TimeOut.toString());
            contentValues.put("UserID", instance.UserID);
            contentValues.put("IsSync", instance.IsSync);
            contentValues.put("StationID", instance.StationID);
            contentValues.put("WaybillsCount", instance.WaybillsCount);
            contentValues.put("Latitude", instance.Latitude);
            contentValues.put("Longitude", instance.Longitude);
            contentValues.put("ReceivedAmt", instance.ReceivedAmt);
            contentValues.put("ReceiptNo", instance.ReceiptNo);
            contentValues.put("StopPointsID", instance.StopPointsID);
            contentValues.put("AL", al);

            result = db.insert("MultiDelivery", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;

    }

    public boolean InsertMultiDeliveryWaybillDetail(MultiDeliveryWaybillDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", instance.WaybillNo);
            contentValues.put("MultiDeliveryID", instance.MultiDeliveryID);
            contentValues.put("IsSync", instance.IsSync);
            result = db.insert("MultiDeliveryWaybillDetail", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertMultiDeliveryDetail(MultiDeliveryDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", instance.BarCode);
            contentValues.put("MultiDeliveryID", instance.MultiDeliveryID);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("MultiDeliveryDetail", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean UpdateMultiDelivery(MultiDelivery instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("IsSync", true);
        try {
            String args[] = {String.valueOf(instance.ID)};
            db.update("MultiDelivery", contentValues, "ID=?", args);

            for (int i = 0; i < instance.multiDeliveryDetails.size(); i++) {
                db.update("MultiDeliveryDetail", contentValues, "ID=?", args);
            }

            for (int i = 0; i < instance.multiDeliveryDetails.size(); i++) {
                db.update("MultiDeliveryWaybillDetail", contentValues, "ID=?", args);
            }
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
        }
        db.close();
        return true;
    }

    //---------------------------------WaybillMeasurement Table-------------------------------
    public boolean InsertWaybillMeasurement(WaybillMeasurement instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", instance.WaybillNo);
            contentValues.put("TotalPieces", instance.TotalPieces);
            contentValues.put("EmployID", instance.EmployID);
            contentValues.put("UserID", instance.UserID);
            contentValues.put("CTime", instance.CTime.toString());
            contentValues.put("IsSync", instance.IsSync);
            contentValues.put("StationID", instance.StationID);
            contentValues.put("HHD", instance.HHD);
            contentValues.put("Weight", instance.Weight);
            contentValues.put("NoNeedVolume", instance.NoNeedVolume);
            contentValues.put("NoNeedVolumeReasonID", instance.NoNeedVolumeReasonID);
            result = db.insert("WaybillMeasurement", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertWaybillMeasurementDetail(WaybillMeasurementDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("PiecesCount", instance.PiecesCount);
            contentValues.put("Width", instance.Width);
            contentValues.put("Length", instance.Length);
            contentValues.put("Height", instance.Height);
            contentValues.put("WaybillMeasurementID", instance.WaybillMeasurementID);
            contentValues.put("IsSync", instance.IsSync);
            result = db.insert("WaybillMeasurementDetail", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void deleteAllMeasurement(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from WaybillMeasurement");
            db.execSQL("delete from WaybillMeasurementDetail");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean UpdateWaybillMeasurement(WaybillMeasurement instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("IsSync", true);
        try {
            String args[] = {String.valueOf(instance.ID)};
            db.update("WaybillMeasurement", contentValues, "ID=?", args);

            for (int i = 0; i < instance.WaybillMeasurementDetails.size(); i++) {
                db.update("WaybillMeasurementDetail", contentValues, "ID=?", args);
            }
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
        }
        db.close();
        return true;
    }

    //---------------------------------Check Point Table-------------------------------
    public boolean InsertCheckPoint(CheckPoint instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("EmployID", instance.EmployID);
            contentValues.put("Date", instance.Date.toString());
            contentValues.put("CheckPointTypeID", instance.CheckPointTypeID);
            contentValues.put("IsSync", instance.IsSync);
            contentValues.put("Latitude", instance.Latitude);
            contentValues.put("Longitude", instance.Longitude);
            contentValues.put("CheckPointTypeDetailID", instance.CheckPointTypeDetailID);
            contentValues.put("CheckPointTypeDDetailID", instance.CheckPointTypeDDetailID);
            contentValues.put("Ref", instance.Reference);
            result = db.insert("CheckPoint", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }


    public boolean InsertMobileNumbers(String name, String Mno, int rawid, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Name", name);
            contentValues.put("MobileNo", Mno);
            contentValues.put("RawID", rawid);
            result = db.insert("MobileNo", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean UpdateConsigneeName(String name, String Mno) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Name", name);
        try {
            String args[] = {String.valueOf(Mno)};
            db.update("MobileNo", contentValues, "MobileNo=?", args);
        } catch (Exception e) {
            // GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
        }
        db.close();
        return true;
    }

    public boolean InsertCheckPointWaybillDetails(CheckPointWaybillDetails instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", instance.WaybillNo);
            contentValues.put("CheckPointID", instance.CheckPointID);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("CheckPointWaybillDetails", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertCheckPointBarCodeDetails(CheckPointBarCodeDetails instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", instance.BarCode);
            contentValues.put("CheckPointID", instance.CheckPointID);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("CheckPointBarCodeDetails", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean UpdateCheckPoint(CheckPoint instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("IsSync", true);
        try {
            String args[] = {String.valueOf(instance.ID)};
            db.update("CheckPoint", contentValues, "ID=?", args);

            for (int i = 0; i < instance.CheckPointBarCodeDetails.size(); i++) {
                db.update("CheckPointBarCodeDetails", contentValues, "ID=?", args);
            }

            for (int i = 0; i < instance.CheckPointWaybillDetails.size(); i++) {
                db.update("CheckPointWaybillDetails", contentValues, "ID=?", args);
            }
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
        }
        db.close();
        return true;
    }

    //---------------------------------My Route Shipments Table-------------------------------
    public boolean InsertMyRouteShipments(MyRouteShipments instance, Context context) {
        long result = 0;

        try {

            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String ddate = df.format(c);

            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("OrderNo", instance.OrderNo);
            contentValues.put("ItemNo", instance.ItemNo);
            contentValues.put("TypeID", instance.TypeID);
            contentValues.put("BillingType", instance.BillingType);
            contentValues.put("CODAmount", instance.CODAmount);
            contentValues.put("DeliverySheetID", instance.DeliverySheetID);
            contentValues.put("Date", instance.Date.toString());
            contentValues.put("ExpectedTime", instance.ExpectedTime.toString());
            contentValues.put("Latitude", instance.Latitude);
            contentValues.put("Longitude", instance.Longitude);
            contentValues.put("ClientID", instance.ClientID);
            contentValues.put("ClientName", instance.ClientName);
            contentValues.put("ClientFName", instance.ClientFName);
            contentValues.put("ClientAddressPhoneNumber", instance.ClientAddressPhoneNumber);
            contentValues.put("ClientAddressFirstAddress", instance.ClientAddressFirstAddress);
            contentValues.put("ClientAddressSecondAddress", instance.ClientAddressSecondAddress);
            contentValues.put("ClientContactName", instance.ClientContactName);
            contentValues.put("ClientContactFName", instance.ClientContactFName);
            contentValues.put("ClientContactPhoneNumber", instance.ClientContactPhoneNumber);
            contentValues.put("ClientContactMobileNo", instance.ClientContactMobileNo);
            contentValues.put("ConsigneeName", instance.ConsigneeName);
            contentValues.put("ConsigneeFName", instance.ConsigneeFName);
            contentValues.put("ConsigneePhoneNumber", instance.ConsigneePhoneNumber);
            contentValues.put("ConsigneeFirstAddress", instance.ConsigneeFirstAddress);
            contentValues.put("ConsigneeSecondAddress", instance.ConsigneeSecondAddress);
            contentValues.put("ConsigneeNear", instance.ConsigneeNear);
            contentValues.put("ConsigneeMobile", instance.ConsigneeMobile);
            contentValues.put("Origin", instance.Origin);
            contentValues.put("Destination", instance.Destination);
            contentValues.put("PODNeeded", instance.PODNeeded);
            contentValues.put("PODDetail", instance.PODDetail);
            contentValues.put("PODTypeCode", instance.PODTypeCode);
            contentValues.put("PODTypeName", instance.PODTypeName);
            contentValues.put("IsDelivered", instance.IsDelivered);
            contentValues.put("NotDelivered", instance.NotDelivered);
            contentValues.put("CourierDailyRouteID", instance.CourierDailyRouteID);
            contentValues.put("HasComplaint", instance.HasComplaint);
            contentValues.put("EmpID", GlobalVar.GV().EmployID);
            contentValues.put("DDate", GlobalVar.getDate());
            contentValues.put("PiecesCount", instance.PiecesCount);
            contentValues.put("Weight", instance.Weight);
            contentValues.put("Sign", instance.Sign);
            contentValues.put("SeqNo", instance.SeqNo);
            contentValues.put("HasDeliveryRequest", instance.HasDeliveryRequest);
            contentValues.put("POS", instance.POS);
            contentValues.put("Notification", 0);
            contentValues.put("Refused", false);

            if (isColumnExist("MyRouteShipments", "OptimzeSerialNo", context))
                contentValues.put("OptimzeSerialNo", 0);

            result = db.insertOrThrow("MyRouteShipments", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return result != -1;
    }

    public String getDeliverysheetEmpID(Context context) {
        String employid = String.valueOf(GlobalVar.getlastlogin(context));
        String tru = "0";
        try {
            Cursor empid = Fill("select Distinct EmpID from MyRouteShipments Where DDate = '" + GlobalVar.getDate() + "'", context);


            if (empid != null && empid.getCount() > 0) {
                tru = "1";
                empid.moveToFirst();
                do {
                    employid = employid + "-" + String.valueOf(empid.getInt(empid.getColumnIndex("EmpID")));

                } while (empid.moveToNext());
            }
            empid.close();
        } catch (SQLiteException e) {

        }

        return employid + "," + tru;
    }


    public void updateMyRouteShipments(Context context, String waybillno, String function, String Lat, String Lng) {

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            if (function.equals("Complaint"))
                contentValues.put("HasComplaint", true);
            else if (function.equals("Request"))
                contentValues.put("HasDeliveryRequest", true);
            else { //(function.equals("Location"))

                contentValues.put("Latitude", Lat);
                contentValues.put("Longitude", Lng);
            }
            try {
                String args[] = {String.valueOf(waybillno)};
                db.update("MyRouteShipments", contentValues, "ItemNo=?", args);
                db.close();
            } catch (Exception e) {
            }

        } catch (SQLiteException e) {
        }
    }


    public boolean InsertMyRouteShipments(MyRouteShipmentsNew instance, Context context) {
        long result = 0;

        try {

            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("OrderNo", instance.OrderNo);
            contentValues.put("ItemNo", instance.ItemNo);
            contentValues.put("TypeID", instance.TypeID);
            contentValues.put("BillingType", instance.BillingType);
            contentValues.put("CODAmount", instance.CODAmount);
            contentValues.put("DeliverySheetID", instance.DeliverySheetID);
            contentValues.put("Date", instance.Date.toString());
            contentValues.put("ExpectedTime", instance.ExpectedTime.toString());
            contentValues.put("Latitude", instance.Latitude);
            contentValues.put("Longitude", instance.Longitude);
            contentValues.put("ClientID", instance.ClientID);
            contentValues.put("ClientName", instance.ClientName);
            contentValues.put("ClientFName", instance.ClientFName);
            contentValues.put("ClientAddressPhoneNumber", instance.ClientAddressPhoneNumber);
            contentValues.put("ClientAddressFirstAddress", instance.ClientAddressFirstAddress);
            contentValues.put("ClientAddressSecondAddress", instance.ClientAddressSecondAddress);
            contentValues.put("ClientContactName", instance.ClientContactName);
            contentValues.put("ClientContactFName", instance.ClientContactFName);
            contentValues.put("ClientContactPhoneNumber", instance.ClientContactPhoneNumber);
            contentValues.put("ClientContactMobileNo", instance.ClientContactMobileNo);
            contentValues.put("ConsigneeName", instance.ConsigneeName);
            contentValues.put("ConsigneeFName", instance.ConsigneeFName);
            contentValues.put("ConsigneePhoneNumber", instance.ConsigneePhoneNumber);
            contentValues.put("ConsigneeFirstAddress", instance.ConsigneeFirstAddress);
            contentValues.put("ConsigneeSecondAddress", instance.ConsigneeSecondAddress);
            contentValues.put("ConsigneeNear", instance.ConsigneeNear);
            contentValues.put("ConsigneeMobile", instance.ConsigneeMobile);
            contentValues.put("Origin", instance.Origin);
            contentValues.put("Destination", instance.Destination);
            contentValues.put("PODNeeded", instance.PODNeeded);
            contentValues.put("PODDetail", instance.PODDetail);
            contentValues.put("PODTypeCode", instance.PODTypeCode);
            contentValues.put("PODTypeName", instance.PODTypeName);
            contentValues.put("IsDelivered", instance.IsDelivered);
            contentValues.put("NotDelivered", instance.NotDelivered);
            contentValues.put("CourierDailyRouteID", instance.CourierDailyRouteID);
            contentValues.put("HasComplaint", instance.HasComplaint);
            contentValues.put("EmpID", GlobalVar.GV().EmployID);
            contentValues.put("DDate", GlobalVar.getDate());
            contentValues.put("PiecesCount", instance.PiecesCount);
            contentValues.put("Weight", instance.Weight);
            contentValues.put("Sign", instance.Sign);


            if (isColumnExist("MyRouteShipments", "OptimzeSerialNo", context))
                contentValues.put("OptimzeSerialNo", 0);

            result = db.insert("MyRouteShipments", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return result != -1;
    }

    public boolean InsertBarCode(String waybill, String barcode, Context context, int IsDelivered, int WaybillID) {
        long result = 0;

        try {

            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", barcode);
            contentValues.put("WayBillNo", waybill);
            contentValues.put("Date", GlobalVar.getDate());
            contentValues.put("IsDelivered", IsDelivered);
            contentValues.put("WayBillID", WaybillID);
            result = db.insert("BarCode", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return result != -1;
    }

    public void deleteDeliverysheetExceptByToday(View view, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ;
            try {
                String args[] = {GlobalVar.getDate()};
                db.delete("MyRouteShipments", "DDate!=?", args);
                db.delete("BarCode", "Date!=?", args);
                db.delete("Complaint", "Date!=?", args);
                db.delete("Productivity", "Date!=?", args);
                GlobalVar.deleteContactRawID(ContactDetails(context), context);
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            }
            db.close();
        } catch (SQLiteException e) {

        }
    }


    public boolean UpdateMyRouteShipmentsWithOptimizationSerial(int ID, int SerialNo, DateTime dateTime, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            contentValues.put("OptimzeSerialNo", SerialNo);
            contentValues.put("ExpectedTime", String.valueOf(dateTime));
            try {
                String args[] = {String.valueOf(ID)};
                db.update("MyRouteShipments", contentValues, "ID=?", args);
                db.close();
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(rootView, e.getMessage(), GlobalVar.AlertType.Error);
                return false;
            }

        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    public boolean UpdateMyRouteShipmentsWithComplaintAndDeliveryRequest(int ID, boolean hasComplaint, boolean hasDeliveryRequest, Context context, View view) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            contentValues.put("HasComplaint", String.valueOf(hasComplaint));
            contentValues.put("HasDeliveryRequest", String.valueOf(hasDeliveryRequest));
            try {
                String args[] = {String.valueOf(ID)};
                db.update("MyRouteShipments", contentValues, "ID=?", args);
                db.close();
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
                return false;
            }

        } catch (SQLiteException e) {
            return false;

        }
        return true;
    }

    public boolean UpdateMyRouteShipmentsWithHeader(String ItemNo, boolean hasComplaint, String header, Context context, View view) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            contentValues.put("HasComplaint", hasComplaint);
            contentValues.put("PODDetail", header);
            try {
                String args[] = {String.valueOf(ItemNo)};
                db.update("MyRouteShipments", contentValues, "ItemNo=?", args);
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
                return false;
            }
            db.close();
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }


    public boolean UpdateMyRouteShipmentsWithDelivery(int ID, View view, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            contentValues.put("IsDelivered", true);
            try {
                String args[] = {String.valueOf(ID)};
                db.update("MyRouteShipments", contentValues, "ID=?", args);
                db.close();
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
                return false;
            }

        } catch (SQLiteException e) {
            return false;

        }
        return true;
    }


    public boolean UpdateMyRouteShipmentsIsDeliverd(Context context, String Waybill, int DeliveryID) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            Cursor mnocursor = Fill("select * from MyRouteShipments where ItemNo='" + Waybill + "'", context);

            if (mnocursor.getCount() > 0) {
                ContentValues contentValues = new ContentValues();

                contentValues.put("IsDelivered", true);
                contentValues.put("OnDeliveryDate", DateTime.now().toString());
                try {
                    String args[] = {String.valueOf(Waybill)};
                    db.update("MyRouteShipments", contentValues, "ItemNo=?", args);
                    db.close();
                    //  UpdatePiece_IsDelivered(DeliveryID, context);
                } catch (Exception e) {
//                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
                    return false;
                }
            }
        } catch (SQLiteException e) {
            return false;

        }
        return true;
    }

    public boolean UpdateMyRouteShipmentsIsPartialDelivered(Context context, String Waybill, int DeliveryID) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            Cursor mnocursor = Fill("select * from MyRouteShipments where ItemNo='" + Waybill + "'", context);

            if (mnocursor.getCount() > 0) {
                ContentValues contentValues = new ContentValues();

                contentValues.put("PartialDelivered", true);
//                contentValues.put("OnDeliveryDate", DateTime.now().toString());
                try {
                    String args[] = {String.valueOf(Waybill)};
                    db.update("MyRouteShipments", contentValues, "ItemNo=?", args);
                    db.close();
//                    UpdatePiece_IsDelivered(DeliveryID, context);
                } catch (Exception e) {
//                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
                    return false;
                }
            }
        } catch (SQLiteException e) {
            return false;

        }
        return true;
    }

    private void UpdatePiece_IsDelivered(int ID, Context context) {
        DBConnections dbConnections = new DBConnections(context, null);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        Cursor resultDetail = dbConnections.Fill("select * from OnDeliveryDetail where DeliveryID = " + ID, context);


        if (resultDetail.getCount() > 0) {
            resultDetail.moveToFirst();
            do {
                String BarCode = resultDetail.getString(resultDetail.getColumnIndex("BarCode"));
                ContentValues contentValues = new ContentValues();
                contentValues.put("IsDelivered", 1);
                try {
                    String args[] = {String.valueOf(BarCode)};
                    db.update("BarCode", contentValues, "BarCode=?", args);
                    db.close();
                } catch (Exception e) {
                }
            }
            while (resultDetail.moveToNext());


        }

    }

    public boolean UpdateMyRouteShipmentsNotDeliverd(Context context, String Waybill) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();

            contentValues.put("NotDelivered", true);
            try {
                String args[] = {String.valueOf(Waybill)};
                db.update("MyRouteShipments", contentValues, "ItemNo=?", args);
                db.close();
            } catch (Exception e) {
//                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
                return false;
            }

        } catch (SQLiteException e) {
            return false;

        }
        return true;
    }

    public boolean UpdateMyRouteShipmentsRefused(Context context, String Waybill) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();

            contentValues.put("Refused", true);
            contentValues.put("OnDeliveryDate", DateTime.now().toString());
            try {
                String args[] = {String.valueOf(Waybill)};
                db.update("MyRouteShipments", contentValues, "ItemNo=?", args);
                db.close();
            } catch (Exception e) {
//                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
                return false;
            }

        } catch (SQLiteException e) {
            return false;

        }
        return true;
    }

    //--------------------------------- Booking Table ----------------------------------

    public void DeleteBooking(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String args[] = {String.valueOf(ID)};
        db.delete("Booking", "ID=?", args);
        db.close();
    }


    public boolean InsertBooking(Booking instance) {
        boolean check = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("ID",instance.ID);
//        contentValues.put("RefNo",instance.RefNo);
//        contentValues.put("ClientID",instance.ClientID);
//        contentValues.put("ClientName",instance.ClientName);
//        contentValues.put("ClientFName",instance.ClientFName);
//        contentValues.put("BookingDate",String.valueOf(instance.BookingDate));
//        contentValues.put("PicesCount",instance.PicesCount);
//        contentValues.put("Weight",instance.Weight);
//        contentValues.put("SpecialInstruction",instance.SpecialInstruction);
//        contentValues.put("OfficeUpTo",instance.OfficeUpTo.toString("HH:mm"));
//        contentValues.put("PickUpReqDT",instance.PickUpReqDT.toString("HH:mm"));
//        contentValues.put("ContactPerson",instance.ContactPerson);
//        contentValues.put("ContactNumber",instance.ContactNumber);
//        contentValues.put("Address",instance.Address);
//        contentValues.put("Latitude",instance.Latitude);
//        contentValues.put("Longitude",instance.Longitude);
//        contentValues.put("Status",instance.Status);
//        contentValues.put("Orgin",instance.Orgin);
//        contentValues.put("Destination",instance.Destination);
//        //contentValues.put("LoadType",instance.LoadType);
//        contentValues.put("BillType",instance.BillType);
//        contentValues.put("EmployID",instance.EmployeeId);

        contentValues.put("ID", 900);
        contentValues.put("RefNo", "test");
        contentValues.put("ClientID", 123);
        contentValues.put("ClientName", "mm");
        contentValues.put("ClientFName", "as");
        contentValues.put("BookingDate", String.valueOf(instance.BookingDate));
        contentValues.put("PicesCount", 2);
        contentValues.put("Weight", 2.0);
        contentValues.put("SpecialInstruction", "eme");
        contentValues.put("OfficeUpTo", String.valueOf(instance.BookingDate));
        contentValues.put("PickUpReqDT", String.valueOf(instance.BookingDate));
        contentValues.put("ContactPerson", instance.ContactPerson);
        contentValues.put("ContactNumber", instance.ContactNumber);
        contentValues.put("Address", instance.Address);
        contentValues.put("Latitude", instance.Latitude);
        contentValues.put("Longitude", instance.Longitude);
        contentValues.put("Status", instance.Status);
        contentValues.put("Orgin", instance.Orgin);
        contentValues.put("Destination", instance.Destination);
        contentValues.put("LoadType", "");
        contentValues.put("BillType", instance.BillType);
        contentValues.put("EmployID", 1024);


        Long result = db.insert("Booking", null, contentValues);

        if (result != -1)
            check = true;
        else
            check = false;
        db.close();
        return result != -1;
    }

    public boolean UpdateBookingStatus(int Id, int StatusId, View view, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Status", StatusId);

            try {
                String args[] = {String.valueOf(Id)};
                db.update("Booking", contentValues, "ID=?", args);
                db.close();
            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
                return false;
            }

        } catch (SQLiteException e) {

        }
        return true;
    }

    //Added by ismail
    public boolean inserSignaturetData(String filename, String empid, Context context) {
        boolean check = false;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMNNAME_FILE, filename);
            contentValues.put(COLUMNNAME_EMPID, empid);

            long rowInserted = db.insert(TABLENAME, null, contentValues);
            if (rowInserted != -1)
                check = true;
            else
                check = false;
        } catch (SQLiteException e) {

        }

        return check;
    }


    public Cursor getSignData(Context context) {
        Cursor res = null;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            res = db.rawQuery("select * from " + TABLENAME + " where "
                    + COLUMNNAME_FLAG + " = 1 " + " order by " + COLUMNID
                    + " asc limit 1", null);

        } catch (SQLiteException e) {

        }
        return res;
    }

    public Integer deleteSignData(Integer id, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            result = db.delete(TABLENAME, "id = ? ",
                    new String[]{Integer.toString(id)});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public boolean InsertAtOrigin(String instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", instance);

            result = db.insert("AtOrigin", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }

    public Integer deleteAtOrigin(Integer id, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            result = db.delete("AtOrigin", "id = ? ",
                    new String[]{Integer.toString(id)});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public boolean InsertPalletize(String instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", instance);

            result = db.insert("Palletize", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }


    public Integer deletePalletize(Integer id, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            result = db.delete("Palletize", "id = ? ",
                    new String[]{Integer.toString(id)});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public boolean InsertTripPlanDetails(String instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", instance);

            result = db.insert("TripPlanDetails", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }

    public Integer deleteTripPlanDetails(Integer id, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            result = db.delete("TripPlanDetails", "id = ? ",
                    new String[]{Integer.toString(id)});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public void deleteAllTrip(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from Palletize");
            db.execSQL("delete from TripPlanDetails");
            db.execSQL("delete from TripPlanDDetails");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteMyRouteShipments(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from MyRouteShipments");
            db.execSQL("delete from BarCode");
            db.close();
        } catch (SQLiteException e) {

        }
    }


    public boolean InsertTripDDetails(String instance, Context context, String tripplanno) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", instance);
            contentValues.put("TripPlanNo", tripplanno);
            contentValues.put("IsSync", 0);

            result = db.insert("TripPlanDDetails", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }


    public Integer deleteTripDDetails(Integer id, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            result = db.delete("TripPlanDDetails", "id = ? ",
                    new String[]{Integer.toString(id)});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public boolean InsertArrivedAtDest(String instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", instance);

            result = db.insert("AtDestination", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }

    public boolean InsertArrivedAtDestImages(String instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", instance);

            result = db.insert("AtDestinationImages", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }


    public Integer deleteArrivedAtDest(Integer id, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            result = db.delete("AtDestination", "id = ? ",
                    new String[]{Integer.toString(id)});
        } catch (SQLiteException e) {

        }
        return result;
    }


    public void deleteAllOrigin(Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("delete from AtOrigin");
            db.close();
        } catch (SQLiteException e) {

        }

    }

    public boolean InsertComplaint(int complaint, int request, String date, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            final String maxid = "SELECT *  FROM  Complaint WHERE Date = '" + date + "' and Attempted = " + GlobalVar.GV().EmployID;
            Cursor cur = db.rawQuery(maxid, null);
            cur.moveToFirst();
            if (cur.getCount() == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("TotalComp", complaint);
                contentValues.put("Request", request);
                contentValues.put("Date", date);
                contentValues.put("Attempted", GlobalVar.GV().EmployID);

                result = db.insert("Complaint", null, contentValues);
                db.close();

            } else
                return false;
        } catch (SQLiteException e) {

        }
        return result != -1;

    }


    public void updateComplaintsReq(Context context, int complaintReq, int id, String function) {

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            if (function.equals("Complaint"))
                contentValues.put("TotalComp", complaintReq);
            else
                contentValues.put("Request", complaintReq);

            try {
                db.update("Complaint", contentValues, "ID =" + id, null);

                db.close();
            } catch (Exception e) {
            }

        } catch (SQLiteException e) {
        }
    }

    public boolean InsertOFD(int OFD, String date, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            final String maxid = "SELECT *  FROM  Productivity WHERE Date = '" + date + "' and Attempted = " + GlobalVar.GV().EmployID;
            Cursor cur = db.rawQuery(maxid, null);
            cur.moveToFirst();
            if (cur.getCount() == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("OFD", OFD);
                contentValues.put("Date", date);
                contentValues.put("Attempted", GlobalVar.GV().EmployID);

                result = db.insert("Productivity", null, contentValues);
                db.close();

            } else
                return false;
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertAppVersion(int Version, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            Cursor cursor = Fill("select * from AppVersion ", context);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                cursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put("Version", Version);

                try {
                    db.update("AppVersion", contentValues, "ID =" + id, null);

                    db.close();
                } catch (Exception e) {
                }
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put("Version", Version);


                result = db.insert("AppVersion", null, contentValues);
                db.close();
            }

        } catch (SQLiteException e) {
            return false;
        }
        return result != -1;
    }

    public void updateAppVersion(Context context, int version) {

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            Cursor cursor = Fill("select * from AppVersion ", context);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                ContentValues contentValues = new ContentValues();
                contentValues.put("Version", version);

                try {
                    db.update("AppVersion", contentValues, "ID =" + id, null);

                    db.close();
                } catch (Exception e) {
                }
            }

        } catch (SQLiteException e) {
        }
    }

    public int GetAppVersion(Context context) {
        int version = 0;
        try {
            Cursor result = Fill("select * from AppVersion", context);
            if (result.getCount() > 0) {
                result.moveToFirst();
                version = result.getInt(result.getColumnIndex("Version"));
            }

        } catch (SQLiteException e) {
            return 0;
        }
        return version;
    }


    public boolean InsertCallLog(String calltype, String mno, String duration, String waybillno, String date, String start, String end, String number, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();
            contentValues.put("Type", calltype);
            contentValues.put("MNO", mno);
            contentValues.put("Duration", duration);
            contentValues.put("WayBillNo", waybillno);
            contentValues.put("Date", date);
            contentValues.put("CallStartTime", start);
            contentValues.put("CallEndTime", end);
            contentValues.put("Number", number);
            contentValues.put("EmpID", getEmpId(context));


            result = db.insert("CallLog", null, contentValues);
            db.close();

        } catch (SQLiteException e) {
            return false;
        }
        return result != -1;
    }

    public int getEmpId(Context context) {
        return GlobalVar.getlastlogin(context);
    }

    public String getWaybillByMobileNo(String mobileNo, Context context) {

        String WaybillNo = "";
//        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        Cursor result = Fill("select * from MyRouteShipments where ConsigneeMobile like '%" + mobileNo + "%'", context);
//        if (result == null)
//            return "0";
        if (result != null && result.getCount() > 0) {
            result.moveToFirst();
            WaybillNo = result.getString(result.getColumnIndex("ItemNo"));
        } else {
            result = Fill("select * from MyRouteShipments where ConsigneePhoneNumber like '%" + mobileNo + "%'", context);
            if (result != null && result.getCount() > 0) {
                result.moveToFirst();
                WaybillNo = result.getString(result.getColumnIndex("ItemNo"));
            } else
                WaybillNo = "0";
        }
        return WaybillNo;
    }

    public HashSet<Integer> getSeqNoByWaybillByMobileNo(String mobileNo, String PhoneNo, Context context) {
        HashSet<Integer> seq = new HashSet<>();

        Cursor result = Fill("select * from MyRouteShipments where ConsigneeMobile like '%" + mobileNo + "%'", context);
        if (result.getCount() > 1) {
            result.moveToFirst();
            do {
                seq.add(result.getInt(result.getColumnIndex("SeqNo")));
            } while (result.moveToNext());
        } else {
            result = Fill("select * from MyRouteShipments where ConsigneePhoneNumber like '%" + mobileNo + "%'", context);
            if (result.getCount() > 1) {
                result.moveToFirst();
                do {
                    seq.add(result.getInt(result.getColumnIndex("SeqNo")));
                } while (result.moveToNext());
            }
        }

        result = Fill("select * from MyRouteShipments where ConsigneeMobile like '%" + PhoneNo + "%'", context);
        if (result.getCount() > 1) {
            result.moveToFirst();
            do {
                seq.add(result.getInt(result.getColumnIndex("SeqNo")));
            } while (result.moveToNext());
        } else {
            result = Fill("select * from MyRouteShipments where ConsigneePhoneNumber like '%" + PhoneNo + "%'", context);
            if (result.getCount() > 1) {
                result.moveToFirst();
                do {
                    seq.add(result.getInt(result.getColumnIndex("SeqNo")));
                } while (result.moveToNext());
            }
        }

        return seq;
    }


    public void UpdateProductivity_Delivered(String date, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            //(SELECT MAX(ID)
            final String maxid = "SELECT *  FROM  Productivity WHERE Date = '" + date + "' and Attempted = " + GlobalVar.GV().EmployID;
            Cursor cur = db.rawQuery(maxid, null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();

                int ID = cur.getInt(cur.getColumnIndex("ID"));
                int delivered = cur.getInt(cur.getColumnIndex("Delivered"));
                cur.close();

                ContentValues cv = new ContentValues();
                cv.put("Delivered", delivered + 1);
                db.update("Productivity", cv, "ID =" + ID, null);
                db.close();
            }
            cur.close();
        } catch (SQLiteException e) {

        }
    }

    public void UpdateComplaint_Delivered(String date, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            //(SELECT MAX(ID)
            final String maxid = "SELECT *  FROM  Complaint WHERE Date = '" + date + "' and Attempted = " + GlobalVar.GV().EmployID;
            Cursor cur = db.rawQuery(maxid, null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();

                int ID = cur.getInt(cur.getColumnIndex("ID"));
                int delivered = cur.getInt(cur.getColumnIndex("Delivered"));
                cur.close();

                ContentValues cv = new ContentValues();
                cv.put("Delivered", delivered + 1);
                db.update("Complaint", cv, "ID =" + ID, null);
                db.close();
            }
            cur.close();
        } catch (SQLiteException e) {

        }

    }

    public void UpdateComplaint_Exceptions(String date, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);//SELECT *  FROM  Productivity WHERE ID = (SELECT MAX(ID)  FROM Productivity)
            final String maxid = "SELECT *  FROM  Complaint WHERE Date = '" + date + "' and Attempted = " + GlobalVar.GV().EmployID;
            Cursor cur = db.rawQuery(maxid, null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                int ID = cur.getInt(cur.getColumnIndex("ID"));
                int exceptions = cur.getInt(cur.getColumnIndex("Exceptions"));
                cur.close();

                ContentValues cv = new ContentValues();
                cv.put("Exceptions", exceptions + 1);
                db.update("Complaint", cv, "ID =" + ID, null);
                db.close();
            }
            cur.close();
        } catch (SQLiteException e) {

        }
    }


    public void UpdateProductivity_Exceptions(String date, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);//SELECT *  FROM  Productivity WHERE ID = (SELECT MAX(ID)  FROM Productivity)
            final String maxid = "SELECT *  FROM  Productivity WHERE Date = '" + date + "' and Attempted = " + GlobalVar.GV().EmployID;
            Cursor cur = db.rawQuery(maxid, null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                int ID = cur.getInt(cur.getColumnIndex("ID"));
                int exceptions = cur.getInt(cur.getColumnIndex("Exceptions"));
                cur.close();

                ContentValues cv = new ContentValues();
                cv.put("Exceptions", exceptions + 1);
                db.update("Productivity", cv, "ID =" + ID, null);
                db.close();
            }
            cur.close();
        } catch (SQLiteException e) {

        }
    }


    public void deletePickupID(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("PickUpAuto", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deletePickupDetails(int pickupid, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(pickupid)};
            db.delete("PickUpDetailAuto", "PickUpID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void updatePickupbyID(int ID, Context context) {

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            try {
                contentValues.put("IsSync", true);

                String args[] = {String.valueOf(ID)};
                db.update("PickUpAuto", contentValues, "ID=?", args);
                db.close();
            } catch (Exception e) {
            }

        } catch (SQLiteException e) {
        }
    }

    public void DeleteAllSyncData(Context context) {

        try {

            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            String args[] = {GlobalVar.getDateMinus2Days(), "1"};

            //Pickup
            db.delete("PickUpAuto", "date(timein) <? And IsSync  =?", args);
            //OnDelivery
            db.execSQL("delete  from OnDeliveryDetail where DeliveryID in  (select ID from OnDelivery " +
                    "where issync = 1 and date(TimeIn) < date())");

            db.delete("OnDelivery", "date(TimeIn) <? And IsSync  =?", args);

            //MultiDelivery
            db.execSQL("delete  from MultiDeliveryWaybillDetail where MultiDeliveryID in  (select ID from MultiDelivery " +
                    "where issync = 1 and date(TimeIn) < date())");
            db.execSQL("delete  from MultiDeliveryDetail where MultiDeliveryID in  (select ID from MultiDelivery " +
                    "where issync = 1 and date(TimeIn) < date())");
            db.delete("MultiDelivery", "date(TimeIn) <? And IsSync  =?", args);

            //NotDeliver Data
//            db.execSQL("delete  from NotDeliveredDetail where NotDeliveredID in  (select ID from NotDelivered " +
//                    "where issync = 1 and date(TimeIn) < date())");
//            db.delete("NotDelivered", "date(TimeIn) <? And IsSync  =?", args);

            //NotDeliver Data
            //db.execSQL("delete  from  NotDelivered where issync = 1 and date(TimeIn) < date()");
            db.delete("NotDelivered", "date(TimeIn) <? And IsSync  =?", args);

            //NightStock
            db.execSQL("delete  from NightStockWaybillDetail where NightStockID in  (select ID from NightStock " +
                    "where issync = 1 and date(CTime) < date())");
            db.execSQL("delete  from NightStockDetail where NightStockID in  (select ID from NightStock " +
                    "where issync = 1 and date(CTime) < date())");
            db.delete("NightStock", "date(CTime) <? And IsSync  =?", args);


            db.close();

        } catch (SQLiteException e) {
            System.out.println(e);
        }

    }


    public void deleteonDeliveryID(int ID, Context context) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            String args[] = {String.valueOf(ID)};
            db.delete("OnDelivery", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void updateOnDeliveryID(int ID, Context context) {

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            try {
                contentValues.put("IsSync", true);

                String args[] = {String.valueOf(ID)};
                db.update("OnDelivery", contentValues, "ID=?", args);
                db.close();
            } catch (Exception e) {
            }

        } catch (SQLiteException e) {
        }
    }


    public void deleteDeliveyDetails(int deliveryID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            UpdatePiece_IsDelivered(deliveryID, context);

            String args[] = {String.valueOf(deliveryID)};
            db.delete("OnDeliveryDetail", "DeliveryID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteDeliveryDeliveyDetails(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from OnDelivery");
            db.execSQL("delete from OnDeliveryDetail");

            db.close();
        } catch (SQLiteException e) {

        }
    }


    public void deleteNotDeliveryID(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("NotDelivered", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void updateNotDeliveryID(int ID, Context context) {

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            try {
                contentValues.put("IsSync", true);

                String args[] = {String.valueOf(ID)};
                db.update("NotDelivered", contentValues, "ID=?", args);
                db.close();
            } catch (Exception e) {
            }

        } catch (SQLiteException e) {
        }
    }


    public void deleteNotDeliveyDetails(int deliveryID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(deliveryID)};
            db.delete("NotDeliveredDetail", "NotDeliveredID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteOnLoadingID(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("OnCloadingForD", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteOnLoadingWayBill(int onLoadingID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(onLoadingID)};
            db.delete("OnCLoadingForDWaybill", "OnCLoadingID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteOnLoadingBarcode(int onLoadingID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(onLoadingID)};
            db.delete("OnCLoadingForDDetail", "OnCLoadingForDID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }


    public void deleteMultiDeliveryID(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("MultiDelivery", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void updateMultiDeliveryID(int ID, Context context) {

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            try {
                contentValues.put("IsSync", true);

                String args[] = {String.valueOf(ID)};
                db.update("MultiDelivery", contentValues, "ID=?", args);
                db.close();
            } catch (Exception e) {
            }

        } catch (SQLiteException e) {
        }
    }


    public void deleteMultiDeliveryWayBill(int MultiDeliveryID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(MultiDeliveryID)};
            db.delete("MultiDeliveryWaybillDetail", "MultiDeliveryID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteMultiDeliveryBarcode(int MultiDeliveryID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(MultiDeliveryID)};
            db.delete("MultiDeliveryDetail", "MultiDeliveryID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteWayBillMeasurement(int WayBillID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(WayBillID)};
            db.delete("WaybillMeasurement", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteWayBillMeasurementDetails(int WayBillID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(WayBillID)};
            db.delete("WaybillMeasurementDetail", "WaybillMeasurementID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteCheckPointID(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("CheckPoint", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteCheckPointWayBill(int MultiDeliveryID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(MultiDeliveryID)};
            db.delete("CheckPointWaybillDetails", "CheckPointID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteCheckPointBarcode(int MultiDeliveryID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(MultiDeliveryID)};
            db.delete("CheckPointBarCodeDetails", "CheckPointID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteCallLogID(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("CallLog", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean InsertDiscrepancy(String instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", instance);

            result = db.insert("Discrepancy", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }

    public void deleteDiscrepancy(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("Discrepancy", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean InsertDevieToken(String instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", instance);

            result = db.insert("DeviceToken", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }

    public void deleteDeviceToken(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("DeviceToken", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean InsertActualLocation(String waybillno, String lat, String longi, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = Fill("select * from MyRouteShipments where ItemNo = '" + waybillno + "'", context);
            JSONObject jsonObject = new JSONObject();

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                try {

                    jsonObject.put("WayBillNo", cursor.getString(cursor.getColumnIndex("ItemNo")));
                    jsonObject.put("ConsigneeID", cursor.getInt(cursor.getColumnIndex("ClientID")));
                    jsonObject.put("ConsigneeAddressID", cursor.getInt(cursor.getColumnIndex("OrderNo")));
                    jsonObject.put("ConsigneePhoneNumber", cursor.getString(cursor.getColumnIndex("ConsigneePhoneNumber")));
                    jsonObject.put("Latitude", lat);
                    jsonObject.put("Longitude", longi);
                    jsonObject.put("UpdateBy", GlobalVar.GV().EmployID);
                    jsonObject.put("UpdatedDate", DateTime.now().toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {

                    jsonObject.put("WayBillNo", waybillno);
                    jsonObject.put("ConsigneeID", 0);
                    jsonObject.put("ConsigneeAddressID", 0);
                    jsonObject.put("ConsigneePhoneNumber", "");
                    jsonObject.put("Latitude", lat);
                    jsonObject.put("Longitude", longi);
                    jsonObject.put("UpdateBy", GlobalVar.GV().EmployID);
                    jsonObject.put("UpdatedDate", DateTime.now().toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", jsonObject.toString());

            result = db.insert("ActualLocation", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }

    public void deleteActualLocarion(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("ActualLocation", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public ArrayList<String> BarCode(String Waybillno, Context context) {
        ArrayList<String> barcode = new ArrayList<>();
        try {
            Cursor barcodecursor = Fill("select * from BarCode Where WayBillNo = '" + Waybillno + "'", context);

            if (barcodecursor.getCount() > 0) {
                barcodecursor.moveToFirst();
                do {

                    barcode.add(barcodecursor.getString(barcodecursor.getColumnIndex("BarCode")));
                } while (barcodecursor.moveToNext());
            }
            barcodecursor.close();
        } catch (SQLiteException e) {

        }
        return barcode;
    }

    public void DeleteContactWithRaw(int ID, String name, String Mno) {
        SQLiteDatabase db = this.getWritableDatabase();
        String args[] = {String.valueOf(ID), name, Mno};
        db.delete("MobileNo", "RawID = ? AND Name = ? AND MobileNo = ?", args);
        db.close();
    }


    public void deleteAllContacts(View view, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            try {
                db.execSQL("delete from MobileNo");

            } catch (Exception e) {
                GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            }
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public ArrayList<HashMap<String, String>> ContactDetails(Context context) {
        ArrayList<HashMap<String, String>> contactdetails = new ArrayList<HashMap<String, String>>();
        try {
            Cursor mnocursor = Fill("select * from MobileNo", context);

            if (mnocursor.getCount() > 0) {
                mnocursor.moveToFirst();
                do {
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("mno", mnocursor.getString(mnocursor.getColumnIndex("MobileNo")));
                    temp.put("name", mnocursor.getString(mnocursor.getColumnIndex("Name")));
                    temp.put("rawid", String.valueOf(mnocursor.getInt(mnocursor.getColumnIndex("RawID"))));
                    contactdetails.add(temp);

                } while (mnocursor.moveToNext());
            }

        } catch (SQLiteException e) {

        }
        return contactdetails;
    }


    public ArrayList<Location> GetLocation(Context context) {
        ArrayList<Location> locations = new ArrayList<>();
        try {
            Cursor mnocursor = Fill("select * from MyRouteShipments where IsDelivered <> 1 and Latitude !='' and Longitude !=''  Limit 22 ", context);

            if (mnocursor.getCount() > 0) {
                mnocursor.moveToFirst();
                do {
                    Location temp = new Location("");
                    temp.setLongitude(Double.parseDouble(mnocursor.getString(mnocursor.getColumnIndex("Longitude"))));
                    temp.setLatitude(Double.parseDouble(mnocursor.getString(mnocursor.getColumnIndex("Latitude"))));
                    locations.add(temp);

                } while (mnocursor.moveToNext());
            }

        } catch (SQLiteException e) {

        }
        return locations;
    }


    public ArrayList<HashMap<String, String>> GetTripDetails(Context context) {

        ArrayList<HashMap<String, String>> tripplan = new ArrayList<>();

        try {
            Cursor mnocursor = Fill("select * from TripPlanDDetails Group by TripPlanNo", context);

            if (mnocursor.getCount() > 0) {
                mnocursor.moveToFirst();
                do {
                    HashMap<String, String> temp = new HashMap<>();

                    int id = mnocursor.getInt(mnocursor.getColumnIndex("ID"));
                    String Json = mnocursor.getString(mnocursor.getColumnIndex("Json"));
                    try {
                        JSONArray jsonArray = new JSONArray(Json);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String tripno = String.valueOf(jsonObject.getInt("TripPlanID"));

                        temp.put("ID", String.valueOf(id));
                        temp.put("TripPlan", tripno);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    tripplan.add(temp);

                } while (mnocursor.moveToNext());
            }

        } catch (SQLiteException e) {

        }
        return tripplan;
    }


    public boolean InsertLocation(String instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", instance);

            result = db.insert("CurrentLocation", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }

    public boolean UpdateLocation(String data, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);//SELECT *  FROM  Productivity WHERE ID = (SELECT MAX(ID)  FROM Productivity)
            final String maxid = "SELECT *  FROM  CurrentLocation";
            Cursor cur = db.rawQuery(maxid, null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                int ID = cur.getInt(cur.getColumnIndex("ID"));
                cur.close();

                ContentValues cv = new ContentValues();
                cv.put("Json", data);
                result = db.update("CurrentLocation", cv, "ID =" + ID, null);
                db.close();
            } else {
                try {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Json", data);

                    result = db.insert("CurrentLocation", null, contentValues);
                    db.close();
                } catch (SQLiteException e) {
                    System.out.println(e);
                }
            }

            cur.close();
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }


    public void deleteLocation(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("CurrentLocation", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean InsertALInOut(String lat, String lon, String timein, String timeout, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            final String maxid = "SELECT *  FROM  Radius200 where Lat = '" + lat + "' and Long ='" + lon + "'";
            Cursor cur = db.rawQuery(maxid, null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                int ID = cur.getInt(cur.getColumnIndex("ID"));
                ContentValues cv = new ContentValues();
                if (cur.getString(cur.getColumnIndex("Timein")).length() == 0) {
                    cv.put("Timein", DateTime.now().minusMinutes(10).toString());
                }
                cur.close();
                cv.put("Timeout", timeout);
                result = db.update("Radius200", cv, "ID =" + ID, null);
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put("Lat", lat);
                contentValues.put("Long", lon);
                contentValues.put("Timein", DateTime.now().toString());
                contentValues.put("Timeout", timeout);

                result = db.insert("Radius200", null, contentValues);
            }
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }


    public boolean AddConsigneeNotify(String data, String WaybillNo, String timeout, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            final String maxid = "SELECT *  FROM  SendConsigneeNotification where WayBillNo = '" + WaybillNo + "'";
            Cursor cur = db.rawQuery(maxid, null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                int ID = cur.getInt(cur.getColumnIndex("ID"));
                ContentValues cv = new ContentValues();
//                if (cur.getString(cur.getColumnIndex("Timein")).length() == 0) {
//                    cv.put("Timein", DateTime.now().minusMinutes(10).toString());
//                }
                cur.close();
                cv.put("Timeout", DateTime.now().toString());
                result = db.update("SendConsigneeNotification", cv, "ID =" + ID, null);
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put("WayBillNo", WaybillNo);
                contentValues.put("Data", data);
                contentValues.put("Timein", DateTime.now().toString());
                contentValues.put("Timeout", timeout);

                result = db.insert("SendConsigneeNotification", null, contentValues);
            }
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void updateMyRouteShipmentsNotifications(Context context, String waybillno) {

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            try {
                contentValues.put("Notification", 1);

                String args[] = {String.valueOf(waybillno)};
                db.update("MyRouteShipments", contentValues, "ItemNo=?", args);
                db.close();
            } catch (Exception e) {
            }

        } catch (SQLiteException e) {
        }
    }


    public void deleteConsigneeNotify(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("SendConsigneeNotification", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteAllRadios200(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from Radius200");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteRadios200(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("Radius200", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean UpdateLastLogin(int EmpID, Context context, int utid) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);//SELECT *  FROM  Productivity WHERE ID = (SELECT MAX(ID)  FROM Productivity)
            final String maxid = "SELECT *  FROM  LastLogin";
            Cursor cur = db.rawQuery(maxid, null);

            if (cur != null && cur.getCount() > 0) {
                cur.moveToFirst();
                int ID = cur.getInt(cur.getColumnIndex("ID"));

//                String AID = cur.getString(cur.getColumnIndex("AndroidID"));
//                System.out.println(AID);

                cur.close();

                ContentValues cv = new ContentValues();
                cv.put("EmpID", EmpID);
                cv.put("UserTypeID", utid);

//                if (AID == null)
//                    cv.put("AndroidID", GlobalVar.getUniqueID(context));

                result = db.update("LastLogin", cv, "ID =" + ID, null);
                db.close();
            } else {
                try {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("EmpID", EmpID);
                    contentValues.put("UserTypeID", utid);
                    contentValues.put("AndroidID", "0");

//                    contentValues.put("AndroidID", GlobalVar.getUniqueID(context));
                    result = db.insert("LastLogin", null, contentValues);
                    db.close();
                } catch (SQLiteException e) {
                    System.out.println(e);
                }
            }

            cur.close();
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean UpdateTruckID(int EmpID, Context context, int TruckID) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);//SELECT *  FROM  Productivity WHERE ID = (SELECT MAX(ID)  FROM Productivity)
            final String query = "SELECT *  FROM  UserME Where EmployID = " + EmpID + " Order by ID Desc Limit 1";
            Cursor cur = db.rawQuery(query, null);

            if (cur != null && cur.getCount() > 0) {
                cur.moveToFirst();
                int ID = cur.getInt(cur.getColumnIndex("ID"));

                ContentValues cv = new ContentValues();
                cv.put("TruckID", TruckID);

                result = db.update("UserME", cv, "ID =" + ID, null);
                db.close();
            }

            cur.close();
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }


    public String GetLastDeliveredWaybill(Context context) {
        String Waybillno = "0";
        try {
            Cursor mnocursor = Fill("select * from MyRouteShipments where IsDelivered = 1 or Refused = 1 order by OnDeliveryDate desc Limit 1", context);

            if (mnocursor.getCount() > 0) {
                mnocursor.moveToFirst();
                do {
                    Waybillno = mnocursor.getString(mnocursor.getColumnIndex("ItemNo"));


                } while (mnocursor.moveToNext());
            } else

                mnocursor.close();
        } catch (SQLiteException e) {

        }
        return Waybillno;
    }

//    public String GetLastRefusedWaybill(Context context) {
//        String Waybillno = "0";
//        try {
//            Cursor mnocursor = Fill("select * from MyRouteShipments where IsDelivered = 1 order by OnDeliveryDate desc Limit 1", context);
//
//            if (mnocursor.getCount() > 0) {
//                mnocursor.moveToFirst();
//                do {
//                    Waybillno = mnocursor.getString(mnocursor.getColumnIndex("ItemNo"));
//
//
//                } while (mnocursor.moveToNext());
//            } else
//
//                mnocursor.close();
//        } catch (SQLiteException e) {
//
//        }
//        return Waybillno;
//    }


    public Location GetLocationByWaybill(Context context, String Waybillno) {
        Location locations = new Location("");

        try {
            Cursor mnocursor = Fill("select * from MyRouteShipments where  ItemNo = '" + Waybillno + "'", context);

            if (mnocursor.getCount() > 0) {
                mnocursor.moveToFirst();
                do {

                    String lat = mnocursor.getString(mnocursor.getColumnIndex("Longitude"));
                    String longi = mnocursor.getString(mnocursor.getColumnIndex("Latitude"));

                    if ((lat.length() > 0 && longi.length() > 0) &&
                            !lat.equals("null") && longi.equals("null")) {
                        locations.setLongitude(Double.parseDouble(lat));
                        locations.setLatitude(Double.parseDouble(longi));
                    } else {
                        locations.setLongitude(0.0);
                        locations.setLatitude(0.0);
                    }

                } while (mnocursor.moveToNext());
            }

        } catch (SQLiteException e) {
            locations.setLongitude(0.0);
            locations.setLatitude(0.0);
        }
        return locations;
    }

    //---------------------------------Night Stock Table-------------------------------
    public boolean InsertNightStock(NightStock instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            contentValues.put("PiecesCount", instance.PieceCount);
            contentValues.put("CTime", instance.CTime.toString());
            contentValues.put("UserID", instance.UserID);
            contentValues.put("IsSync", instance.IsSync);
            contentValues.put("StationID", instance.StationID);
            contentValues.put("WaybillsCount", instance.WaybillsCount);
            contentValues.put("IDs", instance.IDs);
            contentValues.put("BIN", instance.BIN);

            result = db.insert("NightStock", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;

    }

    public boolean InsertNightStockWaybillDetail(NightStockWaybillDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", instance.WaybillNo);
            contentValues.put("NightStockId", instance.NightStockId);
            contentValues.put("IsSync", instance.IsSync);
            result = db.insert("NightStockWaybillDetail", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertNightStockDetail(NightStockDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", instance.BarCode);
            contentValues.put("NightStockId", instance.NightStockId);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("NightStockDetail", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void deleteNightStockD(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("NightStock", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void updateNightStockID(int ID, Context context) {

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            try {
                contentValues.put("IsSync", true);

                String args[] = {String.valueOf(ID)};
                db.update("NightStock", contentValues, "ID=?", args);
                db.close();
            } catch (Exception e) {
            }

        } catch (SQLiteException e) {
        }
    }


    public void deleteNightStockDWayBill(int MultiDeliveryID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(MultiDeliveryID)};
            db.delete("NightStockWaybillDetail", "NightStockID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteNightStockDBarcode(int MultiDeliveryID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(MultiDeliveryID)};
            db.delete("NightStockDetail", "NightStockID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean InsertFacility(int FacilityID, String Code, String Fname, int StationID, int FtypeID, String FTName, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("FacilityID", FacilityID);
            contentValues.put("Code", Code);
            contentValues.put("Name", Fname);
            contentValues.put("Station", StationID);
            contentValues.put("FacilityTypeID", FtypeID);
            contentValues.put("FacilityTypeName", FTName);
            result = db.insert("Facility", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertContacts(String Name, int StationID, String MobileNo, int Isprimary, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Name", Name);
            contentValues.put("StationID", StationID);
            contentValues.put("MobileNo", MobileNo);
            contentValues.put("Isprimary", Isprimary);

            result = db.insert("Contacts", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }


    public void deleteAllFacility(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from Facility");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteAllContacts(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from Contacts");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteAllDeliveryStatusReason(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from DeliveryStatusReason");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean InsertDeliveryStatusReason(int ReasonID, String Code, String Name, int DSID, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("ReasonID", ReasonID);
            contentValues.put("Code", Code);
            contentValues.put("Name", Name);
            contentValues.put("FName", Name);
            contentValues.put("DeliveyStatusID", DSID);
            result = db.insert("DeliveryStatusReason", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertTerminalHandling(TerminalHandling instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("EmployID", instance.EmployID);
            contentValues.put("Date", instance.Date.toString());
            contentValues.put("CheckPointTypeID", instance.CheckPointTypeID);
            contentValues.put("IsSync", instance.IsSync);
            contentValues.put("Latitude", instance.Latitude);
            contentValues.put("Longitude", instance.Longitude);
            contentValues.put("CheckPointTypeDetailID", instance.CheckPointTypeDetailID);
            contentValues.put("Ref", instance.Reference);
//            contentValues.put("Comments", instance.Comments);
            result = db.insertOrThrow("CheckPoint", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }

    public boolean InsertTerminalHandlingBulk(String instance, Context context, int rowcount) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();

            contentValues.put("Json", instance);
            contentValues.put("Count", rowcount);

            result = db.insertOrThrow("TerminalHandling", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void deleteNcl(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("Ncl", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean updateNCL(int ID, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Put the filed which you want to update.
        contentValues.put("IsSync", "1");
        try {
            String args[] = {String.valueOf(ID)};
            db.update("Ncl", contentValues, "ID=?", args);
        } catch (Exception e) {

            return false;
        }
        db.close();
        return true;
    }

    public void deleteNclWayBill(int NclID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(NclID)};
            db.delete("NclWaybillDetail", "NclID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean updateNCLWaybill(int ID, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Put the filed which you want to update.
        contentValues.put("IsSync", "1");
        try {
            String args[] = {String.valueOf(ID)};
            db.update("NclWaybillDetail", contentValues, "NclID=?", args);
        } catch (Exception e) {

            return false;
        }
        db.close();
        return true;
    }

    public void deleteNclBarcode(int NclID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(NclID)};
            db.delete("NclDetail", "NclID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean updateNCLBarcode(int ID, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Put the filed which you want to update.
        contentValues.put("IsSync", "1");
        try {
            String args[] = {String.valueOf(ID)};
            db.update("NclDetail", contentValues, "NclID=?", args);
        } catch (Exception e) {

            return false;
        }
        db.close();
        return true;
    }

    public boolean InsertNcl(Ncl instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();


            contentValues.put("NclNo", instance.NclNo);
            contentValues.put("UserID", instance.UserID);
            contentValues.put("Date", instance.Date.toString());
            contentValues.put("PieceCount", instance.PieceCount);
            contentValues.put("WaybillCount", instance.WaybillCount);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("Ncl", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;

    }

    public boolean InsertNclWaybillDetail(NclWaybillDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", instance.WaybillNo);
            contentValues.put("NclID", instance.NclID);
            contentValues.put("IsSync", instance.IsSync);
            result = db.insert("NclWaybillDetail", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertNclDetail(NclDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", instance.BarCode);
            contentValues.put("NclID", instance.NclID);
            contentValues.put("IsSync", instance.IsSync);

            result = db.insert("NclDetail", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertNclBulk(String instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();


            contentValues.put("NclNo", 0);
            contentValues.put("UserID", 0);
            contentValues.put("Date", 0);
            contentValues.put("PieceCount", 0);
            contentValues.put("WaybillCount", 0);
            contentValues.put("IsSync", false);
            contentValues.put("JsonData", instance);

            result = db.insert("Ncl", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;

    }

    public boolean InsertAtDestWaybill(int Waybill, String tripid, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WayBillNo", Waybill);
            contentValues.put("TrailerNo", tripid);
            contentValues.put("Date", GlobalVar.getDate());

            result = db.insert("AtDestWaybill", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertAtDestPieces(String Barcode, String tripid, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", Barcode);
            contentValues.put("TrailerNo", tripid);
            contentValues.put("Date", GlobalVar.getDate());

            result = db.insert("AtDestPiece", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public Integer deleteAtDestbyTrailerNo(String trilerNo, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            db.delete("AtDestWaybill", "TrailerNo = ? ",
                    new String[]{trilerNo});
            db.delete("AtDestPiece", "TrailerNo = ? ",
                    new String[]{trilerNo});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public boolean InsertAtOriginWaybill(String Waybilno, String pc, String empid, String userid, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", Waybilno);
            contentValues.put("PieceCount", pc);
            contentValues.put("EmployID", empid);
            contentValues.put("UserID", userid);
            contentValues.put("IsSync", "false");
            contentValues.put("StationID", "StationID");
            contentValues.put("ScannedPC", "0");
            contentValues.put("bgcolor", "0");
            contentValues.put("isdelete", "0");
            contentValues.put("Date", GlobalVar.getDate());

            result = db.insert("AtOriginLastWaybill", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertAtOriginPieces(String Waybilno, String barcode, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", Waybilno);
            contentValues.put("BarCode", barcode);
            contentValues.put("IsSync", "false");
            contentValues.put("bgcolor", "0");
            contentValues.put("isdelete", "0");
            contentValues.put("Date", GlobalVar.getDate());

            result = db.insert("AtOriginLastPieces", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean AtOriginScannedWaybill(String WaybillNo, View view) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Put the filed which you want to update.
        contentValues.put("bgcolor", "1");
        try {
            String args[] = {WaybillNo};
            db.update("AtOriginLastWaybill", contentValues, "WaybillNo=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            return false;
        }
        db.close();
        return true;
    }

    public boolean AtOriginScannedPiececode(String Barcode, View view) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Put the filed which you want to update.
        contentValues.put("bgcolor", "1");
        try {
            String args[] = {Barcode};
            db.update("AtOriginLastPieces", contentValues, "BarCode=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            return false;
        }
        db.close();
        return true;
    }

    public boolean AtOriginScannedPiecesCount(String WaybillNo, String piececount, View view) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Put the filed which you want to update.
        contentValues.put("ScannedPC", piececount);
        try {
            String args[] = {WaybillNo};
            db.update("AtOriginLastWaybill", contentValues, "WaybillNo=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            return false;
        }
        db.close();
        return true;
    }

    public void deleteAtOriginLastScans(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from AtOriginLastWaybill");
            db.execSQL("delete from AtOriginLastPieces");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public Integer deleteAtDestPiece(String Barcode, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.delete("AtDestPiece", "BarCode = ? ",
                    new String[]{Barcode});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public Integer deleteAtDestWaybill(String Waybillno, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.delete("AtDestWaybill", "WayBillNo = ? ",
                    new String[]{Waybillno});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public void deleteAtDestLastScans(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from AtDestWaybill");
            db.execSQL("delete from AtDestPiece");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteArrivedatDestPieces(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from AtDestPiece");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean InsertLoadtoDestWaybill(String Waybilno, String tripid, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", Waybilno);
            contentValues.put("TrailerNo", tripid);
            contentValues.put("Date", GlobalVar.getDate());
            result = db.insert("LoadtoDestLastWayBill", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertLoadtoDestPiece(String Barcode, String tripid, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", Barcode);
            contentValues.put("TrailerNo", tripid);
            contentValues.put("Date", GlobalVar.getDate());
            result = db.insert("LoadtoDestLastPiece", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void deleteLoadtoDestLastScans(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from LoadtoDestLastPiece");
            db.execSQL("delete from LoadtoDestLastWayBill");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public Integer deleteLoadtoDestPiece(String Barcode, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.delete("LoadtoDestLastPiece", "BarCode = ? ",
                    new String[]{Barcode});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public Integer deleteLoadtoDestWayBill(String Waybillno, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.delete("LoadtoDestLastWayBill", "WaybillNo = ? ",
                    new String[]{Waybillno});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public boolean UpdateTripDetails(int tripplanno, View view) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Put the filed which you want to update.
        contentValues.put("IsSync", 1);
        try {
            String args[] = {String.valueOf(tripplanno)};
            db.update("TripPlanDDetails", contentValues, "TripPlanNo=?", args);
        } catch (Exception e) {
            GlobalVar.GV().ShowSnackbar(view, e.getMessage(), GlobalVar.AlertType.Error);
            return false;
        }
        db.close();
        return true;
    }

    public Integer deleteLoadtoBytrailerNo(String trilerno, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.delete("LoadtoDestLastWayBill", "TrailerNo = ? ",
                    new String[]{trilerno});
            db.delete("LoadtoDestLastPiece", "TrailerNo = ? ",
                    new String[]{trilerno});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public boolean InsertCustomerRating(String json, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", json);

            result = db.insert("CustomerRating", null, contentValues);

            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public Integer deleteAtCustomerRating(Integer id, Context context) {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            result = db.delete("CustomerRating", "id = ? ",
                    new String[]{Integer.toString(id)});
        } catch (SQLiteException e) {

        }
        return result;
    }

    public void deleteAllCustomerRating(Context context) {
        int result = 0;
        try {

            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from CustomerRating");
            db.close();

        } catch (SQLiteException e) {

        }

    }


    public boolean InCablistInsert(Context context, int EmpID) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();
            contentValues.put("IsDate", GlobalVar.getDate());
            contentValues.put("EmpID", EmpID);

            result = db.insert("InCabCheckList", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean isInCabCheckList(Context context, int EmpID) throws Exception {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            Cursor empid = Fill("select * from InCabCheckList Where EmpID = " + EmpID, context); //IsDate = '" + GlobalVar.getDate() + "'" + " and


            if (empid != null && empid.getCount() > 0) {
                empid.moveToFirst();
                String date = empid.getString(empid.getColumnIndex("IsDate"));
                SimpleDateFormat date1 = new SimpleDateFormat("dd-MM-yyyy");
                Calendar c = Calendar.getInstance();
                c.setTime(date1.parse(date));
                c.add(Calendar.DATE, 6);  // number of days to add
                date = date1.format(c.getTime());


                String curdate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                empid.close();
                if (date.equals(curdate))
                    return true;

            } else {
                empid.close();
                return true;
            }

        } catch (SQLiteException e) {

        }

        return false;
    }

    public boolean UpdateInCabChecklist(Context context, int EmpID) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

        Cursor empid = Fill("select Distinct EmpID from InCabCheckList Where EmpID = " + EmpID, context); //IsDate = '" + GlobalVar.getDate() + "'" + " and


        if (empid != null && empid.getCount() > 0) {


            ContentValues contentValues = new ContentValues();
            //Put the filed which you want to update.
            contentValues.put("IsDate", GlobalVar.getDate());
            try {
                String args[] = {String.valueOf(GlobalVar.GV().EmployID)};
                db.update("InCabCheckList", contentValues, "EmpID=?", args);
            } catch (Exception e) {
                return false;
            }
        } else
            InCablistInsert(context, EmpID);
        db.close();
        return true;
    }

    public void deleteTerminalHandlingID(int ID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(ID)};
            db.delete("TerminalHandling", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deleteLoginIDs(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from UserME");
            db.execSQL("delete from UserMeLogin");
            db.execSQL("delete from FacilityLoggedIn");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean InsertTruck(String TruckName, int truckid, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            final String maxid = "SELECT *  FROM  Truck where TruckID = " + truckid;
            Cursor cur = db.rawQuery(maxid, null);

            if (cur.getCount() == 0) {

                ContentValues contentValues = new ContentValues();
                contentValues.put("Name", TruckName);
                contentValues.put("TruckID", truckid);
                contentValues.put("IsDate", GlobalVar.getDate());

                result = db.insert("Truck", null, contentValues);
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return result != -1;
    }

    public int GetTruck(Context context) {

        int truckID = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            final String maxid = "SELECT *  FROM  UserME where EmployID = " + GlobalVar.GV().EmployID + "  Order by ID Desc Limit 1";
            Cursor cur = db.rawQuery(maxid, null);

            if (cur.getCount() > 0) {

                cur.moveToFirst();
                truckID = cur.getInt(cur.getColumnIndex("TruckID"));
            }
            cur.close();
            db.close();
        } catch (SQLiteException e) {
            System.out.println(e);
        }
        return truckID;
    }

    public boolean DeleteTrucks(Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            String args[] = {GlobalVar.getDate()};
            db.delete("Truck", "IsDate!=?", args);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean DeleteTrucksData(Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            String args[] = {GlobalVar.getDate()};
            db.delete("Truck", null, null);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertDeliverReq(int waybillno, String barcode, String ValidDate, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", waybillno);
            contentValues.put("BarCode", barcode);
            contentValues.put("InsertedDate", GlobalVar.GV().getCurrentDateTime());
            contentValues.put("ValidDate", ValidDate);


            result = db.insert("DeliverReq", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public  void insertDelBulk(JSONArray deliveryReq, Context context) {
        String sql = "insert into DeliverReq (WaybillNo, BarCode, InsertedDate, ValidDate) values (?, ?, ?, ?);";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        //db.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < deliveryReq.length(); i++) {
            //generate some values
            try {

                JSONObject jsonObject1 = deliveryReq.getJSONObject(i);
                String insdate[] = jsonObject1.getString("InsertedDate").split("T");
                stmt.bindString(1, String.valueOf(jsonObject1.getInt("WaybillNo")));
                stmt.bindString(2, jsonObject1.getString("BarCode"));
                stmt.bindString(3, GlobalVar.GV().getCurrentDateTime());
                stmt.bindString(4, GlobalVar.GV().getDateAdd1Day(insdate[0]));

                long entryID = stmt.executeInsert();
                stmt.clearBindings();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
    }

    public  void insertReqBulk(JSONArray rtoReq, Context context) {
        String sql = "insert into RtoReq (WaybillNo, BarCode, InsertedDate, ValidDate) values (?, ?, ?, ?);";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        //db.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < rtoReq.length(); i++) {
            //generate some values
            try {
                JSONObject jsonObject1 = rtoReq.getJSONObject(i);
                stmt.bindString(1, String.valueOf(jsonObject1.getInt("WayBillNo")));
                stmt.bindString(2, jsonObject1.getString("BarCode"));
                stmt.bindString(3, GlobalVar.GV().getCurrentDateTime());
                stmt.bindString(4, GlobalVar.GV().getDateAdd1Day());

                long entryID = stmt.executeInsert();
                stmt.clearBindings();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
    }

    public boolean InsertRtoReq(int waybillno, String barcode, String ValidDate, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", waybillno);
            contentValues.put("BarCode", barcode);
            contentValues.put("InsertedDate", GlobalVar.GV().getCurrentDateTime());
            contentValues.put("ValidDate", ValidDate);

            result = db.insert("RtoReq", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public void deleteDeliverRtoReqData(Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from DeliverReq");
            db.execSQL("delete from RtoReq");
            db.close();
        } catch (SQLiteException e) {

        }
    }


}