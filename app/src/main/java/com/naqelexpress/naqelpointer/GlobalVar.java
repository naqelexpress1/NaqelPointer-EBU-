package com.naqelexpress.naqelpointer;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.naqelexpress.naqelpointer.Activity.BookingCBU.BookingList;
import com.naqelexpress.naqelpointer.Activity.CBM.CBM;
import com.naqelexpress.naqelpointer.Activity.FuelModelEBU.Fuel;
import com.naqelexpress.naqelpointer.Activity.Login.SplashScreenActivity;
import com.naqelexpress.naqelpointer.Activity.MyrouteCBU.MyRouteActivity_Complaince_GroupbyPhn;
import com.naqelexpress.naqelpointer.Activity.Rating.CourierRating;
import com.naqelexpress.naqelpointer.Activity.SkipWaybillNofromRouteLine.SkipWaybillNoinRouteLine;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.Languages;
import com.naqelexpress.naqelpointer.Classes.OnUpdateListener;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.Booking;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointType;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointTypeDDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointTypeDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.DeliveryStatus;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.DB.DBObjects.NoNeedVolumeReason;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserSettings;
import com.naqelexpress.naqelpointer.JSON.DataSync;
import com.naqelexpress.naqelpointer.JSON.ProjectAsyncTask;
import com.naqelexpress.naqelpointer.JSON.Request.DataTableParameters;
import com.naqelexpress.naqelpointer.JSON.Results.CheckPendingCODResult;
import com.naqelexpress.naqelpointer.JSON.Results.CheckPointTypeResult;
import com.naqelexpress.naqelpointer.JSON.Results.GetShipmentForPickingResult;
import com.naqelexpress.naqelpointer.Models.Enum.Enum;
import com.naqelexpress.naqelpointer.Models.Request.AlertRequest;
import com.naqelexpress.naqelpointer.NCLBlockWaybills.NclShipmentActivity;
import com.naqelexpress.naqelpointer.Receiver.LocationupdateInterval;
import com.naqelexpress.naqelpointer.Retrofit.IPointerAPI;
import com.naqelexpress.naqelpointer.callback.AlertCallback;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class GlobalVar {

    public UserSettings currentSettings;
    public boolean autoLogout = false;

    public String AppVersion = "CBU SpAsr Final Test - 28-11-2021"; //"RouteLineSeq 15-01-2021";
    public static int triedTimes = 0;
    public static int triedTimes_ForDelService = 0;
    public static int triedTimes_ForNotDeliverService = 0;
    public static int triedTimes_ForDelSheetService = 0;
    public static int triedTimes_ForDelSheetServicebyNCL = 0;
    public static int triedTimes_ForArrivedatDest = 0;
    public static int triedTimes_ForAtOrigin = 0;
    public static int triedTimes_ForPickup = 0;
    public static int triedTimesCondition = 2;
    public boolean LoginVariation = true; //For EBU true only
    //For TH APP Enable true and AppIDForTH is 1
    public boolean IsTerminalApp = false; //For TH onlyre
    public int AppIDForTH = 0; //for TH only 1
    public String ExcludeCamera = "TC25TC26"; //For EBU true only
    //


    public static int ScanWaybillLength = 9;
    public static int ScanBarcodeLength = 14;
    public static boolean ManualType = false;
    public static String WaybillNoStartSeries = "8"; //812345679

    private String WebServiceVersion = "2.0";
    public int AppID = 6;
    public int AppTypeID = 1;
    public boolean ThereIsMandtoryVersion = false;
    public String NaqelPointerAPILink_For5_1 = "https://naqelpointerpd.naqelksa.com/api/pointer/";
    public String NaqelPointerAPILink_For5_2 = "https://naqelpointersc.naqelksa.com/Api/Pointer/";
    public String NaqelPointerAPILink = "https://naqelpointerpd.naqelksa.com/api/pointer/";
    public String NaqelPointerAPILink_UploadImage = "https://naqelpointersc.naqelksa.com/Api/Pointer/";
    public String NaqelPointerAPILink1_ForDomain = "https://naqelpointerpd.naqelksa.com/api/pointer/";
    public String NaqelPointerAPILink2_ForDomain = "https://naqelpointersc.naqelksa.com/Api/Pointer/";
    public String NaqelPointerAPILinkForHighValueAlarm = "https://infotrack.naqelexpress.com/NaqelPointer/Api/Pointer/";
    public String NaqelPointerLivetrackingPusher = "https://pointercourierlocation.naqelksa.com/api/CourierLocation/InsertCourierLocation";
    public String NaqelApk = "https://naqelpointersc.naqelksa.com/Download/";
    static IPointerAPI iPointerAPI;


    public ArrayList<Integer> haslocation = new ArrayList<>();
    public int ConnandReadtimeout = 60000;
    public int Connandtimeout30000 = 30000;
    public int ConnandReadtimeout50000 = 50000;

    public int loadbalance_Contimeout = 30000;
    public int loadbalance_ConRedtimeout = 30000;

    public boolean isneedOtp = true;

    public boolean isFortesting = false;
    public int isRouteLineSeqLimit = 24;

    public int CourierDailyRouteID = 0;
    public String EmployName = "";
    public String EmployMobileNo = "";
    public String EmployStation = "";
    public boolean SignedIn = false;

    public int EmployID = 0;
    public int UserID = 0;

    public String UserPassword = "";
    public int StationID = 0;

    public LatLng currentLocation = new LatLng(0, 0);
    public String MachineID = "SmartPhone";
    //  public boolean HasInternetAccess = false;
    public final int CAMERA_PERMISSION_REQUEST = 100;

    public static String naqelvehicleimagepath = Environment.getExternalStorageDirectory()
            + "/NaqelVehicleImages";


    //  public DBConnections dbConnections;
    //public Context context;
    public Context MainContext;
    // public View rootView;
    //  public View rootViewMainPage;
    //   public Activity activity;
    private static GlobalVar gv;
    public static boolean gs = false, dsl = false, cptl = false, cptdl = false, cptddl = false, nnvdl = false;
//    private ArrayList<String> DataTypeList = new ArrayList<>();

    //Riyam
    public static final int NclArrivalTH = 1;
    public static final int DsAndInventoryTHCourier = 2;
    public static final int DsValidationCourier = 3;
    public static final int NclGWT = 4;

    public final String NotificationID_RecordVoice = "151";

    public static GlobalVar GV() {
        if (GlobalVar.gv == null) {
            GlobalVar.gv = new GlobalVar();
            GlobalVar.gv.Init();
        }
        return gv;
    }

    public ArrayList<Languages> LanguageList = new ArrayList<>();
    public ArrayList<String> LanguageNameList = new ArrayList<>();
    public ArrayList<Booking> myBookingList;
    public ArrayList<MyRouteShipments> myRouteShipmentList;
    public ArrayList<CheckPendingCODResult> checkPendingCODList;
    public ArrayList<String> optimizedOutOfDeliveryShipmentList;
    public ArrayList<CheckPointTypeResult> checkPointTypeResultsList;


    private void Init() {
        GlobalVar.GV().MachineID = GlobalVar.GV().getManufacturerSerialNumber();
        GlobalVar.GV().myBookingList = new ArrayList<>();
        GlobalVar.GV().myRouteShipmentList = new ArrayList<>();

        //        GlobalVar.GV().checkPendingCODList = new ArrayList<>();
        //  GlobalVar.GV().NaqelPointerAPILink = "http://212.93.160.150/NaqelAPIServices/RouteOptimization/"+GlobalVar.GV().WebServiceVersion+"/WCFRouteOptimization.svc/";
//        GlobalVar.GV().DataTypeList.add("Delivery");
//        GlobalVar.GV().DataTypeList.add("NotDelivered");

        GlobalVar.GV().LanguageList.add(new Languages("ar", "عربي"));
        GlobalVar.GV().LanguageList.add(new Languages("en", "English"));
        GlobalVar.GV().LanguageList.add(new Languages("tl", "Filipino"));
        GlobalVar.GV().LanguageList.add(new Languages("hi", "Hindi"));
        GlobalVar.GV().LanguageList.add(new Languages("ne", "Nepali"));
        GlobalVar.GV().LanguageList.add(new Languages("ta", "Tamil"));
        GlobalVar.GV().LanguageList.add(new Languages("ur", "Urdu"));
    }

    private String getManufacturerSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serial = (String) get.invoke(c, "ril.serialnumber", "unknown");
        } catch (Exception ignored) {
        }
        return serial;
    }

    public boolean IsEnglish() {
        return Locale.getDefault().getLanguage().equals("en");
    }

    public boolean IsArabic() {
        return Locale.getDefault().getLanguage().equals("ar");
    }

    public int GetLanguageID() {
        if (IsEnglish())
            return 1;
        else
            return 2;
    }

    public enum AlertType {
        Info,
        Warning,
        Error
    }

//    public void ShowMessage(Context context, String Message, AlertType alertType) {
//        Toast toast = Toast.makeText(context, Message, Toast.LENGTH_LONG);
//        View view = toast.getView();
//        TextView text = (TextView) view.findViewById(android.R.id.message);
//
//        if (alertType == AlertType.Error) {
//            view.setBackgroundResource(R.color.NaqelRed);
//            text.setTextColor(Color.WHITE);
//        } else if (alertType == AlertType.Info) {
//            view.setBackgroundResource(R.color.NaqelBlue);
//            text.setTextColor(Color.WHITE);
//        } else if (alertType == AlertType.Warning) {
//            view.setBackgroundResource(R.color.NaqelRed);
//            text.setTextColor(Color.WHITE);
//        }
//        toast.show();
//    }

    public void ShowSnackbar(View view, String Message, AlertType alertType) {

        hideKeyboardFrom(view.getContext(), view);
        try {
            Snackbar snackbar = Snackbar.make(view, Message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
//        TextView text = (TextView) view.findViewById(android.R.id.message);

            if (alertType == AlertType.Error) {
                sbView.setBackgroundResource(R.color.NaqelRed);
            } else if (alertType == AlertType.Info) {
                sbView.setBackgroundResource(R.color.NaqelBlue);
            } else if (alertType == AlertType.Warning) {
                sbView.setBackgroundResource(R.color.NaqelBlue);
            }
            snackbar.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void ShowSnackbar(View view, String Message, AlertType alertType, boolean Playsound, Context context) {
        Snackbar snackbar = Snackbar.make(view, Message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();

        if (alertType == AlertType.Error) {
            sbView.setBackgroundResource(R.color.NaqelRed);
            if (Playsound)
                //MakeSound(GlobalVar.GV().context, R.raw.wrongbarcodescan);
                MakeSound(context, R.raw.wrongbarcodescan);
        } else if (alertType == AlertType.Info) {
            sbView.setBackgroundResource(R.color.NaqelBlue);
        } else if (alertType == AlertType.Warning) {
            sbView.setBackgroundResource(R.color.NaqelRed);
        }
        snackbar.show();
    }

    public static void ShowDialog(Activity activity, String title, String Message, boolean Cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(Cancelable);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    private static long getRawContactId(Context ctx) {
        // Inser an empty contact.
        ContentValues contentValues = new ContentValues();
        Uri rawContactUri = ctx.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
        // Get the newly created contact raw id.
        long ret = ContentUris.parseId(rawContactUri);
        return ret;
    }

    private static void insertContactDisplayName(Uri addContactsUri, long rawContactId, String displayName, Context ctx) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, displayName);
        ctx.getContentResolver().insert(addContactsUri, contentValues);

    }

    private static void insertContactPhoneNumber(Uri addContactsUri, long rawContactId, String phoneNumber, Context ctx) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        int phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, phoneContactType);
        ctx.getContentResolver().insert(addContactsUri, contentValues);

    }

    public static void addMobileNumber(String MobileNo, Context context) {

        //deleteContact(context, MobileNo);

        Uri addContactsUri = ContactsContract.Data.CONTENT_URI;

        // Add an empty contact and get the generated id.
        long rowContactId = getRawContactId(context);

        // Add contact name data.
        String displayName = "HasWatsapp";

        insertContactDisplayName(addContactsUri, rowContactId, displayName, context);

        // Add contact phone data.
        insertContactPhoneNumber(addContactsUri, rowContactId, MobileNo, context);
        contactIdByPhoneNumber(context, MobileNo);


//        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
//
//        int rawContactID = ops.size();
//
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
//                .build());
//
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "MadHasWatsapp")
//                .build());
//
//
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNo)
//                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
//                .build());

    }

    public static boolean deleteContact(Context ctx, String phone) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    // if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                    String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                    ctx.getContentResolver().delete(uri, null, null);
                    return true;
                    // }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }

    public static boolean contactIdByPhoneNumber(Context ctx, String phoneNumber) {
        String contactId = null;
        if (phoneNumber != null && phoneNumber.length() > 0) {
            ContentResolver contentResolver = ctx.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup._ID};

            Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                }
                cursor.close();
            }
        }
        boolean watsapp = false;
        if (contactId != null)
            watsapp = haswatsapp(contactId, ctx);
        else
            addMobileNumber(phoneNumber, ctx);


        return watsapp;
    }

    private static boolean haswatsapp(String ContactID, Context context) {
        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND account_type IN (?)";
        String[] selectionArgs = new String[]{ContactID, "com.whatsapp"};
        Cursor cursor = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        boolean hasWhatsApp = cursor.moveToNext();
//        String rowContactId = "";
//        if (hasWhatsApp) {
//            rowContactId = cursor.getString(0);
//        }
//        cursor.close();
        return hasWhatsApp;
    }


