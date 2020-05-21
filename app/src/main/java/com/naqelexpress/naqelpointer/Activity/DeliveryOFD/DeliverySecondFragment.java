package com.naqelexpress.naqelpointer.Activity.DeliveryOFD;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.Activity.DigitalSign.Signature;
import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

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

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;

public class DeliverySecondFragment extends Fragment implements TextWatcher {

    View rootView;
    EditText txtPOS;
    EditText txtCash;
    TextView lbTotal;
    public EditText txtReceiverName, txtotpno;

    //Added by Ismail
    Button btn_get_sign, mClear, mGetSign, mCancel;

    File file;
    Dialog dialog;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap;

    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/NaqelSignature/";
    //String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = "";
    int signmand = 0;
    boolean Isnootp = false;
    CheckBox otpcheckbox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.deliverysecondfragment, container, false);

            txtotpno = (EditText) rootView.findViewById(R.id.otpno);

            txtPOS = (EditText) rootView.findViewById(R.id.txtPOSAmount);
            txtCash = (EditText) rootView.findViewById(R.id.txtCashAmount);
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);
            txtReceiverName = (EditText) rootView.findViewById(R.id.txtCheckPointType);

            txtPOS.addTextChangedListener(this);
            if (DeliveryFirstFragment.IsCODtextboxEnable == 1) {
               txtCash.setKeyListener(null);
            }
            txtCash.addTextChangedListener(this);

            Button validatepayment = (Button) rootView.findViewById(R.id.validatepayament);
            validatepayment.setVisibility(View.VISIBLE);
            validatepayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupvalidatepayments();
                }
            });
            if (GlobalVar.GV().isneedOtp)
                txtotpno.setVisibility(View.VISIBLE);

            txtotpno.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (s.length() == 5) {
                        DBConnections dbConnections = new DBConnections(getContext(), null);
                        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = '" + DeliveryFirstFragment.txtWaybillNo.getText().toString() + "'",
                                getContext());

                        if (result.getCount() > 0) {
                            result.moveToFirst();
                            int otpno = result.getInt(result.getColumnIndex("OTPNo"));
                            String cnname = result.getString(result.getColumnIndex("ConsigneeName"));
                            if (otpno != Integer.parseInt(txtotpno.getText().toString())) {
                                GlobalVar.GV().ShowSnackbar(rootView, "Entered OTPNo is wrong , kindly contact Supervisor", GlobalVar.AlertType.Error);


                            } else
                                txtReceiverName.setText(cnname);
                        }

                    }
                }
            });

            otpcheckbox = (CheckBox) rootView.findViewById(R.id.nootp);

            otpcheckbox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //is chkIos checked?
                    if (((CheckBox) v).isChecked()) {
                        showPopupNoOTP();
                        Isnootp = true;
                    } else {
                        Isnootp = false;
                        popup.dismiss();
                    }

                }
            });

            boolean pos = GlobalVar.getPOS(getContext());
            TextView tv = (TextView) rootView.findViewById(R.id.nopos);
            if (!pos) {
                tv.setVisibility(View.VISIBLE);

                txtPOS.setKeyListener(null);
            } else
                tv.setVisibility(View.GONE);

            signmand = 0;


            Button btn_get_sign = (Button) rootView.findViewById(R.id.signature);

            btn_get_sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DeliveryFirstFragment firstFragment = new DeliveryFirstFragment();
                    String waybill = DeliveryFirstFragment.txtWaybillNo.getText().toString().replace(" ", "");

                    StoredPath = DIRECTORY + waybill + ".png";

                    if (waybill.length() > 0) {
                        Intent in = new Intent(getActivity(), Signature.class);
                        in.putExtra("path", DIRECTORY);
                        in.putExtra("imagename", waybill + ".png");
                        startActivityForResult(in, 1);
                    } else
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.validwaybill), GlobalVar.AlertType.Warning);


                }
            });

            file = new File(DIRECTORY);
            if (!file.exists()) {
                file.mkdirs();
            }

            // Dialog Function
            dialog = new Dialog(getActivity());
            // Removing the features of Normal Dialogs

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_signature);
            dialog.setCancelable(true);


        }
        return rootView;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialog_action();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    boolean deniedPermission = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[0]);
                    if (!deniedPermission) {
                        try {
                            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                            startActivity(i);
                        } catch (Exception e) {
                            GlobalVar.ShowDialog(getActivity(), "STORAGE Permission necessary", "Kindly please contact our Admin", true);
                        }
                    }
                }
                return;
            }

        }
    }

    // Function for Digital Signature
    public void dialog_action() {

        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getActivity().getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) dialog.findViewById(R.id.clear);
        mGetSign = (Button) dialog.findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                DBConnections dbh = new DBConnections(getActivity().getApplicationContext(), null);
                boolean validate = dbh.inserSignaturetData(StoredPath, String.valueOf(GlobalVar.GV().EmployID), getContext());

                if (validate) {
                    //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    Log.v("log_tag", "Panel Saved");
                    view.setDrawingCacheEnabled(true);
                    mSignature.save(view, StoredPath);
                    dialog.dismiss();

                    Toast.makeText(getActivity().getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                    // Calling the same class
                    //recreate();

                    if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.signature.class)) {
                        getActivity().startService(
                                new Intent(getActivity(),
                                        com.naqelexpress.naqelpointer.service.signature.class));
                    }
                } else
                    Toast.makeText(getActivity().getApplicationContext(), "Not Saved", Toast.LENGTH_SHORT).show();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //  getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();
                // Calling the same class
                getActivity().recreate();
            }
        });
        dialog.show();
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        @SuppressLint("WrongThread")
        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getApplication()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        double pos = 0, cash = 0, Total;
        if (txtPOS != null && txtPOS.getText().length() > 0) {
            try {
                pos = Double.parseDouble(txtPOS.getText().toString());
            } catch (NumberFormatException ex) {
            }
        }
        if (txtCash != null && txtCash.length() > 0) {
            try {

                cash = Double.parseDouble(txtCash.getText().toString());
            } catch (NumberFormatException ex) {
            }
        }
        Total = pos + cash;
        lbTotal.setText(getResources().getString(R.string.TotalCollectedAmount) + String.valueOf(Total));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtPOS.setText(savedInstanceState.getString("txtPOS"));
            txtCash.setText(savedInstanceState.getString("txtCash"));
            lbTotal.setText(savedInstanceState.getString("lbTotal"));
            txtReceiverName.setText(savedInstanceState.getString("txtReceiverName"));
            signmand = savedInstanceState.getInt("signmand");


        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtPOS", txtPOS.getText().toString());
        outState.putString("txtCash", txtCash.getText().toString());
        outState.putString("lbTotal", lbTotal.getText().toString());
        outState.putString("txtReceiverName", txtReceiverName.getText().toString());
        outState.putInt("signmand", signmand);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("sign")) {
                        signmand = extras.getInt("sign");

                    }
                }
            }
        }
    }

    EditText iqamaid, phoneno, receivername;
    PopupWindow popup;
    boolean IsCancel = false;

    private void showPopupNoOTP() {


        try {
            RelativeLayout viewGroup = (RelativeLayout) rootView.findViewById(R.id.popup);
            LayoutInflater layoutInflater = (LayoutInflater)
                    getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View layout = layoutInflater.inflate(R.layout.nootp, null);


            int IqamaLength = 0;
            DBConnections dbConnections = new DBConnections(getContext(), null);
            Cursor result = dbConnections.Fill("select IqamaLength from MyRouteShipments Where ItemNo = '"
                            + DeliveryFirstFragment.txtWaybillNo.getText().toString() + "'",
                    getContext());

            if (result.getCount() > 0) {
                result.moveToFirst();
                IqamaLength = result.getInt(result.getColumnIndex("IqamaLength"));

            }
            result.close();
            dbConnections.close();

            iqamaid = (EditText) layout.findViewById(R.id.iqamaid);
            iqamaid.setFilters(new InputFilter[]{new InputFilter.LengthFilter(IqamaLength)});
            phoneno = (EditText) layout.findViewById(R.id.phoneno);
            receivername = (EditText) layout.findViewById(R.id.receivername);

            Button ok = (Button) layout.findViewById(R.id.ok);
            Button cancel = (Button) layout.findViewById(R.id.cancel);
            ok.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (iqamaid.getText().toString().length() == 0) {
                        GlobalVar.GV().ShowSnackbar(rootView, "Kindly enter Iqama No", GlobalVar.AlertType.Error);
                        return;
                    } else if (phoneno.getText().toString().length() == 0) {
                        GlobalVar.GV().ShowSnackbar(rootView, "Kindly enter Mobile No", GlobalVar.AlertType.Error);
                        return;
                    } else if (receivername.getText().toString().length() == 0) {
                        GlobalVar.GV().ShowSnackbar(rootView, "Kindly enter Name", GlobalVar.AlertType.Error);
                        return;
                    }
                    IsCancel = true;
                    popup.dismiss();

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    iqamaid.setText("");
                    phoneno.setText("");
                    receivername.setText("");
                    otpcheckbox.setChecked(false);
                    Isnootp = false;
                    IsCancel = true;
                    popup.dismiss();

                }
            });

            popup = new PopupWindow(getActivity());
            popup.setContentView(layout);
            popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            popup.setFocusable(true);
            popup.update();
            popup.setOutsideTouchable(false);
            int OFFSET_X = 30;
            int OFFSET_Y = 30;
            // Clear the default translucent background
            popup.setBackgroundDrawable(new BitmapDrawable());
            popup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            //           popup.setTouchInterceptor(new View.OnTouchListener() {

