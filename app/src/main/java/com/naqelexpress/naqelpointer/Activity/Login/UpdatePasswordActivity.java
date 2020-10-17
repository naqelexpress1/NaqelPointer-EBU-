package com.naqelexpress.naqelpointer.Activity.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.UpdateUserPwdRequest;
import com.naqelexpress.naqelpointer.JSON.Results.UpdateUserPwdResult;
import com.naqelexpress.naqelpointer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdatePasswordActivity extends AppCompatActivity {

    EditText etPassword , etConfirmPassword;
    Button btnUpdatePwd;

    int employeeID = -2;
    ProgressDialog progressDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        context = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            employeeID = extras.getInt("emp_id");
        }

        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnUpdatePwd = findViewById(R.id.btnUpdatePassword);

        btnUpdatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (GlobalVar.isEmpty(etPassword) || GlobalVar.isEmpty(etConfirmPassword)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(),"Kindly fill all fields" , GlobalVar.AlertType.Error);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(),"Password is not matching" , GlobalVar.AlertType.Error);
                    return;
                }

                UpdateUserPwdRequest updateUserPwdRequest = new UpdateUserPwdRequest();
                updateUserPwdRequest.EmployID = employeeID;
                updateUserPwdRequest.Password = password;
                updateUserPwdRequest.IsDefaultUpdate = true;
                String jsonData = JsonSerializerDeserializer.serialize(updateUserPwdRequest, true);
                new updateUserPwd().execute(jsonData);
            }
        });
    }


    private class updateUserPwd extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        String DomainURL = "";
        String isInternetAvailable = "";


        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(UpdatePasswordActivity.this);
            progressDialog.setMessage("Updating password , Please wait ");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());

        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;


            try {

                URL url = new URL(DomainURL + "UpdateUserPwd");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                try {
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                    httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();
                } catch (java.net.NoRouteToHostException se) {
                    System.out.println(se);
                }
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
                isInternetAvailable = ignored.toString();
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

                UpdateUserPwdResult updateUserPwdResult = new UpdateUserPwdResult(finalJson);
                if (!updateUserPwdResult.HasError) {
                    Toast.makeText(context , "Password updated successfully" , Toast.LENGTH_LONG).show();
                    Intent i = new Intent(UpdatePasswordActivity.this , LoginActivity.class);
                    startActivity(i);
                    finishAffinity();
                } else
                    etPassword.getText().clear();
                etConfirmPassword.getText().clear();
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), updateUserPwdResult.ErrorMessage, GlobalVar.AlertType.Error);
            } else {
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.servererror), GlobalVar.AlertType.Error);
                }
            }

            dismissProgressdialog();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }

    private void dismissProgressdialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}