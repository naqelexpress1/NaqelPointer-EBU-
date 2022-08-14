package com.naqelexpress.naqelpointer.Activity.Login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Results.DefaultResult;
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

public class ChangePassword extends AppCompatActivity {
    private EditText txtEmployID, txtpassword, txtmobileno, txtotpno;
    Button btnGetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpasswordnew);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        txtEmployID = (EditText) findViewById(R.id.txtEmployID);
        txtpassword = (EditText) findViewById(R.id.paswd);
        txtmobileno = (EditText) findViewById(R.id.mobileno);
        txtotpno = (EditText) findViewById(R.id.otp);
        btnGetPassword = (Button) findViewById(R.id.btnGetPassword);
        btnGetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnGetPassword.getText().toString().equals("Verify Mobile No"))
                    GetPassword();
                else
                    UpdatePassword();
            }
        });

        txtEmployID.setText(GlobalVar.GV().EmployID);
    }

    public void GetPassword() {
        if (txtEmployID.getText().toString().equals("")) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.EnterEmployID), GlobalVar.AlertType.Error);
            return;
        } else if (txtpassword.getText().toString().replace(" ", "").length() == 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly Enter Valid Password",
                    GlobalVar.AlertType.Error);
            return;
        } else if (txtmobileno.getText().toString().length() == 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kidnly enter valid Mobile Number",
                    GlobalVar.AlertType.Error);
            return;
        } else if (txtmobileno.getText().toString().length() <= 8) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kidnly enter valid Mobile Number",
                    GlobalVar.AlertType.Error);
            return;
        } else

            GetPassword(Integer.parseInt(txtEmployID.getText().toString()));
    }

    public void UpdatePassword() {
        if (txtEmployID.getText().toString().equals("")) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.EnterEmployID), GlobalVar.AlertType.Error);
            return;
        } else if (txtpassword.getText().toString().replace(" ", "").length() == 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly Enter Valid Password",
                    GlobalVar.AlertType.Error);
            return;
        } else if (txtmobileno.getText().toString().length() == 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kidnly enter valid Mobile Number",
                    GlobalVar.AlertType.Error);
            return;
        } else if (txtmobileno.getText().toString().length() <= 8) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kidnly enter valid Mobile Number",
                    GlobalVar.AlertType.Error);
            return;
        } else if (txtotpno.getText().toString().length() == 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kidnly enter valid Otp Number",
                    GlobalVar.AlertType.Error);
            return;
        } else

            UpdatePassword(Integer.parseInt(txtEmployID.getText().toString()));
    }

    //-------------Get Password ------------------------------------------
    public void GetPassword(int EmployID) {

        JSONObject jsonObject = new JSONObject();
        JSONObject header = new JSONObject();

        try {

            jsonObject.put("MobileNo", "966" + txtmobileno.getText().toString().substring(txtmobileno.getText().toString().length() - 9,
                    txtmobileno.getText().toString().length()));
            jsonObject.put("EmployeeID", EmployID);
            header.put("EmpInfo", jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetPasswordOTPFromServer().execute(header.toString());

    }

    public void UpdatePassword(int EmployID) {

        JSONObject jsonObject = new JSONObject();
        JSONObject header = new JSONObject();

        try {

            jsonObject.put("MobileNo", txtmobileno.getText().toString().substring(txtmobileno.getText().toString().length() - 9,
                    txtmobileno.getText().toString().length()));
            jsonObject.put("EmployeeID", EmployID);

            header.put("EmpInfo", jsonObject);
            header.put("OtpNo", Integer.parseInt(txtotpno.getText().toString()));
            header.put("password", txtpassword.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetPasswordFromServer().execute(header.toString());

    }

    private class GetPasswordOTPFromServer extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ChangePassword.this, "Please wait.",
                    "verify entered mobile no "
                    , true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "VerifyMobileNoForPassowrd");
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
                if (defaultResult.HasError) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ChangePassword.this).create();
                    alertDialog.setTitle(getResources().getString(R.string.app_name));
                    alertDialog.setMessage(defaultResult.ErrorMessage);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    txtotpno.setVisibility(View.VISIBLE);
                    btnGetPassword.setText("Update Password");
                }
            }
            super.onPostExecute(String.valueOf(finalJson));
            progressDialog.dismiss();
        }
    }

    private class GetPasswordFromServer extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ChangePassword.this, "Please wait.",
                    "Loading your request"
                    , true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "UpdatePassowrdbyOtp");
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

                final DefaultResult defaultResult = new DefaultResult(finalJson);

                AlertDialog alertDialog = new AlertDialog.Builder(ChangePassword.this).create();
                alertDialog.setTitle(getResources().getString(R.string.app_name));
                alertDialog.setMessage(defaultResult.ErrorMessage);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (!defaultResult.HasError)
                                    finish();
                            }
                        });
                alertDialog.show();

            }
            super.onPostExecute(String.valueOf(finalJson));
            progressDialog.dismiss();
        }
    }
}