//    public void ShowDialogCustom(Context context, String title, String Message)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(Message);
//        View view = LayoutInflater.from(context).inflate(R.layout.activity_dialog, null);
//        builder.setView(view);
//        builder.show();
//    }

    public static void MakeSound(final Context context, final int url) {
        Thread t = new Thread() {
            public void run() {

                final MediaPlayer barcodeSound = MediaPlayer.create(context, url);
                barcodeSound.start();
                //barcodeSound.stop();
                barcodeSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        try {

                            mp.stop();
                            mp.release();
                            barcodeSound.stop();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        t.start();

    }

    public enum PermissionType {
        AccessFindLocation,
        Camera,
        Phone,
        Storage
    }

    // Check for permission to access Location
    public boolean checkPermission(Activity activity, PermissionType permissionType) {
        if (permissionType == PermissionType.AccessFindLocation) {
            return (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED);
        } else if (permissionType == PermissionType.Camera) {
            return (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED);
        } else if (permissionType == PermissionType.Phone) {
            return (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED);
        } else if (permissionType == PermissionType.Storage) {
            return (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }

    // Asks for permission
    public void askPermission(Activity activity, PermissionType permissionType) {
        if (permissionType == PermissionType.AccessFindLocation) {
            int LOCATION_PERMISSION_REQUEST = 200;
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
        } else if (permissionType == PermissionType.Camera) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST
            );
        } else if (permissionType == PermissionType.Phone) {
            int PHONE_PERMISSION_REQUEST = 300;
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.CALL_PHONE},
                    PHONE_PERMISSION_REQUEST
            );
        }
    }

    public String GetStationByID(int stationID, ArrayList<String> StationNameList, ArrayList<Integer> StationList) {
        String result = "";

        for (int i = 0; i < StationNameList.size(); i++) {
            if (StationList.get(i) == stationID) {
                // result = StationList.get(i) + " : " + StationList.get(i);
                result = StationNameList.get(i);
                break;
            }
        }

        return result;
    }

    public ArrayList<GetShipmentForPickingResult> GetShipmentForPickingResultList;
    public ArrayList<Station> StationList = new ArrayList<>();
    public ArrayList<String> StationNameList = new ArrayList<>();
    public ArrayList<String> StationFNameList = new ArrayList<>();

    public void GetStationList(boolean bringFromServer, final Context context, final View view) {


        DateTime dt = DateTime.now();
        int dayOfMonth = dt.getDayOfMonth();

//        if (GlobalVar.GV().StationList.size() <= 0 || dayOfMonth % 3 == 0)
//            if (GlobalVar.GV().StationList.size() <= 0 || (bringFromServer && LastBringMasterData(context, EmployID) > 0)) {
        if (bringFromServer) {
            DataTableParameters dataTableParameters = new DataTableParameters();
            dataTableParameters.AppID = GlobalVar.GV().AppID;
            dataTableParameters.FilterString = "ID > 0";
            dataTableParameters.Length = 5000;
            dataTableParameters.Source = "ViwStation";
            dataTableParameters.Start = 0;

            String jsonData = JsonSerializerDeserializer.serialize(dataTableParameters, true);
            ProjectAsyncTask task = new ProjectAsyncTask("GetStation", "Post", jsonData);
            task.setUpdateListener(new OnUpdateListener() {
                public void onPostExecuteUpdate(String obj) {
                    GlobalVar.gs = true;
                    new Station(obj, view, context);
                }

                public void onPreExecuteUpdate() {
                    // GlobalVar.GV().ShowSnackbar(view, "Loading Station List", AlertType.Info);
                }

            });
            task.execute();

            // return;
        }


//        DBConnections dbConnections = new DBConnections(context, null);
//        GlobalVar.GV().StationList.clear();// = new ArrayList<>();
//        GlobalVar.GV().StationNameList.clear();// = new ArrayList<>();
//        GlobalVar.GV().StationFNameList.clear();// = new ArrayList<>();
//        Cursor result = dbConnections.Fill("select * from Station");
//        if (result.getCount() > 0) {
//            result.moveToFirst();
//            do {
//                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                String Code = result.getString(result.getColumnIndex("Code"));
//                String Name = result.getString(result.getColumnIndex("Name"));
//                String FName = result.getString(result.getColumnIndex("FName"));
//                int CountryID = Integer.parseInt(result.getString(result.getColumnIndex("CountryID")));
//
//                Station instance = new Station(ID, Code, Name, FName, CountryID);
//                GlobalVar.GV().StationList.add(instance);
//                GlobalVar.GV().StationNameList.add(Code + " : " + Name);
//                GlobalVar.GV().StationFNameList.add(FName);
//            }
//            while (result.moveToNext());
//
//
//        }
//        dbConnections.close();
//        GlobalVar.gs = true;

    }

//    public void GetMasterData(final Activity activity, final View view, final ProgressDialog dialog) {
//        GetDeliveryStatusRequest getDeliveryStatusRequest = new GetDeliveryStatusRequest();
//        String jsonData = JsonSerializerDeserializer.serialize(getDeliveryStatusRequest, true);
//
//        ProjectAsyncTask task = new ProjectAsyncTask("GetMasterData", "Post", jsonData);
//        task.setUpdateListener(new OnUpdateListener() {
//            public void onPostExecuteUpdate(String obj) {
//                try {
//                    Context context = activity.getApplicationContext();
//                    JSONObject jsonObject = new JSONObject(obj);
//                    JSONArray appversion = jsonObject.getJSONArray("AppVersion");
//
//                    JSONObject jo = appversion.getJSONObject(0);
//                    DBConnections dbConnections = new DBConnections(context, null);
//                    dbConnections.InsertAppVersion(jo.getInt("VersionCode"), context);
//                    int versioncode = VersionCode(context);
//                    if (jo.getInt("VersionCode") == versioncode) {
//                        if (jo.getInt("ChangesMainMenu") == 1) {
//                            JSONArray station = jsonObject.getJSONArray("Station");
//                            if (station.length() > 0)
//                                new Station(station.toString(), view, context);
//
//                            JSONArray deliveryStatus = jsonObject.getJSONArray("DeliveyStatus");
//                            if (deliveryStatus.length() > 0)
//                                new DeliveryStatus(deliveryStatus.toString(), view, context);
//
//                            JSONArray checkPointType = jsonObject.getJSONArray("CheckPointType");
//                            if (checkPointType.length() > 0)
//                                new CheckPointType(checkPointType.toString(), view, context);
//
//                            JSONArray checkPointdetail = jsonObject.getJSONArray("CheckPointTypeDetail");
//                            if (checkPointdetail.length() > 0)
//                                new CheckPointTypeDetail(checkPointdetail.toString(), view, context);
//
//                            JSONArray typeDDetails = jsonObject.getJSONArray("TypeDDetails");
//                            if (typeDDetails.length() > 0)
//                                new CheckPointTypeDDetail(typeDDetails.toString(), view, context);
//
//                            JSONArray noNeedVolume = jsonObject.getJSONArray("NoNeedVolume");
//                            if (noNeedVolume.length() > 0)
//                                new NoNeedVolumeReason(noNeedVolume.toString(), view, context);
//
//                            SharedPreferences sharedpreferences = context.getSharedPreferences("naqelSettings", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedpreferences.edit();
//                            editor.putString("division", jsonObject.getString("Division"));
//                            editor.commit();
//                        }
//                    } else
//                        updateApp(activity);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                if (dialog != null && dialog.isShowing())
//                    dialog.dismiss();
//            }
//
//            public void onPreExecuteUpdate() {
//                // GlobalVar.GV().ShowSnackbar(view, "Loading Station List", AlertType.Info);
//            }
//
//        });
//        task.execute();
//    }

    public static int VersionCode(Context context) {
        int versioncode = 0;

        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versioncode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versioncode;
    }

    public boolean CheckDataAvailability(String division, Context context, int EmpID, String pwd, int um) {
        boolean isvalid = false;
        DBConnections dbConnections = new DBConnections(context, null);
        if (division.equals("Courier")) {
            Cursor result = dbConnections.Fill("select * from DeliveryStatus Limit 1", context);
            if (result.getCount() == 0) {
                isvalid = true;
                result.close();
                dbConnections.close();
                return isvalid;
            }
        } else if (division.equals("Express")) {
            Cursor result = dbConnections.Fill("select * from Contacts Limit 1", context);
            if (result.getCount() == 0)
                isvalid = true;
            result.close();
            dbConnections.close();
            return isvalid;
        } else if (division.equals("IRS")) {
            Cursor result = dbConnections.Fill("select * from CheckPointType Limit 1", context);
            if (result.getCount() == 0)
                isvalid = true;
            result.close();
            dbConnections.close();
            return isvalid;
        }


        Cursor result = dbConnections.Fill("select * from UpdateMenu Limit 1", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            int updateMenu = result.getInt(result.getColumnIndex("MenuChanges"));
            if (updateMenu != um)
                isvalid = true;
        } else
            isvalid = true;

        result.close();
        dbConnections.close();
        return isvalid;
    }


    public boolean istxtBoxEnabled(Context context) {
        boolean isvalid = false;
        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from UserME where EmployID = " + GlobalVar.GV().EmployID
                + " Limit 1", context);


        if (result.getCount() > 0) {
            result.moveToFirst();
            if (result.getInt(result.getColumnIndex("DisableEnabletxtBox")) == 1)
                isvalid = true;
        }


        result.close();
        dbConnections.close();
        return isvalid;
    }

    public static boolean isEmpty(EditText editText) {

        if (editText.getText().toString().trim().length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isCourier(Context context) {
        String division = GlobalVar.GV().getDivisionID(context, GlobalVar.GV().EmployID);
        if (division.equals("Courier"))
            return true;
        else
            return false;
    }


    public static int getBinMasterCount(Context context) {
        try {
            DBConnections dbConnections = new DBConnections(context, null);
            return dbConnections.getCount("BINMaster", "", context);
        } catch (Exception ex) {
        }
        return 0;
    }


    public static boolean isBinMasterValueExists(String value, Context context) {
        try {
            DBConnections dbConnections = new DBConnections(context, null);
            return dbConnections.isValueExist("BINMaster", "BINNumber", value, context);
        } catch (Exception ex) {
            return true;
        }
    }


//    public static void updateApp(final Activity activity) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("Info")
//                .setMessage("Kindly Please update our lastest version")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int which) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.naqelexpress.naqelpointer"));
//                        activity.startActivity(intent);
//                        final DBConnections dbConnections = new DBConnections(activity.getApplicationContext(), null);
//                        int id = dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ", activity.getApplicationContext());
//                        UserMeLogin userMeLogin = new UserMeLogin(id);
//                        dbConnections.UpdateUserMeLogout(userMeLogin, activity.getApplicationContext());
//                        activity.finish();
//                    }
//                }).setCancelable(false);//.setNegativeButton("Cancel", null).setCancelable(false);
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//
//    }


    public ArrayList<DeliveryStatus> DeliveryStatusList = new ArrayList<>();
    public ArrayList<String> DeliveryStatusNameList = new ArrayList<>();
    public ArrayList<String> DeliveryStatusFNameList = new ArrayList<>();

    public void GetDeliveryStatusList(boolean bringFromServer, Context context, View view) {

        com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
        //if (GlobalVar.GV().DeliveryStatusList.size() <= 0 || (bringFromServer && LastBringMasterData(context, EmployID) > 0))
        if (bringFromServer) {
            dataSync.GetDeliveryStatus(context, view);
            return;
        }

//        GlobalVar.GV().DeliveryStatusList.clear();// = new ArrayList<>();
//        GlobalVar.GV().DeliveryStatusNameList.clear();// = new ArrayList<>();
//        GlobalVar.GV().DeliveryStatusFNameList.clear();// = new ArrayList<>();
//
//        DBConnections dbConnections = new DBConnections(context, null);
//
//        Cursor result = dbConnections.Fill("select * from DeliveryStatus");
//        if (result.getCount() > 0) {
//            result.moveToFirst();
//            do {
//                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                String Code = result.getString(result.getColumnIndex("Code"));
//                String Name = result.getString(result.getColumnIndex("Name"));
//                String FName = result.getString(result.getColumnIndex("FName"));
//
//                DeliveryStatus instance = new DeliveryStatus(ID, Code, Name, FName);
//                GlobalVar.GV().DeliveryStatusList.add(instance);
//                GlobalVar.GV().DeliveryStatusNameList.add(Name);
//                GlobalVar.GV().DeliveryStatusFNameList.add(FName);
//            }
//            while (result.moveToNext());
//
//
//        }
//
//        dbConnections.close();
//        GlobalVar.dsl = true;

    }

    public ArrayList<CheckPointType> CheckPointTypeList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeFNameList = new ArrayList<>();

    public void GetCheckPointTypeList(boolean bringFromServer, final Context context,
                                      final View view) {

        // if (GlobalVar.GV().CheckPointTypeList.size() <= 0 || (bringFromServer && LastBringMasterData(context, EmployID) > 0)) {
        if (bringFromServer) {

            ProjectAsyncTask task = new ProjectAsyncTask("GetCheckPointTypeFromServer", "Get");
            task.setUpdateListener(new OnUpdateListener() {
                public void onPostExecuteUpdate(String obj) {
                    GlobalVar.cptl = true;
                    new CheckPointType(obj, view, context);
                }

                public void onPreExecuteUpdate() {
                }
            });
            task.execute();
            return;
        }
//
//        GlobalVar.GV().CheckPointTypeList.clear();
//        GlobalVar.GV().CheckPointTypeNameList.clear();
//        GlobalVar.GV().CheckPointTypeFNameList.clear();
//
//        DBConnections dbConnections = new DBConnections(context, null);
//
//        Cursor result = dbConnections.Fill("select * from CheckPointType");
//        if (result.getCount() > 0) {
//            result.moveToFirst();
//            do {
//                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                String Name = result.getString(result.getColumnIndex("Name"));
//                String FName = result.getString(result.getColumnIndex("FName"));
//
//                CheckPointType instance = new CheckPointType(ID, Name, FName);
//                GlobalVar.GV().CheckPointTypeList.add(instance);
//                GlobalVar.GV().CheckPointTypeNameList.add(Name);
//                GlobalVar.GV().CheckPointTypeFNameList.add(FName);
//            }
//            while (result.moveToNext());
//        }
//        dbConnections.close();
//
//        GlobalVar.cptl = true;
    }

    public ArrayList<CheckPointTypeDetail> CheckPointTypeDetailList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailFNameList = new ArrayList<>();

    public void GetCheckPointTypeDetailList(boolean bringFromServer, int checkPointTypeID,
                                            final Context context, final View view) {

        //com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
        // if (GlobalVar.GV().CheckPointTypeDetailList.size() <= 0 || (bringFromServer && LastBringMasterData(context, EmployID) > 0)) {
        if (bringFromServer) {

//            if (!GlobalVar.GV().HasInternetAccess)
//                return;

            //dataSync.GetCheckPointTypeDetail();

            ProjectAsyncTask task = new ProjectAsyncTask("GetCheckPointTypeDetail", "Get");
            task.setUpdateListener(new OnUpdateListener() {
                public void onPostExecuteUpdate(String obj) {
                    GlobalVar.cptdl = true;
                    new CheckPointTypeDetail(obj, view, context);
                }

                public void onPreExecuteUpdate() {
                }
            });
            task.execute();
            return;
        }

//        GlobalVar.GV().CheckPointTypeDetailList.clear();
//        GlobalVar.GV().CheckPointTypeDetailNameList.clear();
//        GlobalVar.GV().CheckPointTypeDetailFNameList.clear();
//
//        String selectCommand = "select * from CheckPointTypeDetail";
//        if (checkPointTypeID > 0)
//            selectCommand += " where CheckPointTypeID=" + checkPointTypeID;
//
//        DBConnections dbConnections = new DBConnections(context, null);
//
//        Cursor result = dbConnections.Fill(selectCommand);
//        if (result.getCount() > 0) {
//            result.moveToFirst();
//            do {
//                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                String Name = result.getString(result.getColumnIndex("Name"));
//                String FName = result.getString(result.getColumnIndex("FName"));
//                int CheckPointTypeID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeID")));
//
//                CheckPointTypeDetail instance = new CheckPointTypeDetail(ID, Name, FName, CheckPointTypeID);
//                GlobalVar.GV().CheckPointTypeDetailList.add(instance);
//                GlobalVar.GV().CheckPointTypeDetailNameList.add(Name);
//                GlobalVar.GV().CheckPointTypeDetailFNameList.add(FName);
//            }
//            while (result.moveToNext());
//
//        }
//
//        dbConnections.close();
//        GlobalVar.cptdl = true;

    }

    public ArrayList<CheckPointTypeDDetail> CheckPointTypeDDetailList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDDetailNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDDetailFNameList = new ArrayList<>();

    public void GetCheckPointTypeDDetailList(boolean bringFromServer,
                                             int checkPointTypeDetailID, final Context context, final View view) {

        com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
//        if (GlobalVar.GV().CheckPointTypeDDetailList.size() <= 0 || (bringFromServer && LastBringMasterData(context, EmployID) > 0)) {
//            if (!GlobalVar.GV().HasInternetAccess)
//                return;

        //dataSync.GetCheckPointTypeDDetail();
        if (bringFromServer) {
            ProjectAsyncTask task = new ProjectAsyncTask("GetCheckPointTypeDDetail", "Get");
            task.setUpdateListener(new OnUpdateListener() {
                public void onPostExecuteUpdate(String obj) {
                    GlobalVar.cptddl = true;
                    new CheckPointTypeDDetail(obj, view, context);
                }

                public void onPreExecuteUpdate() {
                }
            });
            task.execute();
            return;
        }

//        GlobalVar.GV().CheckPointTypeDDetailList.clear();// = new ArrayList<>();
//        GlobalVar.GV().CheckPointTypeDDetailNameList.clear();// = new ArrayList<>();
//        GlobalVar.GV().CheckPointTypeDDetailFNameList.clear();// = new ArrayList<>();
//
//        String selectCommand = "select * from CheckPointTypeDDetail";
//        if (checkPointTypeDetailID > 0)
//            selectCommand += " where CheckPointTypeDetailID=" + checkPointTypeDetailID;
//
//        DBConnections dbConnections = new DBConnections(context, null);
//
//        Cursor result = dbConnections.Fill(selectCommand);
//        if (result.getCount() > 0) {
//            result.moveToFirst();
//            do {
//                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                String Name = result.getString(result.getColumnIndex("Name"));
//                String FName = result.getString(result.getColumnIndex("FName"));
//                int CheckPointTypeDetailID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeDetailID")));
//
//                CheckPointTypeDDetail instance = new CheckPointTypeDDetail(ID, Name, FName, CheckPointTypeDetailID);
//                GlobalVar.GV().CheckPointTypeDDetailList.add(instance);
//                GlobalVar.GV().CheckPointTypeDDetailNameList.add(Name);
//                GlobalVar.GV().CheckPointTypeDDetailFNameList.add(FName);
//            }
//            while (result.moveToNext());
//
//        }
//        dbConnections.close();
//        GlobalVar.cptddl = true;


    }

    public ArrayList<NoNeedVolumeReason> NoNeedVolumeReasonList = new ArrayList<>();
    public ArrayList<String> NoNeedVolumeReasonNameList = new ArrayList<>();
    public ArrayList<String> NoNeedVolumeReasonFNameList = new ArrayList<>();

    public void GetNoNeedVolumeReasonList(boolean bringFromServer, final Context context,
                                          final View view) {

//        if (GlobalVar.GV().NoNeedVolumeReasonList.size() <= 0 || (bringFromServer && LastBringMasterData(context, EmployID) > 0)) {
//            if (!GlobalVar.GV().HasInternetAccess)
//                return;
        if (bringFromServer) {
            ProjectAsyncTask task = new ProjectAsyncTask("GetNoNeedVolumeReason", "Get");
            task.setUpdateListener(new OnUpdateListener() {
                public void onPostExecuteUpdate(String obj) {
                    GlobalVar.nnvdl = true;
                    new NoNeedVolumeReason(obj, view, context);
                }

                public void onPreExecuteUpdate() {
                }
            });
            task.execute();
            return;
        }

//
//        GlobalVar.GV().NoNeedVolumeReasonList.clear();// = new ArrayList<>();
//        GlobalVar.GV().NoNeedVolumeReasonNameList = new ArrayList<>();
//        GlobalVar.GV().NoNeedVolumeReasonFNameList = new ArrayList<>();
//
//        DBConnections dbConnections = new DBConnections(context, null);
//
//        Cursor result = dbConnections.Fill("select * from NoNeedVolumeReason");
//        if (result.getCount() > 0) {
//            result.moveToFirst();
//            do {
//                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                String Name = result.getString(result.getColumnIndex("Name"));
//                String FName = result.getString(result.getColumnIndex("FName"));
//
//                NoNeedVolumeReason instance = new NoNeedVolumeReason(ID, Name, FName);
//                GlobalVar.GV().NoNeedVolumeReasonList.add(instance);
//                GlobalVar.GV().NoNeedVolumeReasonNameList.add(Name);
//                GlobalVar.GV().NoNeedVolumeReasonFNameList.add(FName);
//            }
//            while (result.moveToNext());
//
//
//        }
//
//        dbConnections.close();
//        GlobalVar.nnvdl = true;
//
//        // com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();


    }


    private int LastBringMasterData(Context context, int EmployeeID, View view) {
        int value = 1;

        DBConnections dbConnections = new DBConnections(context, null);

        int Count = dbConnections.getCount("UserSettings", " EmployID = " + String.valueOf(EmployeeID), context);
        if (Count > 0) {
            Cursor result = dbConnections.Fill("select * from UserSettings where EmployID = " + String.valueOf(EmployeeID), context);
            if (result.getCount() > 0) {
                result.moveToFirst();
                do {
                    int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    int EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
                    String IPAddress = result.getString(result.getColumnIndex("IPAddress"));
                    boolean ShowScaningCamera = Boolean.parseBoolean(result.getString(result.getColumnIndex("ShowScaningCamera")));
                    DateTime dateTime = DateTime.now();//.withFieldAdded(DurationFieldType.days(),-30);
                    if (dbConnections.isColumnExist("UserSettings", "LastBringMasterData", context))
                        dateTime = DateTime.parse(result.getString(result.getColumnIndex("LastBringMasterData")));

                    try {
                        DateTime LastBringMasterData = DateTime.parse(result.getString(result.getColumnIndex("LastBringMasterData")));
                        int counts = DateTime.now().getDayOfMonth() - LastBringMasterData.getDayOfMonth();
                        if (counts == 0)
                            value = 0;
                    } catch (Exception e) {
                        value = 0;

                        dbConnections.UpdateSettings(new UserSettings(ID, EmployID, IPAddress, ShowScaningCamera, dateTime), view, context);
                        e.printStackTrace();
                    }
                    GlobalVar.GV().currentSettings = new UserSettings(ID, EmployID, IPAddress, ShowScaningCamera, dateTime);
                }
                while (result.moveToNext());
            }
        } else {
            GlobalVar.GV().currentSettings = new UserSettings("212.93.160.150", true);
            dbConnections.InsertSettings(GlobalVar.GV().currentSettings, context);
            GlobalVar.GV().currentSettings.ID = dbConnections.getMaxID("UserSettings", context);
        }
        dbConnections.close();
        return value;
    }

    public void makeCall(String MobileNo, View view, Activity activity) {
        try {
            if (MobileNo == "0") {
                GlobalVar.GV().ShowSnackbar(view, "Invalid Contact Number", GlobalVar.AlertType.Error);
                return;
            }

//            Intent intent1 = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
//            intent1.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
//                    activity.getPackageName());
//            activity.startActivity(intent1);

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("tel:" + MobileNo));
            if (!GlobalVar.GV().checkPermission(activity, PermissionType.Phone)) {
                GlobalVar.GV().ShowSnackbar(view, activity.getString(R.string.NeedPhonePermission), GlobalVar.AlertType.Error);
                GlobalVar.GV().askPermission(activity, PermissionType.Phone);
            } else
                activity.getApplicationContext().startActivity(intent);
        } catch (Exception e) {

        }
    }

    public void makeCallAwaya(String MobileNo, View view, Activity activity) {
        try {
            if (MobileNo == "0") {
                GlobalVar.GV().ShowSnackbar(view, "Invalid Contact Number", GlobalVar.AlertType.Error);
                return;
            }

//            Intent intent1 = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
//            intent1.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
//                    activity.getPackageName());
//            activity.startActivity(intent1);
            String mno = "";
            if (MobileNo.length() >= 9) {
                mno = "90" + MobileNo.substring(MobileNo.length() - 9);
            } else
                return;

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("tel:" + mno));
            if (!GlobalVar.GV().checkPermission(activity, PermissionType.Phone)) {
                GlobalVar.GV().ShowSnackbar(view, activity.getString(R.string.NeedPhonePermission), GlobalVar.AlertType.Error);
                GlobalVar.GV().askPermission(activity, PermissionType.Phone);
            } else
                activity.getApplicationContext().startActivity(intent);
        } catch (Exception e) {

        }
    }

    public void ChangeMapSettings(GoogleMap mMap, Activity activity, View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        if (!GlobalVar.GV().checkPermission(activity, GlobalVar.PermissionType.AccessFindLocation)) {
            GlobalVar.GV().ShowSnackbar(view, activity.getString(R.string.NeedLocationPermision), GlobalVar.AlertType.Error);
            GlobalVar.GV().askPermission(activity, GlobalVar.PermissionType.AccessFindLocation);
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
        }
    }

