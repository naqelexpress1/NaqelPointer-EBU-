package com.naqelexpress.naqelpointer.Activity.Constants;

import android.app.Activity;
import android.os.Environment;
import android.view.WindowManager;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.utils.SharedHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Constant {

    public static boolean enableLocation = true;


    public static Double payAbleAmount = 0.0;
    public static Double totalEarning = 0.0;

//    public static void showToast(Activity activity, String textToShow) {
//
//        Toast toast = Toast.makeText(activity.getApplicationContext(), textToShow, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);
//
//        TextView v =  toast.getView().findViewById(android.R.id.message);
//        v.setTextColor(Color.parseColor("#FFFFFF"));
//        v.setTextSize(18);
//        v.setGravity(Gravity.CENTER);
//        v.setTypeface(Typeface.DEFAULT);
//
//        View toastView = toast.getView();
//        toastView.setBackgroundResource(R.drawable.toast_message_style);
//        toast.show();
//    }

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

//    public static Customer getCustomer(Context context) {
//        if (customer == null) {
//            customer = SharedHelper.getKeyCustomer(context, SharePreferenceConstants.customerObj);
//        }
//        return customer;
//    }


//    public static void showAlertDialog(final Activity activity, String title, String textToShow) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(activity, R.style.AlertDialog);
//        dialog.setCancelable(false);
//        if (!title.equals("")) {
//            dialog.setTitle(title);
//        } else {
//            dialog.setTitle(null);
//        }
//        dialog.setMessage(textToShow);
//        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                //Action for "Delete".
//            }
//        });
//
//        final AlertDialog alert = dialog.create();
//        dialog.show();
//    }

//    public static void showRateDialog(Activity activity) {
//        RateToCustomerDialog rateToDriverDialog = new RateToCustomerDialog(activity);
//        rateToDriverDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        rateToDriverDialog.setCanceledOnTouchOutside(true);
//        rateToDriverDialog.show();
//    }

    public static File createImageFile(Activity activity, String imageSuffix, int count) throws IOException {
        // Create an image file name
        Long timestamp = System.currentTimeMillis() / 1000;
        String id = String.valueOf(GlobalVar.GV().EmployID);
        String imageFileName = id + "_" + timestamp.toString() + "_" + imageSuffix + "_" + String.valueOf(count);// +".png";
        SharedHelper.putKeyString(activity.getApplicationContext(), "fileName", imageFileName);
//        String driverId = "1";// SharedHelper.getDriverIDOnly(activity);
//        String imageFileName = driverId;
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = new File(storageDir, imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


//    public static String getMonth(int monthOfYear) {
//        String month = "";
//        switch (monthOfYear) {
//            case 1:
//                month = "January";
//                break;
//            case 2:
//                month = "February";
//                break;
//            case 3:
//                month = "March";
//                break;
//            case 4:
//                month = "April";
//                break;
//            case 5:
//                month = "May";
//                break;
//            case 6:
//                month = "June";
//                break;
//            case 7:
//                month = "July";
//                break;
//            case 8:
//                month = "August";
//                break;
//            case 9:
//                month = "September";
//                break;
//            case 10:
//                month = "October";
//                break;
//            case 11:
//                month = "November";
//                break;
//            case 12:
//                month = "December";
//                break;
//        }
//
//        return month;
//    }

}
