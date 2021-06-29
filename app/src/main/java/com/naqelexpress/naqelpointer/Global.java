package com.naqelexpress.naqelpointer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.naqelexpress.naqelpointer.DB.DBConnections;

import java.util.ArrayList;

public class Global {

    Activity activity;
//    static Activity staticactivity;

    public Global(Activity activity) {
        this.activity = activity;
    }

    public void addMobileNumberintoContacts(String ConsigneeName, ArrayList<String> ConsigneeMobile, String waybillno) {


        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactID = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, ConsigneeName)
                .build());


        String mno = "";
        for (String MNo : ConsigneeMobile) {
            mno = MNo;
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MNo)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }
        try {

            activity.getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            DBConnections dbConnections = new DBConnections(activity.getApplicationContext(), null);
            dbConnections.InsertPickupSheetMnos(ConsigneeName, mno, Integer.parseInt(waybillno), activity.getApplicationContext());
            dbConnections.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } finally {
            activity = null;
        }
    }


  /*  public void DeleteContact() {
        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(activity,
                "Info",
                "Your Request is being process,kindly please wait");


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // new DownloadJSON().execute();

                try {

//                    try {
                    DBConnections dbConnections = new DBConnections(activity.getApplicationContext(), null);
                    boolean loop = false;
                    loop = GlobalVar.deleteContactRawID(dbConnections.PickupSheetContactDetails
                            (activity.getApplicationContext()), activity.getApplicationContext(), 1);

//                    int time = 1000;
//                    while (!loop)
//                        Thread.sleep(time);
                    while (loop)
                        handler.postDelayed(this, 1000);


//                    } catch (InterruptedException e) {
//                        e.printStackTrace();


//                    handler.postDelayed(this, 20000);
                } catch (Exception e) {

//                    handler.postDelayed(this, 20000);
                    Log.e("Dashboard thread", e.toString());
                } finally {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                    activity.finish();
                }

            }
        }, 0);

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        activity.finish();
    }*/


    public void DeleteContact() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                DBConnections dbConnections = new DBConnections(activity.getApplicationContext(), null);

                GlobalVar.deleteContactRawID(dbConnections.PickupSheetContactDetails
                        (activity.getApplicationContext()), activity.getApplicationContext(), 1);

                dbConnections.close();
            }
        }, 10);

    }


    public class DeleteContact extends AsyncTask<String, String, String> {


        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {

            try {
                DBConnections dbConnections = new DBConnections(activity.getApplicationContext(), null);
                boolean loop = false;
                loop = GlobalVar.deleteContactRawID(dbConnections.ContactDetails(activity.getApplicationContext()), activity.getApplicationContext(), 1);
                int time = 1000;
                while (!loop)
                    Thread.sleep(time);


            } catch (InterruptedException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            activity.finish();
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(activity,
                    "Info",
                    "Your Request is being process,kindly please wait");
        }


        @Override
        protected void onProgressUpdate(String... text) {


        }

    }

  /*  public void asd() {

        new AsyncTask<Void, Void, Void>() {


            ProgressDialog progressDialog;

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    DBConnections dbConnections = new DBConnections(activity.getApplicationContext(), null);
                    boolean loop = false;
                    loop = GlobalVar.deleteContactRawID(dbConnections.ContactDetails(activity.getApplicationContext()), activity.getApplicationContext(), 1);
                    int time = 1000;
                    while (!loop)
                        Thread.sleep(time);


                } catch (InterruptedException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();

                }
                return null;
            }


//            @Override
//            protected void onPostExecute(Void result) {
//                if (progressDialog != null && progressDialog.isShowing())
//                    progressDialog.dismiss();
//                activity.finish();
//            }


            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(activity,
                        "Info",
                        "Your Request is being process,kindly please wait");
            }


//            @Override
//            protected void onProgressUpdate (String...text){
//
//
//            }

        }.execute();
    }*/
}