//    public void SyncData(Context context, View view) {
//        //   if (GlobalVar.GV().HasInternetAccess) {
//        GlobalVar.GV().ShowSnackbar(view, context.getString(R.string.SyncingStarted), GlobalVar.AlertType.Info);
//        com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
//
//        dataSync.SendPickUpData();
//        dataSync.SendOnDliveryData();
//        dataSync.SendNotDliveryData();
//        dataSync.SendUserMeLoginsData();
//        dataSync.SendOnCloadingForDData();
//        dataSync.SendMultiDeliveryData();
//        dataSync.SendWaybillMeasurementDataData();
//        dataSync.SendCheckPointData();
//
//        GlobalVar.GV().ShowSnackbar(view, "Syncing Finish Successfully", GlobalVar.AlertType.Info);
//
////        } else
////            GlobalVar.GV().ShowSnackbar(view, context.getString(R.string.NoInternetConnection), GlobalVar.AlertType.Warning);
//    }

    public int getIntegerFromString(String str) {
        int i = 0;
        try {
            i = NumberFormat.getInstance().parse(str).intValue();
        } catch (Exception ignored) {
        }
        return i;
    }

    public double getDoubleFromString(String str) {
        double i = 0;
        try {
            i = NumberFormat.getInstance().parse(str).doubleValue();
        } catch (Exception ignored) {
        }
        return i;
    }

    public String GetUserPassword(int EmployID, Context context) {
        String pwd = "";
        if (EmployID > 0) {

            DBConnections dbConnections = new DBConnections(context, null);

            int Count = dbConnections.getCount("UserME", " EmployID = " + String.valueOf(EmployID), context);
            if (Count > 0) {
                Cursor result = dbConnections.Fill("select * from UserME where EmployID = " + String.valueOf(EmployID), context);
                if (result.getCount() > 0) {
                    result.moveToFirst();
                    do {
                        pwd = result.getString(result.getColumnIndex("Password"));
                        GlobalVar.GV().UserPassword = pwd;
                    }
                    while (result.moveToNext());
                }
            }

            dbConnections.close();
        }
        return pwd;
    }


    public void LoadMyRouteShipments(String orderBy,
                                     boolean CheckComplaintandDeliveryRequest, Context context, View view) {

        haslocation.clear();
//        MyRouteActivity.places.clear();
        MyRouteActivity_Complaince_GroupbyPhn.places.clear();
        MyRouteActivity_Complaince_GroupbyPhn.Optmizeplaces.clear();
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location location = GlobalVar.getLastKnownLocation(context);
            location.setSpeed(0);
            //ll = new LatLng(location.getLatitude(), location.getLongitude());
            if (location != null) {
//                MyRouteActivity.places.add(location);
                MyRouteActivity_Complaince_GroupbyPhn.places.add(location);
                MyRouteActivity_Complaince_GroupbyPhn.Optmizeplaces.add(location);
            }
        }

        DBConnections dbConnections = new DBConnections(context, null);

        int position = 1;
        int hasposition = 0;
        if (GlobalVar.GV().CourierDailyRouteID > 0) {
            Cursor result = dbConnections.Fill("select * from MyRouteShipments Where CourierDailyRouteID = " +
                    GlobalVar.GV().CourierDailyRouteID + " order by " + orderBy, context);
            if (result.getCount() > 0) {

                dbConnections.InsertOFD(result.getCount(), GlobalVar.getDate(), context);

                myRouteShipmentList = new ArrayList<>();

                result.moveToFirst();
                do {
                    MyRouteShipments myRouteShipments = new MyRouteShipments();
                    myRouteShipments.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    myRouteShipments.BillingType = result.getString(result.getColumnIndex("BillingType"));
                    myRouteShipments.OrderNo = Integer.parseInt(result.getString(result.getColumnIndex("OrderNo")));
                    myRouteShipments.DsOrderNo = Integer.parseInt(result.getString(result.getColumnIndex("DsOrderNo")));
                    myRouteShipments.ItemNo = result.getString(result.getColumnIndex("ItemNo"));

                    myRouteShipments.TypeID = Integer.parseInt(result.getString(result.getColumnIndex("TypeID")));
                    myRouteShipments.CODAmount = getDoubleFromString(result.getString(result.getColumnIndex("CODAmount")));
                    myRouteShipments.DeliverySheetID = Integer.parseInt(result.getString(result.getColumnIndex("DeliverySheetID")));
                    myRouteShipments.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                    myRouteShipments.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("ExpectedTime")));

                    myRouteShipments.Latitude = result.getString(result.getColumnIndex("Latitude"));
                    myRouteShipments.Longitude = result.getString(result.getColumnIndex("Longitude"));
                    if ((myRouteShipments.Latitude.length() > 0 && myRouteShipments.Longitude.length() > 0) &&
                            !myRouteShipments.Latitude.equals("null") && !myRouteShipments.Longitude.equals("null")) {
                        Location sp = new Location("");
                        try {

                            sp.setLatitude(Double.parseDouble(myRouteShipments.Latitude));
                            sp.setLongitude(Double.parseDouble(myRouteShipments.Longitude));
                            if (Double.parseDouble(myRouteShipments.Longitude) != 0.0) {
                                haslocation.add(position);
                                sp.setSpeed(position);
                            }
                        } catch (Exception e) {
                            sp.setLatitude(0);
                            sp.setLongitude(0);
                        }

                        //Places places = new Places(position, latlong);
                        if (Double.parseDouble(myRouteShipments.Longitude) != 0.0)
//                            MyRouteActivity.places.add(sp);
                            MyRouteActivity_Complaince_GroupbyPhn.places.add(sp);
                        if (result.getInt(result.getColumnIndex("IsPlan")) == 1)
                            MyRouteActivity_Complaince_GroupbyPhn.Optmizeplaces.add(sp);

                    }

                    myRouteShipments.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
                    myRouteShipments.ClientName = result.getString(result.getColumnIndex("ClientName"));
                    myRouteShipments.ClientFName = result.getString(result.getColumnIndex("ClientFName"));
                    myRouteShipments.ClientAddressPhoneNumber = result.getString(result.getColumnIndex("ClientAddressPhoneNumber"));
                    myRouteShipments.ClientAddressFirstAddress = result.getString(result.getColumnIndex("ClientAddressFirstAddress"));
                    myRouteShipments.ClientAddressSecondAddress = result.getString(result.getColumnIndex("ClientAddressSecondAddress"));
                    myRouteShipments.ClientContactName = result.getString(result.getColumnIndex("ClientContactName"));
                    myRouteShipments.ClientContactFName = result.getString(result.getColumnIndex("ClientContactFName"));
                    myRouteShipments.ClientContactPhoneNumber = result.getString(result.getColumnIndex("ClientContactPhoneNumber"));
                    myRouteShipments.ClientContactMobileNo = result.getString(result.getColumnIndex("ClientContactMobileNo"));
                    myRouteShipments.ConsigneeName = result.getString(result.getColumnIndex("ConsigneeName"));
                    myRouteShipments.ConsigneeFName = result.getString(result.getColumnIndex("ConsigneeFName"));
                    myRouteShipments.ConsigneePhoneNumber = result.getString(result.getColumnIndex("ConsigneePhoneNumber"));
                    myRouteShipments.ConsigneeFirstAddress = result.getString(result.getColumnIndex("ConsigneeFirstAddress"));
                    myRouteShipments.ConsigneeSecondAddress = result.getString(result.getColumnIndex("ConsigneeSecondAddress"));
                    myRouteShipments.ConsigneeNear = result.getString(result.getColumnIndex("ConsigneeNear"));
                    myRouteShipments.ConsigneeMobile = result.getString(result.getColumnIndex("ConsigneeMobile"));
                    myRouteShipments.Origin = result.getString(result.getColumnIndex("Origin"));
                    myRouteShipments.Destination = result.getString(result.getColumnIndex("Destination"));
                    myRouteShipments.PODNeeded = Boolean.parseBoolean(result.getString(result.getColumnIndex("PODNeeded")));
                    myRouteShipments.PODDetail = result.getString(result.getColumnIndex("PODDetail"));
                    myRouteShipments.PODTypeCode = result.getString(result.getColumnIndex("PODTypeCode"));
                    myRouteShipments.PODTypeName = result.getString(result.getColumnIndex("PODTypeName"));
                    myRouteShipments.IsDelivered = result.getInt(result.getColumnIndex("IsDelivered")) > 0;
                    myRouteShipments.IsPartialDelivered = result.getInt(result.getColumnIndex("PartialDelivered")) > 0;
                    myRouteShipments.NotDelivered = result.getInt(result.getColumnIndex("NotDelivered")) > 0;
                    myRouteShipments.CourierDailyRouteID = Integer.parseInt(result.getString(result.getColumnIndex("CourierDailyRouteID")));
                    myRouteShipments.OptimzeSerialNo = Integer.parseInt(result.getString(result.getColumnIndex("OptimzeSerialNo")));
                    myRouteShipments.HasComplaint = result.getInt(result.getColumnIndex("HasComplaint")) > 0;
                    myRouteShipments.HasDeliveryRequest = result.getInt(result.getColumnIndex("HasDeliveryRequest")) > 0;
                    myRouteShipments.POS = result.getInt(result.getColumnIndex("POS"));
                    myRouteShipments.IsPaid = result.getInt(result.getColumnIndex("Ispaid"));
                    myRouteShipments.IsMap = result.getInt(result.getColumnIndex("IsMap"));
                    myRouteShipments.CustomDuty = result.getDouble(result.getColumnIndex("CustomDuty"));
                    myRouteShipments.Position = position - 1;
                    myRouteShipments.isOtp = result.getInt(result.getColumnIndex("IsOtp"));

                    myRouteShipmentList.add(myRouteShipments);

                    // radios += 800;
                    position += 1;
                }
                while (result.moveToNext());

                // ReOrderMyRouteShipments(CheckComplaintandDeliveryRequest, view, context);
            }
        }
        dbConnections.close();
    }

    public void LoadMyRouteShipments_RouteOpt(String orderBy,
                                              boolean CheckComplaintandDeliveryRequest, Context context, View view) {

        String Waybillno = "";
        String orderNo = "";
        int NWno = 0, NSeqNo = 0, tCount = 0;
        DBConnections dbConnections = new DBConnections(context, null);

        Cursor result = dbConnections.Fill("select * from SuggestLocations where Date = '" + GlobalVar.getDate() + "'" +
                " and EmpID = " + GlobalVar.GV().EmployID, context);
        if (result != null && result.getCount() > 0) {

            result.moveToFirst();
            String SeqNo = "";
            do {

                String data = result.getString(result.getColumnIndex("StringData"));
                String split[] = data.split("@");
                tCount = split.length - 1;
                for (int i = 1; i < split.length; i++) {
                    String temp[] = split[i].split("_");

                    if (i == 1) {
                        Waybillno = temp[0];
                        orderNo = temp[temp.length - 1];
                        SeqNo = temp[temp.length - 1] + "_" + temp[0];
                        NWno = Integer.parseInt(temp[0]);
                        NSeqNo = Integer.parseInt(temp[temp.length - 1]);
                    } else {
                        orderNo = orderNo + "," + temp[temp.length - 1];
                        Waybillno = Waybillno + "," + temp[0];
                        SeqNo = SeqNo + "@" + temp[temp.length - 1] + "_" + temp[0];
                    }

                }
            }
            while (result.moveToNext());

            dbConnections.InsertMyRouteActionActivity(context, 0, NSeqNo, NWno, 0, tCount, SeqNo);
        }
        myRouteShipmentList.clear();
        myRouteShipmentList = new ArrayList<>();
        if (result != null)
            result.close();

        LoadMyRouteShipments_CBU("0", CheckComplaintandDeliveryRequest, context, view, Waybillno, orderNo);
        LoadMyRouteShipments_CBU(orderBy, CheckComplaintandDeliveryRequest, context, view, Waybillno, orderNo);
    }

    public void LoadMyRouteShipments_CBU(String orderBy,
                                         boolean CheckComplaintandDeliveryRequest, Context context, View view, String Waybillno, String condition) {


        haslocation.clear();
//        MyRouteActivity.places.clear();
        MyRouteActivity_Complaince_GroupbyPhn.places.clear();
        MyRouteActivity_Complaince_GroupbyPhn.Optmizeplaces.clear();
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location location = GlobalVar.getLastKnownLocation(context);
            location.setSpeed(0);
            //ll = new LatLng(location.getLatitude(), location.getLongitude());
            if (location != null) {
//                MyRouteActivity.places.add(location);
                MyRouteActivity_Complaince_GroupbyPhn.places.add(location);

                MyRouteActivity_Complaince_GroupbyPhn.Optmizeplaces.add(location);
            }

        }

        DBConnections dbConnections = new DBConnections(context, null);

        int position = 1;
        int hasposition = 0;
        if (GlobalVar.GV().CourierDailyRouteID > 0) {
            Cursor result = null;
            if (orderBy.equals("0"))
                result = dbConnections.Fill("select * from MyRouteShipments Where CourierDailyRouteID = " +
                        GlobalVar.GV().CourierDailyRouteID + " and ItemNo  in(" + Waybillno + ")", context);
            else {
                result = dbConnections.Fill("select * from MyRouteShipments Where CourierDailyRouteID = " +
                        GlobalVar.GV().CourierDailyRouteID + " and ItemNo not in(" + Waybillno + ") order by DsOrderNo", context);
            }
            if (result.getCount() > 0) {

                dbConnections.InsertOFD(result.getCount(), GlobalVar.getDate(), context);


                result.moveToFirst();
                do {
                    MyRouteShipments myRouteShipments = new MyRouteShipments();
                    myRouteShipments.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    myRouteShipments.BillingType = result.getString(result.getColumnIndex("BillingType"));
                    myRouteShipments.OrderNo = Integer.parseInt(result.getString(result.getColumnIndex("OrderNo")));
                    myRouteShipments.ItemNo = result.getString(result.getColumnIndex("ItemNo"));
                    myRouteShipments.DsOrderNo = result.getInt(result.getColumnIndex("DsOrderNo"));

                    myRouteShipments.TypeID = Integer.parseInt(result.getString(result.getColumnIndex("TypeID")));
                    myRouteShipments.CODAmount = getDoubleFromString(result.getString(result.getColumnIndex("CODAmount")));
                    myRouteShipments.DeliverySheetID = Integer.parseInt(result.getString(result.getColumnIndex("DeliverySheetID")));
                    myRouteShipments.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                    myRouteShipments.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("ExpectedTime")));

                    myRouteShipments.Latitude = result.getString(result.getColumnIndex("Latitude"));
                    myRouteShipments.Longitude = result.getString(result.getColumnIndex("Longitude"));
                    if ((myRouteShipments.Latitude.length() > 0 && myRouteShipments.Longitude.length() > 0) &&
                            !myRouteShipments.Latitude.equals("null") && !myRouteShipments.Longitude.equals("null")) {
                        Location sp = new Location("");
                        try {

                            sp.setLatitude(Double.parseDouble(myRouteShipments.Latitude));
                            sp.setLongitude(Double.parseDouble(myRouteShipments.Longitude));
                            if (Double.parseDouble(myRouteShipments.Longitude) != 0.0) {
                                haslocation.add(position);
                                sp.setSpeed(position);
                            }
                        } catch (Exception e) {
                            sp.setLatitude(0);
                            sp.setLongitude(0);
                        }

                        //Places places = new Places(position, latlong);
                        if (Double.parseDouble(myRouteShipments.Longitude) != 0.0)
//                            MyRouteActivity.places.add(sp);
                            MyRouteActivity_Complaince_GroupbyPhn.places.add(sp);
                        if (result.getInt(result.getColumnIndex("IsPlan")) == 1)
                            MyRouteActivity_Complaince_GroupbyPhn.Optmizeplaces.add(sp);
                    }

                    myRouteShipments.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
                    myRouteShipments.ClientName = result.getString(result.getColumnIndex("ClientName"));
                    myRouteShipments.ClientFName = result.getString(result.getColumnIndex("ClientFName"));
                    myRouteShipments.ClientAddressPhoneNumber = result.getString(result.getColumnIndex("ClientAddressPhoneNumber"));
                    myRouteShipments.ClientAddressFirstAddress = result.getString(result.getColumnIndex("ClientAddressFirstAddress"));
                    myRouteShipments.ClientAddressSecondAddress = result.getString(result.getColumnIndex("ClientAddressSecondAddress"));
                    myRouteShipments.ClientContactName = result.getString(result.getColumnIndex("ClientContactName"));
                    myRouteShipments.ClientContactFName = result.getString(result.getColumnIndex("ClientContactFName"));
                    myRouteShipments.ClientContactPhoneNumber = result.getString(result.getColumnIndex("ClientContactPhoneNumber"));
                    myRouteShipments.ClientContactMobileNo = result.getString(result.getColumnIndex("ClientContactMobileNo"));
                    myRouteShipments.ConsigneeName = result.getString(result.getColumnIndex("ConsigneeName"));
                    myRouteShipments.ConsigneeFName = result.getString(result.getColumnIndex("ConsigneeFName"));
                    myRouteShipments.ConsigneePhoneNumber = result.getString(result.getColumnIndex("ConsigneePhoneNumber"));
                    myRouteShipments.ConsigneeFirstAddress = result.getString(result.getColumnIndex("ConsigneeFirstAddress"));
                    myRouteShipments.ConsigneeSecondAddress = result.getString(result.getColumnIndex("ConsigneeSecondAddress"));
                    myRouteShipments.ConsigneeNear = result.getString(result.getColumnIndex("ConsigneeNear"));
                    myRouteShipments.ConsigneeMobile = result.getString(result.getColumnIndex("ConsigneeMobile"));
                    myRouteShipments.Origin = result.getString(result.getColumnIndex("Origin"));
                    myRouteShipments.Destination = result.getString(result.getColumnIndex("Destination"));
                    myRouteShipments.PODNeeded = Boolean.parseBoolean(result.getString(result.getColumnIndex("PODNeeded")));
                    myRouteShipments.PODDetail = result.getString(result.getColumnIndex("PODDetail"));
                    myRouteShipments.PODTypeCode = result.getString(result.getColumnIndex("PODTypeCode"));
                    myRouteShipments.PODTypeName = result.getString(result.getColumnIndex("PODTypeName"));
                    myRouteShipments.IsDelivered = result.getInt(result.getColumnIndex("IsDelivered")) > 0;
                    myRouteShipments.IsPartialDelivered = result.getInt(result.getColumnIndex("PartialDelivered")) > 0;
                    myRouteShipments.NotDelivered = result.getInt(result.getColumnIndex("NotDelivered")) > 0;
                    myRouteShipments.CourierDailyRouteID = Integer.parseInt(result.getString(result.getColumnIndex("CourierDailyRouteID")));
                    myRouteShipments.OptimzeSerialNo = Integer.parseInt(result.getString(result.getColumnIndex("OptimzeSerialNo")));
                    myRouteShipments.HasComplaint = result.getInt(result.getColumnIndex("HasComplaint")) > 0;
                    myRouteShipments.HasDeliveryRequest = result.getInt(result.getColumnIndex("HasDeliveryRequest")) > 0;
                    myRouteShipments.POS = result.getInt(result.getColumnIndex("POS"));
                    myRouteShipments.IsPaid = result.getInt(result.getColumnIndex("Ispaid"));
                    myRouteShipments.IsMap = result.getInt(result.getColumnIndex("IsMap"));
                    myRouteShipments.CustomDuty = result.getDouble(result.getColumnIndex("CustomDuty"));
                    myRouteShipments.isOtp = result.getInt(result.getColumnIndex("IsOtp"));
                    myRouteShipments.Position = position - 1;

                    myRouteShipmentList.add(myRouteShipments);

                    // radios += 800;
                    position += 1;
                }
                while (result.moveToNext());

                // ReOrderMyRouteShipments(CheckComplaintandDeliveryRequest, view, context);
            }
            if (result != null)
                result.close();

            ArrayList<MyRouteShipments> locallist = new ArrayList<>();

            if (orderBy.equals("0")) {
                String wno[] = Waybillno.split(",");
                for (int i = 0; i < wno.length; i++) {

                    for (int j = 0; j < myRouteShipmentList.size(); j++) {
                        if (wno[i].equals(myRouteShipmentList.get(j).ItemNo)) {
                            locallist.add(myRouteShipmentList.get(j));
                            myRouteShipmentList.remove(j);
                            break;
                        }
                    }
                }

                myRouteShipmentList.clear();
                myRouteShipmentList.addAll(locallist);
            }
        }

        dbConnections.close();
    }

