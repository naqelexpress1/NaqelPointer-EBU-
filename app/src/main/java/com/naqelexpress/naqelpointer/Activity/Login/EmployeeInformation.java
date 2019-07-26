package com.naqelexpress.naqelpointer.Activity.Login;

import android.Manifest;
import android.app.Activity;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Results.DefaultResult;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.CloudStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmployeeInformation extends AppCompatActivity {
    private EditText empname, iqamano, mno;

    String filename;
    ImageView profilephoto;
    Button send;
    EditText otpedit, countrycode;
    LinearLayout forotp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employeinfo);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        forotp = (LinearLayout) findViewById(R.id.forvisible);
        empname = (EditText) findViewById(R.id.empname);
        iqamano = (EditText) findViewById(R.id.iqamano);
        otpedit = (EditText) findViewById(R.id.otp);
        mno = (EditText) findViewById(R.id.mno);
        profilephoto = (ImageView) findViewById(R.id.profilephoto);
        countrycode = (EditText) findViewById(R.id.countrycode);
        countrycode.setKeyListener(null);


        new EmployCountryCode().execute();

        profilephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filename = createfilename(0);
                if (filename.length() > 0) {

                    callcameraIntent(filename, 0);
                }
            }
        });
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (send.getText().toString().equals("Send"))
                    isValid();
                else if (send.getText().toString().equals("OTP verify")) {
                    if (otpCode.equals(otpedit.getText().toString())) {
                        addEmployeeintotable();
                    }
                }

            }
        });


    }

    private void fetchEmployCountryCode() {


    }

    private void addEmployeeintotable() {
        DBConnections db = new DBConnections(getApplicationContext(), null);
        boolean insert = db.EmployeInfo(GlobalVar.GV().EmployID, GlobalVar.GV().EmployName, iqamano.getText().toString(), mno.getText().toString(),
                GlobalVar.GV().StationID, filename, getApplicationContext());
        if (insert)
            finish();
        else {
            addEmployeeintotable();
        }
    }


    protected void callcameraIntent(String imagename, int camerareqid) {

        if (ContextCompat.checkSelfPermission(EmployeeInformation.this,
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(EmployeeInformation.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent cameraIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File newfile = new File(GlobalVar.naqelvehicleimagepath);
            File imgfile = new File(GlobalVar.naqelvehicleimagepath + "/" + imagename);
            if (!newfile.exists())
                newfile.mkdirs();

            Uri outputFileUri = null;
            if (Build.VERSION.SDK_INT > 23)
                outputFileUri = FileProvider.getUriForFile(EmployeeInformation.this, getPackageName() + ".fileprovider",
                        imgfile);
            else
                outputFileUri = Uri.fromFile(imgfile);

            // Uri outputFileUri = Uri.fromFile(imgfile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
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
                        profilephoto.setImageBitmap(BitmapFactory.decodeFile(image
                                .getAbsolutePath()));
                        flag_img = true;

                    } else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.prp),
                                GlobalVar.AlertType.Error);

                }
                break;


        }

    }

    public void compressimage(File imageDir, int size) {
        Bitmap bm = null;
        try {
            Uri outputFileUri = Uri.fromFile(imageDir);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inJustDecodeBounds = true;
            if (size == 0)
                options.inSampleSize = 2;
            else
                options.inSampleSize = 2;

            // bm = Media.getBitmap(mContext.getContentResolver(), imageLoc);
            bm = BitmapFactory.decodeStream(EmployeeInformation.this.getContentResolver()
                    .openInputStream(outputFileUri), null, options);
            FileOutputStream out = new FileOutputStream(imageDir);
            bm.compress(Bitmap.CompressFormat.JPEG, 60, out);

            bm.recycle();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String createfilename(int position) {
        String filename = "";

        String id = String.valueOf(GlobalVar.GV().EmployID);

        if (id.length() > 0) {
            filename = id + ".png";
        }
        return filename;
    }

    boolean flag_img = false;

    private void isValid() {

        if (empname.getText().toString().replace(" ", "").length() > 0) {
            if (iqamano.getText().toString().replace(" ", "").length() > 0) {
                if (mno.getText().toString().replace(" ", "").length() > 0) {
                    if (flag_img) {
                        if (!flag_img_upload)
                            new Image1().execute();
                        else
                            EmpInfo();
                    } else
                        GlobalVar.ShowDialog(EmployeeInformation.this, "Info", "Kindly Capture your image", true);
                } else
                    GlobalVar.ShowDialog(EmployeeInformation.this, "Info", "Kindly enter your valid mobile number", true);
            } else
                GlobalVar.ShowDialog(EmployeeInformation.this, "Info", "Kindly enter iqama number", true);
        } else
            GlobalVar.ShowDialog(EmployeeInformation.this, "Info", "Kindly enter your name", true);
    }

    public void EmpInfo() {

        JSONObject jsonObject = new JSONObject();
        JSONObject header = new JSONObject();

        try {
            jsonObject.put("EmpName", empname.getText().toString());
            jsonObject.put("IqamaNumber", iqamano.getText().toString());
            jsonObject.put("MobileNo", countrycode.getText().toString() + mno.getText().toString());
            jsonObject.put("EmployeeID", GlobalVar.GV().EmployID);
            jsonObject.put("StationID", GlobalVar.GV().StationID);
            jsonObject.put("ImageName", filename);

            header.put("EmpInfo", jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new EmployInfo().execute(header.toString());

    }

    ProgressDialog progressDialog;


    private class EmployCountryCode extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(EmployeeInformation.this, "Please wait.", "your request is being process , kindly please wait"
                        , true);
        }

        @Override
        protected String doInBackground(String... params) {

            JSONObject json = new JSONObject();
            try {
                json.put("StationID", GlobalVar.GV().StationID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String jsonData = json.toString();
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "FetchEmployCountryCode");
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
//                int byteCharacters;
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
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            if (finalJson != null) {
                DefaultResult defaultResult = new DefaultResult(finalJson);

                if (!defaultResult.HasError) {

                    String Countrycode = "";
                    try {
                        JSONObject jsonObject = new JSONObject(finalJson);
                        Countrycode = jsonObject.getString("CountryCode");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    countrycode.setText(Countrycode);


                } else {
                    alertDataMessage(defaultResult.ErrorMessage);
                }
            }
            super.onPostExecute(String.valueOf(finalJson));
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void alertDataMessage(String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(EmployeeInformation.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(errorMessage);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new EmployCountryCode().execute();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    private class EmployInfo extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(EmployeeInformation.this, "Please wait.", "your request is being process , kindly please wait"
                        , true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "EmployeeInfo");
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
//                int byteCharacters;
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
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            if (finalJson != null) {
                DefaultResult defaultResult = new DefaultResult(finalJson);

                if (!defaultResult.HasError) {
//                    Intent intent = new Intent(EmployeeInformation.this, MainPageActivity.class);
//                    startActivity(intent);
                    forotp.setVisibility(View.VISIBLE);
                    disableAllColumns();
                    otpCode = defaultResult.ErrorMessage;
                    send.setText("OTP verify");

                } else {
                    alertData(defaultResult.ErrorMessage);
                }
            }
            super.onPostExecute(String.valueOf(finalJson));
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void disableAllColumns() {
        empname.setKeyListener(null);
        iqamano.setKeyListener(null);

    }

    String otpCode = "";

    private void alertData(String errormsg) {
        AlertDialog alertDialog = new AlertDialog.Builder(EmployeeInformation.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(errormsg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        EmpInfo();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void alertImage() {
        AlertDialog alertDialog = new AlertDialog.Builder(EmployeeInformation.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage("your request no sucessfull , please try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new Image1().execute();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    boolean flag_img_upload = false;

    private class Image1 extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(EmployeeInformation.this, "Please wait.", "Uploading Images to Server"
                        , true);

            super.onPreExecute();
        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... params) {

            boolean delete = false;

            try {

                delete = CloudStorage.uploadFile("naqeldocuments", GlobalVar.naqelvehicleimagepath + "/" + filename, getApplicationContext());

            } catch (Exception e2) {
                e2.printStackTrace();
                return "FALSE";

            }

            File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + filename);

            if (!sourceFile.exists()) {
                return "FALSE";

            } else if (sourceFile.exists() && delete) {
                deletefile();
            }


            return "TRUE";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("TRUE")) {

                progressDialog.dismiss();
                progressDialog = null;

                flag_img_upload = true;
                EmpInfo();

            } else {
                progressDialog.dismiss();
                progressDialog = null;
                alertImage();
            }


        }
    }

    private void deletefile() {
        try {
            File deletefile = new File(GlobalVar.naqelvehicleimagepath + "/"
                    + filename);
            deletefile.delete();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");

        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        return;
    }
}