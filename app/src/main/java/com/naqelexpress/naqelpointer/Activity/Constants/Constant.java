package com.naqelexpress.naqelpointer.Activity.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.WindowManager;

import com.naqelexpress.naqelpointer.Activity.InterCity.Model.InterCityModel;
import com.naqelexpress.naqelpointer.Activity.InterCity.Model.SafetyCurtainsAndCargoPictureModel;
import com.naqelexpress.naqelpointer.Activity.InterCity.Model.TireConditionPicturesModel;
import com.naqelexpress.naqelpointer.Activity.InterCity.Model.VehicleAttachmentsModel;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.utils.SharedHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Constant {

    CountDownTimer cTimer = null;
    public static boolean enableLocation = true;

    public static InterCityModel interCityModel = new InterCityModel();
    public static TireConditionPicturesModel tireConditionPictures = new TireConditionPicturesModel();
    public static SafetyCurtainsAndCargoPictureModel safetyCurtainsAndCargoPictureModel = new SafetyCurtainsAndCargoPictureModel();
    public static VehicleAttachmentsModel vehicleAttachmentsModel = new VehicleAttachmentsModel();

    public static List<File> tireConditionPicture = new ArrayList<File>();
    public static List<File> safetyCurtainsCargoPicture = new ArrayList<File>();
    public static List<File> attachments = new ArrayList<File>();
    public static List<File> AllImages = new ArrayList<File>();



    public static Double payAbleAmount = 0.0;
    public static Double totalEarning = 0.0;

//    public CountDownTimer getcTimer(int timeInSeconds, Activity activity){
//
//        if(cTimer == null){
//            cTimer = new CountDownTimer(timeInSeconds, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    isCalled = true;
//                    myTime = myTime - 1;
//                    Log.d("COUNT DOWN TIMER", String.valueOf(myTime));
//                    Toast.makeText(activity, "TIME " + String.valueOf(myTime), Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onFinish() {
//                    //Call API Here
//                }
//            }
//        }
//        return cTimer;
//    }

    public static void exitConfirmation(Activity activity, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        activity.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void alert(String title, String message, Activity context) {
        try {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setPositiveButton("Ok", null);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setCancelable(false);
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (WindowManager.BadTokenException e) {//
//            Log.e(context.getCallingActivity().toString(), "alert: " + e);
        }
    }


    //    getDate(82233213123L, "dd/MM/yyyy hh:mm:ss.SSS")
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());

    }

    public static File createImageFile(Activity activity, String imageSuffix, int count) throws IOException {
        // Create an image file name
        Long timestamp = System.currentTimeMillis() / 1000;
        String id = String.valueOf(GlobalVar.GV().EmployID);
        String imageFileName = id + "_" + timestamp.toString() + "_" + imageSuffix + "_" + String.valueOf(count);// +".png";
        SharedHelper.putKeyString(activity.getApplicationContext(), "fileName", imageFileName);
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