//    public void LoadMyBookingList(String orderBy, boolean isFromServer) {
//
//        GlobalVar.GV().myBookingList = new ArrayList<Booking>();
//
//        if (isFromServer) {
//            DataSync dataSync = new DataSync();
//            dataSync.GetBookingListSerer();
//        } else {
//
//            Cursor result = dbConnections.Fill("select * from Booking Where EmployID = 1024"); //+ " GlobalVar.GV().EmployID order by " + orderBy
//            if (result.getCount() > 0) {
//                GlobalVar.GV().myBookingList = new ArrayList<Booking>();
//
//                result.moveToFirst();
//                do {
//                    Booking myBooking = new Booking();
//                    myBooking.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                    myBooking.RefNo = result.getString(result.getColumnIndex("RefNo"));
//                    myBooking.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
//                    myBooking.ClientName = result.getString(result.getColumnIndex("ClientName"));
//                    myBooking.BookingDate = DateTime.parse(result.getString(result.getColumnIndex("BookingDate")));
//                    myBooking.PicesCount = Integer.parseInt(result.getString(result.getColumnIndex("PicesCount")));
//                    myBooking.Weight = Double.parseDouble(result.getString(result.getColumnIndex("Weight")));
//                    //myBooking.SpecialInstruction = result.getString(result.getColumnIndex("SpecialInstruction"));
//                    //myBooking.OfficeUpTo = DateTime.parse(result.getString(result.getColumnIndex("OfficeUpTo")));
//                    //myBooking.PickUpReqDT = DateTime.parse(result.getString(result.getColumnIndex("PickUpReqDT")));
//                    myBooking.ContactPerson = result.getString(result.getColumnIndex("ContactPerson"));
//                    myBooking.ContactNumber = result.getString(result.getColumnIndex("ContactNumber"));
//                    myBooking.Address = result.getString(result.getColumnIndex("Address"));
//                    //myBooking.Latitude = result.getString(result.getColumnIndex("Latitude"));
//                    //myBooking.Longitude = result.getString(result.getColumnIndex("Longitude"));
//                    //myBooking.Status = Integer.parseInt(result.getString(result.getColumnIndex("Status")));
//                    myBooking.Orgin = result.getString(result.getColumnIndex("Orgin"));
//                    myBooking.Destination = result.getString(result.getColumnIndex("Destination"));
//                    //myBooking.LoadType = result.getString(result.getColumnIndex("LoadType"));
//                    myBooking.BillType = result.getString(result.getColumnIndex("BillType"));
//                    //myBooking.EmployeeId = Integer.parseInt(result.getString(result.getColumnIndex("EmployeeId")));
//
//                    GlobalVar.GV().myBookingList.add(myBooking);
//                }
//                while (result.moveToNext());
//            }
//
//
//        }
//    }

