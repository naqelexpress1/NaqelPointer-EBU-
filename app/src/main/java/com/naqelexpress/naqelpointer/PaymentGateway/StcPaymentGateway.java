package com.naqelexpress.naqelpointer.PaymentGateway;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mastercard.mpqr.pushpayment.model.AdditionalData;
import com.mastercard.mpqr.pushpayment.model.MAIData;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class StcPaymentGateway extends AppCompatActivity {

    ImageView qrcode;
    String WaybillNo;
    double codamount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcodegeneration);

        qrcode = (ImageView) findViewById(R.id.qrCode);
        Bundle bundle = getIntent().getExtras();
        WaybillNo = getIntent().getExtras().getString("WaybillNo");
        codamount = getIntent().getExtras().getDouble("COD");

        TextView wno = (TextView) findViewById(R.id.waybillno);
        wno.setText(WaybillNo);

        TextView camt = (TextView) findViewById(R.id.codamount);
        camt.setText(String.valueOf(codamount));

        Button btn = (Button) findViewById(R.id.validatepayment);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("WaybillNo", Integer.parseInt(WaybillNo));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new ValidatePayment().execute(jsonObject.toString());
            }
        });
        generateQRCode();


    }

    public void generateQRCode() {
        //construct the PushPaymentData object
        PushPaymentData pushPaymentData = new PushPaymentData();
        try {

            // Payload format indicator
            pushPaymentData.setPayloadFormatIndicator("01");
            // Point of initiation method
            pushPaymentData.setValue("01", "12");
            //Merchant Identifier data 26-51
            String rootTag = "44";
            MAIData maiData = new MAIData(rootTag);
            //maiData.setAID("AID0349509H");
            maiData.setValue("00", "com.naqelexpres"); // Merchant Unique ID
            maiData.setValue("01", "0106STCPAY");
            maiData.setValue("02", "72960877159"); //MID 61239391702
            maiData.setValue("03", "021161273953454"); // Teller ID by Merchant
            //pushPaymentData.setDynamicMAIDTag(maiData);

            pushPaymentData.setMAIData("44", maiData);
            //Merchant Category Code
            pushPaymentData.setMerchantCategoryCode("5812");
            // Transaction currency code
            pushPaymentData.setTransactionCurrencyCode("682");
            // Transaction amount
            pushPaymentData.setTransactionAmount(codamount);
            //Tip Indicator
            pushPaymentData.setTipOrConvenienceIndicator("01");
            // Country code
            pushPaymentData.setCountryCode("SA");
            // Merchant name
            pushPaymentData.setMerchantName("Naqel Express");
            // Merchant city
            pushPaymentData.setMerchantCity("Riyadh");
            // Additional data
            AdditionalData addData = new AdditionalData();
            addData.setBillNumber("19127"); //Bill Number Optionalò
            addData.setStoreId("19127"); //Merchant Branch ID  Optional
            addData.setReferenceId(WaybillNo); //Ref Number WaybillNo
            addData.setTerminalId("19127"); //Merchant Device ID Optional

            pushPaymentData.setAdditionalData(addData);
        } catch (Exception e) {
            //do something for the exception
        }

        try {
            String qrContent = pushPaymentData.generatePushPaymentString();
            Bitmap bitmap = encodeToQrCode(qrContent);

            qrcode.setImageBitmap(bitmap);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private Bitmap encodeToQrCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix;
        int width = 100;  //get desired QR code width
        int height = 100; //get desired QR code height

        try {
            // width = getResources().getDimensionPixelSize(R.dimen.size_qr_code);  //get desired QR code width
            // height = getResources().getDimensionPixelSize(R.dimen.size_qr_code); //get desired QR code height
            Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");     //for allowing Chinese characters
            matrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);

        } catch (WriterException ex) {
            ex.printStackTrace();
            return null;
        }

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bmp;
    }

    private class ValidatePayment extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            progressDialog = ProgressDialog.show(StcPaymentGateway.this,
                    "Please wait.", "Your Request has been process, kindly be patient  ", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "ValidateSTCPayment");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setConnectTimeout(GlobalVar.GV().ConnandReadtimeout);
                httpURLConnection.setReadTimeout(GlobalVar.GV().ConnandReadtimeout);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                dos = httpURLConnection.getOutputStream();
                httpURLConnection.getOutputStream();
                dos.write(jsonData.getBytes());

                ist = httpURLConnection.getInputStream();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return String.valueOf(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ist != null)
                        ist.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (dos != null)
                        dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            super.onPostExecute(String.valueOf(finalJson));

            if (finalJson != null) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(finalJson);


                    if (jsonObject.getBoolean("HasError")) {

                        new SweetAlertDialog(StcPaymentGateway.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Info")
                                .setContentText(jsonObject.getString("ErrorMessage"))
                                .show();
                    } else {
                        new SweetAlertDialog(StcPaymentGateway.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Info")
                                .setContentText(jsonObject.getString("ErrorMessage"))
                                .show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            } else {
                new SweetAlertDialog(StcPaymentGateway.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Info")
                        .setContentText("something went wrong/check your Internet/server is busy,kindly try again later")
                        .show();
            }
            progressDialog.dismiss();
        }
    }

}
