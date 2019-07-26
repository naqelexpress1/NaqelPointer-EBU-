package com.naqelexpress.naqelpointer.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
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
import com.naqelexpress.naqelpointer.DB.DBObjects.NoNeedVolumeReason;
import com.naqelexpress.naqelpointer.DB.DBObjects.NotDelivered;
import com.naqelexpress.naqelpointer.DB.DBObjects.NotDeliveredDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDWaybill;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCloadingForD;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnDelivery;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnDeliveryDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.PickUp;
import com.naqelexpress.naqelpointer.DB.DBObjects.PickUpDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserME;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserSettings;
import com.naqelexpress.naqelpointer.DB.DBObjects.WaybillMeasurement;
import com.naqelexpress.naqelpointer.DB.DBObjects.WaybillMeasurementDetail;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

import java.io.File;

public class DBConnections
        extends SQLiteOpenHelper {
    private static final int Version = 14;
    private static final String DBName = "NaqelPointerDB.db";
    public Context context;
    public View rootView;

    //Added by ismail
    public static final String TABLENAME = "signature";
    public static final String COLUMNID = "id";
    public static final String COLUMNNAME_FILE = "filename";
    public static final String COLUMNNAME_EMPID = "empid";
    public static final String COLUMNNAME_FLAG = "flag";

    public DBConnections(Context context, View view) {
        super(context, DBName, null, Version);
        this.context = context;
        this.rootView = view;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS \"UserME\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"EmployID\" INTEGER NOT NULL , \"Password\" TEXT NOT NULL, \"StationID\" INTEGER NOT NULL , \"RoleMEID\" INTEGER, \"StatusID\" INTEGER NOT NULL, \"MachineID\" TEXT,  \"EmployName\" TEXT, \"EmployFName\" TEXT, \"MobileNo\" TEXT, \"StationCode\" TEXT, \"StationName\" TEXT, \"StationFName\" TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"UserLogs\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , \"UserID\" INTEGER NOT NULL , \"SuperVisorID\" INTEGER NOT NULL, \"IsSync\" BOOL NOT NULL , \"LogTypeID\" INTEGER NOT NULL , \"CTime\" DATETIME NOT NULL , \"MachineID\" TEXT , \"Remarks\" TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"UserMeLogin\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"EmployID\" INTEGER NOT NULL , \"StateID\" INTEGER NOT NULL , \"Date\" DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP, \"HHDName\" TEXT check(typeof(\"HHDName\") = 'text') , \"Version\" TEXT NOT NULL  check(typeof(\"Version\") = 'text') , \"IsSync\" BOOL NOT NULL , \"TruckID\" INTEGER , \"LogoutDate\" DATETIME , \"LogedOut\" BOOL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"OnDelivery\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , \"WaybillNo\" INTEGER NOT NULL , \"ReceiverName\" TEXT (100) NOT NULL , \"PiecesCount\" INTEGER NOT NULL , \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" INTEGER NOT NULL , \"EmployID\" INTEGER NOT NULL , \"StationID\" INTEGER NOT NULL , \"IsPartial\" BOOL NOT NULL  DEFAULT 0, \"Latitude\" TEXT, \"Longitude\" TEXT , \"TotalReceivedAmount\" DOUBLE NOT NULL , \"CashAmount\" DOUBLE NOT NULL DEFAULT 0, \"POSAmount\" DOUBLE NOT NULL DEFAULT 0 , \"IsSync\" BOOL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"OnDeliveryDetail\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"DeliveryID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"Station\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Code\" TEXT, \"Name\" TEXT NOT NULL , \"FName\" TEXT, \"CountryID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"DeliveryStatus\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Code\" TEXT, \"Name\" TEXT NOT NULL , \"FName\" TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"NotDelivered\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"WaybillNo\" TEXT NOT NULL , \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" DATETIME NOT NULL , \"UserID\" INTEGER NOT NULL , \"IsSync\" BOOL NOT NULL , \"StationID\" INTEGER NOT NULL , \"PiecesCount\" INTEGER NOT NULL , \"DeliveryStatusID\" INTEGER NOT NULL , \"Notes\" TEXT, \"Latitude\" TEXT, \"Longitude\" TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"NotDeliveredDetail\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"NotDeliveredID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUp\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"WaybillNo\" TEXT NOT NULL ," +
                " \"ClientID\" INTEGER, \"FromStationID\" INTEGER NOT NULL , \"ToStationID\" INTEGER NOT NULL , " +
                "\"PieceCount\" INTEGER NOT NULL , \"Weight\" DOUBLE, \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" DATETIME NOT NULL , " +
                "\"IsSync\" BOOL NOT NULL , \"UserID\" INTEGER NOT NULL , \"StationID\" INTEGER NOT NULL , \"RefNo\" TEXT, \"Latitude\"" +
                " TEXT, \"CurrentVersion\" TEXT NOT NULL, \"Longitude\" TEXT ,\"LoadTypeID\" INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"PickUpDetail\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"PickUpID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"UserSettings\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE  , \"EmployID\" INTEGER NOT NULL , \"ShowScaningCamera\" BOOL NOT NULL , \"IPAddress\" TEXT NOT NULL , \"LastBringMasterData\" DATETIME)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CourierDailyRoute\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , \"EmployID\" INTEGER NOT NULL , \"StartingTime\" DATETIME NOT NULL , \"StartLatitude\" TEXT, \"StartLongitude\" TEXT, \"EndTime\" DATETIME NULL , \"EndLatitude\" TEXT, \"EndLongitude\" TEXT, \"DeliverySheetID\" INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"OnCloadingForD\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , \"CourierID\" INTEGER NOT NULL , \"UserID\" INTEGER NOT NULL , \"IsSync\" BOOL NOT NULL , \"CTime\" DATETIME NOT NULL , \"PieceCount\" INTEGER NOT NULL , \"TruckID\" TEXT, \"WaybillCount\" INTEGER NOT NULL , \"StationID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"OnCLoadingForDDetail\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , \"BarCode\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"OnCLoadingForDID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"OnCLoadingForDWaybill\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , \"WaybillNo\" TEXT NOT NULL , \"IsSync\" BOOL NOT NULL , \"OnCLoadingID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"MyRouteShipments\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , \"OrderNo\" INTEGER NOT NULL , \"ItemNo\" TEXT NOT NULL , \"TypeID\" INTEGER NOT NULL , \"BillingType\" TEXT, \"CODAmount\" DOUBLE NOT NULL , \"DeliverySheetID\" INTEGER, \"Date\" DATETIME NOT NULL , \"ExpectedTime\" DATETIME, \"Latitude\" TEXT NOT NULL , \"Longitude\" TEXT NOT NULL , \"ClientID\" INTEGER NOT NULL , \"ClientName\" TEXT NOT NULL , \"ClientFName\" TEXT NOT NULL , \"ClientAddressPhoneNumber\" TEXT NOT NULL , \"ClientAddressFirstAddress\" TEXT NOT NULL , \"ClientAddressSecondAddress\" TEXT NOT NULL , \"ClientContactName\" TEXT NOT NULL , \"ClientContactFName\" TEXT NOT NULL , \"ClientContactPhoneNumber\" TEXT , \"ClientContactMobileNo\" TEXT NOT NULL , \"ConsigneeName\" TEXT NOT NULL , \"ConsigneeFName\" TEXT NOT NULL , \"ConsigneePhoneNumber\" TEXT NOT NULL , \"ConsigneeFirstAddress\" TEXT NOT NULL , \"ConsigneeSecondAddress\" TEXT NOT NULL , \"ConsigneeNear\" TEXT NOT NULL , \"ConsigneeMobile\" TEXT NOT NULL , \"Origin\"  TEXT NOT NULL , \"Destination\" TEXT NOT NULL , \"PODNeeded\" BOOL NOT NULL , \"PODDetail\" TEXT NOT NULL , \"PODTypeCode\" TEXT NOT NULL , \"PODTypeName\" TEXT NOT NULL , \"IsDelivered\" BOOL , \"NotDelivered\" BOOL , \"CourierDailyRouteID\" INTEGER , \"OptimzeSerialNo\" INTEGER , \"HasComplaint\" BOOL, \"HasDeliveryRequest\" BOOL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPoint\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"EmployID\" INTEGER NOT NULL , \"Date\" DATETIME NOT NULL , \"CheckPointTypeID\" INTEGER NOT NULL , \"CheckPointTypeDetailID\" INTEGER NULL , \"CheckPointTypeDDetailID\" INTEGER NULL , \"Latitude\" TEXT, \"Longitude\" TEXT, \"IsSync\" BOOL NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointWaybillDetails\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"WaybillNo\" TEXT NOT NULL , \"CheckPointID\" INTEGER NOT NULL , \"IsSync\" BOOL NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointBarCodeDetails\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE , \"BarCode\" TEXT NOT NULL , \"CheckPointID\" INTEGER NOT NULL , \"IsSync\" BOOL NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointType\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Name\" TEXT NOT NULL , \"FName\" TEXT )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointTypeDetail\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Name\" TEXT NOT NULL , \"FName\" TEXT , \"CheckPointTypeID\" INTEGER NOT NULL  )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"CheckPointTypeDDetail\" (\"ID\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"Name\" TEXT NOT NULL , \"FName\" TEXT , \"CheckPointTypeDetailID\" INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"MultiDelivery\"  ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"ReceiverName\" TEXT NOT NULL, \"PiecesCount\" INTEGER NOT NULL, \"TimeIn\" DATETIME NOT NULL , \"TimeOut\" DATETIME NOT NULL , \"UserID\" INTEGER NOT NULL, \"IsSync\" BOOL NOT NULL, \"StationID\" INTEGER NOT NULL, \"WaybillsCount\" INTEGER NOT NULL, \"Latitude\" TEXT, \"Longitude\" TEXT, \"ReceivedAmt\" DOUBLE, \"ReceiptNo\" TEXT, \"StopPointsID\" INTEGER )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"MultiDeliveryDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"BarCode\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"MultiDeliveryID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"MultiDeliveryWaybillDetail\" ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"WaybillNo\" TEXT NOT NULL, \"IsSync\" BOOL NOT NULL, \"MultiDeliveryID\" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"WaybillMeasurement\"  ( \"ID\" INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  UNIQUE, \"WaybillNo\" INTEGER NOT NULL, \"TotalPieces\" INTEGER NOT NULL, \"EmployID\" INTEGER NOT NULL , \"StationID\" INTEGER NOT NULL , \"CTime\" DATETIME NOT NULL, \"IsSync\" BOOL NOT NULL, \"HHD\" TEXT, \"Weight\" DOUBLE NOT NULL, \"NoNeedVolume\" BOOL NOT NULL , \"NoNeedVolumeReasonID\" INTEGER )");
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
                "\"Name\" INTEGER NOT NULL , \"MobileNo\" TEXT )");

        db.execSQL("CREATE TABLE IF NOT EXISTS LoadtoDestination (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Productivity\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"OFD\"  INTEGER NOT NULL DEFAULT 0 , \"Attempted\"  INTEGER NOT NULL DEFAULT 0," +
                "\"Delivered\"  INTEGER NOT NULL DEFAULT 0, \"Exceptions\"  INTEGER NOT NULL DEFAULT 0, \"Date\" DATETIME NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"CallLog\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Type\"  text NOT NULL , \"MNO\"  text NOT NULL," +
                "\"Duration\"  text NOT NULL, \"WayBillNo\"  text , \"Date\" DATETIME NOT NULL, \"CallStartTime\" DATETIME NOT NULL," +
                "\"CallEndTime\" DATETIME NOT NULL , \"Number\"  text)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"AppVersion\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Version\"  INTEGER NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Complaint\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"TotalComp\"  INTEGER NOT NULL DEFAULT 0 , \"Attempted\"  INTEGER NOT NULL DEFAULT 0," +
                "\"Delivered\"  INTEGER NOT NULL DEFAULT 0, \"Exceptions\"  INTEGER NOT NULL DEFAULT 0, \"Date\" DATETIME NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"AtOrigin\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"Palletize\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"TripPlanDetails\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"TripPlanDDetails\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestination\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                "\"Json\"  TEXT NOT NULL )");
    }

    public int getVersion() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.getVersion();
    }

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
            //Added by ismail
            db.execSQL("create table if not exists " + TABLENAME + "(" + COLUMNID
                    + " integer primary key," + COLUMNNAME_FILE + " text,"
                    + COLUMNNAME_EMPID + " text," + COLUMNNAME_FLAG
                    + " integer not null default 1)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"MobileNo\" (\"ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  UNIQUE , " +
                    "\"Name\" INTEGER NOT NULL , \"MobileNo\" TEXT )");

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
                    "\"CallEndTime\" DATETIME NOT NULL , \"Number\"  text)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"Complaint\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"TotalComp\"  INTEGER NOT NULL DEFAULT 0 , \"Attempted\"  INTEGER NOT NULL DEFAULT 0," +
                    "\"Delivered\"  INTEGER NOT NULL DEFAULT 0, \"Exceptions\"  INTEGER NOT NULL DEFAULT 0, \"Date\" DATETIME NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"Palletize\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"TripPlanDetails\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"TripPlanDDetails\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");


            db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestination\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            db.execSQL("CREATE TABLE IF NOT EXISTS \"AtDestination\" (\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL  UNIQUE ," +
                    "\"Json\"  TEXT NOT NULL )");

            if (!isColumnExist("PickUp", "LoadTypeID"))
                db.execSQL("ALTER TABLE PickUp ADD COLUMN LoadTypeID INTEGER DEFAULT 0");
        }


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
            SQLiteDatabase db = getReadableDatabase();
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

            result = db.insert("UserME", null, contentValues);
            db.close();

        } catch (SQLiteException e)

        {

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
            contentValues.put("Date", instance.Date.toString());

            result = db.insert("UserMeLogin", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

        }
        return result != -1;
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
                db.update("UserMELogin", contentValues, "ID=?", args);
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

//    public void deleteUserMELogin(UserMeLogin instance)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        try
//        {
//            String args[] = {String.valueOf(instance.ID)};
//            db.delete("UserMeLogin", "ID=?", args);
//        }
//        catch (Exception e)
//        {
//            GlobalVar.GV().ShowSnackbar(rootView,e.getMessage(), GlobalVar.AlertType.Error);
//        }
//    }

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
    public boolean InsertOnDelivery(OnDelivery instance, Context context) {

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

            result = db.insert("OnDelivery", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
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
    public boolean InsertPickUp(PickUp instance, Context context, int lid) {
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
            result = db.insert("PickUp", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }

    public boolean InsertPickUpDetail(PickUpDetail instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            ContentValues contentValues = new ContentValues();
            contentValues.put("BarCode", instance.BarCode);
            contentValues.put("PickUpID", instance.PickUpID);
            contentValues.put("IsSync", instance.IsSync);
            result = db.insert("PickUpDetail", null, contentValues);
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

    //---------------------------------Not Delivery Table-------------------------------
    public boolean InsertNotDelivered(NotDelivered intstance, Context context) {

        long result = 0;
        try {


            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("WaybillNo", intstance.WaybillNo);
            contentValues.put("TimeIn", String.valueOf(intstance.TimeIn));
            contentValues.put("TimeOut", String.valueOf(intstance.TimeOut));
            contentValues.put("UserID", intstance.UserID);
            contentValues.put("IsSync", intstance.IsSync);
            contentValues.put("StationID", intstance.StationID);
            contentValues.put("PiecesCount", intstance.PiecesCount);
            contentValues.put("DeliveryStatusID", intstance.DeliveryStatusID);
            contentValues.put("Notes", intstance.Notes);
            contentValues.put("Latitude", intstance.Latitude);
            contentValues.put("Longitude", intstance.Longitude);

            result = db.insert("NotDelivered", null, contentValues);
            db.close();

        } catch (SQLiteException e) {

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
            db.delete("Productivity", null, null);
            db.delete("Complaint", null, null);
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
    public boolean InsertMultiDelivery(MultiDelivery instance, Context context) {
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

    public void deleteAllMeasurement() {
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
            result = db.insert("CheckPoint", null, contentValues);
            db.close();
        } catch (SQLiteException e) {

        }
        return result != -1;
    }


    public boolean InsertMobileNumbers(String name, String Mno, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Name", name);
            contentValues.put("MobileNo", Mno);
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

            if (isColumnExist("MyRouteShipments", "OptimzeSerialNo", context))
                contentValues.put("OptimzeSerialNo", 0);

            result = db.insert("MyRouteShipments", null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return result != -1;
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

    public void deleteAllTrip() {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("delete from Palletize");
            db.execSQL("delete from TripPlanDetails");
            db.execSQL("delete from TripPlanDDetails");
            db.close();
        } catch (SQLiteException e) {

        }
    }

    public boolean InsertTripDDetails(String instance, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Json", instance);

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


    public void deleteAllOrigin() {
        int result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("delete from AtOrigin");
            db.close();
        } catch (SQLiteException e) {

        }

    }

    public boolean InsertComplaint(int complaint, String date, Context context) {
        long result = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            final String maxid = "SELECT *  FROM  Complaint WHERE Date = '" + date + "' and Attempted = " + GlobalVar.GV().EmployID;
            Cursor cur = db.rawQuery(maxid, null);
            cur.moveToFirst();
            if (cur.getCount() == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("TotalComp", complaint);
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

            ContentValues contentValues = new ContentValues();
            contentValues.put("Version", Version);


            result = db.insert("AppVersion", null, contentValues);
            db.close();

        } catch (SQLiteException e) {
            return false;
        }
        return result != -1;
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


            result = db.insert("CallLog", null, contentValues);
            db.close();

        } catch (SQLiteException e) {
            return false;
        }
        return result != -1;
    }

    public String getWaybillByMobileNo(String mobileNo, Context context) {

        String WaybillNo = "";
//        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        Cursor result = Fill("select * from MyRouteShipments where ConsigneeMobile like '%" + mobileNo + "'", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            WaybillNo = result.getString(result.getColumnIndex("ItemNo"));
        } else {
            result = Fill("select * from MyRouteShipments where ConsigneePhoneNumber like '%" + mobileNo + "'", context);
            if (result.getCount() > 0) {
                result.moveToFirst();
                WaybillNo = result.getString(result.getColumnIndex("ItemNo"));
            } else
                WaybillNo = "0";
        }
        return WaybillNo;
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
            db.delete("PickUp", "ID=?", args);

            db.close();
        } catch (SQLiteException e) {

        }
    }

    public void deletePickupDetails(int pickupid, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(pickupid)};
            db.delete("PickUpDetail", "PickUpID=?", args);

            db.close();
        } catch (SQLiteException e) {

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

    public void deleteDeliveyDetails(int deliveryID, Context context) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            String args[] = {String.valueOf(deliveryID)};
            db.delete("OnDeliveryDetail", "DeliveryID=?", args);

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

}