//    private void openWhatsApp(Context mycontext, String MobileNo, String Message) {
//        //String smsNumber =MobileNo ;// "+966553234520";
//        String smsNumber = "+919566706786";// "+966553234520";
//
//        Intent sendIntent = new Intent("android.intent.action.MAIN");
//        //Intent sendIntent = new Intent("android.intent.action.SEND_TO");
//        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//
//        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");//phone number without "+" prefix
//        sendIntent.putExtra(Intent.EXTRA_TEXT, Message);
//        mycontext.startActivity(sendIntent);
//
//    }

    public static boolean getPOS(Context context) {
        boolean pos = false;

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor cursor = dbConnections.Fill("select * from MyRouteShipments where EmpID = " + GlobalVar.GV().EmployID
                , context); // + " and DDate = '" + GlobalVar.getDate() + "'"
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int pos1 = cursor.getInt(cursor.getColumnIndex("POS"));
            if (pos1 > 0)
                pos = true;

            cursor.close();
            dbConnections.close();
        }
        return pos;

    }

    public void WhatsApp(Context mycontext, String MobileNo, String Message) {
        PackageManager pm = mycontext.getPackageManager();
        try {

            String toNumber = "+919566706786"; // contains spaces.
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
            sendIntent.putExtra(Intent.EXTRA_TEXT, Message);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setType("text/plain");
            mycontext.startActivity(sendIntent);
            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(mycontext.getApplicationContext(), "WhatsApp no esta instalado!", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendMessageToWhatsAppContact(String number, String waybillno, Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode(waybillno, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendSMS(String phoneNo, String msg, Context context) {
        try {


            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", phoneNo);
            smsIntent.putExtra("sms_body", msg);
            smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(smsIntent);

//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
//            Toast.makeText(context, "Message Sent",
//                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public static void SendSMSbydefault(String mobileno, String text, Context context) {
        try {
            Uri uri = Uri.parse("smsto:" + mobileno);
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", text);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean simISavailable(Context context) {
        boolean validate = false;
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                validate = false;
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                validate = false;
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                validate = false;
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                validate = false;
                break;
            case TelephonyManager.SIM_STATE_READY:
                validate = true;
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                validate = false;
                break;
        }
        return validate;
    }

//    public void MessageWhatsApp(Context context, String MobileNo, String Message) {
//
//        try {
//            MobileNo = MobileNo.replace("00966", "+966");
//            WhatsApp(context, MobileNo, "Hellow");
//            return;
//
//            //String text = Message;// Replace with your message.
//            //String toNumber ="966581776681"; // Replace with mobile phone number without +Sign or leading zeros.
//            //Intent intent = new Intent(Intent.ACTION_VIEW);
//            //intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
//            //context.startActivity(intent);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static String getDivisionID(Context context, int empId) {
        String devision = "";
        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " +
                empId, context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            devision = result.getString(result.getColumnIndex("Division"));

        }
        dbConnections.close();
        return devision;

    }


    public static ArrayList<com.naqelexpress.naqelpointer.Activity.Booking.Booking> getPickupSyncData
            (Context context) {
        ArrayList<com.naqelexpress.naqelpointer.Activity.Booking.Booking> pickupFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from PickUpAuto where IsSync = 0 ", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                com.naqelexpress.naqelpointer.Activity.Booking.Booking pickUpRequest = new com.naqelexpress.naqelpointer.Activity.Booking.Booking();
                //pickUpRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                pickUpRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("WaybillNo")));
                //pickUpRequest.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
                //pickUpRequest.FromStationID = Integer.parseInt(result.getString(result.getColumnIndex("FromStationID")));
                //pickUpRequest.ToStationID = Integer.parseInt(result.getString(result.getColumnIndex("ToStationID")));
                pickUpRequest.PicesCount = Double.parseDouble(result.getString(result.getColumnIndex("PieceCount")));
                pickUpRequest.Weight = Double.parseDouble(result.getString(result.getColumnIndex("Weight")));
                //pickUpRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
                //pickUpRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("TimeOut")));
                //pickUpRequest.UserMEID = Integer.parseInt(result.getString(result.getColumnIndex("UserID")));
                //pickUpRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
                pickUpRequest.RefNo = result.getString(result.getColumnIndex("RefNo"));
                //pickUpRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
                //pickUpRequest.Longitude = result.getString(result.getColumnIndex("Longitude"));
                //pickUpRequest.CurrentVersion = result.getString(result.getColumnIndex("CurrentVersion"));

                pickupFromLocal.add(pickUpRequest);
            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return pickupFromLocal;
    }


    public static ArrayList<com.naqelexpress.naqelpointer.Activity.Booking.Booking> getPickupHistory
            (Context context) {
        ArrayList<com.naqelexpress.naqelpointer.Activity.Booking.Booking> pickupFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from PickUpAuto order by timein desc ", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                com.naqelexpress.naqelpointer.Activity.Booking.Booking pickUpRequest = new com.naqelexpress.naqelpointer.Activity.Booking.Booking();
                //pickUpRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                pickUpRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("WaybillNo")));
                int issync = 0;
                boolean value = result.getInt(result.getColumnIndex("IsSync")) > 0;
                if (value)
                    issync = 1;
                else
                    issync = 0;

                pickUpRequest.Status = issync;
                //pickUpRequest.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
                //pickUpRequest.FromStationID = Integer.parseInt(result.getString(result.getColumnIndex("FromStationID")));
                //pickUpRequest.ToStationID = Integer.parseInt(result.getString(result.getColumnIndex("ToStationID")));
                pickUpRequest.PicesCount = Double.parseDouble(result.getString(result.getColumnIndex("PieceCount")));
                pickUpRequest.Weight = Double.parseDouble(result.getString(result.getColumnIndex("Weight")));
                //pickUpRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
                //pickUpRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("TimeOut")));
                //pickUpRequest.UserMEID = Integer.parseInt(result.getString(result.getColumnIndex("UserID")));
                //pickUpRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
                pickUpRequest.RefNo = result.getString(result.getColumnIndex("RefNo"));
                pickUpRequest.PickUpReqDT = result.getString(result.getColumnIndex("TimeIn"));
                //pickUpRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
                //pickUpRequest.Longitude = result.getString(result.getColumnIndex("Longitude"));
                //pickUpRequest.CurrentVersion = result.getString(result.getColumnIndex("CurrentVersion"));

                pickupFromLocal.add(pickUpRequest);
            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return pickupFromLocal;
    }


    public static ArrayList<MyRouteShipments> getDeliverySyncData(Context context) {
        ArrayList<MyRouteShipments> OnDeleiveryFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from OnDelivery where IsSync = 0", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                onDeliveryRequest.ItemNo = result.getString(result.getColumnIndex("WaybillNo"));
                //  onDeliveryRequest.ReceiverName = result.getString(result.getColumnIndex("ReceiverName"));
                // onDeliveryRequest.PiecesCount = Integer.parseInt(result.getString(result.getColumnIndex("PiecesCount")));
                //   onDeliveryRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
                //   onDeliveryRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("TimeOut")));
                //   onDeliveryRequest.EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
                //   onDeliveryRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
                //    onDeliveryRequest.IsPartial = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsPartial")));
                //    onDeliveryRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
                //     onDeliveryRequest.Longitude = result.getString(result.getColumnIndex("Longitude"));
                //     onDeliveryRequest.ReceivedAmt = Double.parseDouble(result.getString(result.getColumnIndex("TotalReceivedAmount")));
                //onDeliveryRequest.ReceiptNo = result.getString(result.getColumnIndex("ReceiptNo"));
                //onDeliveryRequest.StopPointsID = Integer.parseInt(result.getString(result.getColumnIndex("StopPointsID")));
                //     onDeliveryRequest.POSAmount = Double.parseDouble(result.getString(result.getColumnIndex("POSAmount")));
                //     onDeliveryRequest.CashAmount = Double.parseDouble(result.getString(result.getColumnIndex("CashAmount")));

                OnDeleiveryFromLocal.add(onDeliveryRequest);

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return OnDeleiveryFromLocal;
    }

    public static ArrayList<MyRouteShipments> getDeliveryHistory(Context context) {
        ArrayList<MyRouteShipments> OnDeleiveryFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from OnDelivery order by timein desc", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                onDeliveryRequest.ItemNo = result.getString(result.getColumnIndex("WaybillNo"));

                //  onDeliveryRequest.ReceiverName = result.getString(result.getColumnIndex("ReceiverName"));
                onDeliveryRequest.IsDelivered = result.getInt(result.getColumnIndex("IsSync")) > 0;
                ;
                onDeliveryRequest.PiecesCount = result.getString(result.getColumnIndex("PiecesCount"));
                onDeliveryRequest.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
                //onDeliveryRequest.IsDelivered = result.getInt(result.getColumnIndex("IsDelivered")) > 0;
                //   onDeliveryRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
                //    onDeliveryRequest.IsPartial = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsPartial")));
                //    onDeliveryRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
                //     onDeliveryRequest.Longitude = result.getString(result.getColumnIndex("Longitude"));
                //     onDeliveryRequest.ReceivedAmt = Double.parseDouble(result.getString(result.getColumnIndex("TotalReceivedAmount")));
                //onDeliveryRequest.ReceiptNo = result.getString(result.getColumnIndex("ReceiptNo"));
                //onDeliveryRequest.StopPointsID = Integer.parseInt(result.getString(result.getColumnIndex("StopPointsID")));
                //     onDeliveryRequest.POSAmount = Double.parseDouble(result.getString(result.getColumnIndex("POSAmount")));
                //     onDeliveryRequest.CashAmount = Double.parseDouble(result.getString(result.getColumnIndex("CashAmount")));

                OnDeleiveryFromLocal.add(onDeliveryRequest);

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return OnDeleiveryFromLocal;
    }


    public static ArrayList<MyRouteShipments> getNotDeliverySyncData(Context context) {
        ArrayList<MyRouteShipments> NotDeleiveryFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from NotDelivered where IsSync = 0", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                onDeliveryRequest.ItemNo = result.getString(result.getColumnIndex("WaybillNo"));

                NotDeleiveryFromLocal.add(onDeliveryRequest);

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return NotDeleiveryFromLocal;
    }

    public static ArrayList<MyRouteShipments> getNotDeliveryData(Context context) {
        ArrayList<MyRouteShipments> NotDeleiveryFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from NotDelivered order by TimeIn desc ", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));


                onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                onDeliveryRequest.ItemNo = result.getString(result.getColumnIndex("WaybillNo"));
                //  onDeliveryRequest.ReceiverName = result.getString(result.getColumnIndex("ReceiverName"));
                onDeliveryRequest.IsDelivered = result.getInt(result.getColumnIndex("IsSync")) > 0;
                onDeliveryRequest.PiecesCount = result.getString(result.getColumnIndex("PiecesCount"));
                onDeliveryRequest.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));

                NotDeleiveryFromLocal.add(onDeliveryRequest);

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return NotDeleiveryFromLocal;
    }


    public static ArrayList<MyRouteShipments> getMultiDeliveryHistory(Context context) {
        ArrayList<MyRouteShipments> OnDeleiveryFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from MultiDelivery order by timein desc", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                //onDeliveryRequest.ItemNo = result.getString(result.getColumnIndex("WaybillNo"));
                onDeliveryRequest.IsDelivered = result.getInt(result.getColumnIndex("IsSync")) > 0;
                onDeliveryRequest.TypeID = result.getInt(result.getColumnIndex("WaybillsCount"));
                onDeliveryRequest.PiecesCount = result.getString(result.getColumnIndex("PiecesCount"));
                onDeliveryRequest.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
                OnDeleiveryFromLocal.add(onDeliveryRequest);

            }
            while (result.moveToNext());

        }
        dbConnections.close();
        return OnDeleiveryFromLocal;
    }


    public static ArrayList<MyRouteShipments> getDeliverySheet(Context context) throws ParseException {
        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select ocw.WaybillNo , oc.CTime from OnCLoadingForDWaybill ocw inner join OnCloadingForD oc " +
                "on oc.ID = ocw.OnCLoadingID where ocw.IsSync = 0", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                onDeliveryRequest.ItemNo = result.getString(result.getColumnIndex("WaybillNo"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                //Date dt = formatter.parse();
                onDeliveryRequest.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("CTime")));
                DeliverySheetFromLocal.add(onDeliveryRequest);

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }

    public static ArrayList<MyRouteShipments> getDeliverySheetforEBU(Context context) throws ParseException {
        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select ocw.WaybillNo , oc.CTime , ocw.IsSync from OnCLoadingForDWaybill ocw inner join OnCloadingForD oc " +
                "on oc.ID = ocw.OnCLoadingID order by CTime asc ", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                onDeliveryRequest.ItemNo = result.getString(result.getColumnIndex("WaybillNo"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                onDeliveryRequest.IsDelivered = result.getInt(result.getColumnIndex("IsSync")) > 0;
                //onDeliveryRequest.
                //Date dt = formatter.parse();
                onDeliveryRequest.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("CTime")));
                DeliverySheetFromLocal.add(onDeliveryRequest);

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }

    public static ArrayList<MyRouteShipments> getArrivedatDest(Context context) {

        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from AtDestination order by id desc", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                try {
                    JSONObject jsonObject = new JSONObject(result.getString(result.getColumnIndex("Json")));
                    boolean issync = result.getInt(result.getColumnIndex("IsSync")) > 0;
                    String ScannedTime = result.getString(result.getColumnIndex("CTime"));

                    JSONArray jsonArray = jsonObject.getJSONArray("WayBills");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                        onDeliveryRequest.ItemNo = obj.getString("WayBillNo");
                        onDeliveryRequest.IsDelivered = issync;
                        onDeliveryRequest.ExpectedTime = DateTime.parse(ScannedTime);
                        onDeliveryRequest.TypeID = 125;
                        DeliverySheetFromLocal.add(onDeliveryRequest);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }

    public static ArrayList<MyRouteShipments> getLoadtoDest(Context context) {

        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from TripPlanDetails order by id desc", context);

        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                try {
                    JSONObject jsonObject = new JSONObject(result.getString(result.getColumnIndex("Json")));
                    JSONArray onLoading = jsonObject.getJSONArray("OnLoading");
                    JSONObject onLoadingJSONObject = onLoading.getJSONObject(0);
                    String ScannedTime = onLoadingJSONObject.getString("CTime");
                    boolean issync = result.getInt(result.getColumnIndex("IsSync")) > 0;

                    JSONArray jsonArray = jsonObject.getJSONArray("WayBill");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                        onDeliveryRequest.ItemNo = obj.getString("WaybillNo");
                        onDeliveryRequest.ExpectedTime = DateTime.parse(ScannedTime);
                        onDeliveryRequest.IsDelivered = issync;
                        DeliverySheetFromLocal.add(onDeliveryRequest);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }

    public static ArrayList<MyRouteShipments> getLoadtoDestHistory(Context context) {

        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from LoadtoDestination ", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                try {
                    JSONObject jsonObject = new JSONObject(result.getString(result.getColumnIndex("Json")));

                    JSONArray jsonArray = jsonObject.getJSONArray("WayBill");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                        onDeliveryRequest.ItemNo = obj.getString("WaybillNo");

                        DeliverySheetFromLocal.add(onDeliveryRequest);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }

    public static ArrayList<MyRouteShipments> getAtOrigin(Context context) {

        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from AtOrigin order by id desc", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                try {
                    JSONObject jsonObject = new JSONObject(result.getString(result.getColumnIndex("Json")));
                    boolean IsSync = result.getInt(result.getColumnIndex("IsSync")) > 0;

                    JSONArray jsonArray = jsonObject.getJSONArray("AtOriginWaybillDetails");

                    if (jsonArray.length() > 0) {
                        DateTime ExpectedTime = DateTime.parse(jsonObject.getString("CTime"));

//                       String PiecesCount =jsonObject.getString("CTime");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                            onDeliveryRequest.ItemNo = obj.getString("WaybillNo");
                            onDeliveryRequest.IsDelivered = IsSync;
                            onDeliveryRequest.ExpectedTime = ExpectedTime;
                            onDeliveryRequest.TypeID = 124;
//                        onDeliveryRequest.PiecesCount = result.getString(result.getColumnIndex("PieceCount"));
                            DeliverySheetFromLocal.add(onDeliveryRequest);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }

    public static ArrayList<MyRouteShipments> getNightStock(Context context) {
        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from NightStockWaybillDetail where IsSync = 0", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                onDeliveryRequest.ItemNo = result.getString(result.getColumnIndex("WaybillNo"));

                DeliverySheetFromLocal.add(onDeliveryRequest);

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }

    public static ArrayList<MyRouteShipments> getNightStockHistory(Context context) {
        ArrayList<MyRouteShipments> OnDeleiveryFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from NightStock order by CTime desc", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                //onDeliveryRequest.ItemNo = result.getString(result.getColumnIndex("WaybillNo"));
                onDeliveryRequest.IsDelivered = result.getInt(result.getColumnIndex("IsSync")) > 0;
                onDeliveryRequest.TypeID = -1;
                onDeliveryRequest.PiecesCount = result.getString(result.getColumnIndex("PiecesCount"));
                onDeliveryRequest.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("CTime")));
                OnDeleiveryFromLocal.add(onDeliveryRequest);

            }
            while (result.moveToNext());

        }
        dbConnections.close();
        return OnDeleiveryFromLocal;
    }


    public static ArrayList<MyRouteShipments> getCheckpointData(Context context) {
        ArrayList<MyRouteShipments> OnDeleiveryFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from CheckPointBarCodeDetails", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("BarCode")));
                onDeliveryRequest.ItemNo = result.getString(result.getColumnIndex("BarCode"));

                //  onDeliveryRequest.ReceiverName = result.getString(result.getColumnIndex("ReceiverName"));
                onDeliveryRequest.IsDelivered = false;


                OnDeleiveryFromLocal.add(onDeliveryRequest);

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return OnDeleiveryFromLocal;
    }

    public static ArrayList<MyRouteShipments> getNCLNotSyncData(Context context) {

        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from NCL", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                try {
                    JSONObject jsonObject = new JSONObject(result.getString(result.getColumnIndex("JsonData")));

                    JSONArray jsonArray = jsonObject.getJSONArray("ncldetails");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                        onDeliveryRequest.ItemNo = obj.getString("BarCode");
                        if (result.getInt(result.getColumnIndex("IsSync")) == 0)
                            onDeliveryRequest.IsDelivered = false;
                        else
                            onDeliveryRequest.IsDelivered = true;
                        DeliverySheetFromLocal.add(onDeliveryRequest);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }


    private void ReOrderMyRouteShipments(boolean CheckComplaintandDeliveryRequest,
                                         final View view, final Context context) {
        //Has Complaint and Has Delivery Request has high priority.
        //Has Complaint
        // Has Delivery Request
        //Normal Order
        //Has Delivery Exception
        //Delivered will be removed from the list.
        //GTranslation gTranslation = new GTranslation(AddressText,languageCode);


        if (CheckComplaintandDeliveryRequest) {
            String jsonData = JsonSerializerDeserializer.serialize(GlobalVar.GV().myRouteShipmentList, true);
            ProjectAsyncTask task = new ProjectAsyncTask("CheckComplaintandDeliveryRequest", "Post", jsonData);
            task.setUpdateListener(new OnUpdateListener() {
                public void onPostExecuteUpdate(String obj) {
                    new MyRouteShipments(obj, MyRouteShipments.UpdateType.DeliveryRequestAndComplaint, context, view);
                    GlobalVar.GV().ShowSnackbar(view, "Finish Optimizing Shipments", GlobalVar.AlertType.Info);
                }

                public void onPreExecuteUpdate() {
                    GlobalVar.GV().ShowSnackbar(view, "Checking Complaint and Delivery Requests", GlobalVar.AlertType.Info);
                }
            });
            task.execute();
        }

        ArrayList<MyRouteShipments> tmpRouteShipmentList = new ArrayList<>();
        //tmpRouteShipmentList = GlobalVar.GV().kpi;

        for (int i = 0; i < GlobalVar.GV().myRouteShipmentList.size(); i++) {
            if (GlobalVar.GV().myRouteShipmentList.get(i).HasComplaint &&
                    GlobalVar.GV().myRouteShipmentList.get(i).HasDeliveryRequest &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).IsDelivered &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).NotDelivered)
                tmpRouteShipmentList.add(GlobalVar.GV().myRouteShipmentList.get(i));
        }

        for (int i = 0; i < GlobalVar.GV().myRouteShipmentList.size(); i++) {
            if (GlobalVar.GV().myRouteShipmentList.get(i).HasComplaint &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).HasDeliveryRequest &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).IsDelivered &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).NotDelivered)
                tmpRouteShipmentList.add(GlobalVar.GV().myRouteShipmentList.get(i));
        }

        for (int i = 0; i < GlobalVar.GV().myRouteShipmentList.size(); i++) {
            if (!GlobalVar.GV().myRouteShipmentList.get(i).HasComplaint &&
                    GlobalVar.GV().myRouteShipmentList.get(i).HasDeliveryRequest &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).IsDelivered &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).NotDelivered)
                tmpRouteShipmentList.add(GlobalVar.GV().myRouteShipmentList.get(i));
        }

        for (int i = 0; i < GlobalVar.GV().myRouteShipmentList.size(); i++) {
            if (!GlobalVar.GV().myRouteShipmentList.get(i).HasComplaint &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).HasDeliveryRequest &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).IsDelivered &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).NotDelivered)
                tmpRouteShipmentList.add(GlobalVar.GV().myRouteShipmentList.get(i));
        }

        for (int i = 0; i < GlobalVar.GV().myRouteShipmentList.size(); i++) {
            if (!GlobalVar.GV().myRouteShipmentList.get(i).HasComplaint &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).HasDeliveryRequest &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).IsDelivered &&
                    GlobalVar.GV().myRouteShipmentList.get(i).NotDelivered)
                tmpRouteShipmentList.add(GlobalVar.GV().myRouteShipmentList.get(i));
        }

        GlobalVar.GV().myRouteShipmentList = tmpRouteShipmentList;
    }

    //    public String DateFormat = "dd/MMM/yyyy HH:mm:ss";
//    public java.sql.Date getCurrentSQLDate()
//    {
//        DateTime now = new DateTime();
//        java.sql.Date sqlDate = new java.sql.Date(now.getTime());
//        return sqlDate;
//    }

//    public String getDateTime(DateTime date)
//    {
//        return date.toString();
//        //SimpleDateFormat dateFormat = new SimpleDateFormat(this.DateFormat, Locale.getDefault());
//
////        return dateFormat.format(date);
//    }

//    public String ConvertJsonDate1(String jsonDate)
//    {
////        String jsondate="\/Date(1427959670000)\/";
//        jsonDate=jsonDate.replace("/Date(", "").replace(")/", "");
//        long time = Long.parseLong(jsonDate);
//        Date d = new Date(time);
//        //Log.d("Convertd date is:"+new SimpleDateFormat("dd/MM/yyyy").format(d).toString());
//
//        return new SimpleDateFormat(GlobalVar.GV().DateFormat).format(d).toString();
//    }

    //Date Format
    //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    //GlobalVar.GV().ShowMessage(this,sdf.format(instance.CTime).toString());

    //For Settings Error
    //txtPOS.setError(txtPOS.getText().toString());

    //Open new Intent
    //Intent mainpage = new Intent(this,MainPageActivity.class);
    //startActivity(mainpage);

    // Make Toast
    //Toast.makeText(this, GlobalVar.gv.xTest,Toast.LENGTH_LONG).show();

//    String ackwardDate = "/Date(1376841597000)/";
//    //Dirty convertion
//    Calendar calendar = Calendar.getInstance();
//    String ackwardRipOff = ackwardDate.replace("/Date(", "").replace(")/", "");
//    Long timeInMillis = Long.valueOf(ackwardRipOff);
//    calendar.setTimeInMillis(timeInMillis);
//    GlobalVar.GV().ShowMessage(this,calendar.getTime().toGMTString());


    //                    DateTime dateTime = new DateTime();//new DateTime(1467880743533L, DateTimeZone.forOffsetHours(-5));
//                    System.out.println(dateTime.toString(ISODateTimeFormat.dateTime()));
//                    String json = JsonSerializerDeserializer.serialize(dateTime, Boolean.TRUE);
//                    System.out.println(json);
//                    dateTime = JsonSerializerDeserializer.deserialize(json, DateTime.class);
//                    System.out.println(dateTime.toString(ISODateTimeFormat.dateTime()));

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

//    public static LatLng getNextLatLong(LatLng point) {
//
//
//        double meters = 5;
//
//        double coef = meters * 0.0000089;
//
//        double new_lat = point.latitude + coef;
//
//// pi / 180 = 0.018
//        double new_long = point.longitude + coef / Math.cos(point.latitude * 0.018);
//
//        LatLng newLaLng = new LatLng(new_lat, new_long);
//        return newLaLng;
//    }

    //For testing Purpose
//    public static LatLng getRandomLocation(LatLng point, int radius) {
//
//        radius = 100;
//        List<LatLng> randomPoints = new ArrayList<>();
//        List<Float> randomDistances = new ArrayList<>();
//        Location myLocation = new Location("");
//        myLocation.setLatitude(point.latitude);
//        myLocation.setLongitude(point.longitude);
//
//        //This is to generate 10 random points
//        for (int i = 0; i < 1; i++) {
//            double x0 = point.latitude;
//            double y0 = point.longitude;
//
//            Random random = new Random();
//
//            // Convert radius from meters to degrees
//            double radiusInDegrees = radius / 111000f;
//
//            double u = random.nextDouble();
//            double v = random.nextDouble();
//            double w = radiusInDegrees * Math.sqrt(u);
//            double t = 2 * Math.PI * v;
//            double x = w * Math.cos(t);
//            double y = w * Math.sin(t);
//
//            // Adjust the x-coordinate for the shrinking of the east-west distances
//            double new_x = x / Math.cos(y0);
//
//            double foundLatitude = new_x + x0;
//            double foundLongitude = y + y0;
//            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
//            randomPoints.add(randomLatLng);
//            Location l1 = new Location("");
//            l1.setLatitude(randomLatLng.latitude);
//            l1.setLongitude(randomLatLng.longitude);
//            randomDistances.add(l1.distanceTo(myLocation));
//        }
//        //Get nearest point to the centre
//        int indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances));
//        return randomPoints.get(indexOfNearestPointToCentre);
//    }

    public static boolean AskPermission_Camera(Activity activity, int reqcode) {
        String[] PERMISSIONS = {
                Manifest.permission.CAMERA,

        };
        if (!hasPermissions(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, reqcode);
            return false;
        }
        return true;
    }

    public static boolean AskPermission_Reboot(Activity activity) {
        String[] PERMISSIONS = {
                Manifest.permission.RECEIVE_BOOT_COMPLETED,

        };
        if (!hasPermissions(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, 111);
            return false;
        }
        return true;
    }

    public static boolean AskPermission_Location(Activity activity, int req) {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        if (!hasPermissions(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, req);
            return false;
        }
        return true;
    }

    public static boolean AskPermission_Contcatcs(Activity activity, int req) {
        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        };
        if (!hasPermissions(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, req);
            return false;
        }
        return true;
    }

    public static boolean hasPermissions(Context context, String... permissions) {

        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

//    private void RedirectSettings(Activity activity, String message) {
//        android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(activity);
//        alertBuilder.setCancelable(true);
//        alertBuilder.setTitle(message + " Permission necessary");
//        alertBuilder.setMessage("Kindly please contact admin");
//        android.app.AlertDialog alert = alertBuilder.create();
//        alert.setCancelable(false);
//        alert.show();
//    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Location getLocation(Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
        Location lastKnownLocation = null;
        //  String network   = LocationManager.NETWORK_PROVIDER;
        String gps = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            lastKnownLocation = locationManager.getLastKnownLocation(gps);
        // if (lastKnownLocation == null)
        //     lastKnownLocation = locationManager.getLastKnownLocation(network);
        return lastKnownLocation;

    }

    public static Location getLastKnownLocation(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        if (bestLocation == null) {
            bestLocation = new Location("");
            bestLocation.setLatitude(24.55363);
            bestLocation.setLongitude(46.86221);
        }
        //getLastLocationNewMethod(context);
        return bestLocation;
    }
