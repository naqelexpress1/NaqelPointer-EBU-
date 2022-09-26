package com.naqelexpress.naqelpointer.Activity.ScanWaybill;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.utils.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ScanWaybill extends AppCompatActivity implements View.OnClickListener {
    EditText txtWNo, txtWidth, txtHeight, txtLength;
    ImageView image1, image2;
    int flag_insert = 1;
    String filename;
    HashMap<Integer, String> tempimages;
    String imagenames[] = {"Waybill1.png", "Waybill2.png"};

    private void setTxtWaybillNo() {

        String barcode = txtWNo.getText().toString();
        utilities utilities = new utilities();
        String newBarcode = utilities.findwaybillno(barcode);
        if (newBarcode.length() < 9)
            txtWNo.setText(newBarcode);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanwaybill);
        txtWNo = (EditText) findViewById(R.id.txtwaybillno);
        txtWidth = (EditText) findViewById(R.id.txtwidth);
        txtHeight = (EditText) findViewById(R.id.txtheight);
        txtLength = (EditText) findViewById(R.id.txtlength);
        image1 = (ImageView) findViewById(R.id.image1);
        image1.setOnClickListener(this);
        //image2 = (ImageView) findViewById(R.id.image2);
        //image2.setOnClickListener(this);
        tempimages = new HashMap<Integer, String>();

        txtWNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtWNo != null && txtWNo.getText().length()  >= 9)//every making 9 bcz it was reading mentioned number count in some devices
                    setTxtWaybillNo();
            }
        });

//        txtWNo.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // If the event is a key-down event on the "enter" button
//                if (event.getAction() != KeyEvent.ACTION_DOWN)
//                    return true;
//                else if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    onBackPressed();
//                    return true;
//                } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    if (txtWNo != null && txtWNo.getText().toString().length() >= 8)
////                            setTxtWaybillNo(txtWaybillNo.getText().toString());
//                        setTxtWaybillNo();
//                    return true;
//                }
//                return false;
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1:
                if (flag_insert != 0) {
                    filename = createfilename(0);
                    if (filename.length() > 0) {
                        tempimages.put(0, filename);
                        callcameraIntent(filename, 0);
                    }
                } else

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly capture Waybill ", GlobalVar.AlertType.Warning);
                break;
            case R.id.image2:
                if (flag_insert != 0) {
                    filename = createfilename(1);
                    if (filename.length() > 0) {
                        tempimages.put(1, filename);
                        callcameraIntent(filename, 1);
                    }
                } else

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly capture Waybill", GlobalVar.AlertType.Warning);
                break;

        }
    }

    protected void callcameraIntent(String imagename, int camerareqid) {

        if (ContextCompat.checkSelfPermission(ScanWaybill.this,
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ScanWaybill.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent cameraIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File newfile = new File(GlobalVar.naqelvehicleimagepath);
            File imgfile = new File(GlobalVar.naqelvehicleimagepath + "/" + imagename);
            if (!newfile.exists())
                newfile.mkdirs();

            Uri outputFileUri = null;
            if (Build.VERSION.SDK_INT > 23)
                outputFileUri = FileProvider.getUriForFile(ScanWaybill.this, getPackageName() + ".fileprovider",
                        imgfile);
            else
                outputFileUri = Uri.fromFile(imgfile);

            // Uri outputFileUri = Uri.fromFile(imgfile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, camerareqid);

        } else {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                    GlobalVar.AlertType.Error);
            try {
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(),
                        null);
                intent.setData(uri);
                startActivity(intent);
            } catch (Exception e) {

            }
        }
    }

    private String createfilename(int position) {
        Long timpstamp = System.currentTimeMillis() / 1000;
        String filename = "";

        String id = String.valueOf(GlobalVar.GV().EmployID);

        if (id.length() > 0) {
            filename = "ScanWaybill_"+id + "_" + timpstamp.toString() + "_" + imagenames[position];
        }
        return filename;
    }

    public void onSubmit(View v) {
        System.out.println("Sathish");
        String Waybillno = txtWNo.getText().toString();
        if(Waybillno==null) {
            GlobalVar.ShowDialog(ScanWaybill.this, "Info", "Please enter a waybill", true);
        }
        else if (Waybillno.length()<8) {
            GlobalVar.ShowDialog(ScanWaybill.this, "Info", "Please enter a Valid Waybill", true);
        }
        else if(tempimages.size()==0)
        {
            GlobalVar.ShowDialog(ScanWaybill.this, "Info", "Please Capture the WaybillNo", true);
        }else
        {
            new Image1().execute(createfilename(0));
        }
    }

    ProgressDialog pDialog;

    private class InsertIncident extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;


        @Override
        protected void onPreExecute() {

            // if (progressDialog == null)

            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink_UploadImage + "InsertScanWaybill");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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
            } catch (Exception ignored) {
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
            }
            return null;
