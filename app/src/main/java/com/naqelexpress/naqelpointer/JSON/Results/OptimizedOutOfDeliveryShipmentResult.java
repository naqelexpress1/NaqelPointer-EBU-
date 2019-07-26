package com.naqelexpress.naqelpointer.JSON.Results;

import android.view.View;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sofan on 09/10/2017.
 */

public class OptimizedOutOfDeliveryShipmentResult
    extends DefaultResult
{
    public String WaybillNo;

    private OptimizedOutOfDeliveryShipmentResult ()
    {

    }

    public OptimizedOutOfDeliveryShipmentResult(String finalJson,View view)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(finalJson);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                OptimizedOutOfDeliveryShipmentResult instance = new OptimizedOutOfDeliveryShipmentResult();
                try
                {
                    instance.WaybillNo = jsonObject.getString("WaybillNo");
                }
                catch (JSONException ignored){}
                GlobalVar.GV().optimizedOutOfDeliveryShipmentList.add(instance.WaybillNo);
            }
            GlobalVar.GV().ShowSnackbar(view,"Total Shipments = " + GlobalVar.GV().optimizedOutOfDeliveryShipmentList.size() , GlobalVar.AlertType.Info);
        }
        catch (JSONException ignored){}
    }
}