//
//    public static void lastlogin(Context context, int EmpId) {
//
////        SharedPreferences pref = context.getSharedPreferences("LastLogin", 0); // 0 - for private mode
////        SharedPreferences.Editor editor = pref.edit();
////        editor.putInt("EmpID", EmpId); // Storing integer
////        editor.apply();
//
//        DBConnections dbConnections = new DBConnections(context, null);
//        dbConnections.UpdateLastLogin(EmpId, context);
//        dbConnections.close();
////
//
//    }

    public static int getlastlogin(Context context) {
//        SharedPreferences pref = context.getSharedPreferences("LastLogin", 0); // 0 - for private mode
//        return pref.getInt("EmpID", 0); // Storing integer
        DBConnections dbConnections = new DBConnections(context, null);
        Cursor cursor = dbConnections.Fill("select * from LastLogin ", context); //order by ID desc

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int empid = cursor.getInt(cursor.getColumnIndex("EmpID"));
            cursor.close();
            dbConnections.close();
            return empid;

        } else {
            dbConnections.close();
            cursor.close();
            return 0;
        }

    }


    public static String getDate() {
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd-MM-yyyy");
        return simpledateformat.format(calander.getTime());
    }

    public static String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String datetime = dateformat.format(c.getTime());

        return datetime;
    }

    public static String getCurrentDateTimeSS() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String datetime = dateformat.format(c.getTime());

        return datetime;
    }

    public static String getCurrentDatewithCustomSeconds(int seconds, String dt) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateformat.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.SECOND, seconds);

        String datetime = dateformat.format(c.getTime());

        return datetime;
    }


    public static String getCurrentDate() {
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
        String test = simpledateformat.format(calander.getTime());
        return simpledateformat.format(calander.getTime());
    }

    public static String getDateMinus2Days() {
        Calendar calander = Calendar.getInstance();
        calander.add(Calendar.DATE, -2);
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
        return simpledateformat.format(calander.getTime());
    }

    public static String getDateMinusDays(int dateminus) {
        Calendar calander = Calendar.getInstance();
        calander.add(Calendar.DATE, -dateminus);
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
        return simpledateformat.format(calander.getTime());
    }

    public static String getDateAdd1Day(String add1day) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(add1day));
            c.add(Calendar.DATE, 1);  // number of days to add
            add1day = sdf.format(c.getTime());  // dt is now the new date
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return add1day;

//        Calendar calander = Calendar.getInstance();
//        calander.add(Calendar.DATE, 1);
//        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
//        return simpledateformat.format(calander.getTime());
    }

    public static String getDateAdd1Day() {
        Calendar calander = Calendar.getInstance();
        calander.add(Calendar.DATE, 1);
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
        return simpledateformat.format(calander.getTime());
    }

    public static boolean IsAllowtoScan(String reporttime) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String getCurrentDateTime = sdf.format(c.getTime());
        //String getMyTime = "2019/12/05 16:31";

        if (getCurrentDateTime.compareTo(reporttime) < 0)
            return true;
        else
            return false;
    }

    public static String getCurrentFullDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = dateformat.format(c.getTime());

        return datetime;
    }

    public static String getDivision(Context context) {
        String devision = "";
        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " +
                GlobalVar.GV().EmployID, context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            devision = result.getString(result.getColumnIndex("Division"));
            if (devision.equals("0")) {
                devision = "Courier";

            }
        }
        return devision;
    }


    public static void CloseActivity(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        activity.finish();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean ValidateAutomacticDate_Splashscreen(Context context) {

        //return true;
        if (GlobalVar.GV().EmployID == 90189 || GlobalVar.GV().EmployID == 19127)
            return true;

        int isValidAutoTimeZone = 0;
        if (android.os.Build.VERSION.SDK_INT > 9) {
            isValidAutoTimeZone =
                    android.provider.Settings.System.getInt(context.getContentResolver(),
                            Settings.Global.AUTO_TIME_ZONE, 0); // 1 means Enabled
        } else {
            isValidAutoTimeZone = android.provider.Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AUTO_TIME_ZONE, 0); // 1 means Enabled
        }

        if (isValidAutoTimeZone == 1) {
            int isvalidAutotime = 0;
            if (android.os.Build.VERSION.SDK_INT > 9) {

                isvalidAutotime = android.provider.Settings.System.getInt(context.getContentResolver(),
                        Settings.Global.AUTO_TIME, 0); // 1 means Enabled
            } else {
                isvalidAutotime = android.provider.Settings.System.getInt(context.getContentResolver(),
                        Settings.System.AUTO_TIME, 0); // 1 means Enabled
            }
            if (isvalidAutotime == 1) {

                return true;
            }

        }

        return false;

    }

    public static boolean ValidateAutomacticDate(Context context) {

        //return true;
//        if (GlobalVar.GV().EmployID == 90189 || GlobalVar.GV().EmployID == 19127)
//            return true;

        int isValidAutoTimeZone = 0;
        if (android.os.Build.VERSION.SDK_INT > 9) {
            isValidAutoTimeZone =
                    android.provider.Settings.System.getInt(context.getContentResolver(),
                            Settings.Global.AUTO_TIME_ZONE, 0); // 1 means Enabled
        } else {
            isValidAutoTimeZone = android.provider.Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AUTO_TIME_ZONE, 0); // 1 means Enabled
        }

        if (isValidAutoTimeZone == 1) {
            int isvalidAutotime = 0;
            if (android.os.Build.VERSION.SDK_INT > 9) {

                isvalidAutotime = android.provider.Settings.System.getInt(context.getContentResolver(),
                        Settings.Global.AUTO_TIME, 0); // 1 means Enabled
            } else {
                isvalidAutotime = android.provider.Settings.System.getInt(context.getContentResolver(),
                        Settings.System.AUTO_TIME, 0); // 1 means Enabled
            }
            if (isvalidAutotime == 1) {

                String timeZone = GetLastLoggedinUserTimeZone(context);
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm"); //dd-MMM-yyyy hh:mm:ss z
                sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
                System.out.println(sdf.format(calendar.getTime()));

                Calendar calendar1 = Calendar.getInstance();
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm"); //dd-MMM-yyyy hh:mm:ss z
                System.out.println(sdf1.format(calendar1.getTime()));


                if (sdf.format(calendar.getTime()).equals(sdf1.format(calendar1.getTime()))) {

                    return true;
//                    TimeZone tz = TimeZone.getTimeZone(timeZone);
//                    Calendar mCalendar = new GregorianCalendar();
//                    TimeZone mTimeZone = mCalendar.getTimeZone();
//                    String devicetZ = mTimeZone.getID();
//                    if (timeZone.equals(devicetZ))
//                        return true;
//                    else
//                        return false;
                } else
                    return false;
            }

        }

        return false;

    }

    public static void RedirectSettings(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Info")
                .setMessage("Kindly Enable Automatic Network Provider DateTime & Correct TimeZone")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        try {
                            activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                        } catch (Exception e) {

                        }
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public static boolean deleteContactRawID
            (ArrayList<HashMap<String, String>> contacts, Context context, int callfunction) {
        // First select raw contact id by given name and family name.
        DBConnections dbConnections = new DBConnections(context, null);
        for (HashMap<String, String> contact : contacts) {

//            long rawContactId = Long.parseLong(contact.get("rawid"));
//
            long rawContactId = getRawContactIdByName(contact.get("name"), context);

            if (rawContactId == 0)
                rawContactId = getRawContactIdByName(contact.get("name"), context);

            ContentResolver contentResolver = context.getContentResolver();

            //******************************* delete data table related data ****************************************
            // Data table content process uri.
            Uri dataContentUri = ContactsContract.Data.CONTENT_URI;

            // Create data table where clause.
            StringBuffer dataWhereClauseBuf = new StringBuffer();
            dataWhereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
            dataWhereClauseBuf.append(" = ");
            dataWhereClauseBuf.append(rawContactId);

            // Delete all this contact related data in data table.
            contentResolver.delete(dataContentUri, dataWhereClauseBuf.toString(), null);


            //******************************** delete raw_contacts table related data ***************************************
            // raw_contacts table content process uri.
            Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

            // Create raw_contacts table where clause.
            StringBuffer rawContactWhereClause = new StringBuffer();
            rawContactWhereClause.append(ContactsContract.RawContacts._ID);
            rawContactWhereClause.append(" = ");
            rawContactWhereClause.append(rawContactId);

            // Delete raw_contacts table related data.
            contentResolver.delete(rawContactUri, rawContactWhereClause.toString(), null);

            //******************************** delete contacts table related data ***************************************
            // contacts table content process uri.
            Uri contactUri = ContactsContract.Contacts.CONTENT_URI;

            // Create contacts table where clause.
            StringBuffer contactWhereClause = new StringBuffer();
            contactWhereClause.append(ContactsContract.Contacts._ID);
            contactWhereClause.append(" = ");
            contactWhereClause.append(rawContactId);

            // Delete raw_contacts table related data.
            contentResolver.delete(contactUri, contactWhereClause.toString(), null);
            if (callfunction == 0)
                dbConnections.DeleteContactWithRaw(Integer.parseInt(contact.get("rawid")), contact.get("name"), contact.get("mno"));
            else if (callfunction == 1)
                dbConnections.DeleteContactWithRaw_Pickupsheet(Integer.parseInt(contact.get("rawid")), contact.get("name"), contact.get("mno"));

        }
        dbConnections.close();
        return true;
    }

    private static long getRawContactIdByName(String givenName, Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        // Create query column array.
        String queryColumnArr[] = {ContactsContract.RawContacts._ID};

        // Create where condition clause.
//        String displayName = givenName + " " + familyName;
        String displayName = givenName;
        String whereClause = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + displayName + "'";

        // Query raw contact id through RawContacts uri.
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        // Return the query cursor.
        Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null);

        long rawContactId = -1;

        if (cursor != null) {
            // Get contact count that has same display name, generally it should be one.
            int queryResultCount = cursor.getCount();
            // This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
            if (queryResultCount > 0) {
                // Move to the first row in the result cursor.
                cursor.moveToFirst();
                // Get raw_contact_id.
                rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
            }
        }

        return rawContactId;
    }

    public static boolean isAlaramRunning(Activity activity) {
        Intent intent = new Intent(activity, LocationupdateInterval.class);
        intent.setAction(LocationupdateInterval.ACTION_ALARM_RECEIVER);
        boolean isWorking = (PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
        return isWorking;
    }

    public static boolean find200Radios(double lat, double lon, int radius) {
        boolean isradius = false;
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(lat);

        double foundLongitude = new_x + lon;
        double foundLatitude = y + lat;

        float[] results = new float[1];
        Location.distanceBetween(lat, lon, foundLatitude, foundLongitude, results);
        float distanceInMeters = results[0];
        boolean isWithinradius = distanceInMeters < radius;

        if (isWithinradius)
            isradius = true;
        else
            isradius = false;

        //return new LatLng(foundLatitude, foundLongitude);
        return isradius;


    }

    public static boolean isCourierReachedConsigneeLocation(Context context, double lat, double lon) {
        boolean isradius = false;
        if (lat == 0.0) {
            return true;
        }
        Location location = GlobalVar.getLastKnownLocation(context);
        int radius = 400;
        if (GlobalVar.GV().isFortesting)
            radius = 400000;

        double foundLongitude = 0.0, foundLatitude = 0.0;

        if (location != null) {
            foundLatitude = location.getLatitude();
            foundLongitude = location.getLongitude();
        }


        //     Random random = new Random();

        // Convert radius from meters to degrees
//        double radiusInDegrees = radius / 111000f;
//
//        double u = random.nextDouble();
//        double v = random.nextDouble();
//        double w = radiusInDegrees * Math.sqrt(u);
//        double t = 2 * Math.PI * v;
//        double x = w * Math.cos(t);
//        double y = w * Math.sin(t);
//
//        // Adjust the x-coordinate for the shrinking of the east-west distances
//        double new_x = x / Math.cos(lat);

        // double foundLongitude = new_x + lon;
        // double foundLatitude = y + lat;

        float[] results = new float[1];
        Location.distanceBetween(lat, lon, foundLatitude, foundLongitude, results);
        float distanceInMeters = results[0];
        boolean isWithinradius = distanceInMeters < radius;

        if (isWithinradius)
            isradius = true;
        else
            isradius = false;

        //return new LatLng(foundLatitude, foundLongitude);
        return isradius;


    }

    public static boolean find200Radios5sec(double lat, double lon, int radius, Context
            context) {
        boolean isradius = false;
        double foundLatitude = 0.0, foundLongitude = 0.0;
        Location location = GlobalVar.getLastKnownLocation(context);
        if (location != null) {
            foundLatitude = location.getLatitude();
            foundLongitude = location.getLongitude();
        }

        float[] results = new float[1];
        Location.distanceBetween(lat, lon, foundLatitude, foundLongitude, results);
        float distanceInMeters = results[0];
        boolean isWithinradius = distanceInMeters < radius;

        Toast.makeText(context, String.valueOf(distanceInMeters), Toast.LENGTH_SHORT).show();

        if (isWithinradius)
            isradius = true;
        else
            isradius = false;

        return isradius;


    }

    public static boolean distance(double lat1, double lat2, double lon1,
                                   double lon2, Context context) {

        float[] results = new float[1];
        Location.distanceBetween(lat2, lon2, lat1, lon1, results);
        float distanceInMeters = results[0];
        boolean isWithin1m = distanceInMeters < 5;

        Toast.makeText(context, String.valueOf(distanceInMeters), Toast.LENGTH_SHORT).show();

        return isWithin1m;
    }

    public static boolean distanceLocations(double lat1, double lon1, double lat2,
                                            double lon2, Context context) {

        Location start = new Location("");
        start.setLatitude(lat1);
        start.setLongitude(lon1);

        Location end = new Location("");
        end.setLatitude(lat2);
        end.setLongitude(lon2);

        float distance = start.distanceTo(end);
        boolean isWithin1m = distance < 207;

//        Toast.makeText(context, String.valueOf(distance + "distanceTo" + isWithin1m), Toast.LENGTH_SHORT).show();

        return isWithin1m;
    }


    public static boolean distFrom(double lat1, double lng1, double lat2, double lng2, Context
            context) {
        double earthRadius = 0.00310686; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);
        double dist = earthRadius * c * 1609.344; //* 1609.344

        boolean withinradios = Double.parseDouble(df.format(dist)) <= 0.00310686;
        Toast.makeText(context, String.valueOf(Double.parseDouble(df.format(dist)) + " distFrom " + withinradios), Toast.LENGTH_SHORT).show();
        return withinradios;
    }

    public static String getUniqueID(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//        String uuid = UUID.randomUUID().toString();
        return androidId;

    }

    public static boolean locationEnabled(Context context) {

        if (GlobalVar.getDivision(context).equals("Express")) {
            return true;
        } else if (Build.MANUFACTURER.equals("unknown")) {
            return true;
        }


//        boolean gps_enabled = false;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            // gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (mode == 3) {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void enableLocationSettings(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("GPS Not Enable");  // GPS not found
        builder.setMessage("Kindly please Enable GPS with High Accuracy "); // Want to enable?
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());

            }
        });
        builder.create().show();
    }

    public boolean GetDivision(Context context) {
        if (GlobalVar.GV().EmployID == 19127)
            return false;
        String division = GlobalVar.GV().getDivisionID(context, GlobalVar.GV().EmployID);
        if (division.equals("Express"))
            return false;
        else
            return true;

    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static void disableSoftInputFromAppearing(EditText editText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
        } else {
            editText.setRawInputType(InputType.TYPE_NULL);
            editText.setFocusable(true);
        }
    }

    public void Logout(Context context) {
        DBConnections dbConnections = new DBConnections(context, null);
        int id = dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ", context);
        UserMeLogin userMeLogin = new UserMeLogin(id);
        dbConnections.UpdateUserMeLogout(userMeLogin, context);
        dbConnections.close();
//        android.os.Process.killProcess(android.os.Process.myPid());
        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }
//    public static String getDivision(Context context) {
//        String division = "";
//
//        DBConnections dbConnections = new DBConnections(context, null);
//
//        Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " +
//                GlobalVar.GV().EmployID, context);
//        if (result.getCount() > 0) {
//            result.moveToFirst();
//            division = result.getString(result.getColumnIndex("Division"));
//        }
//        return division;
//    }

    public static void ResetTriedCount() {
        triedTimes = 0;

    }

    public String GetDomainURL(Context context) {
        DBConnections dbConnections = new DBConnections(context, null);
        return dbConnections.GetPrimaryDomain(context);

    }

    public static IPointerAPI getIPointerAPI(String url, int readTimeOut, int connectTimeOut) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient(readTimeOut, connectTimeOut))
                .build();

        iPointerAPI = retrofit.create(IPointerAPI.class);
        return iPointerAPI;
    }

    private static OkHttpClient getClient(int readTimeOut, int connectTimeOut) {


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(readTimeOut, TimeUnit.SECONDS)
                .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
                .build();

        return okHttpClient;
    }


