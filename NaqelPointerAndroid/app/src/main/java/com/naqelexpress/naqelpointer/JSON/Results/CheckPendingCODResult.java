package com.naqelexpress.naqelpointer.JSON.Results;

import android.os.Parcel;
import android.os.Parcelable;

import com.naqelexpress.naqelpointer.Activity.PendingMoney.PendingMoneyActivity;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sofan on 07/10/2017.
 */

public class CheckPendingCODResult implements Parcelable {
    public int WaybillNo;
    public DateTime DeliveryDate;
    public double Amount;

    CheckPendingCODResult() {

    }

    public CheckPendingCODResult(String finalJson) {
        try {
            JSONArray jsonArray = new JSONArray(finalJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CheckPendingCODResult instance = new CheckPendingCODResult();
                try {
                    instance.WaybillNo = Integer.parseInt(jsonObject.getString("WaybillNo"));
                    String dt = jsonObject.getString("DeliveryDate");
                    String result = dt.replaceAll("^/Date\\(", "");
                    instance.DeliveryDate = new DateTime(result); //Long.parseLong(result.substring(0, result.indexOf('T'))));
                    instance.Amount = Double.parseDouble(jsonObject.getString("Amount"));
                } catch (JSONException ignored) {
                }
                PendingMoneyActivity.checkPendingCODList.add(instance);
            }
        } catch (JSONException ignored) {
        }
    }

    protected CheckPendingCODResult(Parcel in) {
        WaybillNo = in.readInt();
        DeliveryDate = (DateTime) in.readSerializable();
        Amount = in.readDouble();
    }

    public static final Creator<CheckPendingCODResult> CREATOR = new Creator<CheckPendingCODResult>() {
        @Override
        public CheckPendingCODResult createFromParcel(Parcel in) {
            return new CheckPendingCODResult(in);
        }

        @Override
        public CheckPendingCODResult[] newArray(int size) {
            return new CheckPendingCODResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(WaybillNo);
        parcel.writeSerializable(DeliveryDate);
        parcel.writeDouble(Amount);
    }
}