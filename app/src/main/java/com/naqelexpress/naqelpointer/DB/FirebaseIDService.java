package com.naqelexpress.naqelpointer.DB;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.naqelexpress.naqelpointer.service.DeviceToken;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;


public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // Log.d(TAG, "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }


    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // FirebaseHandler.DeviceID=token;
        // FirebaseHandler.getInstance().Initialize(token);
        // Add custom implementation, as needed.

        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //preferences.edit().putString(SyncStateContract.Constants.FIREBASE_TOKEN, token).apply();

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//        Cursor result = dbConnections.Fill("select * from UserMeLogin where LogedOut is NULL or LogedOut = 0", getApplicationContext());
//        result.close();
        

        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("EmployID", dbConnections.getEmpId(getApplicationContext()));
            jsonObject.put("DeviceToken", token);
            jsonObject.put("ChangedDate", DateTime.now().toString());


            dbConnections.InsertDevieToken(jsonObject.toString(), getApplicationContext());
            dbConnections.close();

            stopService(new Intent(this, DeviceToken.class));
            startService(new Intent(this, DeviceToken.class));
//            Toast.makeText(getApplicationContext(), "token changed", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            // Toast.makeText(getApplicationContext(), "Exception token changed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}