//


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {

                        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
//                        GlobalVar.ShowDialog(Incident.this, "Info", "Your Request Sucessfully Inserted", true);
                        SucessfullyInsert();
                    } else {
                        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
                        GlobalVar.ShowDialog(ScanWaybill.this, "Error", jsonObject.getString("ErrorMessage"), true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                GlobalVar.ShowDialog(ScanWaybill.this, "Error", "Your Request Not Insert, please try again later", true);
            }
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog = null;
            }

            File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + filename);

            if (sourceFile.exists() ) {
                deletefile(filename);
            }

        }
    }

    private void SucessfullyInsert() {
        AlertDialog alertDialog = new AlertDialog.Builder(ScanWaybill.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage("your request sucessfully inserted");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        tempimages.clear();

                        image1.setBackgroundResource(R.drawable.capture);
                        dialog.dismiss();
                        finish();
                    }
                });
//        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "no",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                });
        alertDialog.show();
    }



    private void insertDatatoServer(String imagename) {
        JSONObject jsonObject = new JSONObject();
        JSONObject incident = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            incident.put("Requestby", GlobalVar.GV().EmployID);
            incident.put("image1", imagename);
            incident.put("WaybillNo", txtWNo.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new InsertIncident().execute(incident.toString());
    }

    private class Image1 extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        int position = 0;

        @Override
        protected void onPreExecute() {

            pDialog = ProgressDialog.show(ScanWaybill.this,
                    "Please wait.", "your Waybill Request is being process.", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {



            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("ImageName", filename);
                jsonObject.put("FileName", convertimage(filename));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String jsonData = jsonObject.toString();
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink_UploadImage + "uploadImage");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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
            } catch (Exception ignored) {
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
            }
            return null;
//


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {

                try {
                    boolean delete = false;

                    if (result.contains("Created Successfully")) {
                        insertDatatoServer(filename);


                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                GlobalVar.ShowDialog(ScanWaybill.this,"Error" , "Please try after sometime" , true);
                if(pDialog !=null)
                {
                    pDialog.dismiss();
                    pDialog = null;
                }
            }

        }
    }

    public void compressimage(File imageDir, int size) {
        Bitmap bm = null;
        try {
            Uri outputFileUri = Uri.fromFile(imageDir);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inJustDecodeBounds = true;

                options.inSampleSize = 2;


            // bm = Media.getBitmap(mContext.getContentResolver(), imageLoc);
            bm = BitmapFactory.decodeStream(this.getContentResolver()
                    .openInputStream(outputFileUri), null, options);
            FileOutputStream out = new FileOutputStream(imageDir);
            bm.compress(Bitmap.CompressFormat.JPEG, 60, out);

            bm.recycle();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String convertimage( String imname) {
        String image = "";
        String imagename = GlobalVar.naqelvehicleimagepath + "/" + imname;

        File imgfile = new File(imagename);


        Uri outputFileUri = null;
        if (Build.VERSION.SDK_INT > 23)
            outputFileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider",
                    imgfile);
        else
            outputFileUri = Uri.fromFile(imgfile);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
            Bitmap lastBitmap = null;
            lastBitmap = bitmap;
            //encoding image to string
            image = getStringImage(lastBitmap);
            Log.d("image", image);
            //passing the image to volley


        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    private void deletefile(String imagename) {
        try {
            File deletefile = new File(GlobalVar.naqelvehicleimagepath + "/"
                    + imagename);
            deletefile.delete();
        } catch (Exception e) {
            System.out.println(e);
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 0:

                if (requestCode == 0
                        && resultCode == Activity.RESULT_OK) {

                    File image = new File(GlobalVar.naqelvehicleimagepath + "/"
                            + filename);

                    int file_size = Integer
                            .parseInt(String.valueOf(image.length() / 1024));
                    if (file_size > 0) {
                        boolean ci = true;
                        while (ci) {
                            long kb = image.length() / 1024;
                            if (kb > 300)
                                compressimage(image, 0);
                            else
                                ci = false;
                        }
                        image1.setImageBitmap(BitmapFactory.decodeFile(image
                                .getAbsolutePath()));



                    } else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.prp),
                                GlobalVar.AlertType.Error);

                }
                break;

        }

    }

}