//    public static String getTestAPIURL(Context context) {
//        return NaqelAPITest_V10;
//    }
//
//    public static String getUATUrl(Context context) {
//        return NaqelAPIUAT;
//    }

    public String GetDomainURLforService(Context context, String ServiceName) {
        DBConnections dbConnections = new DBConnections(context, null);
        if (ServiceName.equals("Delivery"))
            return dbConnections.GetPrimaryDomain_DelService(context);
        else if (ServiceName.equals("DeliverySheetCBU"))
            return dbConnections.GetPrimaryDomain_DelSheetService(context);
        else if (ServiceName.equals("NotDeliver"))
            return dbConnections.GetPrimaryDomain_NotDeliverdService(context);
        else if (ServiceName.equals("DeliverySheetbyNCL"))
            return dbConnections.GetPrimaryDomain_DelSheetServicebyNCL(context);
        else if (ServiceName.equals("ArrivedatDest"))
            return dbConnections.GetPrimaryDomain_ArrivedatDest(context);
        else if (ServiceName.equals("Pickup"))
            return dbConnections.GetPrimaryDomain_ForPickup(context);


        return "";
    }

    public boolean GetDeviceVersion() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        } else {
            return false;
        }
    }


    public void SwitchoverDomain(Context context, String DomainURL) {
        DBConnections dbConnections = new DBConnections(context, null);
        if (GetDeviceVersion())
            dbConnections.UpdateDomaintriedTimes(GlobalVar.GV().triedTimes, DomainURL, context);
        else
            dbConnections.UpdateDomaintriedTimes_For5(GlobalVar.GV().triedTimes, DomainURL, context);

    }

    public void SwitchoverDomain_Service(Context context, String DomainURL, String type) {
        DBConnections dbConnections = new DBConnections(context, null);
        if (type.equals("Delivery"))
            dbConnections.UpdateDomaintriedTimes_ForDelService(DomainURL);
        else if (type.equals("DeliverySheetCBU"))
            dbConnections.UpdateDomaintriedTimes_ForDelSheetService(DomainURL);
        else if (type.equals("NotDeliver"))
            dbConnections.UpdateDomaintriedTimes_NotDeliveredService(DomainURL);
        else if (type.equals("DeliverySheetbyNCL"))
            dbConnections.UpdateDomaintriedTimes_ForDelSheetServicebyNCL(DomainURL);
        else if (type.equals("ArrivedatDest"))
            dbConnections.UpdateDomaintriedTimes_ForArrivedatDest(DomainURL);
        else if (type.equals("AtOrigin"))
            dbConnections.UpdateDomaintriedTimes_ForAtorigin(DomainURL);
        else if (type.equals("Pickup"))
            dbConnections.UpdateDomaintriedTimes_ForPickup(DomainURL);
        dbConnections.close();


    }


    public boolean isValidBarcode(String Barcode) {
        boolean isvalid = true;
//        try {
//            double barcode = Double.parseDouble(Barcode);
//            if (Barcode.length() == 13) {
//                String validChar = Barcode.substring(8, 12);
//                if (!validChar.equals("0000"))
//                    isvalid = false;
//            } else
//                isvalid = false;
//
//        } catch (Exception e) {
//            isvalid = false;
//        }

        return isvalid;

    }


    public boolean isValidBarcodeCons(String Barcode) {
        boolean isvalid = true;
        try {
            double barcode = Double.parseDouble(Barcode);
            if (Barcode.length() == GlobalVar.ScanBarcodeLength || Barcode.length() == 13) {
                int splitlength = 0, waybilllength = 0;

                if (Barcode.length() == 13) {
                    splitlength = 11;
                    waybilllength = 8;
                } else {
                    splitlength = GlobalVar.ScanBarcodeLength - 2;
                    waybilllength = GlobalVar.ScanWaybillLength;
                }

                String validChar = Barcode.substring(waybilllength, splitlength);
                if (!validChar.equals("000"))
                    isvalid = false;
            } else
                isvalid = false;

        } catch (Exception e) {
            isvalid = false;
        }

        return isvalid;

    }

    public boolean isValidNumber(String Barcode) {
        boolean isvalid = true;
        try {
            double barcode = Double.parseDouble(Barcode);

            String validChar = Barcode.substring(8, 12);
            if (!validChar.equals("0000"))
                isvalid = false;


        } catch (Exception e) {
            isvalid = false;
        }

        return isvalid;

    }

    public MyRouteShipments FetchMyRouteShipments(Context context, int notifywaybillno, String colorCode, String Lat, String Lng, boolean isupdate) {

        DBConnections dbConnections = new DBConnections(context, null);

        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where IsNotDelivered in(1 , 2) order by OnDeliveryDate Desc Limit 1", context);

        MyRouteShipments myRouteShipments = new MyRouteShipments();

        if (result.getCount() > 0) {

            result.moveToFirst();


            if (result.getInt(result.getColumnIndex("IsNotDelivered")) == 1)
                return null;
            myRouteShipments.IsDelivered = result.getInt(result.getColumnIndex("IsDelivered")) > 0;
            myRouteShipments.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
            myRouteShipments.BillingType = result.getString(result.getColumnIndex("BillingType"));
            myRouteShipments.OrderNo = Integer.parseInt(result.getString(result.getColumnIndex("OrderNo")));
            myRouteShipments.ItemNo = result.getString(result.getColumnIndex("ItemNo"));
            myRouteShipments.DsOrderNo = result.getInt(result.getColumnIndex("DsOrderNo"));

            myRouteShipments.TypeID = Integer.parseInt(result.getString(result.getColumnIndex("TypeID")));
            myRouteShipments.CODAmount = getDoubleFromString(result.getString(result.getColumnIndex("CODAmount")));
            myRouteShipments.DeliverySheetID = Integer.parseInt(result.getString(result.getColumnIndex("DeliverySheetID")));
            myRouteShipments.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
            myRouteShipments.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("ExpectedTime")));

            myRouteShipments.Latitude = result.getString(result.getColumnIndex("Latitude"));
            myRouteShipments.Longitude = result.getString(result.getColumnIndex("Longitude"));
            if ((myRouteShipments.Latitude.length() > 0 && myRouteShipments.Longitude.length() > 0) &&
                    !myRouteShipments.Latitude.equals("null") && !myRouteShipments.Longitude.equals("null")) {
                Location sp = new Location("");
                try {

                    sp.setLatitude(Double.parseDouble(myRouteShipments.Latitude));
                    sp.setLongitude(Double.parseDouble(myRouteShipments.Longitude));
                } catch (Exception e) {
                    sp.setLatitude(0);
                    sp.setLongitude(0);
                }

                //Places places = new Places(position, latlong);
                if (Double.parseDouble(myRouteShipments.Longitude) != 0.0)
//                    MyRouteActivity.places.add(sp);
                    MyRouteActivity_Complaince_GroupbyPhn.places.add(sp);
            }

            myRouteShipments.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
            myRouteShipments.ClientName = result.getString(result.getColumnIndex("ClientName"));
            myRouteShipments.ClientFName = result.getString(result.getColumnIndex("ClientFName"));
            myRouteShipments.ClientAddressPhoneNumber = result.getString(result.getColumnIndex("ClientAddressPhoneNumber"));
            myRouteShipments.ClientAddressFirstAddress = result.getString(result.getColumnIndex("ClientAddressFirstAddress"));
            myRouteShipments.ClientAddressSecondAddress = result.getString(result.getColumnIndex("ClientAddressSecondAddress"));
            myRouteShipments.ClientContactName = result.getString(result.getColumnIndex("ClientContactName"));
            myRouteShipments.ClientContactFName = result.getString(result.getColumnIndex("ClientContactFName"));
            myRouteShipments.ClientContactPhoneNumber = result.getString(result.getColumnIndex("ClientContactPhoneNumber"));
            myRouteShipments.ClientContactMobileNo = result.getString(result.getColumnIndex("ClientContactMobileNo"));
            myRouteShipments.ConsigneeName = result.getString(result.getColumnIndex("ConsigneeName"));
            myRouteShipments.ConsigneeFName = result.getString(result.getColumnIndex("ConsigneeFName"));
            myRouteShipments.ConsigneePhoneNumber = result.getString(result.getColumnIndex("ConsigneePhoneNumber"));
            myRouteShipments.ConsigneeFirstAddress = result.getString(result.getColumnIndex("ConsigneeFirstAddress"));
            myRouteShipments.ConsigneeSecondAddress = result.getString(result.getColumnIndex("ConsigneeSecondAddress"));
            myRouteShipments.ConsigneeNear = result.getString(result.getColumnIndex("ConsigneeNear"));
            myRouteShipments.ConsigneeMobile = result.getString(result.getColumnIndex("ConsigneeMobile"));
            myRouteShipments.Origin = result.getString(result.getColumnIndex("Origin"));
            myRouteShipments.Destination = result.getString(result.getColumnIndex("Destination"));
            myRouteShipments.PODNeeded = Boolean.parseBoolean(result.getString(result.getColumnIndex("PODNeeded")));
            myRouteShipments.PODDetail = result.getString(result.getColumnIndex("PODDetail"));
            myRouteShipments.PODTypeCode = result.getString(result.getColumnIndex("PODTypeCode"));
            myRouteShipments.PODTypeName = result.getString(result.getColumnIndex("PODTypeName"));

            myRouteShipments.IsPartialDelivered = result.getInt(result.getColumnIndex("PartialDelivered")) > 0;
            myRouteShipments.NotDelivered = result.getInt(result.getColumnIndex("NotDelivered")) > 0;
            myRouteShipments.CourierDailyRouteID = Integer.parseInt(result.getString(result.getColumnIndex("CourierDailyRouteID")));
            myRouteShipments.OptimzeSerialNo = Integer.parseInt(result.getString(result.getColumnIndex("OptimzeSerialNo")));
            myRouteShipments.HasComplaint = result.getInt(result.getColumnIndex("HasComplaint")) > 0;
            myRouteShipments.HasDeliveryRequest = result.getInt(result.getColumnIndex("HasDeliveryRequest")) > 0;
            myRouteShipments.POS = result.getInt(result.getColumnIndex("POS"));
            myRouteShipments.IsPaid = result.getInt(result.getColumnIndex("Ispaid"));
            myRouteShipments.IsMap = result.getInt(result.getColumnIndex("IsMap"));
            myRouteShipments.BGColor = colorCode;
            myRouteShipments.isupdate = isupdate;
            myRouteShipments.Position = result.getInt(result.getColumnIndex("DsOrderNo")) - 1;

            if (notifywaybillno > 0) {
                myRouteShipments.IsNotifyWaybillNo = notifywaybillno;
                myRouteShipments.IsNotifyCust = false;
                myRouteShipments.ParentLatitude = Lat;
                myRouteShipments.ParentLongitude = Lng;
            }

        }
        if (result != null)
            result.close();
        dbConnections.close();
        return myRouteShipments;
    }

    public void PermissionAlert(final Activity activity) {
        SweetAlertDialog eDialog = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);

        eDialog.setCancelable(false);
        eDialog.setTitleText("Info");
        eDialog.setContentText("Our app need the Backgroud Location Permission,please kindly allow me");
        eDialog.setConfirmText("Ok");

        eDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                sDialog.dismissWithAnimation();
//                activity.finish();

            }
        });
        eDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                sDialog.dismissWithAnimation();
                activity.finish();

            }
        });
        eDialog.show();

    }

    public void PermissionAlertInfo(final Activity activity) {
        SweetAlertDialog eDialog = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);

        eDialog.setCancelable(false);
        eDialog.setTitleText("App need the Backgroud Location Permission");
        eDialog.setContentText("Go to Settings - Location - Allow All Time");
        eDialog.setConfirmText("Ok");
        eDialog.setCancelText("Cancel");
        eDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                try {
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"
                            + BuildConfig.APPLICATION_ID));
                    activity.startActivityForResult(i, 15);
                } catch (Exception e) {
                    GlobalVar.ShowDialog(activity, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                }
                sDialog.dismissWithAnimation();

            }
        });
        eDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                sDialog.dismissWithAnimation();
                activity.finish();

            }
        });
        eDialog.show();

    }

    public int isPermissionEnabled(String permissions, Activity activity) {
        return ContextCompat.checkSelfPermission(activity, permissions);
    }

    public static float FindDistancewithinLocation(double Slat, double Slon, double dLat, double dLon) {
        boolean isradius = false;

        if (Slat == 0.0) {
            return 0;
        }

        float[] results = new float[1];
        Location.distanceBetween(Slat, Slon, dLat, dLon, results);

        float distanceInMeters = results[0];

        return distanceInMeters;
    }

    public Location sortLocationbyDistance(List<Location> tLocation, Location origin) {
        List<Location> temp = new ArrayList<>();
        //Location origin = tLocation.get(0);
        //temp.add(origin);
        float distancebyMeter = (float) 500000.0;
        int position = 0;
        for (int i = 0; i < tLocation.size(); i++) {

            float dist = FindDistancewithinLocation(origin.getLatitude(), origin.getLongitude(), tLocation.get(i).getLatitude(), tLocation.get(i).getLongitude());
            if (dist <= distancebyMeter) {
                position = i;
                distancebyMeter = dist;
            }
            tLocation.get(i).setSpeed(dist);
            temp.add(tLocation.get(i));
        }

        Location loc = temp.get(position);
        loc.setAccuracy(position);
        return loc;
    }

    public boolean isSeqComplete(Context context) {

        boolean returnvalue = true;
        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from MyRouteActionActivity", context);
        if (result != null && result.getCount() > 0) {

            result.moveToFirst();
            int isComplete = result.getInt(result.getColumnIndex("isComplete"));
            if (isComplete == 0)
                returnvalue = false;

        }
        result.close();
        dbConnections.close();
        return returnvalue;

    }


    public int GetMyRouteShipmentsCount(Context context) {

        boolean returnvalue = true;
        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from MyRouteShipments  ", context);
        if (result != null && result.getCount() > 0) {
            return result.getCount();

        }

        return 0;

    }

    public ArrayList<MyRouteShipments> LoadMyRouteShipmentsOptimizeMap(String orderBy,
                                                                       boolean CheckComplaintandDeliveryRequest, Context context, View view) {


        MyRouteActivity_Complaince_GroupbyPhn.Optmizeplaces.clear();
        MyRouteActivity_Complaince_GroupbyPhn.places.clear();

        ArrayList<MyRouteShipments> myRouteShipmentList = new ArrayList<>();

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location location = GlobalVar.getLastKnownLocation(context);
            location.setSpeed(0);

            //ll = new LatLng(location.getLatitude(), location.getLongitude());
            if (location != null) {
//                MyRouteActivity.places.add(location);
                MyRouteActivity_Complaince_GroupbyPhn.Optmizeplaces.add(location);
                MyRouteActivity_Complaince_GroupbyPhn.places.add(location);
            }
        }

        DBConnections dbConnections = new DBConnections(context, null);

        int position = 1;

        if (GlobalVar.GV().CourierDailyRouteID > 0) {
            Cursor result = dbConnections.Fill("select * from MyRouteShipments Where CourierDailyRouteID = " +
                    GlobalVar.GV().CourierDailyRouteID + " order by " + orderBy, context);
            if (result.getCount() > 0) {

                dbConnections.InsertOFD(result.getCount(), GlobalVar.getDate(), context);

                myRouteShipmentList = new ArrayList<>();

                result.moveToFirst();
                do {
                    MyRouteShipments myRouteShipments = new MyRouteShipments();
                    myRouteShipments.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    myRouteShipments.BillingType = result.getString(result.getColumnIndex("BillingType"));
                    myRouteShipments.OrderNo = Integer.parseInt(result.getString(result.getColumnIndex("OrderNo")));
                    myRouteShipments.DsOrderNo = Integer.parseInt(result.getString(result.getColumnIndex("DsOrderNo")));
                    myRouteShipments.ItemNo = result.getString(result.getColumnIndex("ItemNo"));

                    myRouteShipments.TypeID = Integer.parseInt(result.getString(result.getColumnIndex("TypeID")));
                    myRouteShipments.CODAmount = getDoubleFromString(result.getString(result.getColumnIndex("CODAmount")));
                    myRouteShipments.DeliverySheetID = Integer.parseInt(result.getString(result.getColumnIndex("DeliverySheetID")));
                    myRouteShipments.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                    myRouteShipments.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("ExpectedTime")));

                    myRouteShipments.Latitude = result.getString(result.getColumnIndex("Latitude"));
                    myRouteShipments.Longitude = result.getString(result.getColumnIndex("Longitude"));
                    if ((myRouteShipments.Latitude.length() > 0 && myRouteShipments.Longitude.length() > 0) &&
                            !myRouteShipments.Latitude.equals("null") && !myRouteShipments.Longitude.equals("null")) {
                        Location sp = new Location("");
                        try {

                            sp.setLatitude(Double.parseDouble(myRouteShipments.Latitude));
                            sp.setLongitude(Double.parseDouble(myRouteShipments.Longitude));
                            if (Double.parseDouble(myRouteShipments.Longitude) != 0.0) {
//                                haslocation.add(position);
                                sp.setSpeed(position);
                            }
                        } catch (Exception e) {
                            sp.setLatitude(0);
                            sp.setLongitude(0);
                        }

                        //Places places = new Places(position, latlong);
                        if (Double.parseDouble(myRouteShipments.Longitude) != 0.0)
//                            MyRouteActivity.places.add(sp);
                            if (result.getInt(result.getColumnIndex("IsPlan")) == 1)
                                MyRouteActivity_Complaince_GroupbyPhn.Optmizeplaces.add(sp);
                        MyRouteActivity_Complaince_GroupbyPhn.places.add(sp);

                    }

                    myRouteShipments.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
                    myRouteShipments.ClientName = result.getString(result.getColumnIndex("ClientName"));
                    myRouteShipments.ClientFName = result.getString(result.getColumnIndex("ClientFName"));
                    myRouteShipments.ClientAddressPhoneNumber = result.getString(result.getColumnIndex("ClientAddressPhoneNumber"));
                    myRouteShipments.ClientAddressFirstAddress = result.getString(result.getColumnIndex("ClientAddressFirstAddress"));
                    myRouteShipments.ClientAddressSecondAddress = result.getString(result.getColumnIndex("ClientAddressSecondAddress"));
                    myRouteShipments.ClientContactName = result.getString(result.getColumnIndex("ClientContactName"));
                    myRouteShipments.ClientContactFName = result.getString(result.getColumnIndex("ClientContactFName"));
                    myRouteShipments.ClientContactPhoneNumber = result.getString(result.getColumnIndex("ClientContactPhoneNumber"));
                    myRouteShipments.ClientContactMobileNo = result.getString(result.getColumnIndex("ClientContactMobileNo"));
                    myRouteShipments.ConsigneeName = result.getString(result.getColumnIndex("ConsigneeName"));
                    myRouteShipments.ConsigneeFName = result.getString(result.getColumnIndex("ConsigneeFName"));
                    myRouteShipments.ConsigneePhoneNumber = result.getString(result.getColumnIndex("ConsigneePhoneNumber"));
                    myRouteShipments.ConsigneeFirstAddress = result.getString(result.getColumnIndex("ConsigneeFirstAddress"));
                    myRouteShipments.ConsigneeSecondAddress = result.getString(result.getColumnIndex("ConsigneeSecondAddress"));
                    myRouteShipments.ConsigneeNear = result.getString(result.getColumnIndex("ConsigneeNear"));
                    myRouteShipments.ConsigneeMobile = result.getString(result.getColumnIndex("ConsigneeMobile"));
                    myRouteShipments.Origin = result.getString(result.getColumnIndex("Origin"));
                    myRouteShipments.Destination = result.getString(result.getColumnIndex("Destination"));
                    myRouteShipments.PODNeeded = Boolean.parseBoolean(result.getString(result.getColumnIndex("PODNeeded")));
                    myRouteShipments.PODDetail = result.getString(result.getColumnIndex("PODDetail"));
                    myRouteShipments.PODTypeCode = result.getString(result.getColumnIndex("PODTypeCode"));
                    myRouteShipments.PODTypeName = result.getString(result.getColumnIndex("PODTypeName"));
                    myRouteShipments.IsDelivered = result.getInt(result.getColumnIndex("IsDelivered")) > 0;
                    myRouteShipments.IsPartialDelivered = result.getInt(result.getColumnIndex("PartialDelivered")) > 0;
                    myRouteShipments.NotDelivered = result.getInt(result.getColumnIndex("NotDelivered")) > 0;
                    myRouteShipments.CourierDailyRouteID = Integer.parseInt(result.getString(result.getColumnIndex("CourierDailyRouteID")));
                    myRouteShipments.OptimzeSerialNo = Integer.parseInt(result.getString(result.getColumnIndex("OptimzeSerialNo")));
                    myRouteShipments.HasComplaint = result.getInt(result.getColumnIndex("HasComplaint")) > 0;
                    myRouteShipments.HasDeliveryRequest = result.getInt(result.getColumnIndex("HasDeliveryRequest")) > 0;
                    myRouteShipments.POS = result.getInt(result.getColumnIndex("POS"));
                    myRouteShipments.IsPaid = result.getInt(result.getColumnIndex("Ispaid"));
                    myRouteShipments.IsMap = result.getInt(result.getColumnIndex("IsMap"));
                    myRouteShipments.Position = position - 1;

                    myRouteShipmentList.add(myRouteShipments);

                    // radios += 800;
                    position += 1;
                }
                while (result.moveToNext());
                result.close();
                // ReOrderMyRouteShipments(CheckComplaintandDeliveryRequest, view, context);
            }
        }

        dbConnections.close();
        return myRouteShipmentList;
    }


    public boolean findNearestOnebyRadius(double lat, double lon, double destlat, double destlong, int radius) {
        boolean isradius = false;
        if (lat == 0.0) {
            return true;
        }

        if (GlobalVar.GV().isFortesting)
            radius = 400000;

        float[] results = new float[1];
        Location.distanceBetween(lat, lon, destlat, destlong, results);
        float distanceInMeters = results[0];
        boolean isWithinradius = distanceInMeters < radius;

        if (isWithinradius)
            isradius = true;
        else
            isradius = false;

        return isradius;


    }

    //Added by : Riyam
    public static String ValidateMobileNo(String mobileno) {

        if (!mobileno.equals("null") && mobileno != null && mobileno.length() > 0 && !mobileno.equals("0")) {
            if (mobileno.length() == 10) {
                String validate = mobileno.substring(0, 1);
                if (validate.equals("0"))
                    mobileno = mobileno.replaceFirst("0", "+966");
            } else {
                if (mobileno.length() > 10) {
                    //String validate = mobileno.substring(0, 2);
                    if (mobileno.contains("00966"))
                        mobileno = mobileno.replaceFirst("00966", "+966");
                    else if (mobileno.contains("+966"))
                        mobileno = mobileno.replaceFirst("\\+966", "+966");
                    else if (mobileno.contains("966"))
                        mobileno = mobileno.replaceFirst("966", "+966");

                } else if (mobileno.length() == 9) {
                    mobileno = "+966" + mobileno;
                }
                //else
//                    mobileno = mobileno;
            }

        }

        return mobileno;
    }

    //Added by : Riyam
    public static String ValidateMobileNoOtherCountry(String mobileno, String CountryCode) {

        if (!mobileno.equals("null") && mobileno != null && mobileno.length() > 0 && !mobileno.equals("0")) {
            if (mobileno.length() >= 9) {
                String mno = mobileno.substring(mobileno.length() - 9, mobileno.length());

                mobileno = "+" + CountryCode + mno;


            }

        }

        return mobileno;
    }


    //Riyam
    public static String getWaybillFromBarcode(String barcode) {
        try {
            return barcode.substring(0, 8);
        } catch (Exception e) {
            //  Log.d(TAG, e.toString());
        }
        return "";
    }

    public static String getCSPhoneNumber() {
        return "920020505";
    }

    public static String getCSEmail() {
        return "cs@NAQEL.com.sa";
    }

    //******************************* End Riyam ****************************

    private AlertCallback alertCallback;

    public void CommonAlertMessageActivity(String title, String msg,
                                           final Activity activity, final AlertRequest alertRequest, final Enum type, String classname) {

        setCallback(classname);

        SweetAlertDialog eDialog = new SweetAlertDialog(activity, alertRequest.getAlertType());

        eDialog.setCancelable(alertRequest.getIsCancelable());
        eDialog.setTitleText(title);
        eDialog.setContentText(msg);
        eDialog.setConfirmText("Yes");

        eDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                sDialog.dismissWithAnimation();
                alertCallback.returnOk(type.getValue(), activity);
                //if (alertRequest.getIsFinish())
                //    activity.finish();

            }
