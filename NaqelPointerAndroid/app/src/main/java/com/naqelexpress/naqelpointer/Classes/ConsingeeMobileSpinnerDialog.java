package com.naqelexpress.naqelpointer.Classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

/**
 * Created by sofan on 12/03/2018.
 */

public class ConsingeeMobileSpinnerDialog {
    private Activity context;
    private AlertDialog alertDialog;
    private String FirstPhoneNo = "0";
    private String SecondPhoneNo = "0";
    private View view;


    public ConsingeeMobileSpinnerDialog(Activity activity, String firstPhoneNo, String secondPhoneNo, View view) {
        this.FirstPhoneNo = firstPhoneNo;
        this.SecondPhoneNo = secondPhoneNo;
        this.context = activity;
        this.view = view;
    }

    public void showSpinerDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        View v = context.getLayoutInflater().inflate(R.layout.consigneemobilespinnerdialog, null);

        final TextView txtFirstPhone = (TextView) v.findViewById(R.id.txtFirstPhone);
        final TextView txtSecondPhone = (TextView) v.findViewById(R.id.txtSecondPhone);

        txtFirstPhone.setText(FirstPhoneNo);
        txtSecondPhone.setText(SecondPhoneNo);

        Button btnCallFirst = (Button) v.findViewById(R.id.btnCallFirst);
        Button btnCallSecond = (Button) v.findViewById(R.id.btnCallSecond);
        //Button btnClose = (Button) v.findViewById(R.id.btnClose);

        btnCallFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVar.GV().makeCall(String.valueOf(txtFirstPhone.getText()), view, context);
            }
        });

        btnCallSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVar.GV().makeCall(String.valueOf(txtSecondPhone.getText()), view, context);
            }
        });

        //btnClose.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                alertDialog.dismiss();
//            }
//        });

        adb.setView(v);
        alertDialog = adb.create();
        //alertDialog.getWindow().getAttributes().windowAnimations = style;//R.style.DialogAnimations_SmileWindow;
        alertDialog.setCancelable(true);

        alertDialog.show();
    }
}