//                public boolean onTouch(View v, MotionEvent event) {
//
//                    if (!isInside(layout, event)) {
//                        GlobalVar.GV().ShowSnackbar(rootView, "Kindly press Ok/Cancel Button", GlobalVar.AlertType.Error);
//                        return false;
//                    }
////                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
////                        //popup.dismiss();
////                        GlobalVar.GV().ShowSnackbar(rootView, "Kindly press Ok/Cancel Button", GlobalVar.AlertType.Error);
////                        return true;
////                    }
////
//                    return true;
//                }
//            });
            popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (!IsCancel) {


                        if (iqamaid.getText().toString().length() == 0) {
                            GlobalVar.GV().ShowSnackbar(rootView, "Kindly enter Iqama No", GlobalVar.AlertType.Error);
                            showPopupNoOTP();
                            return;
                        } else if (phoneno.getText().toString().length() == 0) {
                            showPopupNoOTP();
                            GlobalVar.GV().ShowSnackbar(rootView, "Kindly enter Mobile No", GlobalVar.AlertType.Error);
                            return;
                        } else if (receivername.getText().toString().length() == 0) {
                            showPopupNoOTP();
                            GlobalVar.GV().ShowSnackbar(rootView, "Kindly enter Name", GlobalVar.AlertType.Error);
                            return;
                        }
                    }
                    IsCancel = false;
                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private boolean isInside(View v, MotionEvent e) {
        return !(e.getX() < 0 || e.getY() < 0
                || e.getX() > v.getMeasuredWidth()
                || e.getY() > v.getMeasuredHeight());
    }

    private void popupvalidatepayments() {

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("WaybillNo", Integer.parseInt(DeliveryFirstFragment.txtWaybillNo.getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).create();
        //alertDialog.setTitle(getResources().getString(R.string.app_name));
        alertDialog.setCancelable(false);
        alertDialog.setMessage("kindly choose Payment Info");
        /*alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "StcPay", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                new ValidatePayment().execute(jsonObject.toString(), "0");
                dialog.dismiss();
            }
        });*/
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "Payment Gateway", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new ValidatePayment().execute(jsonObject.toString(), "1");
                dialog.dismiss();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        // android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
//        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//       // final android.support.v7.app.AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//        alertDialog.setCancelable(false);
//        alertDialog.setTitle("Kindly choose payment Information");
//        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "StcPay", new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int id) {
//                new ValidatePayment().execute(jsonObject.toString(), "0");
//                alertDialog.dismiss();
//            }
//        });
//        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Payment Gateway", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                new ValidatePayment().execute(jsonObject.toString(), "1");
//                alertDialog.dismiss();
//            }
//        });
//
//        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                alertDialog.dismiss();
//            }
//        });
//
//        alertDialog.show();
    }

    private class ValidatePayment extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalVar.hideKeyboardFrom(getContext(), rootView);
            progressDialog = ProgressDialog.show(getActivity(),
                    "Please wait.", "Your Request has been process, kindly be patient  ", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            String redirectfn = params[1];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                String function = "ValidateSTCPayment";
                if (redirectfn.equals("1"))
                    function = "ValidatePaymentAgainstWaybill";
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + function);
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

                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Info")
                                .setContentText(jsonObject.getString("ErrorMessage"))
                                .show();
                    } else {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Info")
                                .setContentText(jsonObject.getString("ErrorMessage"))
                                .show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            } else {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Info")
                        .setContentText("something went wrong/check your Internet/server is busy,kindly try again later")
                        .show();
            }
            progressDialog.dismiss();
        }
    }
}