//                activity.finish();


        });
        eDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                sDialog.dismissWithAnimation();
                alertCallback.returnOk(1, activity);

            }
        });


        eDialog.show();
    }

    private void setCallback(String classname) {
//        if (classname.equals("SkipWaybillNoinRouteLine"))
//            alertCallback = new SkipWaybillNoinRouteLine();
//        else if (classname.equals("PickUpFirstFragmentEBU")) //EBU
//            alertCallback = new com.naqelexpress.naqelpointer.Activity.PickUp.PickUpFirstFragment();
//        else if (classname.equals("MyRouteActivity_Complaince_GroupbyPhn"))
//            alertCallback = new MyRouteActivity_Complaince_GroupbyPhn();

        if (classname.equals("SkipWaybillNoinRouteLine"))
            alertCallback = new SkipWaybillNoinRouteLine();
        else if (classname.equals("PickUpFirstFragmentEBU")) //EBU
            alertCallback = new com.naqelexpress.naqelpointer.Activity.PickUp.PickUpFirstFragment();
        else if (classname.equals("MyRouteActivity_Complaince_GroupbyPhn"))
            alertCallback = new MyRouteActivity_Complaince_GroupbyPhn();
        else if (classname.equals("CourierRating"))
            alertCallback = new CourierRating();
        else if (classname.equals("BookingList"))
            alertCallback = new BookingList();
        else if (classname.equals("Fuel"))
            alertCallback = new Fuel();
        else if (classname.equals("com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingList"))
            alertCallback = new com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingList();
        else if (classname.equals("NclShipmentActivity"))
            alertCallback = new NclShipmentActivity();
        else if (classname.equals("com.naqelexpress.naqelpointer.NCLBulk.NclShipmentActivity"))
            alertCallback = new com.naqelexpress.naqelpointer.NCLBulk.NclShipmentActivity();
        else if (classname.equals("CBM"))
            alertCallback = new CBM();
    }

    public void CommonProgessAlertMessageActivity(String title, String msg,
                                                  final Activity activity, final AlertRequest alertRequest,
                                                  String classname) {

        setCallback(classname);

        SweetAlertDialog eDialog = new SweetAlertDialog(activity, alertRequest.getAlertType());

        eDialog.setCancelable(alertRequest.getIsCancelable());
        eDialog.setTitleText(title);
        eDialog.setContentText(msg);
        alertCallback.returnCancel(1, eDialog);
        eDialog.show();

    }

    public void alertMsgAll(String title, String msg, Activity activity, Enum type, String classname) {

        AlertRequest alertRequest = new AlertRequest();
        alertRequest.setAlertType(type.getValue());
        alertRequest.setIsCancelable(false);
        alertRequest.setIsFinish(true);
        alertRequest.setAlrttitle(title);
        alertRequest.setAlrtmessage(msg);

        if (type.getValue() != Enum.PROGRESS_TYPE.getValue())
            CommonAlertMessageActivity(title, msg, activity, alertRequest, type, classname);
        else
            CommonProgessAlertMessageActivity(title, msg, activity, alertRequest, classname);


    }

    public boolean isLastSeqComplete(Context context) {

        boolean returnvalue = false;
        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from UpdateLastSeqNo Limit 1", context);
        if (result != null && result.getCount() > 0) {

            result.moveToFirst();
            int isComplete = result.getInt(result.getColumnIndex("issync"));
            if (isComplete == 1)
                returnvalue = true;

        }
        result.close();
        dbConnections.close();
        return returnvalue;

    }

//    public void erroralert(String title, String msg, Activity activity) {
//        AlertRequest alertRequest = new AlertRequest();
//        alertRequest.setAlertType(Enum.ERROR_TYPE.getValue());
//        alertRequest.setIsCancelable(false);
//        alertRequest.setIsFinish(true);
//        alertRequest.setAlrttitle(title);
//        alertRequest.setAlrtmessage(msg);
//
//        CommonAlertMessageActivity(title, msg, activity, alertRequest);
//
//    }


    public static String GetDateTimeFormat(String datetime) {
        String dtime = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date dt = formatter.parse(datetime);

            DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy");
            String dte = dfmt.format(dt);

            DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
            String time = fmt.print(DateTime.parse(datetime));
            dtime = dte + " " + time;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dtime;
    }

    public static ArrayList<MyRouteShipments> GetArrivedatDestPieces(Context context) {

        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from AtDestination order by id desc", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                try {
                    JSONObject jsonObject = new JSONObject(result.getString(result.getColumnIndex("Json")));
                    boolean issync = result.getInt(result.getColumnIndex("IsSync")) > 0;
                    String ScannedTime = result.getString(result.getColumnIndex("CTime"));

                    JSONArray jsonArray = jsonObject.getJSONArray("Pallets");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                        onDeliveryRequest.ItemNo = obj.getString("PalletNo");
                        onDeliveryRequest.IsDelivered = issync;
                        onDeliveryRequest.ExpectedTime = DateTime.parse(ScannedTime);
                        onDeliveryRequest.TypeID = 125;
                        DeliverySheetFromLocal.add(onDeliveryRequest);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }

    public static ArrayList<MyRouteShipments> getLoadtoDestPiece(Context context) {

        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from TripPlanDetails order by id desc", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                try {
                    JSONObject jsonObject = new JSONObject(result.getString(result.getColumnIndex("Json")));
                    boolean issync = result.getInt(result.getColumnIndex("IsSync")) > 0;

                    JSONArray onLoading = jsonObject.getJSONArray("OnLoading");
                    JSONObject onLoadingJSONObject = onLoading.getJSONObject(0);
                    String ScannedTime = onLoadingJSONObject.getString("CTime");

                    JSONArray jsonArray = jsonObject.getJSONArray("Barcode");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                        onDeliveryRequest.ItemNo = obj.getString("BarCode");
                        onDeliveryRequest.IsDelivered = issync;
                        onDeliveryRequest.ExpectedTime = DateTime.parse(ScannedTime);
                        onDeliveryRequest.TypeID = 125;
                        DeliverySheetFromLocal.add(onDeliveryRequest);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }

    public static ArrayList<MyRouteShipments> getDeliverySheetforEBUPieces(Context context)
            throws ParseException {
        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select ocd.BarCode , oc.CTime , ocd.IsSync from OnCLoadingForDDetail ocd inner join OnCloadingForD oc " +
                "on oc.ID = ocd.OnCLoadingForDID order by CTime asc ", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                onDeliveryRequest.ItemNo = result.getString(result.getColumnIndex("BarCode"));
                onDeliveryRequest.IsDelivered = result.getInt(result.getColumnIndex("IsSync")) > 0;
                onDeliveryRequest.TypeID = 125;
                onDeliveryRequest.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("CTime")));
                DeliverySheetFromLocal.add(onDeliveryRequest);

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }

    public static ArrayList<MyRouteShipments> getAtOriginPieces(Context context) {

        ArrayList<MyRouteShipments> DeliverySheetFromLocal = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from AtOrigin order by id desc", context);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                try {
                    JSONObject jsonObject = new JSONObject(result.getString(result.getColumnIndex("Json")));
                    boolean IsSync = result.getInt(result.getColumnIndex("IsSync")) > 0;

                    JSONArray jsonArray = jsonObject.getJSONArray("AtOriginDetails");

                    if (jsonArray.length() > 0) {
                        DateTime ExpectedTime = DateTime.parse(jsonObject.getString("CTime"));

//                       String PiecesCount =jsonObject.getString("CTime");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            MyRouteShipments onDeliveryRequest = new MyRouteShipments();
                            onDeliveryRequest.ItemNo = obj.getString("BarCode");
                            onDeliveryRequest.IsDelivered = IsSync;
                            onDeliveryRequest.ExpectedTime = ExpectedTime;
                            onDeliveryRequest.TypeID = 125;
//                        onDeliveryRequest.PiecesCount = result.getString(result.getColumnIndex("PieceCount"));
                            DeliverySheetFromLocal.add(onDeliveryRequest);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));

            }
            while (result.moveToNext());


        }
        dbConnections.close();
        return DeliverySheetFromLocal;
    }

    public static int GetEmployID(Context context) {

        int EmployeID = 0;
        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select * from UserMeLogin  order by ID desc Limit 1", context);
        if (result != null && result.getCount() > 0) {
            result.moveToFirst();
            EmployeID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));

        }
        dbConnections.close();
        if (result != null)
            result.close();
        return EmployeID;
    }

    public static HashMap<String, Object> GetEmployID_Name(Context context) {

        HashMap<String, Object> empdetails = new HashMap();

        int EmployeID = 0;
        String EmployName = "";
        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select EmployID , EmployName from UserME  order by ID desc Limit 1", context);
        if (result != null && result.getCount() > 0) {
            result.moveToFirst();
            EmployeID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
            EmployName = result.getString(result.getColumnIndex("EmployName"));
            empdetails.put("EmployeID", EmployeID);
            empdetails.put("EmployName", EmployName);
        }
        dbConnections.close();
        if (result != null)
            result.close();

        return empdetails;
    }

    public static boolean isMyroutesync(Context context) {
        boolean issync = true;
        DBConnections db = new DBConnections(context, null);
        //db.DeleteAllSuggestLocation(getApplicationContext());

        Cursor cursor = db.Fill("select count(*) total from MyRouteCompliance where IsSync = 0", context);
        if (cursor.getCount() > 0) {

            cursor.moveToFirst();

            int count = Integer.parseInt(cursor.getString(cursor.getColumnIndex("total")));
            if (count > 0)
                issync = false;
            db.close();
            cursor.close();
        }

        return issync;
    }

    public static void toGoogle(String lat, String lng, Activity activity, Location location) {
//        Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng);
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
//            activity.startActivity(mapIntent);
//        }
        String api = "http://maps.google.com/maps?saddr=";
        StringBuilder sb = new StringBuilder();
        sb.append(api);


        sb.append(location.getLatitude() + "," + location.getLongitude());
        sb.append("&daddr=");
        sb.append(lat + "," + lng);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
        intent.setPackage("com.google.android.apps.maps");
        activity.startActivity(intent);


    }

    public String getLocationMsg(Context context, String Waybillno, String ClientName) {
        final String locationMsgAr = context.getString(R.string.customerLocationMsg1Ar) + "\n" +
                context.getString(R.string.customerLocationMsg2Ar) + " " +
                Waybillno + " " +
                context.getString(R.string.customerLocationMsg3Ar) + " " +
                ClientName;
        final String locationMsgEn = context.getString(R.string.customerLocationMsg1En) + "\n" +
                context.getString(R.string.customerLocationMsg2En) + " " +
                Waybillno + " " +
                context.getString(R.string.customerLocationMsg3En) + " " +
                ClientName;
        final String infoTrackLink = context.getString(R.string.infotrackLocationLink) + Waybillno;
        return locationMsgAr + "\n\n" + locationMsgEn + "\n\n" + infoTrackLink;
    }

    public String getFrontDoorMsg(Context context, String Waybillno, String ClientName) {
        final String frontDoorMsgAr = context.getString(R.string.frontDoorMsg1Ar) + "\n" +
                context.getString(R.string.frontDoorMsg2Ar) + " " +
                Waybillno + " " +
                context.getString(R.string.frontDoorMsg3Ar) + " " +
                ClientName;
        final String frontDoorMsgEn = context.getString(R.string.frontDoorMsg1En) + "\n" +
                context.getString(R.string.frontDoorMsg2En) + " " +
                Waybillno + " " +
                context.getString(R.string.frontDoorMsg3Ar) + " " +
                ClientName;
        return frontDoorMsgAr + "\n\n" + frontDoorMsgEn;
    }

    public String getFrontDoorMsgAsrPickup(String WaybillNo, String ClientName) {
        String armsg1 = "مرحبا , مندوب ناقل وصل الى موقعك لاستلام شحنتكم ";
        String armsg2 = "من";
        final String frontDoorMsgAr = armsg1 +
                WaybillNo + " " + armsg2 + " " +
                ClientName;
        final String frontDoorMsgEn = "Hello! NAQEL courier has arrived at your front door to pick up Return shipment" +
                WaybillNo + " for " +
                ClientName;
        return frontDoorMsgAr + "\n\n" + frontDoorMsgEn;
    }

    public String getCsSupportMsg(Context context) {
        final String csSupportMsgAr = context.getString(R.string.csSupportMsg1Ar) + "\n\n" +
                GlobalVar.getCSPhoneNumber() + "\n" +
                GlobalVar.getCSEmail();
        final String csSupportMsgMsgEn = context.getString(R.string.csSupportMsg1En) + "\n\n" +
                GlobalVar.getCSPhoneNumber() + "\n" +
                GlobalVar.getCSEmail();
        return csSupportMsgAr + "\n\n" + csSupportMsgMsgEn;
    }


    public static String GetLastLoggedinUserTimeZone(Context context) {
        String TimeZone = "";


        DBConnections dbConnections = new DBConnections(context, null);

        Cursor result = dbConnections.Fill("select TimeZone from UserME order by id desc Limit 1 ", context);
        if (result.getCount() > 0) {

            result.moveToFirst();
            TimeZone = result.getString(result.getColumnIndex("TimeZone"));


        }
        dbConnections.close();
        result.close();

        return TimeZone;
    }

    public static int GetLastLoginEmployCountryID(Context context) {

        int CountryID = 0;
        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("select CountryID from UserME  order by ID desc Limit 1", context);
        if (result != null && result.getCount() > 0) {
            result.moveToFirst();
            CountryID = Integer.parseInt(result.getString(result.getColumnIndex("CountryID")));

        }
        dbConnections.close();
        if (result != null)
            result.close();
        return CountryID;
    }

    public static void savemobilenointocontacts(String phoneno, String mno, String savename, String SNo, String WaybillNo,
                                                Activity activity) {
        ArrayList<String> MNos = new ArrayList<>();
        if (!phoneno.equals("null") && phoneno != null && !phoneno.equals("0")
                && phoneno.length() > 0)

            MNos.add(phoneno);


        if (!mno.equals("null") && mno != null &&
                !mno.equals("0") && mno.length() > 0) {

            MNos.add(mno);
        }

        String sname = "";
        if (savename.equals("ASR"))
            sname = "ASR";
        if (MNos.size() > 0) {
            Global global = new Global(activity);
            global.addMobileNumberintoContacts(SNo + " - " + sname + " - " + WaybillNo, MNos, WaybillNo);
        }
    }
}
