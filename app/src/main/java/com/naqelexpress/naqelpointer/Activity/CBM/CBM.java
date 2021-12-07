package com.naqelexpress.naqelpointer.Activity.CBM;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.CommonResult;
import com.naqelexpress.naqelpointer.Models.Enum.Enum;
import com.naqelexpress.naqelpointer.Models.Request.CBMRequest;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.callback.AlertCallback;
import com.naqelexpress.naqelpointer.callback.Callback;
import com.naqelexpress.naqelpointer.utils.CommonApi;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Hasna on 11/11/18.
 */

public class CBM extends AppCompatActivity implements AlertCallback {


    EditText txtWNo, txtWidth, txtHeight, txtLength;
    static SweetAlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cbmexp);

        txtWNo = (EditText) findViewById(R.id.txtwaybillno);
        txtWidth = (EditText) findViewById(R.id.txtwidth);
        txtHeight = (EditText) findViewById(R.id.txtheight);
        txtLength = (EditText) findViewById(R.id.txtlength);


    }


    public void onSubmit(View v) {

        CBMRequest cbmRequest = isValid();
        if (!cbmRequest.getisValid())
            return;

        GlobalVar.GV().alertMsgAll("Info", "Please wait...", CBM.this,
                Enum.PROGRESS_TYPE, "CBM");


        CommonApi.submitCBM(new Callback<CommonResult>() {
            @Override
            public void returnResult(CommonResult result) {
                System.out.println();

                String msg = result.getErrorMessage();
                if (result.getHasError())
                    GlobalVar.GV().alertMsgAll("Info", msg, CBM.this,
                            Enum.NORMAL_TYPE, "CBM");
                else
                    GlobalVar.GV().alertMsgAll("Info", msg, CBM.this,
                            Enum.SUCCESS_TYPE, "CBM");
                exitdialog();

            }

            @Override
            public void returnError(String message) {
                //mView.showError(message);
                GlobalVar.GV().alertMsgAll("Info", "Something went wrong , please try again", CBM.this,
                        Enum.NORMAL_TYPE, "CBM");
                System.out.println(message);
                exitdialog();
            }
        }, cbmRequest);
    }

    private CBMRequest isValid() {
        String txtheight = txtHeight.getText().toString();
        String txtlength = txtLength.getText().toString();
        String txtwidth = txtWidth.getText().toString();
        String wNo = txtWNo.getText().toString();
        CBMRequest cbmRequest = new CBMRequest();
        cbmRequest.setisValid(false);
        if (wNo.length() > 0 && Double.parseDouble(wNo) > 0)
            if (txtheight.length() > 0 && Double.parseDouble(txtheight) > 0)
                if (txtlength.length() > 0 && Double.parseDouble(txtlength) > 0)
                    if (txtwidth.length() > 0 && Double.parseDouble(txtwidth) > 0) {
                        cbmRequest.setEmployID(GlobalVar.GetEmployID(getApplicationContext()));
                        cbmRequest.setWaybillNo(Integer.parseInt(wNo));
                        cbmRequest.setHeight(Double.parseDouble(txtheight));
                        cbmRequest.setLength(Double.parseDouble(txtlength));
                        cbmRequest.setWidth(Double.parseDouble(txtwidth));
                        cbmRequest.setisValid(true);
                    } else
                        GlobalVar.ShowDialog(CBM.this, "Error", "Please enter Valid Width", true);
                else
                    GlobalVar.ShowDialog(CBM.this, "Error", "Please enter Valid Length", true);
            else
                GlobalVar.ShowDialog(CBM.this, "Error", "Please enter Valid Height", true);


        return cbmRequest;
    }

    private void setRequest() {

    }

    private void exitdialog() {
        if (alertDialog != null) {
            alertDialog.dismissWithAnimation();
            alertDialog = null;
        }
    }

    public void conflict(String Barcode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CBM.this);
        builder.setTitle("Warning " + Barcode)
                .setMessage("This Piece is not belongs to this Employee")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void returnOk(final int value, final Activity activity) {

        this.runOnUiThread(new Runnable() {
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        exitdialog();
                        if (Enum.SUCCESS_TYPE.getValue() == value)
                            activity.finish();

                    }
                });

            }
        });
    }

    @Override
    public void returnCancel(int cancel, SweetAlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }
}
