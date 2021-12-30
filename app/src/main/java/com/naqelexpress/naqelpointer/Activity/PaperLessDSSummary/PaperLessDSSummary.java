package com.naqelexpress.naqelpointer.Activity.PaperLessDSSummary;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Hasna on 11/11/18.
 */

public class PaperLessDSSummary extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dssummery);

        final ImageView barcode_img = (ImageView) findViewById(R.id.barcode);

        TextView dsID = (TextView) findViewById(R.id.dsID);
        TextView courierID = (TextView) findViewById(R.id.courierID);
        TextView iqamaid = (TextView) findViewById(R.id.iqamaid);
        TextView phoneno = (TextView) findViewById(R.id.phoneno);
        TextView routeno = (TextView) findViewById(R.id.routeno);
        TextView plateno = (TextView) findViewById(R.id.plateno);
        TextView fleetNo = (TextView) findViewById(R.id.fleetNo);
        TextView date = (TextView) findViewById(R.id.date);
        TextView kmout = (TextView) findViewById(R.id.kmout);
        TextView posunit = (TextView) findViewById(R.id.posunit);
        TextView totalpieces = (TextView) findViewById(R.id.totalpieces);
        TextView totalwaybills = (TextView) findViewById(R.id.totalwaybills);
        TextView totalcodamount = (TextView) findViewById(R.id.totalcodamount);
        TextView totalcdamount = (TextView) findViewById(R.id.totalcdamount);
        TextView totaldeliveredwbs = (TextView) findViewById(R.id.totaldeliveredwbs);
        TextView totalundeliveredwbs = (TextView) findViewById(R.id.totalundeliveredwbs);


        final HashMap<String, String> hashMap = GlobalVar.getDSSummaryData(getApplicationContext());
        if (Objects.equals(hashMap.get("isDone"), "1")) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.encodeBitmap(hashMap.get("DeliverySheetID"), BarcodeFormat.CODE_128, barcode_img.getWidth(), barcode_img.getHeight());

                        barcode_img.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }, 1000);


            dsID.setText(hashMap.get("DeliverySheetID"));
            courierID.setText(hashMap.get("EmployName"));
            iqamaid.setText(hashMap.get("IqamaNo"));
            phoneno.setText(hashMap.get("MobileNo"));
            routeno.setText(hashMap.get("RouteName"));
            plateno.setText(hashMap.get("PlateNumber"));
            fleetNo.setText(hashMap.get("TruckName"));
            date.setText(hashMap.get("DsDate"));
            kmout.setText(hashMap.get("KMOUT"));
            posunit.setText(hashMap.get("POSName"));
            totalpieces.setText(hashMap.get("BarCodeCount"));
            totalwaybills.setText(hashMap.get("WBCount"));
            totalcodamount.setText(hashMap.get("tCoD"));
            totalcdamount.setText(hashMap.get("tCDAmount"));
            totaldeliveredwbs.setText(hashMap.get("DWCount"));
            totalundeliveredwbs.setText(hashMap.get("NTWCount"));

        }
    }

}
